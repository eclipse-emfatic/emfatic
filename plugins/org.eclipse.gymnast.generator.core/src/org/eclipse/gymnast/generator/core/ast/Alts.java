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
package org.eclipse.gymnast.generator.core.ast;

import java.util.ArrayList;

import org.eclipse.gymnast.runtime.core.ast.ASTNode;

import antlr.Token;


/**
 *
 * @generated by Gymnast from gymnast.ast on Aug 15, 2004 2:28:15 PM
 */
public class Alts extends GymnastASTNode {

	private ArrayList _children = new ArrayList();
	
	/**
	 * @return the number of children of this ASTNode
	 */
	public int getChildCount() {
		return _children.size();
	}

	/**
	 * @param index the index of a child ASTNode to get
	 * @return the child ASTNode at the given index
	 * @throws IndexOutOfBoundsException when the index is out of bounds
	 */
	public ASTNode getChild(int index) {
		return (ASTNode)_children.get(index);
	}

	/**
	 * Add a child to this list.
	 */
	public void addChild(GymnastASTNode child) {
		if (child == null) return;
		if (child._parent != null) throw new RuntimeException();
		_children.add(child);
		child._parent = this;
	}

	/**
	 * Wrap the provided Token in a GymnastTokenNode
	 * and add it as a child of this node.
	 * 
	 * @param token the Token to be added as a child of this node
	 */
	public void addChild(Token token) {
		addChild(new GymnastTokenNode(token));
	}

	/**
	 * Construct a new Alts.
	 */
	public Alts() {
		super();
	}

	/**
	 * This method overrides the superclass <code>acceptImpl</code> providing
	 * the same implementation.  Here <code>this</code> refers to this specific node
	 * class, so the <code>beginVisit</code> and <code>endVisit</code> methods
	 * specific to this type in the visitor will be invoked.
	 */
	public void acceptImpl(GymnastASTNodeVisitor visitor) {
		boolean visitChildren = visitor.beginVisit(this);
		if (visitChildren) visitChildren(visitor);
		visitor.endVisit(this);
	}

}
