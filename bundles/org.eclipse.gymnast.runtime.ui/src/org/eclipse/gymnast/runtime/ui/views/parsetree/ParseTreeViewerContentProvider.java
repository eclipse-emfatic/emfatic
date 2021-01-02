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

package org.eclipse.gymnast.runtime.ui.views.parsetree;

import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class ParseTreeViewerContentProvider implements ITreeContentProvider {

	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ASTNode) {
			ASTNode node = (ASTNode)parentElement;
			return node.getChildren();
		}
		else return new Object[0];
	}

	public Object getParent(Object element) {
		if (element instanceof ASTNode) {
			ASTNode node = (ASTNode)element;
			return node.getParent();
		}
		else return null;
	}

	public boolean hasChildren(Object element) {
		if (element instanceof ASTNode) {
			ASTNode node = (ASTNode)element;
			return node.hasChildren();
		}
		else return false;
	}

	public Object[] getElements(Object inputElement) {
			
		if (inputElement instanceof IParseTreeViewInput) {
			IParseTreeViewInput input = (IParseTreeViewInput)inputElement;
			ASTNode parseRoot = input.getParseRoot();
			if (parseRoot != null) {
				return new Object[] { parseRoot };
			}
		}
		
		return new Object[0];
	}

	public void dispose() {
	}

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
