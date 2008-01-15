/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.runtime.ui.actions;

import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.ui.views.parsetree.IParseTreeViewInput;
import org.eclipse.gymnast.runtime.ui.views.parsetree.ParseTreeView;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class FindInParseTreeView implements IEditorActionDelegate {
	
	IEditorPart _targetEditor;

	public void setActiveEditor(IAction action, IEditorPart targetEditor) {
		_targetEditor = targetEditor;
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		action.setEnabled(isEnabled());
	}
	
	private boolean isEnabled() {
		if (_targetEditor == null) return false;
		if (ParseTreeView.getInstance() == null) return false;
		if (!(_targetEditor instanceof IParseTreeViewInput)) return false;

		return true;
	}

	public void run(IAction action) {
		if (!isEnabled()) return;
		
		IParseTreeViewInput parsingEditor = (IParseTreeViewInput)_targetEditor;
		ASTNode node = parsingEditor.getNodeAtCursor();
		ParseTreeView.getInstance().selectNode(node);
	}

}
