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

import org.eclipse.emf.emfatic.ui.editor.actions.OpenDeclarationAction;
import org.eclipse.gymnast.runtime.ui.editor.LDTEditorActionContributor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.texteditor.ITextEditor;

public class EmfaticEditorActionContributor extends LDTEditorActionContributor {

	// private RetargetTextEditorAction fContentAssistProposal;
	private OpenDeclarationAction fOpenDeclarationAction;

	public EmfaticEditorActionContributor() {
		super();
//		ResourceBundle bundle = EmfaticEditorMessages.getResourceBundle();

		/*
		 * fContentAssistProposal = new RetargetTextEditorAction(bundle,
		 * "ContentAssistProposal."); //$NON-NLS-1$
		 */
//		String commandId = ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS;
		// fContentAssistProposal.setActionDefinitionId(commandId);

	}

	public void contributeToMenu(IMenuManager menu) {
		super.contributeToMenu(menu);

		/*
		 * if (fContentAssistProposal != null) { IMenuManager editMenu = menu
		 * .findMenuUsingPath(IWorkbenchActionConstants.M_EDIT);
		 * editMenu.appendToGroup(IWorkbenchActionConstants.MB_ADDITIONS,
		 * fContentAssistProposal); }
		 */

		if (fOpenDeclarationAction != null) {
			IMenuManager navigateMenu = menu
					.findMenuUsingPath(IWorkbenchActionConstants.M_NAVIGATE);
			if (navigateMenu != null) {
				navigateMenu.appendToGroup(IWorkbenchActionConstants.OPEN_EXT,
						fOpenDeclarationAction);
				navigateMenu.setVisible(true);
			}
		}

	}

	public void setActiveEditor(IEditorPart part) {
		super.setActiveEditor(part);

		ITextEditor editor = null;
		if (part instanceof ITextEditor) {
			editor = (ITextEditor) part;
		}

		/*
		 * fContentAssistProposal.setAction(getAction(editor,
		 * "ContentAssistProposal")); //$NON-NLS-1$
		 */

		if (editor instanceof EmfaticEditor) {
			EmfaticEditor _editor = (EmfaticEditor) part;

			if (fOpenDeclarationAction == null) {
				fOpenDeclarationAction = new OpenDeclarationAction(_editor);
				contributeToMenu(getActionBars().getMenuManager());
			}
			if (fOpenDeclarationAction != null) {
				fOpenDeclarationAction.setEditor(_editor);
			}

		}
	}

}
