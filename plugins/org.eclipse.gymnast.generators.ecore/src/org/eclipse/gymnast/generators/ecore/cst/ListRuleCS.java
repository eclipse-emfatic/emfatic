package org.eclipse.gymnast.generators.ecore.cst;

import java.util.List;

import org.eclipse.gymnast.generator.core.ast.ListRule;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput.EmptyTokenRule.MalformedListRule;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;

/**
 * @author Miguel Garcia, http://www.sts.tu-harburg.de/~mi.garcia/
 * 
 */
public class ListRuleCS extends RuleCS {

	public String e1;
	public String separator;
	public String opt_e2;
	public int lowerBound;
	public String opt_FieldName1;
	public String opt_FieldName2;

	public ListRule lr;

	public ListRuleCS(String name, String e1, String separator, String opt_e2,
			int lowerBound, String opt_FieldName1, String opt_FieldName2,
			RootCS c, ListRule lr) {
		super(c);
		this.name = name;
		this.e1 = e1;
		this.separator = separator;
		this.opt_e2 = opt_e2;
		this.lowerBound = lowerBound;
		this.opt_FieldName1 = opt_FieldName1;
		this.opt_FieldName2 = opt_FieldName2;
		this.lr = lr;
	}

	/**
	 * Reject list rules: <br>
	 * (a) with a non-well-formed repeating item, <br>
	 * (b) with a 2nd repeating item (if specified) different from the first
	 * (thus, we assume repetition consists of only one kind of repeating item)
	 * <br>
	 * (c) containing a list as repeating item <br>
	 * (d) whose name shadows that of a built-in token <br>
	 * 
	 * A list containing a non-constant separator is not rejected (e.g.,
	 * Emfatic.ast contains the token token qidSeparator : DOT | DOLLAR which is
	 * used as separator). Separators are stored by GenUnparseInhaleAndMM in
	 * their own list.
	 * 
	 * A separator may be any kind of token or rule (except list) which is made
	 * up in its textual representation of a single uninterrupted sequence of
	 * printable characters.
	 * 
	 */
	public boolean isMalformed(RootCS c) {
		if (!opt_e2.equals("") && !c.namesMatch(e1, opt_e2)) {
			return true;
		}
		if (c.findListRuleCSByName(e1) != null) {
			return true;
		}
		if (isMalformedSeparator(separator)) {
			return true;
		}
		if (c.isBuiltInToken(name)) {
			return true;
		}
		return false;
	}

	private boolean isMalformedSeparator(String sepName) {
		if (sepName.equals("")) {
			return false;
		}
		if (c.canBeConsideredToken(sepName) || c.isSurroundedByQuotes(sepName)) {
			return false;
		}
		RuleCS refedRuleIfAny = c.getRuleForNameIfAny(e1);
		if (refedRuleIfAny instanceof TokenRuleCS) {
			return false;
		}
		// ListRuleCS is ruled out
		if (refedRuleIfAny instanceof ListRuleCS) {
			return true;
		}
		if (refedRuleIfAny != null) {
			if (refedRuleIfAny instanceof AltRuleCS) {
				return isMalformedSeparator((AltRuleCS) refedRuleIfAny);
			}
			if (refedRuleIfAny instanceof SeqRuleCS) {
				return isMalformedSeparator((SeqRuleCS) refedRuleIfAny);
			}
		}
		return false;
	}

	private boolean isMalformedSeparator(SeqRuleCS srCS) {
		if (srCS.seqexprs.size() > 1) {
			return true;
		}
		if (isMalformedSeparator(srCS.seqexprs.get(0).value)) {
			return true;
		}
		return false;
	}

	private boolean isMalformedSeparator(AltRuleCS refedRule) {
		List<String> terminals = refedRule.explodeTerminalAlternatives();
		boolean res = containsCompositeSeqRule(terminals);
		return res;
	}

	/**
	 * A separator may be any kind of token or rule (except list) which is made
	 * up in its textual representation of a single uninterrupted sequence of
	 * printable characters.
	 * 
	 * 
	 */
	private boolean containsCompositeSeqRule(List<String> terminals) {
		for (String t : terminals) {
			if (!c.canBeConsideredToken(t)) {
				SeqRuleCS refedSeqRule = c.findSeqRuleCSByName(t);
				if (refedSeqRule != null) {
					/*
					 * check if it consist of primitive constituents only, i.e.
					 * its textual representation is a single word.
					 */
					if (refedSeqRule.seqexprs.size() > 1) {
						return true;
					}
					if (isMalformedSeparator(refedSeqRule.seqexprs.get(0).value)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	public String unparseConstantSeparator() {
		assert !isMalformed(c);
		if (c.isSurroundedByQuotes(separator)) {
			return c.unquote(separator);
		}
		if (c.isBuiltInToken(separator)) {
			return c.getValueOfBuiltInToken(separator);
		}
		if (separator != null && separator.equals("")) {
			return " ";
		}
		/*
		 * e.g. qidSeparator with alternativas DOT and STAR. See comment for
		 * isMalformed() in ListRuleCS
		 */
		return null;
	}

	public String getPreferredItemName() {
		String res = opt_FieldName1.equals("") ? e1 : opt_FieldName1;
		return res;
	}

	@Override
	public String toString() {
		String out_e1 = opt_FieldName1.equals("") ? e1 : opt_FieldName1 + "="
				+ e1;
		String out_e2 = opt_FieldName2.equals("") ? opt_e2 : opt_FieldName2
				+ "=" + opt_e2;
		System.out.println("list " + name + " : " + out_e1 + " " + separator
				+ " " + out_e2 + " " + lowerBound);
		return name;
	}

	public boolean hasConstantSeparator() {
		if (separator.equals("") || c.isBuiltInToken(separator)
				|| c.isSurroundedByQuotes(separator)) {
			return true;
		}
		return false;
	}

	public SeqExprKind getKindOfItem() {
		SeqExprCS temp = new SeqExprCS(false, e1, e1, lowerBound, c);
		temp.value = e1;
		SeqExprKind res = temp.getKind();
		return res;
	}

	@Override
	public Rule getRule() {
		return lr;
	}

	public void addParseMessages(ParseContext parseContext) {
		// TODO make finer distionction reflecting those in isMalformed()
		MalformedListRule parseMessage = new MalformedListRule(this);
		parseContext.addParseMessage(parseMessage);
	}

	@Override
	public final boolean canBeRegardedAsBoolean() {
		return false;
	}

}
