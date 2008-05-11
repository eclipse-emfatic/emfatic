package org.eclipse.gymnast.generators.ecore.convert;

import static org.eclipse.gymnast.generators.ecore.convert.ShorthandJava.jDOT;
import static org.eclipse.gymnast.generators.ecore.convert.ShorthandJava.jLPAREN;
import static org.eclipse.gymnast.generators.ecore.convert.ShorthandJava.jPLUS;
import static org.eclipse.gymnast.generators.ecore.convert.ShorthandJava.jRPAREN;
import static org.eclipse.gymnast.generators.ecore.convert.ShorthandJava.jSEMI;
import static org.eclipse.gymnast.generators.ecore.convert.ShorthandJava.jStrBLANK;
import static org.eclipse.gymnast.generators.ecore.convert.ShorthandJava.jStrWithQuoteEscaped;

import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.gymnast.generators.ecore.cst.AltRuleAltsKind;
import org.eclipse.gymnast.generators.ecore.cst.AltRuleCS;
import org.eclipse.gymnast.generators.ecore.cst.ListRuleCS;
import org.eclipse.gymnast.generators.ecore.cst.RootCS;
import org.eclipse.gymnast.generators.ecore.cst.RuleCS;
import org.eclipse.gymnast.generators.ecore.cst.SeqExprCS;
import org.eclipse.gymnast.generators.ecore.cst.SeqExprKind;
import org.eclipse.gymnast.generators.ecore.cst.SeqRuleCS;
import org.eclipse.gymnast.generators.ecore.cst.TokenRuleCS;

public class GenUnparseInhaleAndMMForSeqRules {

	private EClass ecorizer = null;
	private RootCS c = null;
	private String unparseMethodBody = "";
	private static final String newLine = System.getProperty("line.separator");

	public static final String jPPPackage = "org.eclipse.gymnast.prettyprinting";
	public static final String jPPBox = jPPPackage + ".Box";
	public static final String jPPPrettyPrintable = jPPPackage
			+ ".PrettyPrintable";
	public static final String jPPSL = jPPPackage + ".SL";
	public static final String jPPBoxLanguage = jPPPackage + ".BoxLanguage";
	public static final String jPPBoxStack = jPPPackage + ".BoxStack";

	Grammar2Ecore g2e = null;
	private String ecorizeMethodBody = "";

	private String ecorizeArgName = null;
	private String ecorizeResultName = null;
	private String unparseBufferName = null;
	private String ppStackName = null;

	private String ppStackStmts = "";
	private String jPPCommand = jPPPackage + ".PPCommand";

	public GenUnparseInhaleAndMMForSeqRules(EClass ecorizer2, RootCS c2,
			Grammar2Ecore g2e) {

		this.ecorizer = ecorizer2;
		this.c = c2;
		this.g2e = g2e;

		if (g2e.genEcorizer) {
			createEcorizersForSeqRulesAndAltWithSeqOnlyRules();
		}

		for (SeqRuleCS srCS : c.seqRules) {

			assert (srCS.eClass != null);

			/*
			 * here names can be chosen for (ecorize, unparse) java methods so
			 * as not to collide with fields
			 */
			ecorizeArgName = "astNode";
			ecorizeResultName = "res";
			unparseBufferName = "buf";
			ppStackName = "ppBoxStack";

			/*
			 * For example, the first executable stmt of an ecorize method can
			 * be like:
			 * 
			 * SubPackageDecl res =
			 * EmfaticFactory.eINSTANCE.createSubPackageDecl();
			 * 
			 * Such instance is going to be given values for its attributes and
			 * references taking them from the AST node returned by the parser.
			 */
			ecorizeMethodBody = srCS.getAsJavaComment();
			ecorizeMethodBody += newLine + srCS.eClass.getName() + " "
					+ ecorizeResultName + " = " + c.languageFactoryImpl(srCS)
					+ ".eINSTANCE.create" + srCS.eClass.getName() + "();";

			unparseMethodBody = srCS.getAsJavaComment();
			unparseMethodBody += newLine + "StringBuffer " + unparseBufferName
					+ " = new StringBuffer();";

			ppStackStmts = srCS.getAsJavaComment();
			ppStackStmts += newLine
					+ String.format(" %1s %2s = new %3s();", jPPBoxStack,
							ppStackName, jPPBoxStack);

			List<SeqExprCS> items = srCS.seqexprs;
			for (SeqExprCS item : items) {

				SeqExprKind k = item.getKind();

				switch (k) {
				case CONSTANT_CONTENT:
					// a) isBuiltInToken, isSurroundedByQuotes. May be optional
					caseConstantContent(item);
					break;

				case REFERS_TO_RULE_WITH_ECLASS:
				case REFERS_TO_RULE_WITH_EENUM:
					/*
					 * b) the item refers to a rule having an EClassifier as
					 * counterpart
					 */
					caseInvokesRuleWithEClassifier(item);
					break;

				case KEEP_AS_STR:
				case KEEP_AS_CHR:
				case KEEP_AS_INT:
					// c) can be converted to str or int
					caseSFStrOrInt(item, k);
					break;

				case REFERS_TO_LIST_RULE:
					// d) the item stands for an invocation of a list rule
					caseConstituentIsListRule(item);
					break;

				}
			}

			if (g2e.genUnparser) {
				if (g2e.genPrettyPrinter) {
					unparseMethodBody = newLine
							+ "return prettyPrint().toString();";
				} else {
					unparseMethodBody += newLine + "return "
							+ unparseBufferName + ".toString();";
				}
				Grammar2Ecore.newUnparseOperation(srCS.eClass,
						unparseMethodBody);
			}

			if (g2e.genEcorizer) {
				ecorizeMethodBody += newLine + "return " + ecorizeResultName
						+ ";";
				Grammar2Ecore.newMethodBodyAnnotation(srCS.ecorizeEOp,
						ecorizeMethodBody);
			}

			if (g2e.genPrettyPrinter) {
				String ppMethodBody = ppStackStmts;
				ppMethodBody += newLine
						+ String
								.format(
										"return %1s.packHorizUpToWidth(35, 1, %2s.stack.size());",
										ppStackName, ppStackName);
				srCS.prettyPrintEOp = Grammar2Ecore.newPrettyPrintOperation(
						srCS.eClass, ppMethodBody);
			}

		}
	}

	private void createEcorizersForSeqRulesAndAltWithSeqOnlyRules() {
		for (SeqRuleCS srCS : c.seqRules) {
			/*
			 * The create of "ecorize" operations (to perform Gymnast AST Node
			 * -> Ecore-based one) takes place now so as to populate for each
			 * SeqRuleCS its field ecorizeEOp. The method body itself will be
			 * computed afterwards.
			 */
			srCS.ecorizeEOp = Grammar2Ecore.newEcorizeOperation(ecorizer, srCS,
					srCS.eClass);
		}

		/*
		 * Precondition: createEmptyClassesForAltRulesWithSeqItemsOnly() has
		 * been called already.
		 */
		for (AltRuleCS arCS : c.altRules) {
			if (arCS.getKindOfAlts() == AltRuleAltsKind.CONTAINSSEQ) {
				assert arCS.eClass != null;
				arCS.ecorizeEOp = Grammar2Ecore.newEcorizeOperation(ecorizer,
						arCS, arCS.eClass);
				Grammar2Ecore.newGrammarRuleAnnotation(arCS.ecorizeEOp,
						"AltRule", arCS.toString());
				/*
				 * Adding the Java method body to arCS.ecorizeEOp is done as
				 * part of creating the ecorize "big switch", during the
				 * execution of the constructor EcorizeOperation(EClass
				 * ecorizer, RootCS c2)
				 */
			}
		}
	}

	/**
	 * c) can be converted to str or int
	 */
	private void caseSFStrOrInt(final SeqExprCS item, final SeqExprKind k) {

		assert ((k == SeqExprKind.KEEP_AS_INT)
				|| (k == SeqExprKind.KEEP_AS_STR) || (k == SeqExprKind.KEEP_AS_CHR));

		// --------- (1) MM -------------------------------------------

		EStructuralFeature val = null;
		EDataType t = null;

		EClass eC = item.srCS.eClass;

		if (k == SeqExprKind.KEEP_AS_STR) {
			t = EcorePackage.eINSTANCE.getEString();
		} else if (k == SeqExprKind.KEEP_AS_CHR) {
			t = EcorePackage.eINSTANCE.getEChar();
		} else {
			t = EcorePackage.eINSTANCE.getELong();
		}
		val = MyEcoreUtil.newAttribute(item.suggestedName(), eC, t);
		val.setUnsettable(item.isOptional);
		val.setUpperBound(1);

		// --------- (2) stms for inhale (ecorize) ---------------------

		String jOptionalCheck = ecorizeArgName + jDOT
				+ item.gymnastGeneratedGetter() + " != null";

		String rhs = ecorizeArgName + "." + item.gymnastGeneratedGetter()
				+ ".getText()";
		if (k == SeqExprKind.KEEP_AS_CHR) {
			rhs = rhs + ".charAt(0)";
		}
		if (k == SeqExprKind.KEEP_AS_INT) {
			rhs = "Long.valueOf(" + rhs + ")";
		}

		String jSetterInvocation = ecorizeResultName + jDOT + jSetter(val)
				+ "(" + rhs + ") ; ";
		String jStmtUnset = null;
		boolean unsetIfNotInInput = item.isOptional;
		if (item.isOptional) {
			jStmtUnset = ecorizeResultName + jDOT + jUnsetter(val) + "; ";
		}
		ecorizeMethodBody = wrapWithCheckIfOptional(ecorizeMethodBody, item,
				jOptionalCheck, jSetterInvocation, unsetIfNotInInput,
				jStmtUnset);

		// --------- (3) stms for unparse ------------------------------

		if (item.isOptional) {
			jOptionalCheck = jIsSet(val);
		}

		String jAppendStmt = unparseBufferName + ".append(" + jGetter(val)
				+ jPLUS + jStrBLANK + jRPAREN + jSEMI;
		unparseMethodBody = wrapWithCheckIfOptional(unparseMethodBody, item,
				jOptionalCheck, jAppendStmt, false, null);

		// --------- (4) stms for unparse ------------------------------

		if (item.isOptional) {
			jOptionalCheck = jIsSet(val);
		}

		jAppendStmt = ppStackName + ".add(" + jGetter(val) + jRPAREN + jSEMI;
		unsetIfNotInInput = true;
		jStmtUnset = ppStackName + ".add( null );";
		ppStackStmts = wrapWithCheckIfOptional(ppStackStmts, item,
				jOptionalCheck, jAppendStmt, unsetIfNotInInput, jStmtUnset);
	}

	private String jIsSet(EStructuralFeature eSF) {
		String res = "isSet" + RootCS.camelCase(eSF.getName()) + "()";
		return res;
	}

	/**
	 * With trailing "()"
	 * 
	 * @param eSFName
	 * @return
	 */
	private String jUnsetter(EStructuralFeature eSF) {
		String res = "unset" + RootCS.camelCase(eSF.getName()) + "()";
		return res;
	}

	/**
	 * Without trailing "()"
	 * 
	 * @param eSFName
	 * @return
	 */
	private String jSetter(EStructuralFeature eSF) {
		String res = "set" + RootCS.camelCase(eSF.getName());
		return res;
	}

	/**
	 * With trailing "()"
	 * 
	 * @param eSFName
	 * @return
	 */
	private String jGetter(EStructuralFeature eSF) {
		boolean fieldIsBoolean = false;
		if (eSF.getEType().equals(EcorePackage.eINSTANCE.getEBoolean())) {
			fieldIsBoolean = true;
		}
		if (eSF.getEType().equals(EcorePackage.eINSTANCE.getEBooleanObject())) {
			fieldIsBoolean = true;
		}
		String res = fieldIsBoolean ? "is" : "get";
		res += RootCS.camelCase(eSF.getName()) + "()";
		return res;
	}

	private String wrapWithCheckIfOptional(String methodBody, SeqExprCS item,
			String jOptionalCheck, String jStmt, boolean unsetIfNotInInput,
			String jStmtUnset) {
		assert unsetIfNotInInput ? !jStmtUnset.equals("") : true;
		if (item.isOptional) {
			methodBody += newLine + "if (" + jOptionalCheck + ") {";
		}
		methodBody += newLine + jStmt;
		if (item.isOptional) {
			methodBody += newLine + "}";
			if (unsetIfNotInInput) {
				methodBody += " else { " + jStmtUnset + " } ";
			}
		}
		return methodBody;
	}

	/**
	 * d) the item stands for an invocation of a list rule
	 * 
	 * TODO what if the separator is not a constant value but one among many
	 * (e.g. qIdSeparator)
	 * 
	 * the elements are to be printed using .getLiteral() , .unparse , or as is
	 * (implicit toString)
	 * 
	 */
	private void caseConstituentIsListRule(SeqExprCS item) {

		// --------- (1) MM -------------------------------------------

		/*
		 * add always a list for the items, and possibly another for the
		 * separators if these are not constant.
		 */

		ListRuleCS lrCS = c.findListRuleCSByName(item.value);
		String suggestedName = item.optFieldName.equals("") ? lrCS
				.getPreferredItemName() : item.optFieldName;
		EClassifier refedEType = c.getETypeForRuleName(lrCS.e1);

		EStructuralFeature val = null;

		SeqExprKind k = lrCS.getKindOfItem();
		String iteratorItemType = null;

		EClass eC = item.srCS.eClass;
		if (refedEType != null) {
			val = addStructuralFeatureForRuleInvocation(suggestedName,
					lrCS.lowerBound == 0, refedEType, item.srCS.eClass);
		} else {
			EDataType t = null;
			k = lrCS.getKindOfItem();
			if (k == SeqExprKind.KEEP_AS_STR
					|| k == SeqExprKind.CONSTANT_CONTENT) {
				t = EcorePackage.eINSTANCE.getEString();
				iteratorItemType = "String";
			} else if (k == SeqExprKind.KEEP_AS_CHR) {
				t = EcorePackage.eINSTANCE.getEChar();
				iteratorItemType = "Char";
			} else {
				t = EcorePackage.eINSTANCE.getELong();
				iteratorItemType = "Long";
			}
			val = MyEcoreUtil.newAttribute(
					turnIntoPlural(item.suggestedName()), eC, t);
			val.setLowerBound(item.isOptional ? 0 : 1);
		}
		val.setUpperBound(-1);
		val.setUnique(false);
		val.setOrdered(true);
		item.eSFListForRefedItems = val;

		boolean bSeparatorsHaveNodes = !lrCS.separator.equals("");
		boolean bRecordSeparators = bSeparatorsHaveNodes
				&& !lrCS.hasConstantSeparator();
		if (bRecordSeparators) {
			EStructuralFeature valSeparators = MyEcoreUtil.newAttribute(item
					.suggestedName()
					+ "Separators", eC, EcorePackage.eINSTANCE.getEString());
			valSeparators.setLowerBound(item.isOptional ? 0 : 1);
			valSeparators.setUpperBound(-1);
			valSeparators.setUnique(false);
			valSeparators.setOrdered(true);
			item.eSFListForSeparators = valSeparators;
		} else {
			item.eSFListForSeparators = null;
		}

		// --------- (2) stms for inhale (ecorize) ---------------------

		boolean itemCanBeSerialized = item.eSFListForRefedItems.getEType() instanceof EDataType;
		String gymnastGetter = item.gymnastGeneratedGetter();
		/*
		 * This getter returns an instance of the list, whose children are items
		 * proper and separators.
		 */
		String jIsSeparatorLocalVarName = "isSeparator" + val.getName();
		ecorizeMethodBody += newLine
				+ String.format("if ( %1s != null) {", ecorizeArgName + "."
						+ gymnastGetter);

		if (bSeparatorsHaveNodes) {
			ecorizeMethodBody += newLine
					+ String.format("boolean %1s = false;",
							jIsSeparatorLocalVarName);
		}

		ecorizeMethodBody += newLine
				+ "for (org.eclipse.gymnast.runtime.core.ast.ASTNode itemOrSeparator : "
				+ ecorizeArgName + "." + gymnastGetter + ".getChildren() ) {";

		String rhs = null;
		if (itemCanBeSerialized) {
			// no need to call ecorize
			rhs = "itemOrSeparator.getText()";
			if (k == SeqExprKind.KEEP_AS_INT) {
				rhs = "Long.valueOf(" + rhs + ")";
			}
		} else {
			// downcasts included
			String jFQNAST = c.getOption_astPackageName() + "."
					+ c.getOption_astBaseClassName();
			rhs = "ecorize( (" + jFQNAST + ") itemOrSeparator)";
			rhs = "( " + item.eSFListForRefedItems.getEType().getName() + " )"
					+ rhs;
		}

		if (bSeparatorsHaveNodes) {
			ecorizeMethodBody += newLine + "   if (!"
					+ jIsSeparatorLocalVarName + ") { ";
		}
		ecorizeMethodBody += ecorizeResultName + "."
				+ jGetter(item.eSFListForRefedItems) + ".add(" + rhs + "); ";
		if (bSeparatorsHaveNodes) {
			ecorizeMethodBody += newLine + "} /* close if separator */";
		}
		if (bRecordSeparators) {
			ecorizeMethodBody += newLine
					+ String
							.format(
									"else { %1s.%2s.add( itemOrSeparator.getText() ); }",
									ecorizeResultName,
									jGetter(item.eSFListForSeparators));
		}
		if (bSeparatorsHaveNodes) {
			ecorizeMethodBody += newLine + jIsSeparatorLocalVarName + " = !"
					+ jIsSeparatorLocalVarName + ";";
		}
		ecorizeMethodBody += newLine + "} /* close for */";
		ecorizeMethodBody += newLine + "} /* close if */";

		// --------- (3) stmts for unparse ------------------------------

		String iterName = "iter" + item.eSFListForRefedItems.getName();
		String iterNameSeparators = null;
		if (bRecordSeparators) {
			iterNameSeparators = "iter" + item.eSFListForSeparators.getName();
		}
		rhs = iterName + ".next()";
		if (!itemCanBeSerialized) {
			rhs = rhs + ".unparse()";
		}

		if (!itemCanBeSerialized) {
			iteratorItemType = item.eSFListForRefedItems.getEType().getName();
		}
		unparseMethodBody += newLine + "java.util.Iterator<" + iteratorItemType
				+ "> " + iterName + " = " + jGetter(item.eSFListForRefedItems)
				+ ".iterator();";
		if (bRecordSeparators) {
			unparseMethodBody += newLine + "java.util.Iterator<String> "
					+ iterNameSeparators + " = "
					+ jGetter(item.eSFListForSeparators) + ".iterator();";
		}

		unparseMethodBody += newLine + "while (" + iterName + ".hasNext() ) { ";
		unparseMethodBody += newLine + unparseBufferName + ".append(" + rhs
				+ ");";
		String rhsIter = null;
		if (bRecordSeparators) {
			rhsIter = String.format(" ( %1s.hasNext() ? %2s.next() : %3s) ",
					iterNameSeparators, iterNameSeparators, jStrBLANK);

		} else {
			rhsIter = "\"" + lrCS.unparseConstantSeparator() + "\"" + jPLUS
					+ jStrBLANK;
			rhsIter = String.format(" ( %1s.hasNext() ? %2s : %3s) ", iterName,
					rhsIter, jStrBLANK);
		}
		unparseMethodBody += newLine + unparseBufferName + ".append(" + rhsIter
				+ ");";
		unparseMethodBody += newLine + "} /* close while */";

		// --------- (4) stms for prettyPrint ------------------------------

		String jGetterItems = jGetter(item.eSFListForRefedItems);
		String jGetterSeps = null;
		String jAppendExpr = null;
		if (bRecordSeparators) {
			jGetterSeps = jGetter(item.eSFListForSeparators);
			jAppendExpr = String
					.format(jPPBoxLanguage + ".interleaveH(%1s, %2s, 0, 0)",
							jGetterItems, jGetterSeps);
		} else {
			if (lrCS.unparseConstantSeparator().trim().equals("")) {
				jAppendExpr = jGetterItems;
			} else {
				jGetterSeps = jStrWithQuoteEscaped
						+ lrCS.unparseConstantSeparator()
						+ jStrWithQuoteEscaped;
				jAppendExpr = String.format(jPPBoxLanguage
						+ ".interleaveHSepConstant(%1s, %2s, 0, 0)",
						jGetterSeps, jGetterItems);
			}
		}

		ppStackStmts += newLine + ppStackName + ".add(" + jAppendExpr + jRPAREN
				+ jSEMI;

	}

	private String turnIntoPlural(String suggestedName) {
		return suggestedName.endsWith("s") ? suggestedName : suggestedName
				+ "s";
	}

	/**
	 * b) the item refers to a rule having an EClassifier as counterpart
	 * 
	 * a (possibly optional) eSF is added as counterpart to item
	 * 
	 * when inhaling, (detect if present) and set, otherwise set to null (and
	 * not to the default value of enums)
	 * 
	 * when unparsing, invoke with .unparse() for non-enums and with
	 * .getLiteral() for enums
	 * 
	 * @param k
	 * 
	 * 
	 */
	private void caseInvokesRuleWithEClassifier(SeqExprCS item) {

		// --------- (1) MM -------------------------------------------

		String suggestedName = item.suggestedName();
		EClassifier refedEType = c.getETypeForRuleName(item.value);
		assert refedEType != null;
		item.eSF = addStructuralFeatureForRuleInvocation(suggestedName,
				item.isOptional, refedEType, item.srCS.eClass);

		/*
		 * An enum can be assigned null in Java, but EMF intercepts that (in the
		 * setter for the EStructuralFeature) and assigns the default instead
		 */
		boolean optionalForEnumRequired = item.isOptional
				&& item.eSF.getEType() instanceof EEnum;
		if (optionalForEnumRequired) {
			item.eSF.setUnsettable(true);
		}

		// --------- (2) stms for inhale (ecorize) ---------------------

		String rhs = ecorizeArgName + jDOT + item.gymnastGeneratedGetter();
		String jOptionalCheck = rhs + " != null";

		RuleCS refed = item.getRefedRuleIfAny();
		assert refed != null;
		String quotedFirstAlternative = "";
		String quotedSecondAlternative = "";
		if (refed.canBeRegardedAsBoolean()) {
			if (refed instanceof AltRuleCS) {
				AltRuleCS arCS = (AltRuleCS) refed;
				quotedFirstAlternative = arCS.getFirstAlternative();
				quotedSecondAlternative = arCS.getSecondAlternative();
			}
			if (refed instanceof TokenRuleCS) {
				TokenRuleCS trCS = (TokenRuleCS) refed;
				quotedFirstAlternative = trCS.alts.get(0);
				quotedSecondAlternative = trCS.alts.get(1);
			}
			String jBooleanExpr = String.format(" %1s.getText().equals(%2s) ",
					rhs, quotedFirstAlternative );
			rhs = jLPAREN + jBooleanExpr + jRPAREN;
		} else {
			String jEcorizerForRefed = "ecorize" + RootCS.camelCase(refed.name);
			rhs = jEcorizerForRefed + jLPAREN + rhs + jRPAREN;
		}

		String jSetterInvocation = ecorizeResultName + jDOT + jSetter(item.eSF)
				+ "(" + rhs + ") ; ";

		String jSetToNull = null;
		final boolean addAssignNullStmtIfNotInInput = item.isOptional;
		if (optionalForEnumRequired) {
			jSetToNull = " /* The EMF-generated setter intercepts null assignments and sets the enum to its default value. That's why the unsetting below is needed. */ ";
			jSetToNull += newLine + ecorizeResultName + jDOT
					+ jUnsetter(item.eSF) + "; ";
		} else {
			jSetToNull = ecorizeResultName + jDOT + jSetter(item.eSF)
					+ "( null ) ; ";
		}

		ecorizeMethodBody = wrapWithCheckIfOptional(ecorizeMethodBody, item,
				jOptionalCheck, jSetterInvocation,
				addAssignNullStmtIfNotInInput, jSetToNull);

		// --------- (3) stms for unparse ------------------------------

		String jGetter = null;
		if (refedEType instanceof EEnum) {
			jGetter = jGetter(item.eSF) + ".getLiteral()";
			if (item.isOptional) {
				jOptionalCheck = jIsSet(item.eSF);
			}
		} else if (refed.canBeRegardedAsBoolean()) {
			jGetter = String.format("( %1s ? %2s : %3s )", jGetter(item.eSF) , quotedFirstAlternative , quotedSecondAlternative);
		} else {
			jGetter = jGetter(item.eSF) + ".unparse()";
			jOptionalCheck = jGetter(item.eSF) + " != null ";
		}

		String jAppendStmt = unparseBufferName + ".append(" + jGetter + jPLUS
				+ jStrBLANK + jRPAREN + jSEMI;
		unparseMethodBody = wrapWithCheckIfOptional(unparseMethodBody, item,
				jOptionalCheck, jAppendStmt, false, null);

		// --------- (4) stms for prettyPrint ------------------------------

		jGetter = null;
		if (refedEType instanceof EEnum) {
			jGetter = jGetter(item.eSF) + ".getLiteral()";
			if (item.isOptional) {
				jOptionalCheck = jIsSet(item.eSF);
			}
		} else if (refed.canBeRegardedAsBoolean()) {
			jGetter = String.format("( %1s ? %2s : %3s )", jGetter(item.eSF) , quotedFirstAlternative , quotedSecondAlternative);
		} else {
			jGetter = jGetter(item.eSF) + ".prettyPrint()";
			jOptionalCheck = jGetter(item.eSF) + " != null ";
		}

		jAppendStmt = ppStackName + ".add(" + jGetter + jRPAREN + jSEMI;
		boolean unsetIfNotInInput = true;
		String jStmtUnset = ppStackName + ".add( null );";
		ppStackStmts = wrapWithCheckIfOptional(ppStackStmts, item,
				jOptionalCheck, jAppendStmt, unsetIfNotInInput, jStmtUnset);
	}

	/**
	 * a) This case is due to isBuiltInToken or isSurroundedByQuotes. There are
	 * two subcases:
	 * 
	 * a.1) optional -> a boolean eSF. To be set when inhaling, controls whether
	 * a String literal is written when unparsing.
	 * 
	 * a.2) mandatory -> no eSF. Nothing is read when inhaling, a String literal
	 * is written when unparsing.
	 * 
	 * 
	 */
	private void caseConstantContent(SeqExprCS item) {

		// --------- (1) MM -------------------------------------------

		String constantContent = item.unparseValue(c);

		EAttribute val = null;
		if (item.isOptional) {
			EDataType t = EcorePackage.eINSTANCE.getEBoolean();
			String suggestedName = item.suggestedName();
			val = MyEcoreUtil.newAttribute(suggestedName, item.srCS.eClass, t);
			val.setLowerBound(1);
			val.setUpperBound(1);
			item.eSFBooleanForOptionalConstant = val;
		}

		if (c.getOption_ecorizer()) {
			assert MyEcoreUtil.isWellFormed(ecorizer.getEPackage());
		}

		// --------- (2) stms for inhale (ecorize) ---------------------

		if (item.isOptional) {
			String jOptionalCheck = ecorizeArgName + jDOT
					+ item.gymnastGeneratedGetter() + " != null ";
			String jSetterInvocation = ecorizeResultName + jDOT + jSetter(val)
					+ "( true ) ; ";
			final boolean addAssignNullStmtIfNotInInput = true;
			String jSetToNull = ecorizeResultName + jDOT + jSetter(val)
					+ "( false ) ; ";
			ecorizeMethodBody = wrapWithCheckIfOptional(ecorizeMethodBody,
					item, jOptionalCheck, jSetterInvocation,
					addAssignNullStmtIfNotInInput, jSetToNull);
		} else {
			ecorizeMethodBody += newLine
					+ "/* input contains here a mandatory " + constantContent
					+ " */";
		}
		// --------- (3) stms for unparse ------------------------------

		String jArgToAppend = jStrWithQuoteEscaped + constantContent
				+ jStrWithQuoteEscaped;
		String jAppendStmt = unparseBufferName + ".append(" + jArgToAppend
				+ jPLUS + jStrBLANK + jRPAREN + jSEMI;
		if (item.isOptional) {
			String jOptionalCheck = jGetter(val) + " != false ";
			unparseMethodBody = wrapWithCheckIfOptional(unparseMethodBody,
					item, jOptionalCheck, jAppendStmt, false, null);
		} else {
			unparseMethodBody += jAppendStmt;
		}

		// --------- (4) stms for prettyPrint ------------------------------

		jAppendStmt = ppStackName + ".add(" + jArgToAppend + jRPAREN + jSEMI;
		if (item.isOptional) {
			String jOptionalCheck = jGetter(val) + " != false ";
			boolean unsetIfNotInInput = true;
			String jStmtUnset = ppStackName + ".add( null );";
			ppStackStmts = wrapWithCheckIfOptional(ppStackStmts, item,
					jOptionalCheck, jAppendStmt, unsetIfNotInInput, jStmtUnset);
		} else {
			ppStackStmts += newLine + jAppendStmt;
		}

	}

	private String jPPStackAdd(String arg) {
		String jStmt = String.format("prettyPrintStack.add(\" %1s \");", arg);
		return jStmt;
	}

	private EStructuralFeature addStructuralFeatureForRuleInvocation(
			String suggestedName, boolean isOptional, EClassifier refedEType,
			EClass owningClass) {
		EStructuralFeature val = null;

		/*
		 * constituent is seq rule, or token or alt rule with fixed keywords
		 */
		if (refedEType instanceof EClass) {
			EReference valAsRef = MyEcoreUtil.newReference(suggestedName,
					owningClass, (EClass) refedEType);
			valAsRef.setContainment(true);
			val = valAsRef;
		} else {
			val = MyEcoreUtil.newAttribute(suggestedName, owningClass,
					(EDataType) refedEType);
		}
		val.setLowerBound(isOptional ? 0 : 1);
		val.setUpperBound(1);

		return val;
	}

}
