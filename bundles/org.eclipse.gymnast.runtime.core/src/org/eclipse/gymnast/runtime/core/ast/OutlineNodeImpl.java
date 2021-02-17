/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.runtime.core.ast;

import java.util.ArrayList;

/**
 * @author cjdaly@us.ibm.com
 *
 */
public abstract class OutlineNodeImpl extends PropertySourceNodeImpl {

	public OutlineNodeImpl() {
		super();
	}

	public OutlineNodeImpl(TokenInfo tokenInfo) {
		super(tokenInfo);
	}
	

	/////////////////////////////////
	// IOutlineNode implementation //
	/////////////////////////////////
	
	public OutlineNode getOutlineParent() {
		ASTNode parent = getParent();
		while (parent != null) {
			if (parent instanceof OutlineNode) return (OutlineNode)parent;
			parent = parent.getParent();
		}
		
		return null;
	}
	
	public boolean hasOutlineChildren() {
		return getOutlineChildren().length > 0;
	}
	
	public OutlineNode[] getOutlineChildren() {
		ArrayList list = new ArrayList();
		
		for (int i = 0; i < getChildCount(); i++) {
			ASTNode child = this.getChild(i);
			if (child instanceof OutlineNode) {
				list.add(child);
			}
		}

		return (OutlineNode[]) list.toArray(new OutlineNode[list.size()]);
	}

	public String getOutlineText() {
		String text = getText();
		return (text == null) ? "" : text;
	}
	
	public int getOutlineOffset() {
		return getRangeStart();
	}
	
	public int getOutlineLength() {
		return getRangeLength();
	}

}
