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

package org.eclipse.gymnast.generator.core.generator;

import org.eclipse.gymnast.generator.core.ast.AltRule;
import org.eclipse.gymnast.generator.core.ast.AltRuleKind;
import org.eclipse.gymnast.generator.core.ast.AttrList;
import org.eclipse.gymnast.generator.core.ast.Attrs;
import org.eclipse.gymnast.generator.core.ast.ListRule;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.ast.SeqRule;
import org.eclipse.gymnast.generator.core.ast.SimpleExpr;
import org.eclipse.gymnast.generator.core.ast.TokenRule;

/**
 * @author cjdaly@us.ibm.com
 *
 */
public class ASTUtil {
	
	public static String getName(Rule rule) {
		return rule.getChild(0).getChild(1).getText();
	}
	
	public static boolean hasAttr(SimpleExpr se, String attrText) {
		return hasAttr(se.getAttrs(), attrText);
	}
	
	public static boolean hasAttr(Rule rule, String attrText) {

		if (rule instanceof AltRule) {
			AltRule r = (AltRule) rule;
			return hasAttr(r.getDecl().getAttrs(), attrText);
		}
		else if (rule instanceof ListRule) {
			ListRule r = (ListRule) rule;
			return hasAttr(r.getDecl().getAttrs(), attrText);
		}
		else if (rule instanceof SeqRule) {
			SeqRule r = (SeqRule) rule;
			return hasAttr(r.getDecl().getAttrs(), attrText);
		}
		else if (rule instanceof TokenRule) {
			TokenRule r = (TokenRule) rule;
			return hasAttr(r.getDecl().getAttrs(), attrText);
		}
		
		return false;
	}
	
	private static boolean hasAttr(Attrs attrs, String attrText) {
		if (attrs == null) return false;
		AttrList attrList = attrs.getAttrList();
		
		for (int i = 0; i < attrList.getChildCount(); i+=2) {
			if (attrText.equals(attrList.getChild(i).getText())) return true;
		}
		return false;
	}
	
	public static boolean isAbstract(Rule rule) {
		if (rule instanceof AltRule) {
			AltRule altRule = (AltRule)rule;
			return altRule.getDecl().getKind().getText().equals(AltRuleKind.KW_ABSTRACT);
		}
		return false;
	}
	public static boolean isContainer(Rule rule) {
		if (rule instanceof AltRule) {
			AltRule altRule = (AltRule)rule;
			return altRule.getDecl().getKind().getText().equals(AltRuleKind.KW_CONTAINER);
		}
		return false;
	}
	public static boolean isInterface(Rule rule) {
		if (rule instanceof AltRule) {
			AltRule altRule = (AltRule)rule;
			return altRule.getDecl().getKind().getText().equals(AltRuleKind.KW_INTERFACE);
		}
		return false;
	}
	public static boolean isList(Rule rule) {
		return (rule instanceof ListRule);
	}
	public static boolean isSequence(Rule rule) {
		return (rule instanceof SeqRule);
	}
	public static boolean isToken(Rule rule) {
		return (rule instanceof TokenRule);
	}
	
}
