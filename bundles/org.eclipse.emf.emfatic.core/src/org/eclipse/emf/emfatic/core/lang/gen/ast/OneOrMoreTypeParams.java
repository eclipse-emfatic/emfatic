package org.eclipse.emf.emfatic.core.lang.gen.ast;

import java.util.ArrayList;

import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.ast.TokenInfo;

/**
 *
 * @generated by Gymnast from Emfatic.ast on 06.02.2007 17:57:34
 */
public class OneOrMoreTypeParams extends EmfaticASTNode {

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
	public void addChild(EmfaticASTNode child) {
		if (child == null) return;
		if (child._parent != null) throw new RuntimeException();
		_children.add(child);
		child._parent = this;
	}

	/**
	 * Wrap the provided Token in a EmfaticTokenNode
	 * and add it as a child of this node.
	 * 
	 * @param token the Token to be added as a child of this node
	 */
	public void addChild(TokenInfo tokenInfo) {
		addChild(new EmfaticTokenNode(tokenInfo));
	}

	/**
	 * Construct a new OneOrMoreTypeParams.
	 */
	public OneOrMoreTypeParams() {
		super();
	}

	/**
	 * This method overrides the superclass <code>acceptImpl</code> providing
	 * the same implementation.  Here <code>this</code> refers to this specific node
	 * class, so the <code>beginVisit</code> and <code>endVisit</code> methods
	 * specific to this type in the visitor will be invoked.
	 */
	public void acceptImpl(EmfaticASTNodeVisitor visitor) {
		boolean visitChildren = visitor.beginVisit(this);
		if (visitChildren) visitChildren(visitor);
		visitor.endVisit(this);
	}

}