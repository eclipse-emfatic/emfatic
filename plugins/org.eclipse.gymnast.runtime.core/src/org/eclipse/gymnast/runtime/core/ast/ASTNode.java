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

/**
 * @author cjdaly@us.ibm.com
 *  
 */
public interface ASTNode {
    
	//
	// token oriented methods
	//

    /**
     * @return the offset (input position) of the token if this is a token node,
     *         -1 otherwise
     */
    public int getOffset();
	
	/**
	 * @return the token type number of this is a token node, -1 otherwise
	 */
	public int getTokenType();

    /**
     * @return the token text if this is a token node, null otherwise
     */
    public String getText();

    /**
     * @return the length of the token text if this is a token node, 0 otherwise
     */
    public int getTextLength();
    
    
    
	//
	// basic navigation methods
	//
    
	/**
	 * @return the number of children of this ASTNode
	 */
    public int getChildCount();
	
	/**
	 * @param index the index of a child ASTNode to get
	 * @return the child ASTNode at the given index
	 * @throws IndexOutOfBoundsException when the index is out of bounds
	 */
    public ASTNode getChild(int index);

    /**
     * @return true of the node has child ASTNode, false otherwise
     */	
    public boolean hasChildren();

    /**
     * @return the first child of this ASTNode or null if this node has no children
     */
    public ASTNode getFirstChild();

    /**
     * @return the last child of this ASTNode or null if this node has no children
     */
    public ASTNode getLastChild();

    /**
     * @return an array of the children of this ASTNode
     */
    public ASTNode[] getChildren();

	/**
	 * @return the parent of this ASTNode or null if this is the root node of a tree
	 */
    public ASTNode getParent();

    /**
     * @return the root ASTNode of this tree
     */	
    public ASTNode getRoot();

    /**
     * @return true if this ASTNode is the root of a tree, false otherwise
     */
    public boolean isRoot();

    /**
     * @return true if this ASTNode represents a token
     */
    public boolean isTokenNode();
    

    //
    // map ASTNode to input position
    //

    /**
     * @return the input position of the beginning of input text that this
     *         ASTNode represents
     */
    public int getRangeStart();

    /**
     * @return the length of input text that this ASTNode represents
     */
    public int getRangeLength();

    /**
     * @return the input position of the end of input text that this ASTNode
     *         represents
     */
    public int getRangeEnd();
    

    //
    // deep search for leaf and token nodes
    //

    /**
     * @return the first leaf ASTNode in the sub-tree rooted at this ASTNode
     *         Note: a leaf is not necessarily a token (it could be an empty list)
     */
    public ASTNode getFirstLeaf();

    /**
     * @return the last leaf ASTNode in the sub-tree rooted at this ASTNode
     *         Note: a leaf is not necessarily a token (it could be an empty list)
     */
    public ASTNode getLastLeaf();

    /**
     * @return the first token ASTNode n the sub-tree rooted at this ASTNode
     */
    public ASTNode getFirstToken();

    /**
     * @return the last token ASTNode n the sub-tree rooted at this ASTNode
     */
    public ASTNode getLastToken();
    
    
    //
    // mapping input position to ASTNode
    //
    
	/**
     * Searches through the parse tree for the most specific (deepest) node that
     * spans the given position.
     * 
     * @param offset
     *            offset in the source text
     * @param length
     *            length in the source text
     * @param filter
     *            list of classes to include or exclude
     * @param inclusionFilter
     *            true to include this if it matches the filter and false to
     *            exclude this node if it matches filter
     * @return ASTNode best matching the given position
     */
    public ASTNode getNodeAt(int offset, int length, Class[] filter,
            boolean inclusionFilter);

    /**
     * Searches through the parse tree for the most specific (deepest) node that
     * spans the given position.
     * 
     * @param offset
     *            offset in the source text
     * @param length
     *            length in the source text
     * @param filter
     *            class to include or exclude
     * @param inclusionFilter
     *            true to include this if it matches the filter and false to
     *            exclude this node if it matches filter
     * @return ASTNode best matching the given position
     */
    public ASTNode getNodeAt(int offset, int length, Class filter,
            boolean inclusionFilter);

    /**
     * Searches through the parse tree for the most specific (deepest) node that
     * spans the given position.
     * 
     * @param offset
     *            offset in the source text
     * @param filter
     *            class to include or exclude
     * @param inclusionFilter
     *            true to include this if it matches the filter and false to
     *            exclude this node if it matches filter
     * @return ASTNode best matching the given position
     */
    public ASTNode getNodeAt(int offset, Class filter, boolean inclusionFilter);

    /**
     * Searches through the parse tree for the most specific (deepest) node that
     * spans the given position.
     * 
     * @param offset
     *            offset in the source text
     * @param length
     *            length in the source text
     * @return ASTNode best matching the given position
     */
    public ASTNode getNodeAt(int offset, int length);

    
    
    //
    // debugging methods
    //
    
    /**
     * Note: this was used for debugging with incremental parsers
     */
    public void checkTokenOffsets(String input);

    /**
     * Dumps a textual representation of the parse tree rooted at this ASTNode
     * to System.out.
     * 
     * @param level
     *            the indentation level to use for dumping this ASTNode
     */
    public void dump(int level);

    /**
     * @return a representation of the concrete ASTNode class name (called by
     *         the dump() method)
     */
    public String getTypeName();
    
}
