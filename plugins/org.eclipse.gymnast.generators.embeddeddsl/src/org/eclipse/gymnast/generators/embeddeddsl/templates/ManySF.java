package org.eclipse.gymnast.generators.embeddeddsl.templates;

import org.eclipse.emf.codegen.ecore.genmodel.GenFeature;
import org.eclipse.emf.ecore.EClassifier;

public class ManySF extends Modder {

	ManySF(GenFeature gSF) {
		super(gSF);
	}

	@Override
	public String toString() {
		String res = newLine + removeLastSemicolon(signatureDecl()) + " { ";
		res += newLine + "this.myExpr.get" + gSF.getAccessorName() + "().clear();";
		res += newLine + "this.myExpr.get" + gSF.getAccessorName() + "().addAll( java.util.Arrays.asList(items) );";
		res += newLine + " return this; }";
		return res;
	}

	@Override
	public String signatureDecl() {
		String jItemArgType;
		if (PrimitiveSF.hasPrimitiveType(eSF)) {
			jItemArgType = PrimitiveSF.javaTypeForPrimitiveFeature(gSF);
		} else {
			jItemArgType = BeingBuilt.getJavaFQN(gSF.getTypeGenClassifier());
		}
		String methodName = ExprBuilder.escapeJavaKeyword(eSF.getName());
		String jSignature = "public " + getReturnType() + " " + methodName + "( " + jItemArgType + "... items); ";
		return jSignature;
	}

}
