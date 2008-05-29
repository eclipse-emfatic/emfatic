package org.eclipse.gymnast.generators.embeddeddsl.templates;

import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;

public class PrimitiveSF extends Modder {

	private final String _jSetterArgType;

	PrimitiveSF(GenFeature gSF) {
		super(gSF);
		_jSetterArgType = javaTypeForPrimitiveFeature(gSF);
	}

	public static boolean hasPrimitiveType(EStructuralFeature eSF) {
		boolean hasStrType = eSF.getEType() == EcorePackage.eINSTANCE.getEString();
		boolean hasIntType = hasIntType(eSF);
		boolean hasLongType = hasLongType(eSF);
		boolean hasFloatType = hasFloatType(eSF);
		boolean hasDoubleType = hasDoubleType(eSF);
		boolean hasCharType = hasCharType(eSF);
		return hasStrType || hasIntType || hasLongType || hasCharType || hasFloatType || hasDoubleType;
	}

	private static boolean hasDoubleType(EStructuralFeature eSF) {
		boolean hasDoubleType = eSF.getEType() == EcorePackage.eINSTANCE.getEDouble();
		hasDoubleType |= eSF.getEType() == EcorePackage.eINSTANCE.getEDoubleObject();
		return hasDoubleType;
	}

	private static boolean hasFloatType(EStructuralFeature eSF) {
		boolean hasFloatType = eSF.getEType() == EcorePackage.eINSTANCE.getEFloat();
		hasFloatType |= eSF.getEType() == EcorePackage.eINSTANCE.getEFloatObject();
		return hasFloatType;
	}

	private static boolean hasCharType(EStructuralFeature eSF) {
		boolean hasCharType = eSF.getEType() == EcorePackage.eINSTANCE.getEChar();
		hasCharType |= eSF.getEType() == EcorePackage.eINSTANCE.getECharacterObject();
		return hasCharType;
	}

	private static boolean hasStrType(EStructuralFeature eSF) {
		boolean hasStrType = eSF.getEType() == EcorePackage.eINSTANCE.getEString();
		return hasStrType;
	}

	private static boolean hasIntType(EStructuralFeature eSF) {
		boolean hasIntType = eSF.getEType() == EcorePackage.eINSTANCE.getEInt();
		hasIntType |= eSF.getEType() == EcorePackage.eINSTANCE.getEIntegerObject();
		if (eSF.getEType() instanceof EDataType) {
			EDataType eDT = (EDataType) eSF.getEType();
			String itn = eDT.getInstanceTypeName();
			if (itn.equals("java.math.BigInteger") || itn.equals("java.math.BigDecimal")) {
				// FIXME loss of precission without warnign
				hasIntType = true; 
			}
		}
		return hasIntType;
	}

	private static boolean hasLongType(EStructuralFeature eSF) {
		boolean hasIntType = eSF.getEType() == EcorePackage.eINSTANCE.getELong();
		hasIntType |= eSF.getEType() == EcorePackage.eINSTANCE.getELongObject();
		return hasIntType;
	}

	@Override
	public String toString() {
		String res = standardSetterWithArg(getReturnType(), _jSetterArgType);
		return res;
	}

	public static String javaTypeForPrimitiveFeature(GenFeature gf) {
		String res;
		if (gf.getEcoreFeature().isMany()) {
			res = gf.getEcoreFeature().getEGenericType().getEClassifier().getInstanceClass().getCanonicalName();
		} else {
			res = gf.getRawType();
		}
		return res;
	}

	@Override
	public String signatureDecl() {
		return standardSignatureDeclSetterWithArg(getReturnType(), _jSetterArgType) + "; ";
	}

}
