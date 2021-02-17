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

package org.eclipse.emf.emfatic.ui.editor;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.core.lang.gen.ast.BoundExceptWildcard;
import org.eclipse.emf.emfatic.core.lang.gen.ast.CompUnit;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNode;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TopLevelDecl;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.outline.OutlineNode;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ISynchronizable;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class EmfaticEditorSelectionListener implements ISelectionChangedListener {

	private EmfaticEditor _editor = null;

	public EmfaticEditorSelectionListener(EmfaticEditor editor) {
		_editor = editor;
	}

	/**
	 * places as in the Java editor a highlight range on the left for the chosen
	 * class, datatype, enum, or mapentry
	 */
	public void highlightRangeForTopLevelDecl(ITextSelection selection) {
		_editor.resetHighlightRange();
		ITextSelection ts = (ITextSelection) selection;
		/*
		 * highlight the border of the top-level declaration at the cursor, if
		 * any
		 */
		int offset = ts.getOffset();
		int length = ts.getLength();
		ASTNode declAtCursor = _editor.getClosestEnclosingASTNodeAt(offset, length, TopLevelDecl.class);
		if ((declAtCursor != null) && declAtCursor instanceof TopLevelDecl) {
			_editor.setHighlightRange(declAtCursor.getRangeStart(), declAtCursor.getRangeLength(), false);
		}
	}

	public void selectionChanged(SelectionChangedEvent event) {
		ISelection selection = event.getSelection();
		if (selection instanceof ITextSelection) {
			ITextSelection textSelection = (ITextSelection) selection;
			highlightRangeForTopLevelDecl(textSelection);
			markOccurrences(textSelection);
			selectInOutline(textSelection);
		}
	}

	private void selectInOutline(ITextSelection ts) {
		int offset = ts.getOffset();
		int length = ts.getLength();
		EmfaticASTNode n = _editor.getClosestEnclosingASTNodeAt(offset, length, Object.class);
		CompUnit compUnit = (CompUnit) _editor.getParseRoot();
		if (compUnit == null) {
			return;
		}
		Map<ASTNode, OutlineNode> a2o = compUnit.getCst2Outline();
		OutlineNode toHighlight = findOutlineNodeFor(n, a2o);
		if (toHighlight == null) {
			return;
		}
		ISelection currentOutlineSelection = _editor.getContentOutlinePage().getSelection();
		OutlineNode selected = null;
		if (currentOutlineSelection instanceof TreeSelection) {
			selected = (OutlineNode) ((TreeSelection) currentOutlineSelection).getFirstElement();
		}
		boolean skipSelect = (selected == toHighlight);
		if (!skipSelect) {
			_editor.getContentOutlinePage().selectFromEditor(toHighlight);
		}
	}

	private OutlineNode findOutlineNodeFor(ASTNode n, Map<ASTNode, OutlineNode> a2o) {
		if (a2o == null) {
			return null; 
		}
		OutlineNode res = a2o.get(n);
		if (res != null) {
			return res;
		}
		if (n != null && n.getParent() != null) {
			res = findOutlineNodeFor(n.getParent(), a2o);
			return res;
		}
		return null;
	}

	private void markOccurrences(ITextSelection ts) {
		removeOccurrenceAnnotations();
		int offset = ts.getOffset();
		int length = ts.getLength();
		BoundExceptWildcard cstUse = (BoundExceptWildcard) _editor.getClosestEnclosingASTNodeAt(offset, length,
				BoundExceptWildcard.class);
		if (cstUse == null) {
			return;
		}
		EObject ecoreDecl = _editor.getEcoreDecl2CstUse().getInv(cstUse.getRawTNameOrTVarOrParamzedTName());
		if (ecoreDecl == null) {
			return;
		}
		Set<ASTNode> occuNodes = _editor.getEcoreDecl2CstUse().get(ecoreDecl);
		Map<Annotation, Position> annotationMap = new HashMap<Annotation, Position>();
		for (ASTNode n : occuNodes) {
			String message = "";
			Position pos = new Position(n.getRangeStart(), n.getRangeLength());
			try {
				message = _editor.getDocument().get(pos.offset, pos.length);
			} catch (BadLocationException ex) {
				// Skip this match
				continue;
			}
			Annotation ann = new Annotation("org.eclipse.jdt.ui.occurrences", false, message);
			annotationMap.put(ann, pos);
		}
		IDocumentProvider documentProvider = _editor.getDocumentProvider();
		IDocument document = _editor.getDocument();
		IAnnotationModel annotationModel = documentProvider.getAnnotationModel(_editor.getEditorInput());
		if (annotationModel == null)
			return;
		Object lock = getLockObject(document);
		if (lock == null) {
			updateAnnotations(annotationModel, annotationMap);
		} else {
			synchronized (lock) {
				updateAnnotations(annotationModel, annotationMap);
			}
		}
	}

	private void updateAnnotations(IAnnotationModel annotationModel, Map annotationMap) {
		if (annotationModel instanceof IAnnotationModelExtension) {
			((IAnnotationModelExtension) annotationModel).replaceAnnotations(fOccurrenceAnnotations, annotationMap);
		} else {
			removeOccurrenceAnnotations();
			Iterator iter = annotationMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry mapEntry = (Map.Entry) iter.next();
				annotationModel.addAnnotation((Annotation) mapEntry.getKey(), (Position) mapEntry.getValue());
			}
		}
		fOccurrenceAnnotations = (Annotation[]) annotationMap.keySet().toArray(
				new Annotation[annotationMap.keySet().size()]);
	}

	private void removeOccurrenceAnnotations() {
		IDocumentProvider documentProvider = _editor.getDocumentProvider();
		if (documentProvider == null) {
			return;
		}
		IAnnotationModel annotationModel = documentProvider.getAnnotationModel(_editor.getEditorInput());
		if (annotationModel == null || fOccurrenceAnnotations == null) {
			return;
		}
		IDocument document = documentProvider.getDocument(_editor.getEditorInput());
		Object lock = getLockObject(document);
		if (lock == null) {
			updateAnnotationModelForRemoves(annotationModel);
		} else {
			synchronized (lock) {
				updateAnnotationModelForRemoves(annotationModel);
			}
		}
	}

	private void updateAnnotationModelForRemoves(IAnnotationModel annotationModel) {
		if (annotationModel instanceof IAnnotationModelExtension) {
			((IAnnotationModelExtension) annotationModel).replaceAnnotations(fOccurrenceAnnotations, null);
		} else {
			for (int i = 0, length = fOccurrenceAnnotations.length; i < length; i++) {
				annotationModel.removeAnnotation(fOccurrenceAnnotations[i]);
			}
		}
		fOccurrenceAnnotations = null;
	}

	private Annotation[] fOccurrenceAnnotations = null;

	/**
	 * Installs this selection changed listener with the given selection
	 * provider. If the selection provider is a post selection provider, post
	 * selection changed events are the preferred choice, otherwise normal
	 * selection changed events are requested.
	 * 
	 * @param selectionProvider
	 */
	public void install(ISelectionProvider selectionProvider) {
		if (selectionProvider == null) {
			return;
		}
		if (selectionProvider instanceof IPostSelectionProvider) {
			IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
			provider.addPostSelectionChangedListener(this);
		} else {
			selectionProvider.addSelectionChangedListener(this);
		}
	}

	/**
	 * Removes this selection changed listener from the given selection
	 * provider.
	 * 
	 * @param selectionProvider
	 */
	public void uninstall(ISelectionProvider selectionProvider) {
		if (selectionProvider == null) {
			return;
		}
		if (selectionProvider instanceof IPostSelectionProvider) {
			IPostSelectionProvider provider = (IPostSelectionProvider) selectionProvider;
			provider.removePostSelectionChangedListener(this);
		} else {
			selectionProvider.removeSelectionChangedListener(this);
		}
	}

	private Object getLockObject(IDocument doc) {
		Object lock = null;
		if (doc instanceof ISynchronizable) {
			lock = ((ISynchronizable) doc).getLockObject();
		}
		return lock;
	}

}
