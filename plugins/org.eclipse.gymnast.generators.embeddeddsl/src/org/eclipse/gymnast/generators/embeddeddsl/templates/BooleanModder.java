package org.eclipse.gymnast.generators.embeddeddsl.templates;

import java.util.Map;

import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.gymnast.generators.ecore.convert.MyEcoreUtil;

/**
 * setter or yes/no methods, the latter derived from an annotation with
 * source="gymnast" and two entries:
 * 
 * a) key="yes" , value="methodNameForTrue"
 * 
 * a) key="no" , value="methodNameForFalse"
 * 
 */
public class BooleanModder extends Modder {

	private final boolean followsYesNoMethodIdiom;
	private String yesMethodName;
	private String noMethodName;
	private final boolean _refersToClassWithoutFields;

	public BooleanModder(GenFeature gSF) {
		super(gSF);
		Map<String, String> anns = Modder.getGymnastAnnotations(gSF);
		// first lookup in structural feature declaration
		yesMethodName = ExprBuilder.escapeJavaKeyword(anns.get("yes"));
		noMethodName = ExprBuilder.escapeJavaKeyword(anns.get("no"));
		// then lookup in type declaration
		if (yesMethodName == null) {
			anns = Modder.getGymnastAnnotations(gSF.getEcoreFeature().getEType());
			yesMethodName = ExprBuilder.escapeJavaKeyword(anns.get("yes"));
		}
		if (noMethodName == null) {
			anns = Modder.getGymnastAnnotations(gSF.getEcoreFeature().getEType());
			noMethodName = ExprBuilder.escapeJavaKeyword(anns.get("no"));
		}
		/* FIXME problem marker if yesMethodName .equals noMethodName */
		followsYesNoMethodIdiom = (yesMethodName != null) && (noMethodName != null);
		_refersToClassWithoutFields = refersToClassWithoutFields(eSF);
	}

	public static boolean canBeConsideredBoolean(EStructuralFeature eSF) {
		boolean res = hasBooleanType(eSF) || refersToClassWithoutFields(eSF);
		return res;
	}

	/**
	 * For example, the standard Grammar2Ecore translation of
	 * 
	 * sequence classDecl : (abstractModifier)? ...
	 * 
	 * sequence abstractModifier : "abstract" ;
	 * 
	 * results in class ClassDecl extends TopLevelDecl {
	 * 
	 * !unique val Annotation[*] annotation;
	 * 
	 * val AbstractModifier abstractModifier;
	 * 
	 * with AbstractModifier in turn being defined as:
	 * 
	 * class AbstractModifier extends Umbrellaemfatic { }
	 * 
	 * i.e. an AbstractModifier instantion contains no further information than
	 * telling that it exists (like a boolean).
	 * 
	 */
	private static boolean refersToClassWithoutFields(EStructuralFeature eSF) {
		if (eSF.getEType() instanceof EClass) {
			EClass refed = (EClass) eSF.getEType();
			if (refed.getEAllStructuralFeatures().isEmpty() && !refed.isAbstract()) {
				if (MyEcoreUtil.getSubTypesOf(refed).isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}

	private static boolean hasBooleanType(EStructuralFeature eSF) {
		boolean res = eSF.getEType() == EcorePackage.eINSTANCE.getEBoolean();
		res |= eSF.getEType() == EcorePackage.eINSTANCE.getEBooleanObject();
		return res;
	}

	@Override
	public String toString() {
		String res = newLine
				+ (followsYesNoMethodIdiom ? moddersForYesNoIdiom(getReturnType()) : modderForBoolArgIdiom(getReturnType()));
		return res;
	}

	private Object modderForBoolArgIdiom(String returnInterfaceName) {
		String methodName = ExprBuilder.escapeJavaKeyword(eSF.getName());
		String jModder = "public " + returnInterfaceName + " " + methodName + "() {";
		jModder += newLine + jStmtSetToArg();
		jModder += newLine + " return this; }";
		return jModder;
	}

	private String jStmtSetToArg() {
		String res;
		String jSetStmt = "this.myExpr.set" + gSF.getAccessorName();
		if (_refersToClassWithoutFields) {
			EClass refed = (EClass) eSF.getEType();
			String jInstantiate = beingBuilt.jInstantiateFor(refed);
			res = newLine + jSetStmt + " ( " + jInstantiate + " ); ";
		} else {
			res = jSetStmt + "(true);";
		}
		return res;
	}

	private Object moddersForYesNoIdiom(String returnInterfaceName) {
		assert followsYesNoMethodIdiom;
		String jModderYes = "public " + returnInterfaceName + " " + yesMethodName + "() {";
		String jModderNo = "public " + returnInterfaceName + " " + noMethodName + "() {";
		String jSetStmt = "this.myExpr.set" + gSF.getAccessorName();
		if (_refersToClassWithoutFields) {
			EClass refed = (EClass) eSF.getEType();
			String jInstantiate = beingBuilt.jInstantiateFor(refed);
			jModderYes += newLine + jSetStmt + "(" + jInstantiate + ");";
			jModderNo += newLine + jSetStmt + "(null);";
		} else {
			jModderYes += newLine + jSetStmt + "(true);";
			jModderNo += newLine + jSetStmt + "(false);";
		}
		jModderYes += newLine + " return this; }";
		jModderNo += newLine + " return this; }";

		String res = jModderYes + newLine + jModderNo;
		return res;
	}

	@Override
	public String signatureDecl() {
		String res = "";
		String jReturnType = getReturnType();
		if (followsYesNoMethodIdiom) {
			String jModderYes = "public " + jReturnType + " " + yesMethodName + "() ;";
			String jModderNo = "public " + jReturnType + " " + noMethodName + "() ;";
			res = jModderYes + newLine + jModderNo + newLine;
		} else {
			String methodName = ExprBuilder.escapeJavaKeyword(eSF.getName());
			res = "public " + jReturnType + " " + methodName + "() ;" + newLine;
		}
		return res; 
	}
}
