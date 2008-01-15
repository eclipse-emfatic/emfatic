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
 *
 * @generated by Gymnast from gymnast.ast on Aug 15, 2004 2:28:15 PM
 */
public class ListRuleDecl extends GymnastASTNode  {

	public static final String KW_LIST = "list";

	private GymnastTokenNode _list_KW;
	private Id _name;
	private Attrs _attrs;
	private GymnastTokenNode _colon;

	public GymnastTokenNode getList_KW() {
		return _list_KW;
	}
	public Id getName() {
		return _name;
	}
	public Attrs getAttrs() {
		return _attrs;
	}
	public GymnastTokenNode getColon() {
		return _colon;
	}


	/**
	 * @return the number of children of this ASTNode
	 */
	public int getChildCount() {
		int count = 0;
		if (_list_KW != null) count++;
		if (_name != null) count++;
		if (_attrs != null) count++;
		if (_colon != null) count++;

		return count;
	}

	/**
	 * @param index the index of a child ASTNode to get
	 * @return the child ASTNode at the given index
	 * @throws IndexOutOfBoundsException when the index is out of bounds
	 */
	public ASTNode getChild(int index) {
		int count = -1;
		if ((_list_KW != null) && (++count == index)) return _list_KW;
		if ((_name != null) && (++count == index)) return _name;
		if ((_attrs != null) && (++count == index)) return _attrs;
		if ((_colon != null) && (++count == index)) return _colon;

		throw new IndexOutOfBoundsException();
	}
	
	/**
	 * Construct a new ListRuleDecl.
	 */
	public ListRuleDecl(
		Token list_KW,
		Id name,
		Attrs attrs,
		Token colon
	) {
		super();

		if (list_KW != null) {
			_list_KW = new GymnastTokenNode(list_KW);
			if (_list_KW._parent != null) throw new RuntimeException();
			_list_KW._parent = this;
		}
		if (name != null) {
			_name = name;
			if (_name._parent != null) throw new RuntimeException();
			_name._parent = this;
		}
		if (attrs != null) {
			_attrs = attrs;
			if (_attrs._parent != null) throw new RuntimeException();
			_attrs._parent = this;
		}
		if (colon != null) {
			_colon = new GymnastTokenNode(colon);
			if (_colon._parent != null) throw new RuntimeException();
			_colon._parent = this;
		}

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
