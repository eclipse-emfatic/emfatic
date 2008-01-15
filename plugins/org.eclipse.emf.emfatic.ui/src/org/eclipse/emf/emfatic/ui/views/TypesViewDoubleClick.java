/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.views;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNode;
import org.eclipse.emf.emfatic.ui.editor.EmfaticEditor;
import org.eclipse.emf.emfatic.ui.hyperlinks.EmfaticHyperlinkDetector;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;

public class TypesViewDoubleClick extends Action {

	private TypesView _tv;
	private TreeViewer _typesViewer;

	public TypesViewDoubleClick(TypesView tv, TreeViewer typesViewer) {
		_tv = tv;
		_typesViewer = typesViewer;
	}

	public void run() {
		/*
		 * TODO keep a reference to the document where the clicked type is being
		 * edited
		 */
		ISelection selection = _typesViewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj instanceof EClass) {
			// TODO find the right one! 
			EmfaticEditor editor = _tv.getActiveEmfaticEditor();
			if (editor != null) {
				EmfaticASTNode landingPlace = EmfaticHyperlinkDetector
						.getLandingPlace((EClass) obj, editor);
				if (landingPlace != null) {
					editor.setSelection(landingPlace, true);
					editor.setFocus();
				}
			}
		}
	}

}