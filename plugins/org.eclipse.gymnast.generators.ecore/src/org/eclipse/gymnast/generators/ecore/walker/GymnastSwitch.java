package org.eclipse.gymnast.generators.ecore.walker;

import java.util.List;
import java.util.Map;

import org.eclipse.gymnast.generator.core.ast.AltRule;
import org.eclipse.gymnast.generator.core.ast.CompUnit;
import org.eclipse.gymnast.generator.core.ast.Grammar;
import org.eclipse.gymnast.generator.core.ast.HeaderSection;
import org.eclipse.gymnast.generator.core.ast.ListRule;
import org.eclipse.gymnast.generator.core.ast.SeqRule;
import org.eclipse.gymnast.generator.core.ast.SimpleExpr;
import org.eclipse.gymnast.generator.core.ast.TokenRule;

/**
 * An <code>abstract class</code> instead of an <code>interface</code> so
 * that those methods not necessary to override can be inherited.
 * 
 * @author Miguel Garcia, http://www.sts.tu-harburg.de/~mi.garcia/
 * 
 * @param <T>
 */
public abstract class GymnastSwitch<T> {

	public T handleRoot(CompUnit root, T resHeader, T resGrammar) {
		return null;
	}

	public T handleHeader(HeaderSection headerSection, String languageName,
			Map<String, String> resOptions) {
		return null;
	}

	public T handleGrammar(Grammar grammar, List<T> resTokenRules,
			List<T> resSeqRules, List<T> resAltRules, List<T> resListRules) {
		return null;
	}

	public T handleTokenRule(TokenRule tr, String name, List<String> attrs,
			List<String> alts) {
		return null;
	}

	public T handleSeqRule(SeqRule sr, String name, List<String> attrs,
			List<T> resSeqExprs) {
		return null;
	}

	public T handleSimpleExprInRule(SeqRule sr, SimpleExpr se,
			boolean isOptional, String optFieldName, String value,
			List<String> attrs, int position) {
		return null;
	}

	public T handleAltRule(AltRule ar, String name, List<String> alts,
			List<String> attrs, List<T> resPreSeqs, List<T> resPostSeqs) {
		return null;
	}

	public T handleListRule(ListRule lr, List<String> attrs, String name,
			String e1, String separator, String opt_e2, int lowerBound,
			String opt_FieldName1, String opt_FieldName2) {
		return null;
	}

	public T handleSimpleExprInPreOrPostInAltRule(AltRule ar, SimpleExpr se, boolean isOptional, String optFieldName,
			String value, List<String> attrs, int size) {
		return null;
	}

}
