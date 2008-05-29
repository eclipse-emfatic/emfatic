package org.eclipse.gymnast.generators.ecore.cst;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gymnast.generator.core.generator.Util;
import org.eclipse.gymnast.generators.ecore.convert.GenUnparseInhaleAndMMForSeqRules;

/**
 * @author Miguel Garcia, http://www.sts.tu-harburg.de/~mi.garcia/
 * 
 */
public class SeqExprCS {

	public int position;
	public String value;
	public String optFieldName;
	public boolean isOptional;
	public SeqRuleCS srCS;
	private RootCS c;

	/**
	 * Invariant (this.eSF != null) iff ((this.getKind() ==
	 * REFERS_TO_RULE_WITH_EENUM || this.getKind() ==
	 * REFERS_TO_RULE_WITH_ECLASS) && (@see {@link
	 * GenUnparseInhaleAndMMForSeqRules() has run}))
	 */
	public EStructuralFeature eSF = null;
	public EStructuralFeature eSFListForRefedItems;
	public EStructuralFeature eSFListForSeparators;
	
	public SeqExprCS(boolean isOptional, String optFieldName, String value,
			int position, RootCS c) {
		this.isOptional = isOptional;
		this.optFieldName = optFieldName;
		this.value = value;
		this.position = position;
		this.c = c;
	}

	@Override
	public String toString() {
		String prefix = (optFieldName.equals("")) ? "" : optFieldName + "=";
		String res;
		if (isOptional) {
			if (!prefix.equals("")) {
				res = "(" + prefix + value + ")?";
			} else {
				res = value + "?";
			}
		} else {
			res = prefix + value;
		}
		return res;
	}

	public String unparseValue(RootCS c) {
		if (c.isSurroundedByQuotes(value)) {
			return c.unquote(value);
		}
		if (c.isBuiltInToken(value)) {
			return RootCS.getValueOfBuiltInToken(value);
		}
		assert false;
		throw new RuntimeException();
	}

	/**
	 * @return a readable name for a field, e.g. instead of INT_LITERAL returns
	 * 	intLit.
	 */
	public String suggestedName() {
		String res = optFieldName.equals("") ? value : optFieldName;
		if (c.isSurroundedByQuotes(res)) {
			res = c.unquote(res);
		}
		if (res.equals("STRING_LITERAL") || res.equals("ID")) {
			res = "strLit";
		} else if (res.equals("CHAR_LITERAL")) {
			res = "chrLit";
		} else if (res.equals("INT_LITERAL")) {
			res = "intLit";
		}
		return res;
	}

	public SeqExprKind getKind() {
		if (c.isBuiltInToken(value) || c.isSurroundedByQuotes(value)) {
			return SeqExprKind.CONSTANT_CONTENT;
		}

		EClassifier refedEType = c.getETypeForRuleName(value);
		if (refedEType instanceof EClass) {
			return SeqExprKind.REFERS_TO_RULE_WITH_ECLASS;
		}
		if (refedEType instanceof EEnum) {
			return SeqExprKind.REFERS_TO_RULE_WITH_EENUM;
		}
		if (value.equals("STRING_LITERAL") || value.equals("ID")
				|| c.getAltOrTokenRulesWithStrings().contains(value)) {
			return SeqExprKind.KEEP_AS_STR;
		}
		if (value.equals("CHAR_LITERAL")) {
			return SeqExprKind.KEEP_AS_CHR;
		}
		if (value.equals("INT_LITERAL")
				|| c.getAltOrTokenRulesWithIntsOnly().contains(value)) {
			return SeqExprKind.KEEP_AS_INT;
		}
		if (c.getNamesOfListRules().contains(value)) {
			return SeqExprKind.REFERS_TO_LIST_RULE;
		}
		RuleCS r = c.getRuleForNameIfAny(value);
		if (r != null && r.canBeRegardedAsBoolean()) {
			return SeqExprKind.REFERS_TO_RULE_WITH_EENUM;
		}
		assert false;
		throw new RuntimeException();
	}

	public String gymnastGeneratedGetter() {
		if (optFieldName.equals("")) {
			SeqExprKind k = getKind();
			/*
			 * FIXME what if more than one non-field-name shows up. Then
			 * suffixes like 1, 2, are necessary
			 */
			switch (k) {

			case CONSTANT_CONTENT:
				String res = optFieldName.equals("") ? value : optFieldName;
				String postfix = "";
				if (RootCS.isSurroundedByQuotes(res)) {
					res = RootCS.unquote(res);
					postfix = "_KW";
				} else {
					res = res.toLowerCase();
				}
				res = "get" + Util.toUppercaseName(res) + postfix + "()";
				return res;

			case KEEP_AS_CHR:
				return "getChar_literal()";
			case KEEP_AS_STR:
				if (value.equals("ID")) {
					return "getId()";
				}
				if (value.equals("STRING_LITERAL")) {
					return "getString_literal()";
				}
				break;
			case KEEP_AS_INT:
				return "getInt_literal()";

			}

		}
		String res = optFieldName.equals("") ? value : optFieldName;
		res = "get" + Util.toUppercaseName(res) + "()";
		return res;
	}

	public RuleCS getRefedRuleIfAny() {
		RuleCS res = srCS.c.getRuleForNameIfAny(this.value);
		return res;

	}

}
