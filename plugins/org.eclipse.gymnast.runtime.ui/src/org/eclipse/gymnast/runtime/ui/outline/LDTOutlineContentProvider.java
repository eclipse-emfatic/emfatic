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

package org.eclipse.gymnast.runtime.ui.outline;

import org.eclipse.gymnast.runtime.core.outline.OutlineNode;
import org.eclipse.gymnast.runtime.ui.editor.LDTEditor;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class LDTOutlineContentProvider implements ITreeContentProvider {
	
	private LDTEditor _editor;
	
	public LDTOutlineContentProvider(LDTEditor editor) {
		_editor = editor;
	}
	
	public Object getParent(Object element) {
		if (element instanceof OutlineNode){
			OutlineNode n = (OutlineNode)element;
			return n.getParent();
		}
		return null;
	}
	
	public boolean hasChildren(Object element) {
		if (element instanceof OutlineNode){
			OutlineNode n = (OutlineNode)element;
			return n.hasChildren();
		}
		return false;
	}
	
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof OutlineNode){
			OutlineNode n = (OutlineNode)parentElement;
			return n.getChildren();
		}
		return new Object[0];
	}
	
	public Object[] getElements(Object inputElement) {
		
		Object[] elements = _editor.getOutlineElements();
		
		if (elements == null) return new Object[0];
		else return elements;
	}
	
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}
	
	public void dispose() {

	}

}
