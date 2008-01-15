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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;

import org.eclipse.gymnast.generator.core.ast.AltRule;
import org.eclipse.gymnast.generator.core.ast.Alts;
import org.eclipse.gymnast.generator.core.ast.GymnastASTNodeVisitor;
import org.eclipse.gymnast.generator.core.ast.HeaderSection;
import org.eclipse.gymnast.generator.core.ast.Option;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.ast.SimpleExpr;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class GrammarInfo extends GymnastASTNodeVisitor implements IGrammarInfo {
	
	private GeneratorContext _context;
	private GeneratorUtil _util;
	
	private String _languageName;
	
	private Hashtable _options = new Hashtable();

	private ArrayList _rules = new ArrayList();
	private Hashtable _ruleNameToRule = new Hashtable();
	private Hashtable _ruleToRuleRefCollector = new Hashtable();
	
	// key is rule name
	// value is ArrayList of altRule names that reference the rule in an alt
	private Hashtable _altReverseMap = new Hashtable();
	
	private String _entryRuleName = "compUnit";
	private String _entryRuleClassName = "CompUnit";
	
	private Rule _currentRule;
	
	public GrammarInfo(GeneratorContext context) {
		_context = context;
		_util = _context.getUtil();
		
		init();
	}
	
	private void init() {
		visit(_context.getCompUnit());
		
		for (int i = 0; i < _rules.size(); i++) {
			Rule rule = (Rule)_rules.get(i);
			initRuleParams(rule);
			
			if (ASTUtil.hasAttr(rule, "entry")) {
			    _entryRuleName = getRuleName(rule);
			    _entryRuleClassName = getTypeName(rule);
			}
		}
	}
	
	private void initRuleParams(Rule rule) {
		RuleRefCollector refs = getRuleRefCollector(rule);
		String[] altReferencers = getAltReferencers(ASTUtil.getName(rule));
		
		for (int i = 0; i < altReferencers.length; i++) {
			String altReferencerName = altReferencers[i];
			Rule altRule = getRule(altReferencerName);
			RuleRefCollector altRuleRefs = getRuleRefCollector(altRule);
			String[] labels = altRuleRefs.getLabels();
			for (int j = 0; j < labels.length; j++) {
				String label = labels[j];
				if (altRuleRefs.isAltSeq(label)) {
					refs.addInherited(label, altRuleRefs.getType(label));
				}
			}
		}
	}
	
	public String getLanguageName() {
		return _languageName;
	}
	
	public String getEntryRuleName() {
		return _entryRuleName;
	}
	
	public String getEntryRuleClassName() {
		return _entryRuleClassName;
	}
	
	public Rule[] getRules() {
		return (Rule[]) _rules.toArray(new Rule[_rules.size()]);
	}
	
	public Rule getRule(String name) {
		return (Rule)_ruleNameToRule.get(name);
	}
	
	public String getRuleName(Rule rule) {
	    return ASTUtil.getName(rule);
	}
	
	public String getTypeName(Rule rule) {
	    return _util.toUppercaseName(getRuleName(rule));
	}
	
	public String getOptionValue(String name) {
		if (!_options.containsKey(name)) return null;
		
		String value = (String)_options.get(name);
		value = Util.removeSurroundingQuotes(value);
		return value;
	}

	public RuleRefCollector getRuleRefCollector(Rule rule) {
		return (RuleRefCollector)_ruleToRuleRefCollector.get(rule);
	}
	
	public String[] getAltReferencers(String ruleName) {
		if (_altReverseMap.containsKey(ruleName)) {
			ArrayList altList = (ArrayList)_altReverseMap.get(ruleName);
			return (String[]) altList.toArray(new String[altList.size()]);
		}
		return new String[]{};
	}
	
	public String[] getLiterals() {
		final HashSet literals = new HashSet();
		
		new GymnastASTNodeVisitor() {
			public boolean beginVisit(SimpleExpr simpleExpr) {
				String value = simpleExpr.getValue().getText();
				if (value.startsWith("\"")) {
					literals.add(Util.removeSurroundingQuotes(value));
				}
				return false;
			}
		}.visit(_context.getCompUnit());
		
		return (String[]) literals.toArray(new String[literals.size()]);
	}
	
	public boolean beginVisit(HeaderSection headerSection) {
		_languageName = headerSection.getName().getText();
		_util.report("language: " + getLanguageName());
		return true;
	}
	
	public boolean beginVisit(Option option) {
		String name = option.getName().getText();
		String value = option.getValue().getText();
		
		_options.put(name, value);
		
		_util.report("option " + name + " = " + value);
		return false;
	}

	public boolean beginVisit(Rule rule) {
		String name = ASTUtil.getName(rule);
		if (_ruleNameToRule.containsKey(name)) {
			throw new RuntimeException("Rule " + name + "declared twice!");
		}
		_util.report("rule: " + name);
		
		_currentRule = rule;
		_rules.add(rule);
		_ruleNameToRule.put(name, rule);
		
		RuleRefCollector ruleRefs = new RuleRefCollector(rule, _context);
		_ruleToRuleRefCollector.put(rule, ruleRefs);
		
		return true;
	}
	
	public boolean beginVisit(Alts alts) {
		if (_currentRule instanceof AltRule) {
			for (int i = 0; i < alts.getChildCount(); i++) {
				ASTNode child = alts.getChild(i);
				if (child instanceof SimpleExpr) {
					SimpleExpr se = (SimpleExpr)child;
					
					String refRuleName = se.getValue().getText();
					ArrayList altList = null;
					if (!_altReverseMap.containsKey(refRuleName)) {
						altList = new ArrayList();
						_altReverseMap.put(refRuleName, altList);
					}
					else {
						altList = (ArrayList)_altReverseMap.get(refRuleName);
					}
					altList.add(ASTUtil.getName(_currentRule));
				}
			}
		}
		return false;
	}

}
