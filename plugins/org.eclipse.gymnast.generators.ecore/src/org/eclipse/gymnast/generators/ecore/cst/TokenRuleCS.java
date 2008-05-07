package org.eclipse.gymnast.generators.ecore.cst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EEnum;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.ast.TokenRule;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput.EmptyTokenRule;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput.EmptyTokenRule.MalformedTokenRule;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput.EmptyTokenRule.RuleNameShadowsBuiltInToken;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;

public class TokenRuleCS extends RuleCS {

	public TokenRule tr;

	public TokenRuleCS(String name, List<String> alts2, List<String> attrs2, RootCS c, TokenRule tr) {
		super(c);
		this.name = name;
		this.alts = alts2;
		this.attrs = attrs2;
		this.tr = tr;
	}

	public List<String> alts;

	/**
	 * Invariant:
	 * 
	 * (this.enu != null) iff (Grammar2Ecore.convert() has initialized it and
	 * this.getKindOfAlts() == FIXED_KEYWORDS)
	 * 
	 * @see AltRuleCS.enu
	 * 
	 */
	public EEnum enu = null;

	public List<String> explodeTerminalAlternatives() {
		Set<String> alreadyExploded = new HashSet<String>();
		return explTermAlts(this, alreadyExploded);
	}

	/**
	 * This method assumes the argument is a FIXED_KEYWORDS token rule.
	 * Surrounding quotes are removed for the returned items.
	 * 
	 * @param trCS
	 * @param alreadyExploded
	 * @return
	 */
	private List<String> explTermAlts(TokenRuleCS trCS, Set<String> alreadyExploded) {
		assert innerGetKindOfAlts(trCS) == TokenRuleAltsKind.FIXED_KEYWORDS;
		List<String> res = new ArrayList<String>();
		for (String alt : trCS.alts) {
			if (c.isSurroundedByQuotes(alt)) {
				res.add(alt);
			} else if (c.isBuiltInToken(alt)) {
				res.add(alt);
			} else {
				TokenRuleCS innerTR = c.findTokenRuleCSByName(alt);
				assert innerTR != null;
				if (innerTR != null && !alreadyExploded.contains(alt)) {
					alreadyExploded.add(alt);
					res.addAll(explTermAlts(innerTR, alreadyExploded));
				}
			}
		}
		return res;
	}

	public TokenRuleAltsKind getKindOfAlts() {
		return innerGetKindOfAlts(this);
	}

	private TokenRuleAltsKind innerGetKindOfAlts(TokenRuleCS trCS) {
		if (trCS.alts.isEmpty()) {
			/*
			 * calling isEmpty() above on the alts collection instead of
			 * isEmpty(TokenRuleCS) is intentional, as (1) the checks below will
			 * catch malformedness due to empty constituents and (2) to allow
			 * invoking this method from isEmpty(TokenRuleCS) or its invokers.
			 */
			return TokenRuleAltsKind.MALFORMED;
		}
		boolean areThereInts = false;
		boolean areThereArbitraryIDs = false;
		boolean areThereFixedKeywords = false;
		for (String alt : trCS.alts) {
			if (alt.equals("ID") || alt.equals("CHAR_LITERAL") || alt.equals("STRING_LITERAL")) {
				areThereArbitraryIDs = true;
			} else if (c.isIntegerLiteral(alt) || alt.equals("INT_LITERAL")) {
				areThereInts = true;
			} else if ((c.isSurroundedByQuotes(alt) && !c.isIntegerLiteral(alt)) || c.isBuiltInToken(alt)) {
				areThereFixedKeywords = true;
				/*
				 * the default case, if all alts are like this will lead to
				 * TokenRuleAltsKind.FIXED_KEYWORDS and thus
				 * canBeConvertedToEnum()
				 */
			} else {
				TokenRuleCS refedTR = c.findTokenRuleCSByName(alt);
				if (refedTR == null) {
					return TokenRuleAltsKind.MALFORMED;
				}
				TokenRuleAltsKind refedKind = innerGetKindOfAlts(refedTR);
				switch (refedKind) {
				case MALFORMED:
					return TokenRuleAltsKind.MALFORMED;

				case ID_OR_MIXEDSTRINT:
					areThereArbitraryIDs = true;
					areThereInts = true;
					// don't return just yet, let the other alts be checked for
					// MALFORMED
					break;

				case INTS:
					areThereInts = true;
					break;

				case FIXED_KEYWORDS:
					areThereFixedKeywords = true;
					break;
				}
			}
		}
		if (areThereArbitraryIDs || (areThereFixedKeywords && areThereInts)) {
			return TokenRuleAltsKind.ID_OR_MIXEDSTRINT;
		} else {
			if (!areThereFixedKeywords && areThereInts) {
				return TokenRuleAltsKind.INTS;
			}
			return TokenRuleAltsKind.FIXED_KEYWORDS;
		}

	}

	@Override
	public Rule getRule() {
		return tr;
	}

	public void addParseMessages(ParseContext parseContext) {
		if (c.isBuiltInToken(name)) {
			RuleNameShadowsBuiltInToken parseMessage = new RuleNameShadowsBuiltInToken(this);
			parseContext.addParseMessage(parseMessage);
		}
		if (getKindOfAlts() == TokenRuleAltsKind.MALFORMED) {
			MalformedTokenRule parseMessage = new MalformedTokenRule(this);
			parseContext.addParseMessage(parseMessage);
		}
		if (isEmpty()) {
			EmptyTokenRule parseMessage = new EmptyTokenRule(this);
			parseContext.addParseMessage(parseMessage);
		}
	}

	public boolean isEmpty() {
		boolean res = alts.size() == 0;
		return res;
	}

}
