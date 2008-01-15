/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.generators.parser.javacc;

import org.eclipse.gymnast.generator.core.ast.AltRule;
import org.eclipse.gymnast.generator.core.ast.Alts;
import org.eclipse.gymnast.generator.core.ast.GymnastASTNodeVisitor;
import org.eclipse.gymnast.generator.core.ast.ListRule;
import org.eclipse.gymnast.generator.core.ast.OptSubSeq;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.ast.SeqRule;
import org.eclipse.gymnast.generator.core.ast.SimpleExpr;
import org.eclipse.gymnast.generator.core.ast.TokenRule;
import org.eclipse.gymnast.generator.core.generator.ASTUtil;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.generator.core.generator.GeneratorUtil;
import org.eclipse.gymnast.generator.core.generator.GrammarInfo;
import org.eclipse.gymnast.generator.core.generator.RuleRefCollector;

public class JavaCCGrammarWriter extends GymnastASTNodeVisitor {

	private final GeneratorContext _context;
	private final GeneratorUtil _util;
	private final GrammarInfo _grammarInfo;
	
	private StringBuffer _buffer;
	
	public JavaCCGrammarWriter(GeneratorContext context) {
		_context = context;
		_util = context.getUtil();
		_grammarInfo = context.getGrammarInfo();
	}
	
	public StringBuffer writeGrammar() {
		_buffer = new StringBuffer();
		this.visit(_context.getCompUnit());
		return _buffer;
	}
	
	public boolean beginVisit(AltRule rule) {
		String ruleName = ASTUtil.getName(rule);
		String typeName = _util.toUppercaseName(ruleName);
		String returnParamName = getReturnParamName(rule);
		
		writeDecl(rule, ruleName, typeName);
		writeJavaDecls(rule, typeName, returnParamName, false);
		writeln("{");

		return true;
	}
	public boolean beginVisit(Alts alts) {
		AltRule rule = (AltRule)alts.getParent().getParent();
		String returnParamName = getReturnParamName(rule);
		
		if (rule.getBody().getPreSeq() != null) writeln();
		
		write("  ( ");
		for (int i = 0; i < alts.getChildCount(); i+=2) {
			SimpleExpr se = (SimpleExpr)alts.getChild(i);
			if (i > 0) write("  | ");
			write(returnParamName);
			write("=");
			String refRuleName = se.getValue().getText();
			write(refRuleName);

			RuleRefCollector refs = _grammarInfo.getRuleRefCollector(rule);
			
			Rule altRule = _grammarInfo.getRule(refRuleName);
			RuleRefCollector altRefs = _grammarInfo.getRuleRefCollector(altRule);
			write("(");
			if (altRefs.getInheritedLabelCount() > 0) {
				String[] labels = altRefs.getLabels();
				for (int j = 0; j < altRefs.getInheritedLabelCount(); j++) {
					String label = labels[j];
					if (j > 0) write(", ");
					if (refs.hasLabel(label)) write(label);
					else write("null");
				}
			}
			write(")");
			write(" { return " + returnParamName + "; }");
			
			writeln();
		}
		writeln("  )");
		writeln("}");
		return false;
	}
	public void endVisit(AltRule rule) {
		writeln();
	}
	
	public boolean beginVisit(ListRule rule) {
		String ruleName = ASTUtil.getName(rule);
		String typeName = _util.toUppercaseName(ruleName);
		String returnParamName = getReturnParamName(rule);
		
		writeDecl(rule, ruleName, typeName);
		writeJavaDecls(rule, typeName, returnParamName, true);
		
		SimpleExpr listExpr = rule.getBody().getListExpr();
		
		if (rule.getBody().getLparen() == null) {
			write("{ ( ");
			write(listExpr);
			write(" { ");
			write(returnParamName + ".addChild(" + getAddChildExpression(listExpr) + ");");
			write(" } ");
			write(")");
			write(rule.getBody().getListMark().getText());
			
			write(" { return " + returnParamName  + "; }");
			
			if (ASTUtil.hasAttr(rule, "entry")) {
				write(" <EOF>");
			}
			writeln(" }");
		}
		else {
			write("{ ( ");
			write(listExpr);
			write(" { ");
			write(returnParamName + ".addChild(" + getAddChildExpression(listExpr) + ");");
			write(" } ");
			write(")");
			writeln();
			
			SimpleExpr separator = rule.getBody().getSeparator();
			SimpleExpr listExpr2 = rule.getBody().getListExpr2();
			write("  ( ");
			write(separator);
			write(" ");
			write(listExpr2);
			write(" { ");
			write(returnParamName + ".addChild(" + getAddChildExpression(separator) + "); ");
			write(returnParamName + ".addChild(" + getAddChildExpression(listExpr2) + ");");
			write(" } ");
			write(")");
			write(rule.getBody().getListMark().getText());
			
			write(" { return " + returnParamName  + "; }");
			
			if (ASTUtil.hasAttr(rule, "entry")) {
				write(" <EOF>");
			}
			writeln(" }");
		}
		
		writeln();
		return false;
	}
	private String getAddChildExpression(SimpleExpr listExpr) {
		String listExprRule = listExpr.getValue().getText();
		if (_util.isTokenReference(listExprRule)) {
			return "createTokenInfo(" + _util.getLabel(listExpr) + ")";
		}
		else {
			return _util.getLabel(listExpr);
		}
	}
	
	public boolean beginVisit(SeqRule rule) {
		String ruleName = ASTUtil.getName(rule);
		String typeName = _util.toUppercaseName(ruleName);
		String returnParamName = getReturnParamName(rule);
		
		writeDecl(rule, ruleName, typeName);
		writeJavaDecls(rule, typeName, returnParamName, false);
		writeln("{");
		return true;
	}
	public void endVisit(SeqRule rule) {
		String ruleName = ASTUtil.getName(rule);
		String typeName = _util.toUppercaseName(ruleName);
		String returnParamName = getReturnParamName(rule);
		
		if (ASTUtil.hasAttr(rule, "entry")) {
			write("<EOF>");
		}
		
		writeln();
		write("  { " + returnParamName + " = new " + typeName + "(");
		RuleRefCollector refs = _grammarInfo.getRuleRefCollector(rule);
		String[] labels = refs.getLabels();
		for (int i = 0; i < labels.length; i++) {
			String label = labels[i];
			String type = refs.getType(label);
			
			String ref;
			if (_util.isTokenReference(type)) {
				ref = "createTokenInfo(" + label + ")";
			}
			else {
				ref = label;
			}
			
			if (i == 0) write(ref);
			else write(", " + ref);
		}
		writeln(");");
		writeln("    return " + returnParamName + "; }");
		writeln("}");
		writeln();
	}
	
	public boolean beginVisit(TokenRule tokenRule) {

		String ruleName = ASTUtil.getName(tokenRule);
		String typeName = _util.toUppercaseName(ruleName);
		
		writeDecl(tokenRule, ruleName, typeName);
		
		writeln("{ Token tok; }");
		write  ("{ ");
		
		Alts alts = tokenRule.getBody();
		
		for (int i = 0; i < alts.getChildCount(); i+=2) {
		    SimpleExpr se = (SimpleExpr)alts.getChild(i);
		    String text = se.getValue().getText();
		    if (i > 0) {
		    	write("| ");
		    }
		    write("tok=");
		    if (text.charAt(0) == '"') {
		    	write(text);
		    }
		    else {
		    	write("<" + text + ">");
		    }
		    write(" { return new " + typeName + " (createTokenInfo(tok)); }");
		    writeln();
		}
		
		writeln("}");
		writeln();
		return false;
	}
	
	public boolean beginVisit(OptSubSeq optSubSeq) {
		write("( ");
		return true;
	}
	public void endVisit(OptSubSeq optSubSeq) {
		write(")? ");
	}
	public boolean beginVisit(SimpleExpr simpleExpr) {
		write(simpleExpr);
		write(" ");
		return false;
	}
	
	private void writeDecl(Rule rule, String ruleName, String typeName) {
		if (ASTUtil.hasAttr(rule, "entry")) {
			write("public ");
		}
		
		write(typeName);
		write(" ");
		write(ruleName);
		write("(");
		
		RuleRefCollector refs = _grammarInfo.getRuleRefCollector(rule);
		if (refs.getInheritedLabelCount() > 0) {
			String[] labels = refs.getLabels();
			for (int i = 0; i < refs.getInheritedLabelCount(); i++) {
				String label = labels[i];
				String type = _util.toUppercaseName(refs.getType(label));
				if (_util.isTokenReference(type)) {
					type = "TokenInfo";
				}
				
				if (i > 0) write(", ");
				write(type + " " + label);
			}
		}
		writeln(") :");
	}
	
	private void writeJavaDecls(Rule rule, String typeName, String returnParamName, boolean callCtor) {
		write("{ ");
		
		String ctor = null;
		if (callCtor) ctor = "new " + typeName + "()";
		else ctor = "null";
		write(typeName + " " + returnParamName + " = " + ctor + ";");
		
		RuleRefCollector refs = _grammarInfo.getRuleRefCollector(rule);
		String[] labels = refs.getLabels();
		for (int i = 0; i < labels.length; i++) {
			String label = labels[i];
			String type = _util.toUppercaseName(refs.getType(label));
			
			if (_util.isTokenReference(type)) {
				writeln();
				write("  Token " + label + " = null;");
			}
			else if (!refs.isInherited(label))
			{
				writeln();
				write("  " + type + " " + label + " = null;");
			}
		}
		
		writeln(" }");
	}
	
	private void write(SimpleExpr simpleExpr) {
		String rule = simpleExpr.getValue().getText();
		String label = _util.getLabel(simpleExpr);
		
		if (_util.isTokenReference(rule)) {
			if(rule.charAt(0) == '"') {
				write(label + "=" + rule + "");
			}
			else {
				write(label + "=<" + rule + ">");
			}
		}
		else {
			write(label + "=" + rule + "()");
		}
	}
	
	private String getReturnParamName(Rule rule) {
		return "retVal";
	}
	
	//
	//
	//
	
	private void write(String s) {
		_buffer.append(s);
	}
	private void writeln() {
		write("\n");
	}
	private void writeln(String s) {
		write(s);
		writeln();
	}
	
}
