package org.eclipse.gymnast.generators.embeddeddsl.templates;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenClassifier;
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.ecore.EClass;

public class BeingBuilt {

	boolean isProgressiveInterface;

	private List<Modder> modders = new ArrayList<Modder>();

	private final String _methodName;
	private final EClass _eC;
	private final GenClass _gC;
	final String _beingBuiltClassName;
	final String _factoryFQN;

	protected ExprBuilder exprBuilder;

	public BeingBuilt(GenClass gC) {
		_gC = gC;
		_eC = (EClass) gC.getEcoreClassifier();
		_beingBuiltClassName = _eC.getName() + "BeingBuilt";

		String factoryInstanceName = gC.getGenPackage().getFactoryInstanceName();
		_factoryFQN = getJavaFQN(gC.getGenPackage()) + "." + gC.getGenPackage().getFactoryInterfaceName() + "." + factoryInstanceName;

		for (GenFeature gSF : _gC.getAllGenFeatures()) {
			if (gSF.isChangeable() && !gSF.isDerived() && !gSF.isMapType() && !gSF.isSuppressedSetVisibility() && !gSF.isContainer()) {
				Modder m = Modder.chooseModderFor(gSF);
				addModder(m);
			}
		}

		_methodName = ExprBuilder.toLowercaseName(_eC.getName());

		for (Batch batch : getBatches()) {
			for (Modder m : batch.modders) {
				m.myBatch = batch;
			}
		}
	}

	static String getJavaFQN(GenPackage gP) {
		
		final String strBP = gP.getBasePackage();
		String gPName = gP.getEcorePackage().getName();
		String res = strBP == null ? gPName : (strBP + "." + gPName);
		return res; 
		
//		String res = "";
//		GenPackage tP = gP;
//		boolean notLooped = true;
//		while (tP != null) {
//			res = tP.getEcorePackage().getName() + (notLooped ? "" : "." + res);
//			notLooped = false;
//			tP = tP.getSuperGenPackage();
//		}
//		if (gP.getBasePackage() != null) {
//			res = basePackageWithoutRoot + "." + res;
//		}
//		return res;
	}

	public static String getJavaFQN(GenClassifier gC) {
		String localName = gC.getEcoreClassifier().getName();
		String jFQN = getJavaFQN(gC.getGenPackage()) + "." + localName;
		return jFQN;
	}

	void addModder(Modder m) {
		modders.add(m);
		m.beingBuilt = this;
	}

	private static final String newLine = System.getProperty("line.separator");

	/**
	 * method that returns the ExprBeingBuilt for this seqRule
	 */
	String toStringMethod() {
		List<Batch> batches = getBatches();
		String returnType = batches.size() > 0 ? batches.get(0).interfaceName : _beingBuiltClassName;
		String res = String.format("public static %1s %2s() {", returnType, _methodName);
		String jInstantiate = String.format("%1s.create%2s()", _factoryFQN, ExprBuilder.toUppercaseName(_eC.getName()));
		String jInvokeConstructor = String.format("new %1s ( %2s )", _beingBuiltClassName, jInstantiate);
		res += newLine + "return " + jInvokeConstructor + "; ";
		res += newLine + "}";
		return res;
	}

	private List<Batch> getBatches() {
		boolean lumpTogether = Modder.annotationHasKeyValue(_gC, "progressiveInterface", false);
		// lumpTogether = true;
		List<Batch> batches = new ArrayList<Batch>();
		Iterator<Modder> iM = modders.iterator();
		int count = 0;
		Batch currentBatch = new Batch(_beingBuiltClassName, count);
		while (iM.hasNext()) {
			Modder m = (Modder) iM.next();
			currentBatch.modders.add(m);
			if (!lumpTogether) {
				boolean cond1 = !iM.hasNext();
				boolean cond2 = !m.eSF.isUnsettable() && m.eSF.getLowerBound() != 0;
				if (cond1 || cond2 || m.eSF.isMany()) {
					batches.add(currentBatch);
					if (iM.hasNext()) {
						count++;
						currentBatch = new Batch(_beingBuiltClassName, count);
					}
				}
			}
		}
		if (lumpTogether) {
			batches.add(currentBatch);
		}
		if (batches.size() == 1) {
			batches.get(0).interfaceName = _beingBuiltClassName;
			batches.get(0).returnInterfaceName = _beingBuiltClassName;
		}
		return batches;
	}

	static class Batch {
		public Batch(String beingBuiltClassName, int count) {
			interfaceName = beingBuiltClassName + count;
			returnInterfaceName = beingBuiltClassName + (count + 1);
		}

		public String interfaceName;
		public String returnInterfaceName;
		public List<Modder> modders = new ArrayList<Modder>();

		public boolean areAllModdersOptional() {
			for (Modder m : modders) {
				if (m.eSF.isMany()) {
					return false;
				}
				if (m.eSF.isUnsettable() == false && m.eSF.getLowerBound() > 0) {
					return false;
				}
			}
			return true;
		}

		public String toStringInterface(boolean amILastBatch) {

			String res = "public interface " + interfaceName + " {";
			for (Modder m : modders) {
				res += newLine + m.signatureDecl();
			}
			if (areAllModdersOptional() && amILastBatch) {
				/*
				 * if all modders are optional, then toAST() will also be part
				 * of this interface, therefore we don't right now close the
				 * interface declaration by adding a right curly brace.
				 */

				res += newLine + "/* allModdersAreOptional */";
			} else {
				res += newLine + "}";
			}
			return res;
		}
	}

	String toStringInnerClass() {
		List<Batch> batches = getBatches();
		String jTypeMyExpr = getJavaFQN(_gC);

		String res = newLine + "public static class " + _beingBuiltClassName + " ";
		if (batches.size() > 1) {
			res += " implements ";
			Iterator<Batch> iB = batches.iterator();
			boolean commaRequired = false;
			while (iB.hasNext()) {
				BeingBuilt.Batch batch = (BeingBuilt.Batch) iB.next();
				res += commaRequired ? ", " : "";
				commaRequired = true;
				res += batch.interfaceName;
			}
			Batch lastBatch = batches.get(batches.size() - 1);
			if (!lastBatch.areAllModdersOptional()) {
				res += commaRequired ? ", " : "";
				res += lastBatch.returnInterfaceName;
			}
		}
		res += " {";
		res += newLine + "private final " + jTypeMyExpr + " myExpr;";

		res += newLine + String.format("public %1s toAST() { return this.myExpr; }", jTypeMyExpr);

		res += newLine + jConstructor(jTypeMyExpr);

		for (Batch b : batches) {
			for (Modder m : b.modders) {
				res += newLine + m.toString();
			}
		}

		res += newLine + "}";

		if (batches.size() > 1) {
			Iterator<Batch> iB = batches.iterator();
			while (iB.hasNext()) {
				BeingBuilt.Batch b = (BeingBuilt.Batch) iB.next();
				boolean amILastBatch = !iB.hasNext();
				res += newLine + b.toStringInterface(amILastBatch);
			}

			Batch lastBatch = batches.get(batches.size() - 1);
			if (lastBatch.areAllModdersOptional()) {
				res += newLine + String.format("public %1s toAST();", jTypeMyExpr);
				res += "}";
			} else {
				res += "public interface " + lastBatch.returnInterfaceName + " { ";
				res += newLine + String.format("public %1s toAST();", jTypeMyExpr);
				res += "}";
			}

		}

		return res;
	}

	private String jConstructor(String jTypeMyExpr) {
		String jConstructor = String.format("%1s( %2s  arg) {", _beingBuiltClassName, jTypeMyExpr);
		jConstructor += newLine + " this.myExpr = arg;";
		// add unSet for each unsettable
		for (GenFeature gF : _gC.getGenFeatures()) {
			if (gF.isUnsettable()) {
				jConstructor += newLine + " this.myExpr.unset" + gF.getAccessorName() + "();";
			}
		}
		jConstructor += newLine + "}";
		return jConstructor;
	}

	public String jInstantiateFor(GenClass refed) {

		String factoryInstanceName = refed.getGenPackage().getFactoryInstanceName();
		String factoryOfRefedTypeFQN = getJavaFQN(refed.getGenPackage()) + "." + refed.getGenPackage().getFactoryInterfaceName() + "." + factoryInstanceName;
		
		String res = String.format("%1s.create%2s()", factoryOfRefedTypeFQN, ExprBuilder.toUppercaseName(refed.getName()));
		return res;
	}
}
