package org.eclipse.gymnast.generators.ecore.errors;

import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generators.ecore.cst.AltRuleCS;
import org.eclipse.gymnast.generators.ecore.cst.ListRuleCS;
import org.eclipse.gymnast.generators.ecore.cst.RuleCS;
import org.eclipse.gymnast.generators.ecore.cst.SeqRuleCS;
import org.eclipse.gymnast.generators.ecore.cst.TokenRuleCS;
import org.eclipse.gymnast.runtime.core.parser.ParseError;

public class Grammar2EcoreInvalidInput extends ParseError {

	public static class EmptyTokenRule extends Grammar2EcoreInvalidInput {

		public EmptyTokenRule(TokenRuleCS trCS) {
			String nameText = trCS.name;
			String message = "Empty token rule: " + nameText;
			int rangeStart = trCS.tr.getRangeStart();
			int rangeLength = trCS.tr.getRangeLength();
			init(message, rangeStart, rangeLength);
		}

		public static class MultipleEntryRules extends Grammar2EcoreInvalidInput {

			public MultipleEntryRules(RuleCS ruleCS) {
				String nameText = ruleCS.name;
				String message = "More than one [entry] rule : " + nameText;
				int rangeStart = ruleCS.getRule().getRangeStart();
				int rangeLength = ruleCS.getRule().getRangeLength();
				init(message, rangeStart, rangeLength);
			}
		}

		public static class RuleNameShadowsBuiltInToken extends Grammar2EcoreInvalidInput {
			public RuleNameShadowsBuiltInToken(RuleCS rCS) {
				String nameText = rCS.name;
				String message = "The name of rule " + nameText + " shadows that of a built-in token.";
				int rangeStart = rCS.getRule().getRangeStart();
				int rangeLength = rCS.getRule().getRangeLength();
				init(message, rangeStart, rangeLength);
			}
		}

		public static class MalformedTokenRule extends Grammar2EcoreInvalidInput {

			public MalformedTokenRule(TokenRuleCS trCS) {
				String nameText = trCS.name;
				String message = "A token rule ("
						+ nameText
						+ ") should consist only of primitive alternatives: constant keywords, other token rules, or built-in tokens (ID, STRING_LITERAL, CHAR_LITERAL, INT)";
				int rangeStart = trCS.getRule().getRangeStart();
				int rangeLength = trCS.getRule().getRangeLength();
				init(message, rangeStart, rangeLength);
			}
		}

		public static class RuleWithNameDuplicate extends Grammar2EcoreInvalidInput {

			public RuleWithNameDuplicate(RuleCS rCS) {
				String nameText = rCS.name;
				String message = "Rule with name duplicate : " + nameText;
				int rangeStart = rCS.getRule().getRangeStart();
				int rangeLength = rCS.getRule().getRangeLength();
				init(message, rangeStart, rangeLength);
			}
		}

		public static class MalformedListRule extends Grammar2EcoreInvalidInput {

			public MalformedListRule(ListRuleCS lrCS) {
				String nameText = lrCS.name;
				String message = "List rule " + nameText
						+ " is rejected. It has one ore more of (a) a non-well-formed repeating item, "
						+ "(b) a 2nd repeating item (if specified) different from the first, "
						+ "(c) a list as repeating item, " + "(d) whose name shadows that of a built-in token";
				int rangeStart = lrCS.getRule().getRangeStart();
				int rangeLength = lrCS.getRule().getRangeLength();
				init(message, rangeStart, rangeLength);
			}
		}

		public static class MalformedAltRule extends Grammar2EcoreInvalidInput {

			public MalformedAltRule(AltRuleCS lrCS) {
				String nameText = lrCS.name;
				String message = "Alt rule "
						+ nameText
						+ " is rejected. It has one or more of (a) an alternative whose name can't be resolved, "
						+ "(b) one or more constituent alternative of list kind, "
						+ "(c) an alt rule, when its constituents are recursively exploded, should contain two or more alternatives, "
						+ "(d) mixed tokens and rules (seq, alt) among the alternatives, "
						+ "(e) its name shadows that of a built-in token, "  
						+ "(f) quoted keywords are allowed in token rules but not in alt rules.";
				int rangeStart = lrCS.getRule().getRangeStart();
				int rangeLength = lrCS.getRule().getRangeLength();
				init(message, rangeStart, rangeLength);
			}
		}

		public static class MalformedSeqRule extends Grammar2EcoreInvalidInput {

			public MalformedSeqRule(SeqRuleCS srCS) {
				String nameText = srCS.name;
				String message = "Seq rule " + nameText
						+ " is rejected. It has one or more of (a) a constituent whose name can't be resolved, "
						+ "(b) duplicate fields, among the specified field names (they are optional), "
						+ "(e) its name shadows that of a built-in token. "
						+ "A SeqRule may contain optional lists, alternatives, sequences, and tokens.";
				int rangeStart = srCS.getRule().getRangeStart();
				int rangeLength = srCS.getRule().getRangeLength();
				init(message, rangeStart, rangeLength);
			}
		}

		public static class MissingGrammarOption extends GeneralMessage {

			public MissingGrammarOption(String optionName) {
				super("Missing grammar option : " + optionName);
			}
		}

		public static class CoverageOfAltAndTokenRules extends GeneralMessage {

			public CoverageOfAltAndTokenRules() {
				super(
						"Please check the source code of org.eclipse.gymnast.generators.ecore.convert.EcoreGeneratorFromGymnastGrammar. "
								+ "For some reason method checkCoverageOfAltAndTokenRules() detected an error.");
			}
		}

		public static class GeneralMessage extends Grammar2EcoreInvalidInput {

			public GeneralMessage(String msg) {
				String nameText = "";
				String message = msg;
				int rangeStart = 0;
				int rangeLength = 1;
				init(message, rangeStart, rangeLength);
			}
			
			public GeneralMessage(String msg, String ruleName, Rule r) {
				String nameText = "";
				String message = msg + " in rule " + ruleName;
				int rangeStart = r.getRangeStart();
				int rangeLength = r.getRangeLength();
				init(message, rangeStart, rangeLength);
			}
		}

	}

}
