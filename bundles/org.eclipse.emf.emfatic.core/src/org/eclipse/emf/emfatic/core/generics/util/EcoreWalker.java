/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.core.generics.util;

import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.util.EcoreSwitch;

/**
 * An EcoreWalker navigates the full Ecore Abstract Syntax Tree (AST). It
 * implements the <code>accept</code> operation of the Visitor pattern for an
 * EModelElement object. In this way the <code>accept</code> operation need
 * not be part of EModelElement
 * <p>
 * This walker navigates the Ecore AST pre-order: the current node is visited,
 * then its children.
 * <p>
 * Users must subclass EcoreSwitch and call
 * <code>walk(mainPackage, visitor)</code>.
 * <p>
 * Example usage:
 * <p>
 * class MyVisitor extends EcoreSwitch { .... }
 * <p>
 * MyVisitor v = new MyVisitor (); <br>
 * EcoreWalker w = new EcoreWalker(); <br>
 * Object result = w.walk(eP, v); <br>
 * 
 * @see EcoreSwitch
 */
public class EcoreWalker {

	/**
	 * Navigates the complete AST and ensures that each node is visited by
	 * <code>visitor</code>.
	 * 
	 * @param eME
	 *            the EModelElement to be visited
	 * @param visitor
	 *            the visitor that should visit the expression
	 * @return
	 */
	public EcoreSwitch<?> walk(EModelElement eME, EcoreSwitch<?> v) {
		v.doSwitch(eME);
		visitOwnedParts(eME, v);
		return v;
	}

	private EcoreSwitch<?> visitOwnedParts(EModelElement e, EcoreSwitch<?> v) {
		// the annotations
		for (Object oa : e.getEAnnotations()) {
			EAnnotation a = (EAnnotation) oa;
			walk(a, v);
			// don't return yet
		}
		// EPackage
		if (e instanceof EPackage) {
			EPackage eP = (EPackage) e;
			// visit owned parts and only then return
			// i.e. visit owned classifiers and visit subpackages
			for (Object oC : eP.getEClassifiers()) {
				EClassifier c = (EClassifier) oC;
				walk(c, v);
			}
			for (Object osubP : eP.getESubpackages()) {
				EPackage subP = (EPackage) osubP;
				walk(subP, v);
			}
			return v;
		}
		// EClassifier can be EClass or EDataType (EDataType in turn can be
		// EEnum)
		if (e instanceof EClass) {
			EClass eC = (EClass) e;
			// visit the owned attributes
			for (Object oA : eC.getEAttributes()) {
				EAttribute a = (EAttribute) oA;
				// an attribute has no owned parts, therefore don't walk it
				v.doSwitch(a);
			}
			// visit the owned references
			for (Object oR : eC.getEReferences()) {
				EReference r = (EReference) oR;
				v.doSwitch(r);
			}
			// visit the owned operations
			for (Object oO : eC.getEOperations()) {
				EOperation o = (EOperation) oO;
				v.doSwitch(o);
				// visit the owned formal parameters
				for (Object op : o.getEParameters()) {
					EParameter p = (EParameter) op;
					v.doSwitch(p);
				}
			}
			return v;
		}
		if (e instanceof EDataType) {
			// handle EEnum
			if (e instanceof EEnum) {
				EEnum ee = (EEnum) e;
				// the enum itself was already visited by virtue of it being an
				// EClassifier
				for (Object eL : ee.getELiterals()) {
					EEnumLiteral eeL = (EEnumLiteral) eL;
					v.doSwitch(eeL);
				}
				return v;
			}
			// it's a non-EEnum EDataType
			//EDataType eD = (EDataType) e;
			// the datatype itself was already visited by virtue of it being an
			// EClassifier
			return v;
		}
		return v;
	}
}