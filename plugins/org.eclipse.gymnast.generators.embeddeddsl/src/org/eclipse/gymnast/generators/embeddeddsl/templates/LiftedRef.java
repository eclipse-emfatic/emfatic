package org.eclipse.gymnast.generators.embeddeddsl.templates;

import java.util.Iterator;

import org.eclipse.emf.codegen.ecore.genmodel.GenClass;
import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;

public class LiftedRef extends Modder {

	LiftedRef(GenFeature gSF) {
		super(gSF);
		this.eR = (EReference) gSF.getEcoreFeature();
	}

	public EReference eR; // its refed type has no subtypes

	/* derive a get non-null, set one by one args */
	// derive unset if unsettable
	@Override
	public String toString() {
		EClass refed = (EClass) eR.getEType();
		GenClass genRefed = gSF.getTypeGenClass();
		String jInstantiate = beingBuilt.jInstantiateFor(genRefed);
		String jSetStmt = "this.myExpr.set" + gSF.getAccessorName() + "( " + jInstantiate + " );";
		String jGetNonNull = String.format("if ( this.myExpr.get%1s() == null ) { %2s }", gSF.getAccessorName(), jSetStmt);

		// all struct features are str, char, or int
		assert refed.getEAllAttributes().equals(refed.getEAllStructuralFeatures());

		String jAssignments = "";
		Iterator<GenFeature> iF = gSF.getTypeGenClass().getAllGenFeatures().iterator();
		while (iF.hasNext()) {
			GenFeature attr = iF.next();
			String jArgName = ExprBuilder.escapeJavaKeyword(attr.getName());
			// make an assignment
			String jAssignment;
			if (attr.getEcoreFeature().isMany()) {
				String prefix = "this.myExpr.get" + gSF.getAccessorName() + "().get" + attr.getAccessorName() + "()";
				jAssignment = prefix + ".clear();";
				jAssignment += newLine + prefix + ".addAll(" + jArgName + "); /*isManyLiftedRef*/ ";
			} else {
				jAssignment = "this.myExpr.get" + gSF.getAccessorName() + "().set" + attr.getAccessorName() + "(" + jArgName
						+ "); /*LiftedRef*/ ";
			}
			jAssignments += newLine + jAssignment;
		}

		String res = newLine + removeLastSemicolon(signatureDecl()) + " { ";
		res += newLine + jGetNonNull;
		res += newLine + jAssignments;
		res += newLine + " return this; }";
		return res;
	}

	@Override
	public String signatureDecl() {
		String jFormalParamsList = "";
		Iterator<GenFeature> iF = gSF.getTypeGenClass().getAllGenFeatures().iterator();
		while (iF.hasNext()) {
			GenFeature attr = iF.next();
			String jArgName = ExprBuilder.escapeJavaKeyword(attr.getName());
			String jParamDecl = PrimitiveSF.javaTypeForPrimitiveFeature(attr) + " " + jArgName;
			// make a formal parameter
			jFormalParamsList += jParamDecl + (iF.hasNext() ? ", " : "");

		}
		String methodName = ExprBuilder.escapeJavaKeyword(eR.getName());
		String jSignature = "public " + getReturnType() + " " + methodName + "( " + jFormalParamsList + " ); ";
		return jSignature;
	}

}
