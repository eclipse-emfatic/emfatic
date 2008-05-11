package org.eclipse.gymnast.generators.ecore.convert;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.gymnast.generators.ecore.cst.AltRuleAltsKind;
import org.eclipse.gymnast.generators.ecore.cst.AltRuleCS;
import org.eclipse.gymnast.generators.ecore.cst.RootCS;
import org.eclipse.gymnast.generators.ecore.cst.RuleCS;
import org.eclipse.gymnast.generators.ecore.cst.SeqRuleCS;
import org.eclipse.gymnast.generators.ecore.cst.TokenRuleAltsKind;
import org.eclipse.gymnast.generators.ecore.cst.TokenRuleCS;

import sun.security.action.GetLongAction;

public class Grammar2Ecore {

	public RootCS c;
	public final EPackage eP;
	
	private static final String newLine = System.getProperty("line.separator");

	private String unparseMethodBody = "";
	private EClass ecorizer = null;
	boolean genEcorizer = true;
	boolean genUnparser = true;
	boolean genPrettyPrinter = true;
	private boolean addUmbrellaType;
	private String umbrellaTypeName;

	public Grammar2Ecore(RootCS c) {
		assert c.isWellFormed();
		this.c = c;
		eP = EcoreFactory.eINSTANCE.createEPackage();
		eP.setName(c.languageName);
		eP.setNsPrefix(c.languageName);
		eP.setNsURI(c.languageName);
	}

	private void createEnumsForTokenRulesWithFixedKeywords(EClass ecorizer) {
		for (TokenRuleCS trCS : c.tokenRules) {
			if (trCS.getKindOfAlts() == TokenRuleAltsKind.FIXED_KEYWORDS && !trCS.canBeRegardedAsBoolean()) {
				List<String> enumLits = trCS.explodeTerminalAlternatives();
				enumLits = unquote(enumLits);
				EEnum enu = MyEcoreUtil.newEnum(eP, trCS.name, enumLits);
				/* assign built textual representation for tokens */
				for (EEnumLiteral eL : enu.getELiterals()) {
					if (c.isBuiltInToken(eL.getName())) {
						eL.setLiteral(RootCS.getValueOfBuiltInToken(eL.getName()));
					}
				}
				/* notice that enu.name may differ from trCS.name */
				trCS.enu = enu;
				if (genEcorizer) {
					new EcorizeOperation(trCS, enu, ecorizer, c);
				}
			}
		}
	}

	public List<String> getTokenRulesWithFixedKeywords() {
		List<String> res = new ArrayList<String>();
		for (TokenRuleCS trCS : c.tokenRules) {
			if (trCS.getKindOfAlts() == TokenRuleAltsKind.FIXED_KEYWORDS) {
				res.add(trCS.name);
			}
		}
		return res;
	}

	private void createEnumsForAltRulesWithFixedKeywords(EClass ecorizer) {
		for (AltRuleCS arCS : c.altRules) {
			if (arCS.getKindOfAlts() == AltRuleAltsKind.FIXED_KEYWORDS && !arCS.canBeRegardedAsBoolean()) {
				List<String> enumLits = arCS.explodeTerminalAlternatives();
				enumLits = unquote(enumLits);
				EEnum enu = MyEcoreUtil.newEnum(eP, arCS.name, enumLits);
				/* notice that enu.name may differ from arCS.name */
				arCS.enu = enu;
				if (genEcorizer) {
					new EcorizeOperation(arCS, enu, ecorizer, c);
				}
			}
		}
	}

	public List<String> getAltRulesWithFixedKeywords() {
		List<String> res = new ArrayList<String>();
		for (AltRuleCS arCS : c.altRules) {
			if (arCS.getKindOfAlts() == AltRuleAltsKind.FIXED_KEYWORDS) {
				res.add(arCS.name);
			}
		}
		return res;
	}

	private List<String> unquote(List<String> enumLits) {
		List<String> res = new ArrayList<String>();
		for (String s : enumLits) {
			if (s.startsWith("\"") && s.endsWith("\"")) {
				s = s.substring(1, s.length() - 1);
			}
			res.add(s);
		}
		return res;
	}

	private String unquote(String s) {
		String res = s.substring(1, s.length() - 1);
		return res;
	}

	public void convert() {
		convert(c.getOption_ecorizer(), c.getOption_unparser(), c.getOption_prettyPrinter(), c
				.getOption_addUmbrellaType(), c.getOption_umbrellaTypeName());
	}

	public void convert(boolean genEcorizer, boolean genUnparser, boolean genPrettyPrinter, boolean addUmbrellaType,
			String umbrellaTypeName) {

		this.genEcorizer = genEcorizer;
		this.genUnparser = genUnparser;
		this.genPrettyPrinter = genPrettyPrinter;
		this.addUmbrellaType = addUmbrellaType;
		this.umbrellaTypeName = umbrellaTypeName;
		
		/*
		 * Before code generation proper, some in-memory maps are filled. As an
		 * exception to the above, a few Java methods (ecorizers returning
		 * enums) are generated while populating those maps.
		 */
		if (genEcorizer) {
			ecorizer = MyEcoreUtil.newClass(eP, "Ecorizer", false, null);
		}

		createEnumsForTokenRulesWithFixedKeywords(ecorizer);
		createEnumsForAltRulesWithFixedKeywords(ecorizer);

		createEmptyClassesForSeqRules();
		createEmptyClassesForAltRulesWithSeqItemsOnly();

		/*
		 * Most of the Java code generation is done from now on.
		 * 
		 * For each SeqRule, an unparse() method (returning String) is added to
		 * its Ecore-based counterpart.
		 * 
		 * Along the way, the method body for an ecorizer method is also
		 * generated (such method takes a Gymnast AST node as input, and
		 * instantiates, populates and returns its Ecore-based counterpart).
		 * 
		 */
		new GenUnparseInhaleAndMMForSeqRules(ecorizer, c, this);

		/*
		 * The "big switch" is generated: a method that returns an instantiated
		 * Ecore-based class given an AST node (i.e. given an instance of the
		 * Gymnast-generated <languageName>ASTNode). This big switch invokes the
		 * type-specific ecorizers generated before.
		 * 
		 */
		if (this.genEcorizer) {
			new EcorizeOperation(ecorizer, c);
		}

		if (this.addUmbrellaType) {
			String typeName = "umbrella" + c.languageName;
			if (!c.getOption_umbrellaTypeName().equals("")) {
				typeName = c.getOption_umbrellaTypeName();
			}
			addUmbrellaType(typeName);
		}

	}

	/**
	 * 
	 * add umbrella type over all productions (well, except enums). That
	 * simplifies later writing an instanceof or adding a single method that all
	 * AST nodes should support (e.g., prettyPrint() ).
	 * 
	 */
	private void addUmbrellaType(String typeName) {

		EClassifier superType = null;
		if (c.getOption_prettyPrinter()) {
			/*
			 * FIXME should make the umbrella type implement the interface
			 * below. But en Ecore class (the created umbrella type) cannot have
			 * an EDataType amongs its supertypes ...
			 */
			superType = EcorizeOperation.getEDataTypeForJavaClass(eP,
					GenUnparseInhaleAndMMForSeqRules.jPPPrettyPrintable);
		}

		EClass umbrellaType = MyEcoreUtil.newClass(eP, typeName, true, null);
		EList<EClassifier> cs = eP.getEClassifiers();
		for (EClassifier classifier : cs) {
			if (classifier instanceof EClass && classifier != ecorizer && classifier != umbrellaType) {
				EClass clz = (EClass) classifier;
				if (clz.getESuperTypes().isEmpty()) {
					clz.getESuperTypes().add(umbrellaType);
					assert MyEcoreUtil.isWellFormed(eP);
				}
			}
		}
	}

	public static void newUnparseOperation(EClass eC, String unparseMethodBody) {
		EOperation uOp = MyEcoreUtil.newOperation("unparse", eC, EcorePackage.eINSTANCE.getEString());
		if (unparseMethodBody != null) {
			/*
			 * No method body is needed when adding just the signature of
			 * unparse() to an abstract EClass (for example, those resulting
			 * from AltRuleCS with kind CONTAINSSEQONLY
			 */
			MyEcoreUtil.newAnnotation(uOp, "http://www.eclipse.org/emf/2002/GenModel", "body", unparseMethodBody);
		}
	}

	/**
	 * Precondition: createEmptyClassesForSeqRules() has been called already.
	 */
	private void createEmptyClassesForAltRulesWithSeqItemsOnly() {
		for (AltRuleCS arCS : c.altRules) {
			if (arCS.getKindOfAlts() == AltRuleAltsKind.CONTAINSSEQ) {
				EClass ecForAltRule = MyEcoreUtil.newClass(eP, arCS.name, true, null);
				arCS.eClass = ecForAltRule;
				// comment: the AltRule production
				newGrammarRuleAnnotation(ecForAltRule, "AltRule", arCS.toString());
				// unparse method signature
				if (genUnparser) {
					newUnparseOperation(ecForAltRule, null);
				}
				// classes for all SeqRule are available
				List<String> constituentSeqs = arCS.alts;
				for (String srName : constituentSeqs) {
					EClass ecOfRefedSeqRule = c.getETypeForSeqRuleName(srName);
					EList<EClass> ecRefedSupertypes = ecOfRefedSeqRule.getESuperTypes();
					if (!ecRefedSupertypes.contains(ecForAltRule)) {
						ecRefedSupertypes.add(ecForAltRule);
					}
				}
			}
		}
	}

	/**
	 * Adds an annotation with source "gymnast" as a comment to ease tracing the
	 * correspondence between grammar productions and the EClassifiers generated
	 * for them.
	 * 
	 * @param eAnnotated
	 * @param ruleKind
	 * @param ruleDefinition
	 */
	public static void newGrammarRuleAnnotation(EModelElement eAnnotated, String ruleKind, String ruleDefinition) {
		MyEcoreUtil.newAnnotation(eAnnotated, "gymnast", ruleKind, ruleDefinition);
	}

	private void createEmptyClassesForSeqRules() {
		for (SeqRuleCS srCS : c.seqRules) {
			EClass ec = MyEcoreUtil.newClass(eP, srCS.name, false, null);
			srCS.eClass = ec;
			newGrammarRuleAnnotation(ec, "SeqRule", srCS.toString());
		}
	}

	public static EOperation newEcorizeOperation(EClass ecorizer, RuleCS seqOrAltRule, EClass seqOrAltRuleEClass) {

		// ------ operation ----------------------------------------------
		EOperation eOp = MyEcoreUtil.newOperation("ecorize" + RootCS.camelCase(seqOrAltRule.name), ecorizer,
				seqOrAltRuleEClass);

		String inputArgJavaType = seqOrAltRule.getJavaFQNInGymnast();

		// ------ parameter ----------------------------------------------
		EParameter param = EcoreFactory.eINSTANCE.createEParameter();
		param.setName("astNode");
		EClassifier gymGenClass = EcorizeOperation.getEDataTypeForJavaClass(ecorizer.getEPackage(), inputArgJavaType);
		param.setEType(gymGenClass);
		eOp.getEParameters().add(param);

		return eOp;
	}

	public static EAnnotation newMethodBodyAnnotation(EOperation eOp, String methodBody) {
		EAnnotation res = MyEcoreUtil
				.newAnnotation(eOp, "http://www.eclipse.org/emf/2002/GenModel", "body", methodBody);
		return res;
	}

	public static EOperation newPrettyPrintOperation(EClass eC, String ppMethodBody) {
		EClassifier gymPPBoxClass = EcorizeOperation.getEDataTypeForJavaClass(eC.getEPackage(), GenUnparseInhaleAndMMForSeqRules.jPPBox);
		EOperation ppEOp = MyEcoreUtil.newOperation("prettyPrint", eC, gymPPBoxClass);
		MyEcoreUtil.newAnnotation(ppEOp, "http://www.eclipse.org/emf/2002/GenModel", "body", ppMethodBody);
		return ppEOp;
	}

}
