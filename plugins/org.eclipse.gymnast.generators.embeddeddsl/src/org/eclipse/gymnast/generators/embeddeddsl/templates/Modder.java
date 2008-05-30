package org.eclipse.gymnast.generators.embeddeddsl.templates;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.emf.codegen.ecore.genmodel.GenAnnotation;
import org.eclipse.emf.codegen.ecore.genmodel.GenBase;
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.gymnast.generators.embeddeddsl.MyEcoreUtil;
import org.eclipse.gymnast.generators.embeddeddsl.templates.BeingBuilt.Batch;

public abstract class Modder {

	protected BeingBuilt beingBuilt;

	protected final GenFeature gSF;
	protected final EClass eC;
	protected final EStructuralFeature eSF;

	Batch myBatch;

	public static final String newLine = System.getProperty("line.separator");

	public static final String GYMNAST_ANNOTATION_SOURCE = "Gymnast";

	public Modder(GenFeature gSF) {
		this.gSF = gSF;
		this.eSF = gSF.getEcoreFeature();
		this.eC = eSF.getEContainingClass();
	}

	public static Modder chooseModderFor(GenFeature gSF) {
		assert gSF.isChangeable() && !gSF.isDerived();

		EStructuralFeature eSF = gSF.getEcoreFeature();

		if (eSF.isMany()) {
			return new ManySF(gSF);
		} else if (BooleanModder.canBeConsideredBoolean(eSF)) {
			return new BooleanModder(gSF);
		} else if (gSF.getTypeGenEnum() != null) {
			// used to check eSF.getEType() instanceof EEnum
			return new EnumModder(gSF);
		} else if (PrimitiveSF.hasPrimitiveType(eSF)) {
			return new PrimitiveSF(gSF);
		} else if (gSF.isReferenceType()) {
			return chooseRefModderFor(gSF);
		}
		
		assert false;
		return null;
	}

	private static Modder chooseRefModderFor(GenFeature gSF) {
		if (isAmenableToRefLifting(gSF)) {
			return new LiftedRef(gSF);
		} else {
			return new NonLiftedRef(gSF);
		}
	}

	public static boolean isAmenableToRefLifting(GenFeature gSF) {
		EReference eR = (EReference) gSF.getEcoreFeature();
		if (eR.getEType() instanceof EDataType) {
			return false;
		}
		/*
		 * the restriction below still allows for referring to an owned child
		 * from a dedicated variable. One way is passing as actual argument the
		 * result of an in-place assignment ( a =
		 * FluentInterface.construct(arg1, ...))
		 */
		if (eR.isContainer()) {
			return false;
		}
		if (!(eR.getEType() instanceof EClass)) {
			return false;
		}
		EClass refedType = (EClass) eR.getEType();
		for (EStructuralFeature eSF : refedType.getEAllStructuralFeatures()) {
			if (!PrimitiveSF.hasPrimitiveType(eSF)) {
				return false;
			}
			if (eSF.isMany()) {
				return false;
			}
		}
		/*
		 * should have no subtypes, only then can its constructor args be
		 * lifted.
		 */
		if (!MyEcoreUtil.getSubTypesOf(refedType).isEmpty()) {
			return false;
		}
		return true;
	}

	public abstract String toString();

	/**
	 * gymnast annotations at the GenFeature take precedence over those in the
	 * refed EStructuralFeature. To obtain annotations at the EStructuralFeature
	 * invoke Modder.getGymnastAnnotations( EStructuralFeature)
	 */
	public static Map<String, String> getGymnastAnnotations(GenBase gB) {
		EModelElement eME = gB.getEcoreModelElement();
		Map<String, String> res = getGymnastAnnotations(eME);
		GenAnnotation gA = gB.getGenAnnotation(GYMNAST_ANNOTATION_SOURCE);
		if (gA != null) {
			for (Map.Entry<String, String> entry : gA.getDetails()) {
				res.put(entry.getKey(), entry.getValue());
			}
		}
		return res;
	}

	public static Map<String, String> getGymnastAnnotations(EModelElement eME) {
		Map<String, String> res = new HashMap<String, String>();
		EAnnotation eA = eME.getEAnnotation(GYMNAST_ANNOTATION_SOURCE);
		if (eA != null) {
			for (Map.Entry<String, String> entry : eA.getDetails()) {
				res.put(entry.getKey(), entry.getValue());
			}
		}
		return res;
	}

	protected static boolean annotationHasKeyValue(GenBase gB, String key, boolean value) {
		Map<String, String> anns = getGymnastAnnotations(gB);
		boolean res = innerAnnotationHasKeyValue(anns, key, value);
		return res;

	}

	protected static boolean annotationHasKeyValue(EModelElement eME, String key, boolean value) {
		Map<String, String> anns = getGymnastAnnotations(eME);
		boolean res = innerAnnotationHasKeyValue(anns, key, value);
		return res;
	}

	private static boolean innerAnnotationHasKeyValue(Map<String, String> anns, String key, boolean value) {
		boolean res = false;
		if (anns != null) {
			String val = anns.get(key);
			if (val != null) {
				val = val.toLowerCase();
				if (value) {
					res = val.equals("yes") || val.equals("true");
					return res;
				} else {
					res = val.equals("no") || val.equals("false");
					return res;
				}
			}
		}
		return false;
	}

	protected String standardSetterWithArg(String returnInterfaceName, String jSetterArgType) {
		String jSetStmt = "this.myExpr.set" + gSF.getAccessorName() + "(arg);";
		String res = standardSignatureDeclSetterWithArg(returnInterfaceName, jSetterArgType) + " { " + newLine + jSetStmt + newLine
				+ " return this; } ";
		return res;
	}

	protected String standardSignatureDeclSetterWithArg(String returnInterfaceName, String jSetterArgType) {
		String modderName = ExprBuilder.escapeJavaKeyword(eSF.getName());
		String jModderArgDecl = jSetterArgType + " arg";
		String jSignature = String.format("public %1s %2s  ( %3s ) ", returnInterfaceName, modderName, jModderArgDecl);
		return jSignature;
	}

	public abstract String signatureDecl();
	
	protected String getReturnType() {
		boolean cond1 = eSF.isUnsettable();
		boolean cond2 = eSF.getLowerBound() == 0 && !eSF.isMany();
		String jReturnType = (cond1 || cond2 ? myBatch.interfaceName : myBatch.returnInterfaceName);
		return  jReturnType;
	}

	protected String removeLastSemicolon(String signatureDecl) {
		int lio = signatureDecl.lastIndexOf(';');
		String res = signatureDecl.substring(0, lio);
		return res;
	}



}
