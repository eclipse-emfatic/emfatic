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

package org.eclipse.gymnast.generators.parser.antlr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.gymnast.generator.core.ast.AltRule;
import org.eclipse.gymnast.generator.core.ast.Alts;
import org.eclipse.gymnast.generator.core.ast.GymnastASTNodeVisitor;
import org.eclipse.gymnast.generator.core.ast.HeaderSection;
import org.eclipse.gymnast.generator.core.ast.ListRule;
import org.eclipse.gymnast.generator.core.ast.OptSubSeq;
import org.eclipse.gymnast.generator.core.ast.Option;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.ast.SeqRule;
import org.eclipse.gymnast.generator.core.ast.SimpleExpr;
import org.eclipse.gymnast.generator.core.ast.TokenRule;
import org.eclipse.gymnast.generator.core.generator.ASTUtil;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.generator.core.generator.GeneratorUtil;
import org.eclipse.gymnast.generator.core.generator.GrammarInfo;
import org.eclipse.gymnast.generator.core.generator.RuleRefCollector;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class AntlrGrammarWriter extends GymnastASTNodeVisitor {
	
	private GeneratorContext _context;
	private GeneratorUtil _util;
	private GrammarInfo _grammarInfo;
	
	private StringBuffer _buffer;
	
	public AntlrGrammarWriter(GeneratorContext context) {
		_context = context;
		_util = context.getUtil();
		_grammarInfo = _context.getGrammarInfo();
	}
	
	public StringBuffer writeGrammar() {
		_buffer = new StringBuffer();
		this.visit(_context.getCompUnit());
		return _buffer;
	}
	
	public boolean beginVisit(HeaderSection headerSection) {
		writeln();
		writeln("header");
		writeln("{");
		writeln("package " + _context.getParserPackageName() + ";");
		writeln("import " + _context.getASTPackageName() + ".*;");
		writeln("import " + _context.getLDT_ParserPackageName() + ".*;");
		writeln("import " + _context.getLDT_ASTNodePackageName() + ".*;");
		writeln("}");
		writeln();
		writeln("class " + headerSection.getName().getText() + "Parser extends Parser;");
		writeln();
		if (headerSection.getOptionsSection() != null) {
			writeln("options {");
			new GymnastASTNodeVisitor() {
				public boolean beginVisit(Option option) {
					String optionName = option.getName().getText();
					if (isValidAntlrOption(optionName)) {
						write("  " + optionName);
						write("=" + option.getValue().getText());
						writeln(";");
						writeln();
					}
					return false;
				}
				
				private boolean isValidAntlrOption(String optionName) {
					// TODO: compile exhaustive list
					if ("k".equals(optionName)) return true;
					
					return false;
				}
			}.visit(headerSection.getOptionsSection().getOptionList());
			writeln("}");
		}
		
		
		writeln("{"); // begin inline Java
		writeln("    private ParseError createParseError(RecognitionException ex) {");
		writeln("        return " + headerSection.getName().getText() + "ParserDriver.createParseError(ex);");
		writeln("    }");
		writeln();
		
		writeln("    private TokenInfo createTokenInfo(Token tok) {");
		writeln("        if (tok == null) return null;");
		writeln("        else return new TokenInfo(tok.getText(), tok.getColumn(), tok.getType());");
		writeln("    }");
		writeln();
		
		writeErrorHandlers();
		
		writeln("}"); // end inline Java
		writeln();
		return true;
	}
	
	public boolean beginVisit(AltRule rule) {
		String ruleName = ASTUtil.getName(rule);
		String typeName = _util.toUppercaseName(ruleName);
		String returnParamName = getReturnParamName(rule);
		
		writeDecl(rule, ruleName, typeName, returnParamName, false);
		writeln(":");
		writeJavaDecls(rule);

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
			if (altRefs.getInheritedLabelCount() > 0) {
				write("[");
				String[] labels = altRefs.getLabels();
				for (int j = 0; j < altRefs.getInheritedLabelCount(); j++) {
					String label = labels[j];
					if (j > 0) write(", ");
					if (refs.hasLabel(label)) write(label);
					else write("null");
				}
				write("]");
			}
			
			writeln();
		}
		writeln("  )");
		return false;
	}
	public void endVisit(AltRule rule) {
		writeln(";");
		writeln();
	}

	public boolean beginVisit(ListRule rule) {
		String ruleName = ASTUtil.getName(rule);
		String typeName = _util.toUppercaseName(ruleName);
		String returnParamName = getReturnParamName(rule);
		
		writeDecl(rule, ruleName, typeName, returnParamName, true);
		writeln(":");
		
		writeJavaDecls(rule);
		
		SimpleExpr listExpr = rule.getBody().getListExpr();
		
		if (rule.getBody().getLparen() == null) {
			write("  ( ");
			write(listExpr);
			write(" { ");
			write(returnParamName + ".addChild(" + getAddChildExpression(listExpr) + ");");
			write(" } ");
			write(")");
			write(rule.getBody().getListMark().getText());
			
			if (ASTUtil.hasAttr(rule, "entry")) {
				write(" EOF");
			}
			writeln();
		}
		else {
			write("  ");
			write(listExpr);
			write(" { ");
			write(returnParamName + ".addChild(" + getAddChildExpression(listExpr) + ");");
			write(" } ");
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
			
			if (ASTUtil.hasAttr(rule, "entry")) {
				write(" EOF");
			}
			writeln();
		}
		
		writeln(";");
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
		
		writeDecl(rule, ruleName, typeName, returnParamName, false);
		writeln(":");
		writeJavaDecls(rule);
		write("  ");
		return true;
	}
	public void endVisit(SeqRule rule) {
		String ruleName = ASTUtil.getName(rule);
		String typeName = _util.toUppercaseName(ruleName);
		String returnParamName = getReturnParamName(rule);
		
		if (ASTUtil.hasAttr(rule, "entry")) {
			write("EOF");
		}
		
		writeln();
		write("{ " + returnParamName + " = new " + typeName + "(");
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
		writeln("); }");
		writeln(";");
		writeln();
	}
	
	public boolean beginVisit(TokenRule rule) {
		String ruleName = ASTUtil.getName(rule);
		String typeName = _util.toUppercaseName(ruleName);
		String returnParamName = getReturnParamName(rule);
		
		writeDecl(rule, ruleName, typeName, returnParamName, false);
		
		writeln("{ Token tok = LT(1); }");
		write  (": ( ");
		
		Alts alts = rule.getBody();
		
		for (int i = 0; i < alts.getChildCount(); i+=2) {
			SimpleExpr se = (SimpleExpr)alts.getChild(i);
			if (i == 0) writeln(se.getValue().getText());
			else writeln("  | " + se.getValue().getText());
		}
		
		writeln("  )");
		writeln("{ " + returnParamName + " = new " + typeName + "(createTokenInfo(tok)); }");
		writeln(";");
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
	
	private void writeErrorHandlers() {
		
		try {
			InputStream input = this.getClass().getResourceAsStream("ErrorHandlers.txt");
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));
			
			String line = reader.readLine();
			while(line != null) {
				writeln(line);
				line = reader.readLine();
			}
		}
		catch (IOException ex) {
			_util.reportError(ex);
		}
	}
	
	private void writeDecl(Rule rule, String ruleName, String typeName, String returnParamName, boolean callCtor) {
		
		write(ruleName);
		
		RuleRefCollector refs = _grammarInfo.getRuleRefCollector(rule);
		if (refs.getInheritedLabelCount() > 0) {
			write(" [ ");
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
			write(" ] ");
		}
		
		String ctor = null;
		if (callCtor) ctor = "new " + typeName + "()";
		else ctor = "null";
		
		writeln(" returns [ " + typeName + " " + returnParamName + " = " + ctor + " ]");
	}
	
	private void writeJavaDecls(Rule rule) {
		RuleRefCollector refs = _grammarInfo.getRuleRefCollector(rule);
		boolean foundDecl = false;
		String[] labels = refs.getLabels();
		for (int i = 0; i < labels.length; i++) {
			String label = labels[i];
			String type = _util.toUppercaseName(refs.getType(label));
			if (!_util.isTokenReference(type) && !refs.isInherited(label))
			{
				if (!foundDecl) {
					write("{ ");
					foundDecl = true;
				}
				write(type + " " + label + " = null; ");
			}
		}
		if (foundDecl) writeln("}");
	}
	
	private void write(SimpleExpr simpleExpr) {
		String rule = simpleExpr.getValue().getText();
		String label = _util.getLabel(simpleExpr);
		
		if (_util.isTokenReference(rule)) {
			write(label + ":" + rule);
		}
		else {
			write(label + "=" + rule);
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
