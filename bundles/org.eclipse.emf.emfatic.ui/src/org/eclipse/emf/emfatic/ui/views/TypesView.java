/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.views;

import java.lang.ref.WeakReference;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ClassDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNode;
import org.eclipse.emf.emfatic.ui.EmfaticUIPlugin;
import org.eclipse.emf.emfatic.ui.editor.EmfaticEditor;
import org.eclipse.emf.emfatic.ui.editor.EmfaticEditor.ReferedEcoreDecl;
import org.eclipse.emf.emfatic.ui.hyperlinks.EmfaticHyperlinkDetector;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ViewForm;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IShowInTarget;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.part.ViewPart;

public class TypesView extends ViewPart implements IShowInTarget {

	public static final String ID = "org.eclipse.emf.emfatic.ui.views.TypesView";

	public TypesView() {
		super();
	}

	private TreeViewer typesViewer;
	private TreeViewer memberViewer;
	// private DrillDownAdapter drillDownAdapter;
	private Action actionSupertypeH;
	private Action actionSubtypeH;
	private TypesViewDoubleClick doubleClickAction;
	private String _currentClassifier = "";
	private PageBook fPagebook;
	private Label fNoHierarchyShownLabel;
	private SashForm fTypeMethodsSplitter;
	private ViewForm fTypeViewerViewForm;
	private PageBook fViewerbook;
	private ViewForm fMethodViewerViewForm;
	private TableViewer fMethodsViewer;
	private CLabel fMethodViewerPaneLabel;
	private IAction fShowInheritedMembersAction;
	private IAction fSortByDefiningTypeAction;

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {

		/*
		 * A pagebook is a composite control where only a single control is
		 * visible at a time. It is similar to a notebook, but without tabs.
		 */
		fPagebook = new PageBook(parent, SWT.NONE);

		// page 1 of page book (no hierarchy label)

		fNoHierarchyShownLabel = new Label(fPagebook, SWT.TOP + SWT.LEFT
				+ SWT.WRAP);
		fNoHierarchyShownLabel
				.setText(TypeHierarchyMessages.TypeHierarchyViewPart_empty);

		// page 2 of page book (viewers)

		fTypeMethodsSplitter = new SashForm(fPagebook, SWT.VERTICAL);
		fTypeMethodsSplitter.setVisible(false);

		fTypeViewerViewForm = new ViewForm(fTypeMethodsSplitter, SWT.NONE);

		Control typeViewerControl = createTypeViewerControl(fTypeViewerViewForm);
		fTypeViewerViewForm.setContent(typeViewerControl);

		fMethodViewerViewForm = new ViewForm(fTypeMethodsSplitter, SWT.NONE);
		fTypeMethodsSplitter.setWeights(new int[] { 35, 65 });

		Control methodViewerPart = createMethodViewerControl(fMethodViewerViewForm);
		fMethodViewerViewForm.setContent(methodViewerPart);

		fMethodViewerPaneLabel = new CLabel(fMethodViewerViewForm, SWT.NONE);
		fMethodViewerViewForm.setTopLeft(fMethodViewerPaneLabel);

		fTypeMethodsSplitter.pack();

		makeTypeViewerActions();
		hookTypeViewerClickActions();
		contributeToActionBars();

		fillTheMethodViewerToolbar();

	}

	private void fillTheMethodViewerToolbar() {
		ToolBar methodViewerToolBar = new ToolBar(fMethodViewerViewForm,
				SWT.FLAT | SWT.WRAP);
		fMethodViewerViewForm.setTopCenter(methodViewerToolBar);

		fShowInheritedMembersAction = new Action("Show inherited members",
				IAction.AS_CHECK_BOX) {
			public void run() {
				fMethodsViewer.refresh();
			}
		};
		fShowInheritedMembersAction.setToolTipText("Show inherited members");
		fShowInheritedMembersAction.setImageDescriptor(EmfaticUIPlugin
				.getImageDescriptor("typesView/inher_co"));
		fShowInheritedMembersAction.setChecked(false);

		fSortByDefiningTypeAction = new Action("Sort by defining type",
				IAction.AS_CHECK_BOX) {
			public void run() {
				if (!isChecked()) {
					fMethodsViewer.setSorter(null);
				} else {
					fMethodsViewer.setSorter(new ViewerSorter() {

						@Override
						public int compare(Viewer viewer, Object e1, Object e2) {

							int subtyping = compareSubtyping(e1, e2);
							if (subtyping != 0) {
								return subtyping;
							}
							String s1 = MethodsViewLabelProvider.labelFor(e1,
									true);
							String s2 = MethodsViewLabelProvider.labelFor(e2,
									true);
							return s1.compareTo(s2);
						}

						private int compareSubtyping(Object e1, Object e2) {
							int res = 0;
							EClass declClass1 = declaringClass(e1);
							EClass declClass2 = declaringClass(e2);
							if (declClass1 != null && declClass2 != null) {
								if (declClass1.getEAllSuperTypes().contains(
										declClass2)) {
									return -1;
								}
								if (declClass2.getEAllSuperTypes().contains(
										declClass1)) {
									return 1;
								}
							}
							return res;
						}

						private EClass declaringClass(Object e) {
							EClass eC = null;
							if (e instanceof EStructuralFeature) {
								EStructuralFeature sf = (EStructuralFeature) e;
								eC = sf.getEContainingClass();
							}
							if (e instanceof EOperation) {
								EOperation eOp = (EOperation) e;
								eC = eOp.getEContainingClass();
							}
							return eC;
						}
					});
				}
				fMethodsViewer.refresh();
			}
		};
		fSortByDefiningTypeAction.setToolTipText("Sort by defining type");
		fSortByDefiningTypeAction.setImageDescriptor(EmfaticUIPlugin
				.getImageDescriptor("typesView/definingtype_sort_co"));
		fSortByDefiningTypeAction.setChecked(false);

		// fill the method viewer tool bar
		ToolBarManager lowertbmanager = new ToolBarManager(methodViewerToolBar);
		lowertbmanager.add(new Separator());
		lowertbmanager.add(fShowInheritedMembersAction);
		lowertbmanager.add(fSortByDefiningTypeAction);
		lowertbmanager.add(new Separator());

		lowertbmanager.update(true);

	}

	private void initActionMember(IAction a, final String msg) {
		a.setText(msg);
		a.setToolTipText(msg);
		a.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJS_INFO_TSK));
	}

	private Control createMethodViewerControl(ViewForm parent) {

		/*
		 * how to use a table viewer:
		 * http://www-128.ibm.com/developerworks/library/os-ecgui2/
		 * 
		 */
		fMethodsViewer = new TableViewer(parent, SWT.BORDER
				| SWT.FULL_SELECTION | SWT.MULTI);
		fMethodsViewer.setContentProvider(new MethodsViewContentProvider(this));
		fMethodsViewer.setLabelProvider(new MethodsViewLabelProvider(this));

		TableColumn column = new TableColumn(fMethodsViewer.getTable(),
				SWT.LEFT);
		column.setText("Name");
		column.setWidth(400);

		fMethodsViewer.getTable().setHeaderVisible(true);

		Control control = fMethodsViewer.getTable();

		// on double click navigate in the editor to declaration
		fMethodsViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (!(event.getSelection() instanceof IStructuredSelection)) {
					return;
				}
				IStructuredSelection ssel = (IStructuredSelection) event
						.getSelection();
				if (!(ssel.getFirstElement() instanceof EObject)) {
					return;
				}
				EObject eO = (EObject) ssel.getFirstElement();
				// TODO find the right one!
				EmfaticEditor editor = getActiveEmfaticEditor();
				if (editor != null) {
					EmfaticASTNode landingPlace = EmfaticHyperlinkDetector
							.getLandingPlace(eO, editor);
					if (landingPlace != null) {
						editor.setSelection(landingPlace, true);
						editor.setFocus();
					}
				}
			}
		});

		return control;
	}

	private Control createTypeViewerControl(Composite parent) {
		fViewerbook = new PageBook(parent, SWT.NULL);

		// KeyListener keyListener= createKeyListener();

		// Create the viewers
		typesViewer = new TreeViewer(fViewerbook, SWT.MULTI | SWT.H_SCROLL
				| SWT.V_SCROLL);

		typesViewer.setSorter(new ViewerSorter() {

			@Override
			public int compare(Viewer viewer, Object e1, Object e2) {

				if (e1 instanceof EClass && e2 instanceof EClass) {
					EClass eC1 = (EClass) e1;
					EClass eC2 = (EClass) e2;
					return eC1.getName().compareToIgnoreCase(eC2.getName());
				}
				return super.compare(viewer, e1, e2);
			}
		});

		typesViewer.setContentProvider(new TypesViewContentProvider(this));
		typesViewer.setLabelProvider(new TypesViewLabelProvider());

		return fViewerbook;
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		manager.add(actionSupertypeH);
		manager.add(actionSubtypeH);
		manager.add(new Separator());
	}

	private void makeTypeViewerActions() {
		actionSupertypeH = new Action("Show the supertype hierarchy",
				IAction.AS_CHECK_BOX) {

			public void run() {
				actionSubtypeH.setChecked(!actionSupertypeH.isChecked());
				fMethodsViewer.setInput(null);
				typesViewer.refresh();
			}
		};
		actionSupertypeH.setToolTipText("Show the supertype hierarchy");
		actionSupertypeH.setImageDescriptor(EmfaticUIPlugin
				.getImageDescriptor("typesView/super_co"));
		actionSupertypeH.setChecked(false);

		actionSubtypeH = new Action("Show the subtype hierarchy",
				IAction.AS_CHECK_BOX) {
			public void run() {
				actionSupertypeH.setChecked(!actionSubtypeH.isChecked());
				fMethodsViewer.setInput(null);
				typesViewer.refresh();
			}
		};
		actionSubtypeH.setText("Show the subtype hierarchy");
		actionSubtypeH.setToolTipText("Show the subtype hierarchy");
		actionSubtypeH.setImageDescriptor(EmfaticUIPlugin
				.getImageDescriptor("typesView/sub_co"));
		actionSubtypeH.setChecked(true);

		doubleClickAction = new TypesViewDoubleClick(this, typesViewer);
	}

	private void hookTypeViewerClickActions() {

		// on double click navigate in the editor to EClass declaration
		typesViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				doubleClickAction.run();
			}
		});

		// on single click update the members view
		typesViewer
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						IStructuredSelection ssel = (IStructuredSelection) event
								.getSelection();
						if (!(ssel.getFirstElement() instanceof EClass)) {
							fMethodsViewer.setInput(null);
							return;
						}
						EClass eC = (EClass) ssel.getFirstElement();
						fMethodsViewer.setInput(eC);
					}
				});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(typesViewer.getControl().getShell(),
				"Types View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		fPagebook.setFocus();
	}

	public void setInput(WeakReference<EClass> wrC, boolean navigateInEditor) {
		EClass eC = wrC.get();
		_currentClassifier = getQualifiedName(eC);
		typesViewer.setInput(wrC);
		fPagebook.showPage(fTypeMethodsSplitter);
		fViewerbook.showPage(typesViewer.getControl());
		typesViewer.getTree().setFocus();
	}

	private String getQualifiedName(EClassifier eC) {
		String res = eC.getName();
		EPackage eP = eC.getEPackage();
		do {
			res = eP.getName() + "." + res;
			eP = eP.getESuperPackage();
		} while (eP != null);
		return res;
	}

	/**
	 * see p. 467 and p. 499 of JDGE 2nd Ed
	 */
	public boolean show(ShowInContext context) {
		if (context == null) {
			return false;
		}
		if (!(context.getSelection() instanceof ITextSelection)) {
			return false;
		}
		if (!(context.getInput() instanceof IFile)) {
			return false;
		}
		EmfaticEditor editor = getActiveEmfaticEditor();
		if (editor == null) {
			return false;
		}
		ITextSelection ts = (ITextSelection) context.getSelection();
		int offset = ts.getOffset();
		int length = ts.getLength();
		ReferedEcoreDecl red = editor.getReferedEcoreDecl(offset, length);

		EObject ecoreDecl = null;

		if (red == null || red.ecoreDecl == null) {
			// perhaps the cursor is at a declaration and not at a usage
			ASTNode n = editor.getClosestEnclosingASTNodeAt(offset,
					ClassDecl.class);
			ecoreDecl = editor.getCstDecl2EcoreAST().get(n);
			if (ecoreDecl == null) {
				return false;
			}
		} else {
			ecoreDecl = red.ecoreDecl;
		}

		if (!(ecoreDecl instanceof EClass)) {
			return false;
		}
		EClass eC = (EClass) ecoreDecl;
		WeakReference<EClass> wrC = new WeakReference<EClass>(eC);
		setInput(wrC, false);
		return true;
	}

	EmfaticEditor getEditor(IFile f) {
		IWorkbenchPage[] wps = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getPages();
		for (IWorkbenchPage wp : wps) {
			IEditorPart[] edsInPage = wp.getEditors();
			for (IEditorPart ep : edsInPage) {
				if (ep instanceof EmfaticEditor) {
					EmfaticEditor ee = (EmfaticEditor) ep;
					if (!(ee.getEditorInput() instanceof FileEditorInput)) {
						return null;
					}
					FileEditorInput fei = (FileEditorInput) ee.getEditorInput();
					if (fei.getFile() == f) {
						return ee;
					}
				}
			}
		}
		return null;
	}

	EmfaticEditor getActiveEmfaticEditor() {
		IEditorPart ep = PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getActivePage().getActiveEditor();
		if (ep instanceof EmfaticEditor) {
			EmfaticEditor ee = (EmfaticEditor) ep;
			return ee;
		}
		return null;
	}

	public Object getTypesViewerInput() {
		Object i = typesViewer.getInput();
		return i;
	}

	public boolean isShowinSuperTypeHierarchy() {
		return actionSupertypeH.isChecked();
	}

	public boolean isShowingInheritedMembers() {
		return fShowInheritedMembersAction.isChecked();
	}

	public boolean isSortingByDefiningType() {
		return fSortByDefiningTypeAction.isChecked();
	}

}