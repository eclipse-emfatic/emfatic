package org.eclipse.gymnast.generators.embeddeddsl.templates;

import org.eclipse.emf.codegen.ecore.genmodel.GenEnumLiteral;
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EStructuralFeature;

/**
 * terminal2method or terminal2enumliteral
 * 
 */
public class EnumModder extends Modder {

	private final boolean _terminal2method;
	private final EEnum refed;

	public EnumModder(GenFeature gSF) {
		super(gSF);
		refed = (EEnum) eSF.getEType();
		_terminal2method = isTerminal2Method(gSF);
	}

	private boolean isTerminal2Method(GenFeature gSF) {
		if (annotationHasKeyValue(gSF, "terminal2method", false)) {
			return false;
		}
		if (annotationHasKeyValue(refed, "terminal2method", false)) {
			return false;
		}
		/*
		 * if (refed.getELiterals().size() > 3) { return false; }
		 */
		return true;
	}

	@Override
	public String toString() {
		String res = "";
		res += newLine
				+ (_terminal2method ? moddersTerminal2Method(getReturnType()) : modderForTerminalAsArgIdiom(getReturnType()));
		return res;
	}

	private String moddersTerminal2Method(String returnInterfaceName) {
		String jAllModders = "";
		for (GenEnumLiteral gEL : gSF.getTypeGenEnum().getGenEnumLiterals()) {

			String localName = refed.getName();
			String jEnumFQN = beingBuilt.getJavaFQN(gSF.getGenPackage()) + "." + localName;
			String jSetterArg = jEnumFQN + "." + gEL.getEnumLiteralInstanceConstantName();

			String jOneModder = "public " + returnInterfaceName + " " + methodName(gEL) + "() {";
			jOneModder += newLine + String.format("this.myExpr.set%1s( %2s );", gSF.getAccessorName(), jSetterArg);
			jOneModder += newLine + " return this; }";

			jAllModders += jOneModder;
		}

		return jAllModders;
	}

	private String methodName(GenEnumLiteral gEL) {
		String methodName = eSF.getName() + ExprBuilder.toUppercaseName(gEL.getName());
		methodName = ExprBuilder.escapeJavaKeyword(methodName);

		return methodName;
	}

	private Object modderForTerminalAsArgIdiom(String returnInterfaceName) {
		String jModder = jModder(returnInterfaceName) + " { ";
		jModder += newLine + "this.myExpr.set" + gSF.getAccessorName() + "(arg);";
		jModder += newLine + " return this; }";
		return jModder;
	}

	private String jModder(String returnInterfaceName) {
		String methodName = ExprBuilder.escapeJavaKeyword(eSF.getName());
		String jEnumJavaType = beingBuilt.getJavaFQN(gSF.getTypeGenClassifier());
		String jModder = "public " + returnInterfaceName + " " + methodName + "(" + jEnumJavaType + " arg) ";
		return jModder;
	}

	@Override
	public String signatureDecl() {
		String res = "";
		String jReturnType = getReturnType();
		if (_terminal2method) {
			for (GenEnumLiteral gEL : gSF.getTypeGenEnum().getGenEnumLiterals()) {
				res += newLine + "public " + jReturnType + " " + methodName(gEL) + "() ;";
			}
		} else {
			res = jModder(jReturnType) + " ; ";
		}
		return res;
	}

}
