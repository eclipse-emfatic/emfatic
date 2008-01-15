package org.eclipse.gymnast.generator.core.generator;

import org.eclipse.gymnast.generator.core.ast.Rule;

public interface IGrammarInfo {
	public String getLanguageName(); 
	
	public String getEntryRuleName();
	
	public String getEntryRuleClassName();
	
	public Rule[] getRules();
	
	public Rule getRule(String name);
	
	public String getRuleName(Rule rule) ;
	
	public String getTypeName(Rule rule) ;
	
	public String getOptionValue(String name);

	public RuleRefCollector getRuleRefCollector(Rule rule) ;
	
	public String[] getAltReferencers(String ruleName) ;
	
	public String[] getLiterals() ;
	
}
