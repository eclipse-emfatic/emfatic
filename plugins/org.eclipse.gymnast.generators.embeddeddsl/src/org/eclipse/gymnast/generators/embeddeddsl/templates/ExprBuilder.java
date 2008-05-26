package org.eclipse.gymnast.generators.embeddeddsl.templates;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenClassifier;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;

public class ExprBuilder {

	private List<BeingBuilt> seqRules = new ArrayList<BeingBuilt>();

	private String _basePackage;
	public String _langName;
	public String _javaClassName;

	public ExprBuilder(GenModel genModel) {
		// FIXME precond: one root gen package
		GenPackage genPackage = genModel.getGenPackages().get(0);
		_basePackage = genPackage.getBasePackage();
		_langName = escapeJavaKeyword(genPackage.getEcorePackage().getName());
		_javaClassName = toUppercaseName(_langName) + "ExprBuilder";
		visitPackage(genPackage);
	}

	public static String escapeJavaKeyword(String name) {
		if (name == null || name.equals("")) {
			return name;
		}
		if (org.eclipse.emf.codegen.util.CodeGenUtil.isJavaReservedWord(name)) {
			name = "_" + name;
		}
		return name;
	}

	private void visitPackage(GenPackage gP) {
		List<GenPackage> subPs = gP.getSubGenPackages();
		for (GenPackage subP : subPs) {
			visitPackage(subP);
		}
		List<GenClassifier> cls = gP.getGenClassifiers();
		for (GenClassifier gC : cls) {
			/*
			 * FIXME precond: EClass names should be unique across all nested
			 * packages, given that they will be flatenned into instantiation
			 * methods in the ExprBuilder class
			 */
			if (!isAuxiliaryClass(gC.getEcoreClassifier())) {
				addBeingBuiltFor(gC);
			}
		}
	}

	private boolean isAuxiliaryClass(EClassifier eC) {
		if (eC.getName().equals("Ecorizer")) {
			return true;
		}
		return false;
	}

	private void addBeingBuiltFor(GenClassifier gC) {
		EClassifier eCl = gC.getEcoreClassifier();
		if (eCl instanceof EClass) {
			EClass eC = (EClass) eCl;
			if (!eC.isAbstract()) {
				BeingBuilt bb = new BeingBuilt((GenClass) gC);
				addBeingBuilt(bb);
			}
		}
	}

	public void addBeingBuilt(BeingBuilt b) {
		b.exprBuilder = this;
		seqRules.add(b);
	}

	/**
	 * Uppercase the first letter of the name to make a good Java class name (so
	 * "myName" -> "MyName")
	 * 
	 * @param name
	 * 		name to be uppercased
	 * @return name with first character uppercased
	 */
	public static String toUppercaseName(String name) {
		String val;
		char firstChar = Character.toUpperCase(name.charAt(0));
		if (name.length() > 1) {
			val = firstChar + name.substring(1);
		} else {
			val = Character.toString(firstChar);
		}
		return val;
	}

	public static final String newLine = System.getProperty("line.separator");

	@Override
	public String toString() {
		String packageName = "";
		if (_basePackage != null && !_basePackage.equals("")) {
			packageName = _basePackage + ".";
		}
		packageName += _langName;
		String res = "package " + packageName + ";";
		res += newLine;
		res += newLine + "public class " + toUppercaseName(_langName) + "ExprBuilder {";
		for (BeingBuilt bb : seqRules) {
			res += newLine + bb.toStringMethod();
		}
		for (BeingBuilt bb : seqRules) {
			res += newLine + bb.toStringInnerClass();
		}
		res += newLine + "}";
		return res;
	}

	/**
	 * Lowercase the first letter of the name to make a good Antlr rule name (so
	 * "MyName" -> "myName")
	 * 
	 * @param name
	 * 		name to be lowercased
	 * @return name with first character lowercased
	 */
	public static String toLowercaseName(String name) {
		String val;
		char firstChar = Character.toLowerCase(name.charAt(0));
		if (name.length() > 1) {
			val = firstChar + name.substring(1);
		} else {
			val = Character.toString(firstChar);
		}
		return val;
	}

}
