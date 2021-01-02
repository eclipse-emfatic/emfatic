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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.Diagnostician;

public class GenericsUtil {

	public static EGenericType getEGenericType(EClassifier resC) {
		EGenericType resG = EcoreFactory.eINSTANCE.createEGenericType();
		resG.setEClassifier(resC);
		return resG;
	}

	public static EGenericType getEListOf(EClassifier elementType) {
		EGenericType res = EcoreFactory.eINSTANCE.createEGenericType();
		EDataType eL = EcorePackage.eINSTANCE.getEEList();
		res.setEClassifier(eL);
		EGenericType gtElementType = getEGenericType(elementType);
		res.getETypeArguments().add(gtElementType);
		return res;
	}

	public static EGenericType getEListOf(EGenericType elementType) {
		EGenericType res = EcoreFactory.eINSTANCE.createEGenericType();
		EDataType eL = EcorePackage.eINSTANCE.getEEList();
		res.setEClassifier(eL);
		res.getETypeArguments().add(elementType);
		return res;
	}

	public static EGenericType getRefToTypeParam(ETypeParameter refedTP) {
		EGenericType res = EcoreFactory.eINSTANCE.createEGenericType();
		res.setETypeParameter(refedTP);
		return res;
	}

	public static EGenericType getUnboundedWildcard() {
		EGenericType res = EcoreFactory.eINSTANCE.createEGenericType();
		return res;
	}

	public static EGenericType getUpperBoundedWildcard(EGenericType gt) {
		EGenericType res = EcoreFactory.eINSTANCE.createEGenericType();
		res.setEUpperBound(gt);
		return res;
	}

	public static EGenericType getLowerBoundedWildcard(EGenericType gt) {
		EGenericType res = EcoreFactory.eINSTANCE.createEGenericType();
		res.setELowerBound(gt);
		return res;
	}

	public static EGenericType getParameterizedType(EClassifier declaredType, List<EGenericType> typeArgs) {
		EGenericType res = getEGenericType(declaredType);
		res.getETypeArguments().addAll(typeArgs);
		return res;
	}

	/**
	 * from org.eclipse.emf.ecore.provider.EGenericItemProvider
	 */
	public static String getText(EGenericType eGenericType) {
		ETypeParameter eTypeParameter = eGenericType.getETypeParameter();
		if (eTypeParameter != null) {
			String name = eTypeParameter.getName();
			return name == null ? "null" : name;
		} else {
			EClassifier eClassifier = eGenericType.getEClassifier();
			if (eClassifier != null) {
				List<EGenericType> eTypeArguments = eGenericType.getETypeArguments();
				if (eTypeArguments.isEmpty()) {
					String name = eClassifier.getName();
					return name == null ? "null" : name;
				} else {
					StringBuilder result = new StringBuilder();
					result.append(eClassifier.getName());
					result.append('<');
					for (Iterator<EGenericType> i = eTypeArguments.iterator();;) {
						result.append(getText(i.next()));
						if (i.hasNext()) {
							result.append(", ");
						} else {
							break;
						}
					}
					result.append('>');
					return result.toString();
				}
			} else {
				EGenericType eUpperBound = eGenericType.getEUpperBound();
				if (eUpperBound != null) {
					return "? extends " + getText(eUpperBound);
				} else {
					EGenericType eLowerBound = eGenericType.getELowerBound();
					if (eLowerBound != null) {
						return "? super " + getText(eLowerBound);
					} else {
						return "?";
					}
				}
			}
		}
	}

	/**
	 * from org.eclipse.emf.ecore.provider.ETypeParameterItemProvider
	 */
	public static String getText(ETypeParameter eTypeParameter) {
		if (eTypeParameter.getEBounds().isEmpty()) {
			String name = eTypeParameter.getName();
			return name == null ? "null" : name;
		} else {
			StringBuilder result = new StringBuilder();
			result.append(eTypeParameter.getName());
			result.append(" extends ");
			for (Iterator<EGenericType> i = eTypeParameter.getEBounds().iterator(); i.hasNext();) {
				result.append(getText(i.next()));
				if (i.hasNext()) {
					result.append(" & ");
				}
			}
			return result.toString();
		}
	}

	public static boolean isRefToTypeParam(EGenericType type) {
		boolean res = type.getEClassifier() == null;
		res &= type.getETypeParameter() != null;
		return res;
	}

	public static boolean isWildcard(EGenericType type) {
		boolean res = type.getEClassifier() == null;
		res &= type.getETypeParameter() == null;
		return res;
	}

	public static boolean isParameterizedType(EGenericType type) {
		boolean res = (type.getEClassifier() != null) && (type.getETypeArguments().size() > 0);
		return res;
	}
	
	public static boolean isRefToClassifier(EGenericType type) {
		boolean res = isParameterizedType(type) || isRefToNonGeneric(type) || isRawTypeReference(type); 
		return res; 
	}

	public static boolean isRawTypeReference(EGenericType type) {
		boolean res = (type.getEClassifier() != null) && (type.getETypeArguments().size() == 0)
				&& (type.getEClassifier().getETypeParameters().size() > 0);
		return res;
	}

	public static boolean isRefToNonGeneric(EGenericType type) {
		boolean res = (type.getEClassifier() != null) && (type.getEClassifier().getETypeParameters().size() == 0);
		return res;
	}

	public static boolean isUnboundedWildcard(EGenericType type) {
		boolean res = isWildcard(type) && (type.getEUpperBound() == null) && (type.getELowerBound() == null);
		return res;
	}

	public static boolean isUpperBoundedWildcard(EGenericType type) {
		boolean res = isWildcard(type) && (type.getEUpperBound() != null);
		return res;
	}

	public static boolean isLowerBoundedWildcard(EGenericType type) {
		boolean res = isWildcard(type) && (type.getELowerBound() != null);
		return res;
	}

	public static boolean isWellFormed(EGenericType gt) {
		Diagnostician diagnostician = new Diagnostician();
		final Diagnostic diagnostic = diagnostician.validate(gt);
		boolean res = (diagnostic.getSeverity() == Diagnostic.OK);
		return res;
	}

	public static List<String> getDiagnosticMsgs(EGenericType gt) {
		List<String> res = new ArrayList<String>();
		Diagnostician diagnostician = new Diagnostician();
		final Diagnostic diagnostic = diagnostician.validate(gt);
		if (diagnostic.getSeverity() == Diagnostic.OK) {
			return res;
		}
		for (Diagnostic childDiagnostic : diagnostic.getChildren()) {
			String msg = childDiagnostic.getMessage();
			res.add(msg);
		}
		return res;
	}

}
