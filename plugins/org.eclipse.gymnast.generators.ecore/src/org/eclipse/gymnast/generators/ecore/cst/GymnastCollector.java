package org.eclipse.gymnast.generators.ecore.cst;

import java.util.List;
import java.util.Map;

import org.eclipse.gymnast.generator.core.ast.AltRule;
import org.eclipse.gymnast.generator.core.ast.CompUnit;
import org.eclipse.gymnast.generator.core.ast.HeaderSection;
import org.eclipse.gymnast.generator.core.ast.ListRule;
import org.eclipse.gymnast.generator.core.ast.Seq;
import org.eclipse.gymnast.generator.core.ast.SeqRule;
import org.eclipse.gymnast.generator.core.ast.SimpleExpr;
import org.eclipse.gymnast.generator.core.ast.TokenRule;
import org.eclipse.gymnast.generators.ecore.walker.GymnastSwitch;

/**
 * 
 * If this visitor is invoked with a <code>GymnastWalker</code> on the
 * <code>compUnit</code> ("root") production, only reachable rules will be
 * visited.
 * 
 * 
 * @author Miguel Garcia, http://www.sts.tu-harburg.de/~mi.garcia/
 * 
 */
public class GymnastCollector extends GymnastSwitch<Object> {

	public final RootCS c;

	public GymnastCollector(RootCS c) {
		this.c = c;
	}

	public Object handleAltRule(AltRule ar, String name, List<String> alts, List<String> attrs,
			List<Object> resPreSeqs, List<Object> resPostSeqs) {
		AltRuleCS arc = new AltRuleCS(name, alts, attrs, c, ar);
		c.altRules.add(arc);
		for (Object object : resPreSeqs) {
			SeqExprCS sec = (SeqExprCS) object;
			arc.preSeq.add(sec);
		}
		for (Object object : resPostSeqs) {
			SeqExprCS sec = (SeqExprCS) object;
			arc.postSeq.add(sec);
		}
		return null;
	}

	public Object handleHeader(HeaderSection headerSection, String languageName, Map<String, String> resOptions) {
		c.languageName = languageName;
		c.options = resOptions;
		return null;
	}

	public Object handleListRule(ListRule lr, List<String> attrs, String name, String e1, String separator,
			String opt_e2, int lowerBound, String opt_FieldName1, String opt_FieldName2) {
		ListRuleCS lrc = new ListRuleCS(name, e1, separator, opt_e2, lowerBound, opt_FieldName1, opt_FieldName2, c, lr);
		c.listRules.add(lrc);
		return null;
	}

	public Object handleSeqRule(SeqRule sr, String name, List<String> attrs, List<Object> resSeqExprs) {
		SeqRuleCS src = new SeqRuleCS(name, attrs, c, sr);
		int position = 0; 
		for (Object object : resSeqExprs) {
			SeqExprCS sec = (SeqExprCS) object;
			src.seqexprs.add(sec);
			sec.srCS = src;
			sec.position = position; 
			position++;
		}
		c.seqRules.add(src);
		return null;
	}

	public Object handleSimpleExprInRule(SeqRule sr, SimpleExpr se, boolean isOptional, String optFieldName,
			String value, List<String> attrs, int position) {
		SeqExprCS sec = new SeqExprCS(isOptional, optFieldName, value, position, c);
		return sec;
	}

	public Object handleTokenRule(TokenRule tr, String name, List<String> attrs, List<String> alts) {
		TokenRuleCS trc = new TokenRuleCS(name, alts, attrs, c, tr);
		c.tokenRules.add(trc);
		return null;
	}

	@Override
	public Object handleSimpleExprInPreOrPostInAltRule(AltRule ar, SimpleExpr se, boolean isOptional,
			String optFieldName, String value, List<String> attrs, int size) {
		SeqExprCS sec = new SeqExprCS(isOptional, optFieldName, value, -1, c);
		return sec;
	}

	private void distributePreAndPostSeqInAltRulesRules() {
		if (!c.isWellFormed()) {
			return; 
		}
		for (AltRuleCS arCS : c.altRules) {
			if (!arCS.preSeq.isEmpty() || !arCS.preSeq.isEmpty()) {
				arCS.distributePreAndPostSeqToConstituentSeqRules();
			}
		}
	}

	@Override
	public Object handleRoot(CompUnit root, Object resHeader, Object resGrammar) {
		distributePreAndPostSeqInAltRulesRules();
		return null;
	}

}
