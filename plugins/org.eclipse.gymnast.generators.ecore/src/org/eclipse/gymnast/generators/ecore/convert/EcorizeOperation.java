package org.eclipse.gymnast.generators.ecore.convert;

import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
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

public class EcorizeOperation {

	private static final String newLine = System.getProperty("line.separator");
	private RootCS c;
	private EPackage eP;
	private String methodBody;
	private EClass ecorizer;

	/**
	 * Generates code such as <br>
	 * <code> 
	 * public ExtendsOrSuper ecorizeExtendsOrSuper(
	 * <br> org.eclipse.emf.emfatic.core.lang.gen.ast.ExtendsOrSuper
	 * extendsOrSuper) 
	 * <br>{ 
	 * <br>String input = extendsOrSuper.getText(); 
	 * <br>return 	 Emfatic.ExtendsOrSuper.get(input); 
	 * <br>}
	 * </code>
	 * 
	 * 
	 * @param trCS
	 * @param enu
	 * @param ecorizer
	 * @param c2
	 */
	public EcorizeOperation(TokenRuleCS trCS, EEnum enu, EClass ecorizer, RootCS c2) {
		assert trCS.getKindOfAlts() == TokenRuleAltsKind.FIXED_KEYWORDS;
		this.c = c2;
		this.eP = ecorizer.getEPackage();
		String inputArgJavaType = trCS.getJavaFQNInGymnast();
		addEcorizeEnumOperation(trCS.name, enu, ecorizer, inputArgJavaType, trCS.toString());
		assert MyEcoreUtil.isWellFormed(ecorizer.getEPackage());
	}

	public EcorizeOperation(AltRuleCS arCS, EEnum enu, EClass ecorizer, RootCS c2) {
		assert arCS.getKindOfAlts() == AltRuleAltsKind.FIXED_KEYWORDS;
		this.c = c2;
		this.eP = ecorizer.getEPackage();
		String inputArgJavaType = arCS.getJavaFQNInGymnast();
		addEcorizeEnumOperation(arCS.name, enu, ecorizer, inputArgJavaType, arCS.toString());
		assert MyEcoreUtil.isWellFormed(ecorizer.getEPackage());
	}

	/**
	 * 
	 * Generates the big switch distinguishing among all AST node kinds. Sthg
	 * like: <code>
	 * public Object ecorize(EmfaticASTNode astNode) {<br> 
	 * The return type is java.lang.Object because <br>
	 * neither EObject nor any of its subypes <br>
	 * covers both the EEnum's as well as <br>
	 * the EClass'es that are valid EMF AST nodes. <br><br> 
	 * if (astNode instanceof org.eclipse.emf.emfatic.core.lang.gen.ast.QidSeparator) { <br>
	 * return ecorizeQidSeparator((org.eclipse.emf.emfatic.core.lang.gen.ast.QidSeparator) 
	 * astNode);<br>
	 *  }<br>
	 *  ...
	 * </code>
	 * 
	 * @param ecorizer
	 * @param c2
	 */
	public EcorizeOperation(EClass ecorizer, RootCS c2) {
		this.c = c2;
		this.eP = ecorizer.getEPackage();
		this.ecorizer = ecorizer;

		createMethodBodiesForEcorizersForAltWithSeqOnlyRules();
		assert MyEcoreUtil.isWellFormed(ecorizer.getEPackage());

		createEOpAndMethodBodyForBigSwitch();
		assert MyEcoreUtil.isWellFormed(ecorizer.getEPackage());

	}

	private void createEOpAndMethodBodyForBigSwitch() {
		// ------ parameter ----------------------------------------------
		EParameter param = EcoreFactory.eINSTANCE.createEParameter();
		param.setName("astNode");
		String javaFQNParam = c.getOption_astPackageName() + "." + c.getOption_astBaseClassName();
		EClassifier paramEcoreDT = getEDataTypeForJavaClass(eP, javaFQNParam);
		param.setEType(paramEcoreDT);
		// ------ operation ----------------------------------------------
		String opName = "ecorize";

		EOperation eOp = MyEcoreUtil.newOperation(opName, ecorizer, EcorePackage.eINSTANCE.getEJavaObject());
		eOp.getEParameters().add(param);
		// ------ body ----------------------------------------------
		String comment = "	/* The return type is java.lang.Object because neither EObject nor any of its subypes covers both the EEnum's as well as the EClass'es that are valid EMF AST nodes. Thus, there is no  getOption_ecoreBaseClassName() unlike There is no getOption_astBaseClassName()  */";

		methodBody = comment;

		for (TokenRuleCS trCS : c.getTokenRulesWithEnums()) {
			methodBody += newLine + oneSwitch(trCS, param);
		}
		for (AltRuleCS arCS : c.getAltRulesWithEnums()) {
			methodBody += newLine + oneSwitch(arCS, param);
		}
		for (AltRuleCS arCS : c.altRules) {
			if (arCS.getKindOfAlts() == AltRuleAltsKind.CONTAINSSEQ) {
				assert arCS.eClass != null;
				methodBody += newLine + oneSwitch(arCS, param);
			}
		}
		for (SeqRuleCS srCS : c.seqRules) {
			assert srCS.eClass != null;
			methodBody += newLine + oneSwitch(srCS, param);
		}

		methodBody += newLine + "return null;"; // FIXME

		// ------ EAnnotation ----------------------------------------------
		MyEcoreUtil.newAnnotation(eOp, "http://www.eclipse.org/emf/2002/GenModel", "body", methodBody);

	}

	private void createMethodBodiesForEcorizersForAltWithSeqOnlyRules() {
		for (AltRuleCS arCS : c.altRules) {
			if (arCS.getKindOfAlts() == AltRuleAltsKind.CONTAINSSEQ) {
				assert arCS.eClass != null;
				// classes and ecoreEOp for all SeqRule are available
				String methodBody = "";
				List<String> constituentSeqs = arCS.alts;
				for (String srName : constituentSeqs) {
					SeqRuleCS refedSeqRule = (SeqRuleCS) c.getRuleForNameIfAny(srName);
					assert refedSeqRule != null;
					methodBody += newLine + oneSwitch(refedSeqRule, refedSeqRule.ecorizeEOp.getEParameters().get(0));
				}
				/*
				 * the two following stmts are necessary because otherwise the
				 * compieler cannot determine that the cases above are
				 * exhaustive and this section of code is unreachable
				 */
				methodBody += newLine + "assert false;";
				methodBody += newLine + "return null;";
				Grammar2Ecore.newMethodBodyAnnotation(arCS.ecorizeEOp, methodBody);
			}
		}

	}

	private String oneSwitch(RuleCS r, EParameter param) {
		String methodBody = "";
		String javaFQNofTypeofArg = r.getJavaFQNInGymnast();
		methodBody = "if (" + param.getName() + " instanceof " + javaFQNofTypeofArg + ") {";
		methodBody += newLine + "return ecorize" + c.camelCase(r.name) + " ( (" + javaFQNofTypeofArg + ") "
				+ param.getName() + " );";
		methodBody += newLine + "}";
		return methodBody;
	}

	private void addEcorizeEnumOperation(String ruleName, EEnum enu, EClass ecorizer, String inputArgJavaType,
			String originalProduction) {
		/*
		 * an item in fixedKeywords may be a ref to a built-in token (e.g, DOT,
		 * STAR) or a user provided fixed keyword ("state"). In the latter case
		 * quoted.
		 */

		// ------ operation ----------------------------------------------
		String ecorizeOpName = "ecorize" + c.camelCase(ruleName);
		EOperation eOp = MyEcoreUtil.newOperation(ecorizeOpName, ecorizer, enu);
		// ------ parameter ----------------------------------------------
		EParameter param = EcoreFactory.eINSTANCE.createEParameter();
		param.setName(ruleName);
		EClassifier gymGenClass = getEDataTypeForJavaClass(enu.getEPackage(), inputArgJavaType);
		param.setEType(gymGenClass);
		eOp.getEParameters().add(param);
		// ------ body ----------------------------------------------
		String methodBody = "/*" + originalProduction + " */";
		methodBody += newLine + "String input = " + ruleName + ".getText();";
		String returnTypeAsJavaFQN = getJavaFQNGeneratedByEcore(enu);
		methodBody += newLine + "return " + returnTypeAsJavaFQN + ".get(input); ";
		// ------ EAnnotation ----------------------------------------------
		MyEcoreUtil.newAnnotation(eOp, "http://www.eclipse.org/emf/2002/GenModel", "body", methodBody);
	}

	private String getJavaFQNGeneratedByEcore(EClassifier eC) {
		String res = c.getOption_genModelBasePackage();
		if (!res.equals("")) {
			res += ".";
		}
		res += MyEcoreUtil.getEcoreFQN(eC);
		return res;
	}

	public static EClassifier getEDataTypeForJavaClass(EPackage eP, String javaTypeFQN) {
		String ecoreName = javaTypeFQN.replace('.', '_');
		for (EClassifier c : eP.getEClassifiers()) {
			if ((c instanceof EDataType) && !(c instanceof EEnum)) {
				EDataType dt = (EDataType) c;
				if (dt.getName().equals(ecoreName)) {
					return c;
				}
			}
		}
		EDataType eDT = EcoreFactory.eINSTANCE.createEDataType();

		// eDT.setInstanceClass(clazz);
		eDT.setInstanceClassName(javaTypeFQN);
		eDT.setName(ecoreName);
		eDT.setSerializable(false);
		eP.getEClassifiers().add(eDT);

		return eDT;
	}

}
