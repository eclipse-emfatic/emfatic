/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.editor;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.core.generics.util.OneToManyMap;
import org.eclipse.emf.emfatic.core.generics.util.OneToOneMap;
import org.eclipse.emf.emfatic.core.lang.gen.ast.BoundExceptWildcard;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ClassDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.CompUnit;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNode;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Reference;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TypeWithMulti;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Wildcard;
import org.eclipse.emf.emfatic.ui.hyperlinks.EmfaticHyperlinkDetector;
import org.eclipse.emf.emfatic.ui.outline.EmfaticContentOutlinePage;
import org.eclipse.emf.emfatic.ui.partition.EmfaticDocumentProvider;
import org.eclipse.emf.emfatic.ui.redsquiggles.EmfaticCSTChangeListener;
import org.eclipse.emf.emfatic.ui.views.TypesView;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.outline.OutlineNode;
import org.eclipse.gymnast.runtime.ui.editor.LDTEditor;
import org.eclipse.gymnast.runtime.ui.editor.LDTSourceViewerConfiguration;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionSupport;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.part.IShowInSource;
import org.eclipse.ui.part.IShowInTargetList;
import org.eclipse.ui.part.ShowInContext;
import org.eclipse.ui.texteditor.ContentAssistAction;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

public class EmfaticEditor extends LDTEditor implements IShowInTargetList,
		IShowInSource {

	private EmfaticEditorSelectionListener selectionListener = new EmfaticEditorSelectionListener(
			this);
	private EmfaticContentOutlinePage _emfaticContentOutlinePage;
	private EmfaticKeyListener _keyListener = null;
	private OutlineNode[] lastShownOutlineNodes = new OutlineNode[0];
	private IViewReference _typesViewReference = null;

	public EmfaticEditor() {
		addParseTreeChangedListener(new EmfaticCSTChangeListener(this));
		setDocumentProvider(new EmfaticDocumentProvider());
	}

	protected LDTSourceViewerConfiguration createSourceViewerConfiguration() {
		return new EmfaticSourceViewerConfiguration(this);
	}

	@Override
	protected EmfaticOutlineConfiguration createOutlineConfiguration() {
		return new EmfaticOutlineConfiguration(this);
	}

	private ProjectionSupport projectionSupport;
	private ProjectionAnnotationModel annotationModel;
	private Annotation[] oldAnnotations;

	/**
	 * folding support, as explained in
	 * http://www.eclipse.org/articles/Article-Folding-in-Eclipse-Text-Editors/folding.html
	 */
	public void updateFoldingStructure(List<Position> positions) {
		initFoldingSupport();
		Annotation[] annotations = new Annotation[positions.size()];
		// the new (annotation, position) pairs
		HashMap<ProjectionAnnotation, Position> newAnnotations = new HashMap<ProjectionAnnotation, Position>();
		for (int i = 0; i < positions.size(); i++) {
			ProjectionAnnotation annotation = new ProjectionAnnotation();
			newAnnotations.put(annotation, positions.get(i));
			annotations[i] = annotation;
		}
		annotationModel.modifyAnnotations(oldAnnotations, newAnnotations, null);
		oldAnnotations = annotations;
	}

	/**
	 * folding support, as explained in
	 * http://www.eclipse.org/articles/Article-Folding-in-Eclipse-Text-Editors/folding.html
	 * 
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		initFoldingSupport();
		selectionListener.install(getSelectionProvider());
		_keyListener = new EmfaticKeyListener(this);
		getSourceViewer().getTextWidget().addKeyListener(_keyListener);
	}

	private void initFoldingSupport() {
		if (annotationModel == null) {
			ProjectionViewer viewer = (ProjectionViewer) getSourceViewer();
			projectionSupport = new ProjectionSupport(viewer,
					getAnnotationAccess(), getSharedColors());
			// see AntEditor for hovers displaying HTML
			/*
			 * A summary is an annotation that gets created out of all
			 * annotations with a type that has been registered through this
			 * method and that are inside the folded region.
			 * 
			 */
			projectionSupport
					.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.error");
			projectionSupport
					.addSummarizableAnnotationType("org.eclipse.ui.workbench.texteditor.warning");
			projectionSupport.install();
			// turn projection mode on
			viewer.doOperation(ProjectionViewer.TOGGLE);
			annotationModel = viewer.getProjectionAnnotationModel();
		}
	}

	/**
	 * folding support, as explained in
	 * http://www.eclipse.org/articles/Article-Folding-in-Eclipse-Text-Editors/folding.html
	 * 
	 * 
	 * @see org.eclipse.ui.texteditor.AbstractDecoratedTextEditor#createSourceViewer(org.eclipse.swt.widgets.Composite,
	 *      org.eclipse.jface.text.source.IVerticalRuler, int)
	 */
	protected ISourceViewer createSourceViewer(Composite parent,
			IVerticalRuler ruler, int styles) {
		fAnnotationAccess = getAnnotationAccess();
		fOverviewRuler = createOverviewRuler(getSharedColors());
		ISourceViewer viewer = new ProjectionViewer(parent, ruler,
				getOverviewRuler(), isOverviewRulerVisible(), styles);
		// ensure decoration support has been created and configured.
		getSourceViewerDecorationSupport(viewer);
		// viewer.addTextListener(textListener);
		return viewer;
	}

	/*
	 * LDT has already support for this (as well as for ITextListener). I
	 * realized after adding it here
	 */
	public EmfaticASTNode getClosestEnclosingASTNodeAt(int offset, Class<?> filter) {
		return getClosestEnclosingASTNodeAt(offset, 0, filter);
	}

	public EmfaticASTNode getClosestEnclosingASTNodeAt(int offset, int length,
			Class<?> filter) {
		EmfaticASTNode node = getClosestEnclosingASTNodeAtWithin(
				getParseRoot(), offset, length, filter);
		return node;
	}

	private EmfaticASTNode getClosestEnclosingASTNodeAtWithin(ASTNode within,
			int offset, int length, Class<?> filter) {
		if (within == null) {
			return null;
		}
		ASTNode node = within.getNodeAt(offset, 0);
		// search within the for any ASTNode
		for (int i = offset + 1; (node == null) && (i < offset + length); i++) {
			node = getParseRoot().getNodeAt(i, 0);
		}
		if (node != null) {
			/*
			 * An outer node fulfilling the filter condition may have been found
			 * which hides a more specific node also fulfilling the filter
			 */
			if (node.getChildren().length > 0) {
				for (ASTNode child : node.getChildren()) {
					ASTNode innerNode = getClosestEnclosingASTNodeAtWithin(
							child, offset, length, filter);
					if (innerNode != null) {
						node = innerNode;
						break;
					}
				}
			}
			/*
			 * search upwards till finding a node of type filter, or reaching
			 * the root
			 */
			while (!filter.isInstance(node) && (node.getParent() != null)) {
				node = node.getParent();
			}
		}
		if (filter.isInstance(node)) {
			return (EmfaticASTNode) node;
		} else {
			return null;
		}
	}

	@Override
	public void dispose() {
		if (selectionListener != null) {
			selectionListener.uninstall(getSelectionProvider());
			selectionListener = null;
		}
		super.dispose();
	}

	public OneToOneMap<ASTNode, EObject> getCstDecl2EcoreAST() {
		CompUnit compUnit = (CompUnit) getParseRoot();
		if (compUnit != null) {
			return compUnit.getCstDecl2EcoreAST();
		}
		return null;
	}

	public OneToManyMap<EObject, ASTNode> getEcoreDecl2CstUse() {
		CompUnit compUnit = (CompUnit) getParseRoot();
		if (compUnit != null) {
			return compUnit.getEcoreDecl2CstUse();
		}
		return null;
	}

	public IDocument getDocument() {
		IDocument doc = null;
		if (getDocumentProvider() == null) {
			return doc;
		}
		doc = getDocumentProvider().getDocument(getEditorInput());
		return doc;
	}

	public void openTarget(EmfaticASTNode linkTarget) {
		setSelection(linkTarget, true);
	}

	public void setSelection(EmfaticASTNode reference, boolean moveCursor) {
		if (reference == null) {
			if (moveCursor) {
				resetHighlightRange();
				markInNavigationHistory();
			}
			return;
		}

		if (moveCursor) {
			markInNavigationHistory();
		}

		ISourceViewer sourceViewer = getSourceViewer();
		if (sourceViewer == null) {
			return;
		}
		StyledText textWidget = sourceViewer.getTextWidget();
		if (textWidget == null) {
			return;
		}

		try {
			int offset = reference.getRangeStart();
			if (offset < 0) {
				return;
			}
			int length = reference.getRangeLength();
			textWidget.setRedraw(false);
			if (length > 0) {
				setHighlightRange(offset, length, moveCursor);
			}
			if (!moveCursor) {
				return;
			}
			if (offset > -1 && length > 0) {
				sourceViewer.revealRange(offset, length);
				// Selected region begins one index after offset
				sourceViewer.setSelectedRange(offset, length);
				markInNavigationHistory();
			}
		} catch (IllegalArgumentException x) {
			x.printStackTrace();
		} finally {
			textWidget.setRedraw(true);
		}
	}

	public void setContentOutlinePage(
			EmfaticContentOutlinePage emfaticContentOutlinePage) {
		_emfaticContentOutlinePage = emfaticContentOutlinePage;

	}

	protected EmfaticContentOutlinePage getContentOutlinePage() {
		return _emfaticContentOutlinePage;
	}

	public EObject getEcoreDeclAtCursor() {
		ISelection selection = getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection) {
			ITextSelection ts = (ITextSelection) selection;
			int offset = ts.getOffset();
			int length = ts.getLength();
			ReferedEcoreDecl red = getReferedEcoreDecl(offset, length);
			if (red == null || red.ecoreDecl == null) {
				return null;
			}
			EObject ecoreDecl = red.ecoreDecl;
			return ecoreDecl;
		}
		return null;
	}

	public EObject gotoDeclaration() {
		EObject ecoreDecl = getEcoreDeclAtCursor();
		if (ecoreDecl == null) {
			return null;
		}
		EmfaticASTNode landingPlace = EmfaticHyperlinkDetector.getLandingPlace(
				ecoreDecl, this);
		setSelection(landingPlace, true);
		return ecoreDecl;
	}

	public ReferedEcoreDecl getReferedEcoreDecl(int offset, int length) {
		/*
		 * first chance: Wildcard
		 */
		EmfaticASTNode node = getClosestEnclosingASTNodeAt(offset, length,
				Wildcard.class);
		if (node != null) {
			// it can still be an unbounded wildcard
			node = ((Wildcard) node).getBoundExceptWildcard();
		}
		if (node == null) {
			// second chance: BoundExceptWildcard
			node = getClosestEnclosingASTNodeAt(offset, length,
					BoundExceptWildcard.class);
			if (node == null) {
				// thir chance: TypeWithMult
				node = getClosestEnclosingASTNodeAt(offset, length,
						TypeWithMulti.class);
				if (node == null) {
					// fourth chance: Reference (for an opposite reference)
					node = getClosestEnclosingASTNodeAt(offset, length,
							Reference.class);
					if (node == null) {
						return null;
					}
					node = ((Reference) node).getOppositeName();
				} else {
					// leave a BoundExceptWildcard in node
					node = ((TypeWithMulti) node).getName();
				}
			}
		}
		// actually the QualifiedID was placed in the bigMap
		if (node instanceof BoundExceptWildcard) {
			node = ((BoundExceptWildcard) node)
					.getRawTNameOrTVarOrParamzedTName();
		}
		EObject ecoreDecl = getEcoreDecl2CstUse().getInv(node);
		ReferedEcoreDecl res = new ReferedEcoreDecl();
		res.ecoreDecl = ecoreDecl;
		res.node = node;
		return res;
	}

	public class ReferedEcoreDecl {
		public EObject ecoreDecl;
		public EmfaticASTNode node;
	}

	protected void createActions() {
		super.createActions();
		ResourceBundle bundle = EmfaticEditorMessages.getResourceBundle();

		IAction action = new ContentAssistAction(bundle,
				"ContentAssistProposal.", this); //$NON-NLS-1$
		action
				.setActionDefinitionId(ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS);
		setAction("ContentAssistProposal", action); //$NON-NLS-1$
		markAsStateDependentAction("ContentAssistProposal", true); //$NON-NLS-1$
		// TODO PlatformUI.getWorkbench().getHelpSystem().setHelp(action,
		// helpContextId);

	}

	@Override
	public OutlineNode[] getOutlineElements() {
		OutlineNode[] candidateOutlineNodes = super.getOutlineElements();
		if (candidateOutlineNodes != null && candidateOutlineNodes.length > 0) {
			lastShownOutlineNodes = candidateOutlineNodes;
		}
		return lastShownOutlineNodes;
	}

	/**
	 * see p. 467 and p. 499 of JDGE 2nd Ed
	 */
	public String[] getShowInTargetIds() {
		EObject ecoreDecl = getEcoreDeclAtCursor();
		String[] res = null;
		if (ecoreDecl == null) {
			ecoreDecl = getEcoreDeclAtCursor2();
			if (ecoreDecl == null) {
				res = new String[] {};
			} else {
				res = new String[] {TypesView.ID };
			}
			
		} else {
			res = new String[] { TypesView.ID };
		}
		return res;
	}

	private EObject getEcoreDeclAtCursor2() {
		ISelection selection = getSelectionProvider().getSelection();
		if (selection instanceof ITextSelection) {
			ITextSelection ts = (ITextSelection) selection;
			int offset = ts.getOffset();
			//int length = ts.getLength();
			ASTNode node = getClosestEnclosingASTNodeAt(offset, ClassDecl.class);
			if (node == null) {
				return null; 
			}
			EObject ecoreDecl = getCstDecl2EcoreAST().get(node);
			return ecoreDecl;
		}
		return null;

	}

	/**
	 * see p. 467 and p. 499 of JDGE 2nd Ed
	 */
	public ShowInContext getShowInContext() {
		FileEditorInput fei = (FileEditorInput) getEditorInput();
		ISelection selection = getSelectionProvider().getSelection();
		IFile f = fei.getFile();
		ShowInContext res = new ShowInContext(f, selection);
		return res;
	}

	public void showInTypeHierarchy(EClass openedDecl) {
		if (!(openedDecl instanceof EClass)) {
			return;
		}
		TypesView tv = getTypesView();
		if (tv == null) {
			return;
		}
		WeakReference<EClass> wrC = new WeakReference<EClass>(
				(EClass) openedDecl);
		tv.setInput(wrC, true);
	}

	public TypesView getTypesView() {
		// 1) try if there's a cached reference
		if (_typesViewReference != null) {
			TypesView tv = (TypesView) _typesViewReference.getView(false);
			if (tv != null) {
				return tv;
			}
		}
		// 2) look for the view opened by the user
		IWorkbenchPage wp = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		IViewReference _typesViewReference = wp.findViewReference(TypesView.ID);
		if (_typesViewReference != null) {
			TypesView tv = (TypesView) _typesViewReference.getView(true);
			if (tv != null) {
				return tv;
			}
		}
		// 3) open it programatically
		try {
			IViewPart tv = wp.showView(TypesView.ID);
			return (TypesView) tv;
		} catch (PartInitException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	
	
}
