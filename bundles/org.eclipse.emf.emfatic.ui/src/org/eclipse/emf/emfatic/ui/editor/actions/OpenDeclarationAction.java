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

package org.eclipse.emf.emfatic.ui.editor.actions;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.ui.editor.EmfaticEditor;
import org.eclipse.jdt.ui.actions.IJavaEditorActionDefinitionIds;
import org.eclipse.jdt.ui.actions.OpenAction;
import org.eclipse.jface.commands.ActionHandler;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.ui.handlers.IHandlerService;

public class OpenDeclarationAction extends OpenAction {

	private EmfaticEditor _editor;

	public OpenDeclarationAction(EmfaticEditor editor) {
		super(editor.getSite());
		_editor = editor;
		setActionDefinitionId(IJavaEditorActionDefinitionIds.OPEN_EDITOR);
		IHandlerService handlerServer = (IHandlerService) editor.getSite()
				.getService(IHandlerService.class);
		handlerServer.activateHandler(
				IJavaEditorActionDefinitionIds.OPEN_EDITOR, new ActionHandler(
						this));

		setText(EmfaticEditorActionMessages
				.getString("OpenDeclarationAction.0")); //$NON-NLS-1$
		setDescription(EmfaticEditorActionMessages
				.getString("OpenDeclarationAction.1")); //$NON-NLS-1$
		setToolTipText(EmfaticEditorActionMessages
				.getString("OpenDeclarationAction.1")); //$NON-NLS-1$
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void run() {
		if (_editor == null) {
			return;
		}

		EObject openedDecl = _editor.gotoDeclaration();

		if (openedDecl instanceof EClass) {

			_editor.showInTypeHierarchy((EClass) openedDecl);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jdt.ui.actions.SelectionDispatchAction#selectionChanged(org.eclipse.jface.text.ITextSelection)
	 */
	public void selectionChanged(ITextSelection selection) {
		setEnabled(_editor != null);
	}

	public void setEditor(EmfaticEditor editor) {
		_editor = editor;
	}
}
