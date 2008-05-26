package org.eclipse.gymnast.generators.embeddeddsl.templates;

import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.ecore.EReference;

/**
 * in order not to interrupt the fluent interface, the arg is passed to a setter
 */
public class NonLiftedRef extends Modder {

	NonLiftedRef(GenFeature gSF) {
		super(gSF);
		this.eR = (EReference) gSF.getEcoreFeature();
	}

	EReference eR;

	@Override
	public String toString() {
		String jSetterArgType = BeingBuilt.getJavaFQN(gSF.getTypeGenClassifier());
		String res = standardSetterWithArg(getReturnType(), jSetterArgType);
		return res;
	}

	@Override
	public String signatureDecl() {
		String jSetterArgType = BeingBuilt.getJavaFQN(gSF.getTypeGenClassifier());
		return standardSignatureDeclSetterWithArg(getReturnType(), jSetterArgType) + ";";
	}
}
