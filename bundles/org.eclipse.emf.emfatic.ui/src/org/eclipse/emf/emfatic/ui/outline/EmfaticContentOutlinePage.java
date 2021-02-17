/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.outline;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ClassDecl;
import org.eclipse.emf.emfatic.ui.EmfaticUIPlugin;
import org.eclipse.emf.emfatic.ui.editor.EmfaticEditor;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.outline.OutlineNode;
import org.eclipse.gymnast.runtime.ui.outline.LDTContentOutlinePage;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPageSite;

public class EmfaticContentOutlinePage extends LDTContentOutlinePage {

	private EmfaticEditor _editor = null;

	private Action annFilterAction;
	private AnnotationFilter annFilter;

	private Action attrFilterAction;
	private AttrFilter attrFilter;

	private Action refFilterAction;
	private RefFilter refFilter;

	private Action opFilterAction;
	private OpFilter opFilter;

	private Action typeParamFilterAction;
	private TypeParamFilter typeParamFilter;

	private Action fOpenInTypeHierarchy;

	private Menu fMenu;

	public EmfaticContentOutlinePage(EmfaticEditor _editor) {
		super(_editor);
		this._editor = _editor;
		_editor.setContentOutlinePage(this);
	}

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		makeFilterActions();
		IActionBars bars = getSite().getActionBars();
		IToolBarManager manager = bars.getToolBarManager();
		manager.add(annFilterAction);
		manager.add(attrFilterAction);
		manager.add(refFilterAction);
		manager.add(opFilterAction);
		manager.add(typeParamFilterAction);
		// allows external contributions
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
		manager.add(new Separator());
		// TODO decorator
		// getTreeViewer().setLabelProvider(createOutlineLabelProvideDecorated());

		populateContextMenu();
	}

	private void populateContextMenu() {
		fOpenInTypeHierarchy = new Action() {
			public void run() {
				OutlineNode element = getSelectedNode();
				if (element != null) {
					if (element.getASTNode() != null) {
						if (element.getASTNode() instanceof ClassDecl) {
							ClassDecl cd = (ClassDecl) element.getASTNode();
							EObject eO = _editor.getCstDecl2EcoreAST().get(cd);
							if (eO != null && (eO instanceof EClass)) {
								_editor.showInTypeHierarchy((EClass) eO);
							}
						}
					}
				}
				
			}
		};
		fOpenInTypeHierarchy.setText("fOpenInTypeHierarchy");
		fOpenInTypeHierarchy.setToolTipText("fOpenInTypeHierarchy");
		fOpenInTypeHierarchy.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages().getImageDescriptor(
						ISharedImages.IMG_OBJS_INFO_TSK));

		// see AntEditor and p. 470 on JDGE 2nd Ed.
		TreeViewer viewer = getTreeViewer();
		MenuManager manager = new MenuManager("#PopUp"); //$NON-NLS-1$
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager menuManager) {
				contextMenuAboutToShow(menuManager);
			}
		});
		fMenu = manager.createContextMenu(viewer.getTree());
		viewer.getTree().setMenu(fMenu);

		IPageSite site = getSite();
		site.registerContextMenu(
				"org.eclipse.emf.emfatic.ui.outline", manager, viewer); //$NON-NLS-1$
	}

	public ILabelProvider createOutlineLabelProvideDecorated() {
		// ILabelProvider ldtOLP = (ILabelProvider)
		// getTreeViewer().getLabelProvider();
		// ILabelDecorator decorator = new EmfaticLabelDecorator();
		// DecoratingLabelProvider decoratedOLP = new
		// DecoratingLabelProvider(ldtOLP, decorator);
		// return decoratedOLP;
		return null;
	}

	void makeFilterActions() {

		annFilterAction = new AnnFilterAction("Show Annotations",
				IAction.AS_CHECK_BOX, annFilter, getTreeViewer());
		annFilterAction.setText("Hide Annotations");
		annFilterAction.setToolTipText("Hide Annotations");
		// Image annImg = EmfaticOutlineBuilder.getAnnotationImage();
		ImageDescriptor id = getImageDescriptorFromIconsFolder("HideEAnnotations.gif");
		annFilterAction.setImageDescriptor(id);

		attrFilterAction = new AttrFilterAction("Show Attributes",
				IAction.AS_CHECK_BOX, attrFilter, getTreeViewer());
		attrFilterAction.setText("Hide Attributes");
		attrFilterAction.setToolTipText("Hide Attributes");
		id = getImageDescriptorFromIconsFolder("HideEAttributes.gif");
		attrFilterAction.setImageDescriptor(id);

		refFilterAction = new RefFilterAction("Show References",
				IAction.AS_CHECK_BOX, refFilter, getTreeViewer());
		refFilterAction.setText("Hide References");
		refFilterAction.setToolTipText("Hide References");
		id = getImageDescriptorFromIconsFolder("HideEReferences.gif");
		refFilterAction.setImageDescriptor(id);

		opFilterAction = new OpFilterAction("Show Operations",
				IAction.AS_CHECK_BOX, opFilter, getTreeViewer());
		opFilterAction.setText("Hide Operations");
		opFilterAction.setToolTipText("Hide Operations");
		id = getImageDescriptorFromIconsFolder("HideEOperations.gif");
		opFilterAction.setImageDescriptor(id);

		typeParamFilterAction = new TypeParamFilterAction("Show Type Params",
				IAction.AS_CHECK_BOX, typeParamFilter, getTreeViewer());
		typeParamFilterAction.setText("Hide Type Params");
		typeParamFilterAction.setToolTipText("Hide Type Params");
		id = getImageDescriptorFromIconsFolder("HideETypeParameters.gif");
		typeParamFilterAction.setImageDescriptor(id);
	}

	/**
	 * @param name
	 *            the name of the icon file
	 * @return an Image
	 */
	static public Image getImageFromIconsFolder(String name) {
		String iconPath = "icons/";
		try {
			URL installURL = EmfaticUIPlugin.getDefault().getBundle().getEntry(
					"/");
			URL url = new URL(installURL, iconPath + name);
			ImageDescriptor imageDescriptor = ImageDescriptor
					.createFromURL(url);
			Image image = imageDescriptor.createImage();
			return image;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	/**
	 * @param name
	 *            the name of the icon file
	 * @return an Image
	 */
	static public ImageDescriptor getImageDescriptorFromIconsFolder(String name) {
		String iconPath = "icons/";
		try {
			URL installURL = EmfaticUIPlugin.getDefault().getBundle().getEntry(
					"/");
			URL url = new URL(installURL, iconPath + name);
			ImageDescriptor imageDescriptor = ImageDescriptor
					.createFromURL(url);
			return imageDescriptor;
		} catch (MalformedURLException e) {
			return null;
		}
	}

	public boolean showingAnnotations() {
		return !annFilterAction.isChecked();
	}

	public boolean showingAttributes() {
		return !attrFilterAction.isChecked();
	}

	public boolean showingOperations() {
		return !opFilterAction.isChecked();
	}

	public boolean showingReferences() {
		return !refFilterAction.isChecked();
	}

	public boolean showingTypeParams() {
		return !typeParamFilterAction.isChecked();
	}

	public void selectFromEditor(OutlineNode toHighlight) {
		if (toHighlight != null) {
			ASTNode astNode = toHighlight.getASTNode();
			OutlineNode outlineRoot = null;
			TreeItem root = getTreeViewer().getTree().getTopItem();
			Object rootData = root.getData();
			if (rootData instanceof OutlineNode) {
				outlineRoot = (OutlineNode) rootData;
				while (outlineRoot.getParent() != null) {
					outlineRoot = outlineRoot.getParent();
				}
			}
			toHighlight = findOutlineNodeForASTNode(outlineRoot, astNode);
			TreeViewer viewer = getTreeViewer();
			viewer.removeSelectionChangedListener(this);
			viewer.setSelection(new StructuredSelection(toHighlight), true);
			// TODO this should be a preference:
			viewer.reveal(toHighlight);
			viewer.addSelectionChangedListener(this);
		}
	}

	private OutlineNode findOutlineNodeForASTNode(OutlineNode nodeInTree,
			ASTNode astNode) {
		if (nodeInTree == null) {
			return null;
		}
		ASTNode treeASTNode = nodeInTree.getASTNode();
		if (treeASTNode == astNode) {
			return nodeInTree;
		}
		if (nodeInTree.getChildren() != null) {
			for (OutlineNode outlineChild : nodeInTree.getChildren()) {
				OutlineNode candidate = findOutlineNodeForASTNode(outlineChild,
						astNode);
				if (candidate != null) {
					return candidate;
				}
			}
		}
		return null;
	}

	private void contextMenuAboutToShow(IMenuManager menuManager) {
		if (selectionIsEClass()) {
			menuManager.add(fOpenInTypeHierarchy);
		}
		menuManager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private boolean selectionIsEClass() {
		OutlineNode o = getSelectedNode();
		if (o.getASTNode() != null) {
			if (o.getASTNode() instanceof ClassDecl) {
				ClassDecl cd = (ClassDecl) o.getASTNode();
				EObject eO = _editor.getCstDecl2EcoreAST().get(cd);
				if (eO != null && (eO instanceof EClass)) {
					return true;
				}
			}
		}
		return false;
	}

	private OutlineNode getSelectedNode() {
		ISelection iselection = getSelection();
		if (iselection instanceof IStructuredSelection) {
			IStructuredSelection selection = (IStructuredSelection) iselection;
			if (selection.size() == 1) {
				Object selected = selection.getFirstElement();
				if (selected instanceof OutlineNode) {
					return (OutlineNode) selected;
				}
			}
		}
		return null;
	}

}
