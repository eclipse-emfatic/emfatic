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

/**
 * A Visitor pattern stub implementation for language Gymnast.
 *
 * @generated by Gymnast from gymnast.ast on Aug 15, 2004 2:28:15 PM
 */
public class GymnastASTNodeVisitor {

	/**
	 * The external entry point used to perform a visit beginning at the given node.
	 * 
	 * @param node the GymnastASTNode to visit
	 */
	public final void visit(GymnastASTNode node) {
		node.accept(this);
	}

	/**
	 * Called just before <code>beginVisit</code> for each node being visited.
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param node the GymnastASTNode currently being visited
	 */
	public void preVisit(GymnastASTNode node) {
	}

	/**
	 * Called just after <code>endVisit</code> for each node being visited.
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param node the GymnastASTNode currently being visited
	 */
	public void postVisit(GymnastASTNode node) {
	}

	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the generic one called for node types that
	 * don't provide a specific overloaded form of <code>beginVisit</code>.
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param node the GymnastASTNode currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(GymnastASTNode node) {
		return true;
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the generic one called for node types that
	 * don't provide a specific overloaded form of <code>endVisit</code>.
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param node the GymnastASTNode currently being visited
	 */
	public void endVisit(GymnastASTNode node) {
	}

	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>GymnastTokenNode</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param tokenNode the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(GymnastTokenNode tokenNode) {
		return beginVisit((GymnastASTNode)tokenNode);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>GymnastTokenNode</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param tokenNode the node currently being visited
	 */
	public void endVisit(GymnastTokenNode tokenNode) {
		endVisit((GymnastASTNode)tokenNode);
	}

	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>CompUnit</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param compUnit the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(CompUnit compUnit) {
		return beginVisit((GymnastASTNode)compUnit);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>CompUnit</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param compUnit the node currently being visited
	 */
	public void endVisit(CompUnit compUnit) {
		endVisit((GymnastASTNode)compUnit);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>HeaderSection</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param headerSection the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(HeaderSection headerSection) {
		return beginVisit((GymnastASTNode)headerSection);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>HeaderSection</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param headerSection the node currently being visited
	 */
	public void endVisit(HeaderSection headerSection) {
		endVisit((GymnastASTNode)headerSection);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>OptionsSection</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param optionsSection the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(OptionsSection optionsSection) {
		return beginVisit((GymnastASTNode)optionsSection);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>OptionsSection</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param optionsSection the node currently being visited
	 */
	public void endVisit(OptionsSection optionsSection) {
		endVisit((GymnastASTNode)optionsSection);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>OptionList</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param optionList the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(OptionList optionList) {
		return beginVisit((GymnastASTNode)optionList);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>OptionList</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param optionList the node currently being visited
	 */
	public void endVisit(OptionList optionList) {
		endVisit((GymnastASTNode)optionList);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>Option</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param option the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(Option option) {
		return beginVisit((GymnastASTNode)option);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>Option</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param option the node currently being visited
	 */
	public void endVisit(Option option) {
		endVisit((GymnastASTNode)option);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>OptionValue</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param optionValue the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(OptionValue optionValue) {
		return beginVisit((GymnastTokenNode)optionValue);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>OptionValue</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param optionValue the node currently being visited
	 */
	public void endVisit(OptionValue optionValue) {
		endVisit((GymnastTokenNode)optionValue);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>Grammar</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param grammar the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(Grammar grammar) {
		return beginVisit((GymnastASTNode)grammar);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>Grammar</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param grammar the node currently being visited
	 */
	public void endVisit(Grammar grammar) {
		endVisit((GymnastASTNode)grammar);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>Rule</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param rule the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(Rule rule) {
		return beginVisit((GymnastASTNode)rule);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>Rule</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param rule the node currently being visited
	 */
	public void endVisit(Rule rule) {
		endVisit((GymnastASTNode)rule);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>AltRule</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param altRule the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(AltRule altRule) {
		return beginVisit((Rule)altRule);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>AltRule</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param altRule the node currently being visited
	 */
	public void endVisit(AltRule altRule) {
		endVisit((Rule)altRule);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>AltRuleDecl</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param altRuleDecl the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(AltRuleDecl altRuleDecl) {
		return beginVisit((GymnastASTNode)altRuleDecl);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>AltRuleDecl</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param altRuleDecl the node currently being visited
	 */
	public void endVisit(AltRuleDecl altRuleDecl) {
		endVisit((GymnastASTNode)altRuleDecl);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>AltRuleKind</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param altRuleKind the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(AltRuleKind altRuleKind) {
		return beginVisit((GymnastTokenNode)altRuleKind);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>AltRuleKind</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param altRuleKind the node currently being visited
	 */
	public void endVisit(AltRuleKind altRuleKind) {
		endVisit((GymnastTokenNode)altRuleKind);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>AltRuleBody</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param altRuleBody the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(AltRuleBody altRuleBody) {
		return beginVisit((GymnastASTNode)altRuleBody);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>AltRuleBody</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param altRuleBody the node currently being visited
	 */
	public void endVisit(AltRuleBody altRuleBody) {
		endVisit((GymnastASTNode)altRuleBody);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>Alts</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param alts the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(Alts alts) {
		return beginVisit((GymnastASTNode)alts);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>Alts</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param alts the node currently being visited
	 */
	public void endVisit(Alts alts) {
		endVisit((GymnastASTNode)alts);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>AltSeq</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param altSeq the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(AltSeq altSeq) {
		return beginVisit((GymnastASTNode)altSeq);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>AltSeq</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param altSeq the node currently being visited
	 */
	public void endVisit(AltSeq altSeq) {
		endVisit((GymnastASTNode)altSeq);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>ListRule</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param listRule the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(ListRule listRule) {
		return beginVisit((Rule)listRule);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>ListRule</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param listRule the node currently being visited
	 */
	public void endVisit(ListRule listRule) {
		endVisit((Rule)listRule);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>ListRuleDecl</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param listRuleDecl the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(ListRuleDecl listRuleDecl) {
		return beginVisit((GymnastASTNode)listRuleDecl);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>ListRuleDecl</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param listRuleDecl the node currently being visited
	 */
	public void endVisit(ListRuleDecl listRuleDecl) {
		endVisit((GymnastASTNode)listRuleDecl);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>ListRuleBody</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param listRuleBody the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(ListRuleBody listRuleBody) {
		return beginVisit((GymnastASTNode)listRuleBody);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>ListRuleBody</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param listRuleBody the node currently being visited
	 */
	public void endVisit(ListRuleBody listRuleBody) {
		endVisit((GymnastASTNode)listRuleBody);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>ListMark</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param listMark the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(ListMark listMark) {
		return beginVisit((GymnastTokenNode)listMark);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>ListMark</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param listMark the node currently being visited
	 */
	public void endVisit(ListMark listMark) {
		endVisit((GymnastTokenNode)listMark);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>SeqRule</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param seqRule the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(SeqRule seqRule) {
		return beginVisit((Rule)seqRule);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>SeqRule</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param seqRule the node currently being visited
	 */
	public void endVisit(SeqRule seqRule) {
		endVisit((Rule)seqRule);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>SeqRuleDecl</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param seqRuleDecl the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(SeqRuleDecl seqRuleDecl) {
		return beginVisit((GymnastASTNode)seqRuleDecl);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>SeqRuleDecl</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param seqRuleDecl the node currently being visited
	 */
	public void endVisit(SeqRuleDecl seqRuleDecl) {
		endVisit((GymnastASTNode)seqRuleDecl);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>Seq</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param seq the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(Seq seq) {
		return beginVisit((GymnastASTNode)seq);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>Seq</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param seq the node currently being visited
	 */
	public void endVisit(Seq seq) {
		endVisit((GymnastASTNode)seq);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>TokenRule</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param tokenRule the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(TokenRule tokenRule) {
		return beginVisit((Rule)tokenRule);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>TokenRule</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param tokenRule the node currently being visited
	 */
	public void endVisit(TokenRule tokenRule) {
		endVisit((Rule)tokenRule);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>TokenRuleDecl</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param tokenRuleDecl the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(TokenRuleDecl tokenRuleDecl) {
		return beginVisit((GymnastASTNode)tokenRuleDecl);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>TokenRuleDecl</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param tokenRuleDecl the node currently being visited
	 */
	public void endVisit(TokenRuleDecl tokenRuleDecl) {
		endVisit((GymnastASTNode)tokenRuleDecl);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>Expr</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param expr the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(Expr expr) {
		return beginVisit((GymnastASTNode)expr);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>Expr</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param expr the node currently being visited
	 */
	public void endVisit(Expr expr) {
		endVisit((GymnastASTNode)expr);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>OptSubSeq</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param optSubSeq the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(OptSubSeq optSubSeq) {
		return beginVisit((Expr)optSubSeq);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>OptSubSeq</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param optSubSeq the node currently being visited
	 */
	public void endVisit(OptSubSeq optSubSeq) {
		endVisit((Expr)optSubSeq);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>SimpleExpr</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param simpleExpr the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(SimpleExpr simpleExpr) {
		return beginVisit((Expr)simpleExpr);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>SimpleExpr</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param simpleExpr the node currently being visited
	 */
	public void endVisit(SimpleExpr simpleExpr) {
		endVisit((Expr)simpleExpr);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>Attrs</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param attrs the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(Attrs attrs) {
		return beginVisit((GymnastASTNode)attrs);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>Attrs</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param attrs the node currently being visited
	 */
	public void endVisit(Attrs attrs) {
		endVisit((GymnastASTNode)attrs);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>AttrList</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param attrList the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(AttrList attrList) {
		return beginVisit((GymnastASTNode)attrList);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>AttrList</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param attrList the node currently being visited
	 */
	public void endVisit(AttrList attrList) {
		endVisit((GymnastASTNode)attrList);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>Atom</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param atom the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(Atom atom) {
		return beginVisit((GymnastTokenNode)atom);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>Atom</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param atom the node currently being visited
	 */
	public void endVisit(Atom atom) {
		endVisit((GymnastTokenNode)atom);
	}
	/**
	 * This is called, for each node being visited, just after <code>preVisit</code> and
	 * before (optionally) visiting the children of the node.
	 * This <code>beginVisit</code> method is the specific one called for nodes
	 * of type <code>Id</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param id the node currently being visited
	 * @return true to visit the children of the node, false to prevent visiting the children of the node
	 */
	public boolean beginVisit(Id id) {
		return beginVisit((GymnastTokenNode)id);
	}

	/**
	 * This is called, for each node being visited, after <code>beginVisit</code> and
	 * (optionally) visiting the children of the node and before <code>postVisit</code>.
	 * This <code>endVisit</code> method is the specific one called for nodes
	 * of type <code>Id</code>
	 * This may be overridden to provide special behavior at that point in processing.
	 * 
	 * @param id the node currently being visited
	 */
	public void endVisit(Id id) {
		endVisit((GymnastTokenNode)id);
	}


}