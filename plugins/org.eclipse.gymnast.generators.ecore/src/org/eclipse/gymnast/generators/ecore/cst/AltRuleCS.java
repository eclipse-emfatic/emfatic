package org.eclipse.gymnast.generators.ecore.cst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.gymnast.generator.core.ast.AltRule;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput.EmptyTokenRule.MalformedAltRule;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;

public class AltRuleCS extends RuleCS {

	public List<String> alts;

	public List<SeqExprCS> preSeq = new ArrayList<SeqExprCS>();
	public List<SeqExprCS> postSeq = new ArrayList<SeqExprCS>();
	
	public AltRule ar;
	

	/**
	 * Invariant:
	 * 
	 * (this.enu != null) iff (Grammar2Ecore.convert() has initialized it and
	 * this.getKindOfAlts() == FIXED_KEYWORDS)
	 * 
	 * @see TokenRuleCS.enu
	 * 
	 */
	public EEnum enu = null;

	/**
	 * Invariant:
	 * 
	 * (this.eClass != null) iff (Grammar2Ecore.convert() has initialized it and
	 * this.getKindOfAlts() == CONTAINSSEQ)
	 * 
	 * @see TokenRuleCS.enu
	 * 
	 */
	public EClass eClass = null;

	public EOperation ecorizeEOp = null;

	public AltRuleCS(String name, List<String> alts, List<String> attrs, RootCS c, AltRule ar) {
		super(c);
		this.name = name;
		this.alts = alts;
		this.attrs = attrs;
		this.ar = ar;
	}

	/**
	 * Reject alt rules having: <br>
	 * (a) an alternative whose name can't be resolved (if resolved, its
	 * well-formedness is checked together with all rules of its kind, so that's
	 * not checked again in this method), <br>
	 * (b) one or more constituent alternative of list kind <br>
	 * (c) an alt rule, when its constituents are recursively exploded, should
	 * contain two or more alternatives.<br>
	 * (d) mixed tokens and rules (seq, alt) among the alternatives.<br>
	 * (e) whose name shadows that of a built-in token <br>
	 * 
	 */
	public boolean isMalformed() {

		if (c.isBuiltInToken(name)) {
			return true;
		}
		
		if (getKindOfAlts() == AltRuleAltsKind.MALFORMED) {
			return true;
		}

		/*
		 * in case preSeq and postSeq have not yet been distributed to the
		 * constituent SeqRule's, check that they can be distributed
		 */
		Set<SeqRuleCS> seqRulesOnWhichAnAltRuleDistributesPreOrPostSeq = new HashSet<SeqRuleCS>();
		for (AltRuleCS arCS : c.altRules) {
			if (!arCS.preSeq.isEmpty() || !arCS.preSeq.isEmpty()) {
				if (!arCS.preAndPostSeqInAltRulesCanBeDistributed()) {
					return true;
				}
				for (String alt : arCS.alts) {
					SeqRuleCS srCS = c.findSeqRuleCSByName(alt);
					if (srCS == null || seqRulesOnWhichAnAltRuleDistributesPreOrPostSeq.contains(srCS)) {
						return true;
					}
					seqRulesOnWhichAnAltRuleDistributesPreOrPostSeq.add(srCS);
				}
			}
		}

		return false;
	}

	public List<String> explodeTerminalAlternatives() {
		Set<String> alreadyExploded = new HashSet<String>();
		return explTermAlts(this, alreadyExploded);
	}

	/**
	 * Explodes a (well-formed) AltRule into its leaves: seq rules,
	 * "canBeConsideredToken".
	 * 
	 * @param innerAltCS
	 * @return
	 */
	private List<String> explTermAlts(AltRuleCS aCS, Set<String> alreadyExploded) {
		List<String> res = new ArrayList<String>();
		for (String name : aCS.alts) {
			if (c.canBeConsideredToken(name)) {
				if (!res.contains(name)) {
					res.add(name);
				}
			} else if (!alreadyExploded.contains(name)) {
				alreadyExploded.add(name);
				AltRuleCS innerAltRuleCS = c.findAltRuleCSByName(name);
				if (innerAltRuleCS != null) {
					List<String> toAdd = explTermAlts(innerAltRuleCS, alreadyExploded);
					for (String s : toAdd) {
						if (!res.contains(s)) {
							res.add(s);
						}
					}
				} else if (c.findSeqRuleCSByName(name) != null) {
					if (!res.contains(name)) {
						res.add(name);
					}
				}
				TokenRuleCS innerTokenRuleCS = c.findTokenRuleCSByName(name);
				if (innerTokenRuleCS != null) {
					List<String> toAdd = innerTokenRuleCS.explodeTerminalAlternatives();
					for (String s : toAdd) {
						if (!res.contains(s)) {
							res.add(s);
						}
					}
				}
			}
		}
		return res;
	}

	public AltRuleAltsKind getKindOfAlts() {
		List<String> recursiveAlts = explodeTerminalAlternatives();
		if (recursiveAlts.size() < 2) {
			return AltRuleAltsKind.MALFORMED;
		}
		boolean areThereInts = false;
		boolean areThereArbitraryIDs = false;
		boolean areThereFixedKeywords = false;
		boolean areThereSeqRules = false;
		for (String alt : recursiveAlts) {

			if (alt.equals("ID") || alt.equals("CHAR_LITERAL") || alt.equals("STRING_LITERAL")) {
				areThereArbitraryIDs = true;
			} else if (c.isIntegerLiteral(alt) || alt.equals("INT_LITERAL")) {
				areThereInts = true;
			} else if ((c.isSurroundedByQuotes(alt) && !c.isIntegerLiteral(alt)) || c.isBuiltInToken(alt)) {
				areThereFixedKeywords = true;
			} else {
				TokenRuleCS refedTR = c.findTokenRuleCSByName(alt);
				if (refedTR != null) {
					switch (refedTR.getKindOfAlts()) {
					case MALFORMED:
						return AltRuleAltsKind.MALFORMED;
					case FIXED_KEYWORDS:
						areThereFixedKeywords = true;
						break;
					case INTS:
						areThereInts = true;
						break;
					case ID_OR_MIXEDSTRINT:
						areThereArbitraryIDs = true;
						break;
					default:
						assert false;
						break;
					}
				} else {
					SeqRuleCS srCS = c.findSeqRuleCSByName(alt);
					if (srCS == null) {
						return AltRuleAltsKind.MALFORMED;
					}
					areThereSeqRules = true;
				}
			}

		}
		boolean someNonSeqRule = areThereArbitraryIDs || areThereFixedKeywords || areThereInts;
		if (someNonSeqRule && areThereSeqRules) {
			return AltRuleAltsKind.MALFORMED;
		}
		if (areThereSeqRules) {
			return AltRuleAltsKind.CONTAINSSEQ;
		}
		// the remaining cases are the same as for TokenRuleAltsKind
		if (areThereArbitraryIDs || (areThereFixedKeywords && areThereInts)) {
			return AltRuleAltsKind.ID_OR_MIXEDSTRINT;
		} else {
			if (!areThereFixedKeywords && areThereInts) {
				return AltRuleAltsKind.INTS;
			}
			return AltRuleAltsKind.FIXED_KEYWORDS;
		}
	}

	@Override
	public String toString() {
		String res = "abstract " + name + " " + attrsToString() + " : ";
		res += "(";
		for (SeqExprCS ps : preSeq) {
			res += ps.toString() + " ";
		}
		res += ")";
		for (Iterator<String> ai = alts.iterator(); ai.hasNext();) {
			res += ai.next();
			res += ai.hasNext() ? "| " : "";
		}
		res += "(";
		for (SeqExprCS ps : postSeq) {
			res += ps.toString() + " ";
		}
		res += ")";
		return res;
	}

	/**
	 * Emfatic.ast contains an example occurrence of an optional leading (or
	 * traling) list of SeqExpr in an AltRule:
	 * 
	 * abstract topLevelDecl : (annotations) subPackageDecl | classDecl |
	 * dataTypeDecl | enumDecl | mapEntryDecl ;
	 * 
	 * In this case, only one SeqExpr appears (annotations) but in general can
	 * be one or more.
	 * 
	 * In terms of the generated AST classes, such SeqExpr's are added to each
	 * alternative SeqRule (thus this well-formedness check, if this AltRule
	 * contains a preSeq or a postSeq, then all alternatives must be SeqRules
	 * (and not AltRule).
	 * 
	 * The same SeqRule may not appear in different AltRule's being given in
	 * each different preSeq and postSeq (because that could lead to confusion
	 * if no unique names but just values are used).
	 * 
	 * @return
	 */
	public boolean preAndPostSeqInAltRulesCanBeDistributed() {
		if (!preSeq.isEmpty() || !postSeq.isEmpty()) {
			/* do not iterate over explodeTerminalAlternatives() */
			for (String alt : alts) {
				SeqRuleCS srCS = c.findSeqRuleCSByName(alt);
				if (srCS == null) {
					return false;
				}
			}
		}
		return true;
	}

	public void distributePreAndPostSeqToConstituentSeqRules() {
		assert preAndPostSeqInAltRulesCanBeDistributed();
		if (preSeq.isEmpty() && postSeq.isEmpty()) {
			return;
		}
		for (String alt : alts) {
			SeqRuleCS srCS = c.findSeqRuleCSByName(alt);
			int position = 0;
			List<SeqExprCS> newseqexprs = new ArrayList<SeqExprCS>();
			for (SeqExprCS ps : preSeq) {
				SeqExprCS clone = new SeqExprCS(ps.isOptional, ps.optFieldName, ps.value, position, c);
				clone.srCS = srCS;
				newseqexprs.add(clone);
				position++;
			}
			for (SeqExprCS existing : srCS.seqexprs) {
				existing.position += preSeq.size();
				newseqexprs.add(existing);
			}
			position = srCS.seqexprs.size();
			for (SeqExprCS ps : postSeq) {
				SeqExprCS clone = new SeqExprCS(ps.isOptional, ps.optFieldName, ps.value, position, c);
				clone.srCS = srCS;
				newseqexprs.add(clone);
				position++;
			}
			srCS.seqexprs = newseqexprs;
		}
		preSeq = new ArrayList<SeqExprCS>();
		postSeq = new ArrayList<SeqExprCS>();
	}

	@Override
	public Rule getRule() {
		return ar;
	}

	public void addParseMessages(ParseContext parseContext) {
		// TODO make finer distionction reflecting those in isMalformed()
		MalformedAltRule parseMessage = new MalformedAltRule(this);
		parseContext.addParseMessage(parseMessage);
	}
}
