/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.emf.emfatic.core.generator.ecore;

import org.eclipse.emf.emfatic.core.lang.gen.ast.KeyEqualsValue;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.parser.ParseWarning;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public abstract class EmfaticSemanticWarning extends ParseWarning {
	public static class UnknownEPackageNamespaceAttributeKey extends EmfaticSemanticWarning {

		UnknownEPackageNamespaceAttributeKey(KeyEqualsValue keyEqualsValue) {
			String key = TokenText.Get(keyEqualsValue.getKey());
			String message = "Ignoring unknown EPackage namespace annotation key: " + key
					+ ". (Use \"uri\" or \"prefix\" for keys here.)";
			init(message, keyEqualsValue.getRangeStart(), keyEqualsValue.getRangeLength());
		}
	}

	public static class UnknownAttributeImplicitKey extends EmfaticSemanticWarning {

		UnknownAttributeImplicitKey(KeyEqualsValue keyEqualsValue, String generatedKey) {
			String message = "Annotation with undefined implicit key name.  Using key: " + generatedKey
					+ ". (See documentation for use of " + "EmfaticAnnotationMap" + " annotation)";
			init(message, keyEqualsValue.getRangeStart(), keyEqualsValue.getRangeLength());
		}
	}

	public static class AnnotationMappingProblem extends EmfaticSemanticWarning {

		public AnnotationMappingProblem(KeyEqualsValue keyEqualsValue, String message) {
			init(message, keyEqualsValue.getRangeStart(), keyEqualsValue.getRangeLength());
		}
	}

	public static class EcoreValidatorDiagnostic extends EmfaticSemanticWarning {

		public EcoreValidatorDiagnostic(ASTNode node, String message) {
			init(message, node.getRangeStart(), node.getRangeLength());
		}

		public EcoreValidatorDiagnostic(String message, int rangeStart, int rangeLength) {
			init(message, rangeStart, rangeLength);
		}
	}

	public EmfaticSemanticWarning() {
	}
}
