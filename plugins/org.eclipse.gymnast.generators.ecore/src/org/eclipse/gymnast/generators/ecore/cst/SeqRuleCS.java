package org.eclipse.gymnast.generators.ecore.cst;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.ast.SeqRule;
import org.eclipse.gymnast.generators.ecore.errors.Grammar2EcoreInvalidInput.EmptyTokenRule.MalformedSeqRule;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;

public class SeqRuleCS extends RuleCS {

	public List<SeqExprCS> seqexprs = new ArrayList<SeqExprCS>();

	/**
	 * Invariant:
	 * 
	 * (this.eClass != null) iff (Grammar2Ecore.convert() has initialized it
	 * 
	 * 
	 */
	public EClass eClass = null;

	public EOperation ecorizeEOp = null;

	public SeqRule sr;

	public EOperation prettyPrintEOp = null;

	public SeqRuleCS(String name, List<String> attrs, RootCS c, SeqRule sr) {
		super(c);
		this.name = name;
		this.attrs = attrs;
		this.sr = sr;
	}

	public boolean hasDuplicateFieldNames() {
		Set<String> fieldNames = new HashSet<String>();
		for (SeqExprCS se : seqexprs) {
			String fn = se.optFieldName;
			if (!fn.equals("")) {
				if (fieldNames.contains(fn)) {
					return true;
				}
				fieldNames.add(se.optFieldName);
			}
		}
		return false;
	}

	public boolean hasNonResolvableConstituent(RootCS c) {
		for (SeqExprCS se : seqexprs) {
			String itemName = se.value;
			if (!c.canBeResolved(itemName)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Reject seq rules having: <br>
	 * (a) a constituent whose name can't be resolved (if resolved, its
	 * well-formedness is checked together with all rules of its kind, so that's
	 * not checked again in this method), <br>
	 * (b) duplicate fields, among the specified field names (they are optional)<br>
	 * (c) whose name shadows that of a built-in token <br>
	 * 
	 * A SeqRule may contain optional lists, alternatives, sequences, and
	 * tokens.
	 * 
	 * A SeqRule has always an EClass as counterpart (in the field eClass).
	 * 
	 */
	public boolean isMalformed(RootCS c) {
		if (c.isBuiltInToken(name)) {
			return true;
		}
		if (hasDuplicateFieldNames()) {
			return true;
		}
		if (hasNonResolvableConstituent(c)) {
			return true;
		}
		return false;
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		res.append("sequence " + name + " " + attrsToString());
		res.append(" : ");
		for (SeqExprCS se : seqexprs) {
			res.append(se.toString() + " ");
		}
		return res.toString();
	}

	@Override
	public Rule getRule() {
		return sr;
	}

	public String getAsJavaComment() {
		// FIXME escape comment delimiters that might appear
		String res = "/* " + this.toString() + " */";
		return res;
	}

	public void addParseMessages(ParseContext parseContext) {
		// TODO Add finer distinctions reflecting those in isMalformed()
		MalformedSeqRule parseMessage = new MalformedSeqRule(this);
		parseContext.addParseMessage(parseMessage);
	}
}
