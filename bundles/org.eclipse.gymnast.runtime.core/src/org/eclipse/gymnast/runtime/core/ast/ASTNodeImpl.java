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

package org.eclipse.gymnast.runtime.core.ast;

import java.util.ArrayList;

/**
 * 
 * @author cjdaly@us.ibm.com
 */
public abstract class ASTNodeImpl implements ASTNode {

	private static final TokenInfo NO_TOKEN = new TokenInfo(null, -1, -1);
	private final TokenInfo _tokenInfo;
	
	public ASTNodeImpl() {
		_tokenInfo = NO_TOKEN;
	}
	
	public ASTNodeImpl(TokenInfo tokenInfo) {
		_tokenInfo = tokenInfo;
	}
	
	public ASTNodeImpl(int offset, int type, String text) {
		_tokenInfo = new TokenInfo(text, offset, type);
	}
	
	//
	// local data accessors
	//
	
	public final int getOffset() {
		return _tokenInfo.getOffset();
	}
	
	public int getTokenType() {
		return _tokenInfo.getType();
	}
	
	public String getText() {
		return _tokenInfo.getText();
	}
	
	public final int getTextLength() {
		if (getText() == null) return 0;
		else return getText().length();
	}
	
	//
	// basic children methods
	//

	/**
	 * @return the number of children of this ASTNode
	 */
	public abstract int getChildCount();
	
	/**
	 * @param index the index of a child ASTNode to get
	 * @return the child ASTNode at the given index
	 * @throws IndexOutOfBoundsException when the index is out of bounds
	 */
	public abstract ASTNode getChild(int index);
	
	public final boolean hasChildren() {
		return getChildCount() != 0;
	}
	
	public final ASTNode getFirstChild() {
		if (getChildCount() == 0) return null;
		return getChild(0);
	}
	
	public final ASTNode getLastChild() {
		if (getChildCount() == 0) return null;
		else return getChild(getChildCount() - 1);
	}
	
	// TODO: this is slow ... push it up to the implementors of getChildCount()
	// and getChild(i) or get rid of it.
	public ASTNode[] getChildren()
	{
		ArrayList list = new ArrayList();
		for (int i = 0; i < getChildCount(); i++) {
			list.add(getChild(i));
		}
		return (ASTNode[]) list.toArray(new ASTNode[list.size()]);
	}
	
	//
	// parent methods
	//
	
	/**
	 * @return the parent of this ASTNode or null if this is the root node of a tree
	 */
	public abstract ASTNode getParent();
	
	public final ASTNode getRoot() {
		ASTNode root = this;
		while (root.getParent() != null) {
			root = root.getParent();
		}
		return root;
	}
	
	public final boolean isRoot() {
		return (getParent() == null);
	}
	
	//
	// derived child methods
	//
	
	public final boolean isTokenNode() {
		return ((hasChildren() == false) && (getText() != null));
	}
	
	public final int getRangeStart() {
		ASTNode firstToken = getFirstToken();
		if (firstToken == null) return -1;
		else return firstToken.getOffset();
	}
	
	public final int getRangeLength() {
		ASTNode lastToken = getLastToken();
		if (lastToken == null) return 0;
		else return (lastToken.getOffset() + lastToken.getTextLength()) - getRangeStart();
	}
	
	public final int getRangeEnd() {
		return getRangeStart() + getRangeLength();
	}
	
	public final ASTNode getFirstLeaf() {
		if (!hasChildren()) return this;

		ASTNode first = getFirstChild();
		return first.getFirstLeaf();
	}
	
	public final ASTNode getLastLeaf() {
		if (!hasChildren()) return this;

		ASTNode last = getLastChild();
		return last.getLastLeaf();
	}
	
	public final ASTNode getFirstToken() {
		if (!hasChildren()) {
			if (isTokenNode()) return this;
			else return null;
		}
		else {
			for (int i = 0; i < getChildCount(); i++) {
				ASTNode n = getChild(i);
				ASTNode temp = n.getFirstToken();
				if (temp != null) return temp;
			}
			return null;
		}
	}
	
	public final ASTNode getLastToken() {
		if (!hasChildren()) {
			if (isTokenNode()) return this;
			else return null;
		}
		else {
			for (int i = getChildCount()-1; i >= 0; i--) {
				ASTNode n = getChild(i);
				ASTNode temp = n.getLastToken();
				if (temp != null) return temp;
			}
			return null;
		}
	}
	
	/**
	 * @param offset in the source text
	 * @param length in the source text
	 * @param filter list of classes to include or exclude
	 * @param inclusionFilter true to include this if it matches the filter
	 *        and false to exclude this node if it matches filter
	 * @return ASTNode best matching the given position
	 * 
	 * Search through the parse tree for the most specific (deepest) node
	 * that spans the given position.
	 */
	public final ASTNode getNodeAt(int offset, int length, Class[] filter, boolean inclusionFilter) {

		boolean nodeTypeOk = true;

		if (filter != null) {
			boolean foundMatch = false;
			for (int i = 0; i < filter.length; i++){
				if (filter[i].isInstance(this)) {
					foundMatch = true;
				}
			}
			
			nodeTypeOk = !(inclusionFilter ^ foundMatch);
		}
		
		if ((offset >= getRangeStart()) && (offset + length < getRangeEnd())){
			for (int i = 0; i < getChildCount(); i++) {
				ASTNode child = getChild(i);
				ASTNode x = child.getNodeAt(offset, length, filter, inclusionFilter);
				if (x != null) return x;
			}
			
			if (nodeTypeOk) return this;
		}
		
		return null;
	}
	public final ASTNode getNodeAt(int offset, int length, Class filter, boolean inclusionFilter) {
		return getNodeAt(offset, length, new Class[] { filter }, inclusionFilter);
	}
	public final ASTNode getNodeAt(int offset, Class filter, boolean inclusionFilter) {
		return getNodeAt(offset, 0, new Class[] { filter }, inclusionFilter);
	}
	public final ASTNode getNodeAt(int offset, int length) {
		return getNodeAt(offset, length, new Class[] { }, false);
	}

	
	
	/////////////////////////////////////////////////////////////////////////////////////
	// BEGIN Debug method section
	public final void checkTokenOffsets(String input) {
		int offset = getOffset();
		if (offset != -1) {
			String tokenText = getText();
			if (tokenText != null) {
				try {
					String inputText = input.substring(offset, offset + tokenText.length());
				}
				catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
		
		for (int i = 0; i < getChildCount(); i++) {
			ASTNode child = getChild(i);
			child.checkTokenOffsets(input);
		}
		
	}
	
	
	public void dump(int level){

		
		for (int i = 0; i < getChildCount(); i++) {
			ASTNode child = getChild(i);
			child.dump(level+1);
		}
		
	}

	//
	public String getTypeName()
	{
		String fullName = this.getClass().getName();
		int pos = fullName.lastIndexOf(".");
		if (pos == -1) return fullName;
		else return fullName.substring(pos+1);
	}
	// END Debug method section
	///////////////////////////////////////////////////////////////////////////////////

	
}
