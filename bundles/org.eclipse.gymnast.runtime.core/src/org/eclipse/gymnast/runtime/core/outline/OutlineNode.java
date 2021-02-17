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

package org.eclipse.gymnast.runtime.core.outline;

import java.util.ArrayList;

import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.swt.graphics.Image;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class OutlineNode {
	
	private ASTNode _astNode;
	
	private OutlineNode _parent;
	private ArrayList _children = new ArrayList();
	
	private String _text;
	private Image _image;
	
	//
	// Constructors
	//
	
	public OutlineNode(String text) {
		this(null, text, null);
	}
	
	public OutlineNode(String text, Image image) {
		this(null, text, image);
	}
	
	public OutlineNode(ASTNode astNode, String text) {
		this(astNode, text, null);
	}
	
	public OutlineNode(ASTNode astNode, String text, Image image) {
		_astNode = astNode;
		_text = text;
		_image = image;
	}
	
	//
	//
	//
	
	public ASTNode getASTNode() {
		return _astNode;
	}
	
	public OutlineNode getParent() {
		return _parent;
	}
	
	public boolean hasChildren() {
		return _children.size() > 0;
	}
	
	public OutlineNode[] getChildren() {
		return (OutlineNode[]) _children.toArray(new OutlineNode[_children.size()]);
	}
	
	public void addChild(OutlineNode node) {
		node._parent = this;
		_children.add(node);
	}
	
	public String getText() {
		return _text;
	}
	
	public Image getImage() {
		return _image;
	}
	
}
