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

import org.eclipse.gymnast.runtime.core.ast.ASTNode;

import antlr.Token;


/**
 * Represents a token in the language grammar.
 * Can be subclassed to represent specific token subgroups.
 *
 * @generated by Gymnast from gymnast.ast on Aug 15, 2004 2:28:15 PM
 */
public class GymnastTokenNode extends GymnastASTNode {

	/**
	 * @return the number of children of this ASTNode
	 */
	public final int getChildCount() {
		// token rules cannot have children!
		return 0;
	}

	/**
	 * @param index the index of a child ASTNode to get
	 * @return the child ASTNode at the given index
	 * @throws IndexOutOfBoundsException when the index is out of bounds
	 */
	public final ASTNode getChild(int index) {
		// token rules cannot have children!
		throw new IndexOutOfBoundsException();
	}

	/**
	 * Construct a new GymnastTokenNode
	 */
	public GymnastTokenNode(Token token) {
		super(token);
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
