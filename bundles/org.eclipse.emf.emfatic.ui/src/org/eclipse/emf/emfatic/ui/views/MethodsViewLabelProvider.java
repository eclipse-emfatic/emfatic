/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.views;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.emfatic.core.generator.emfatic.Writer;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

public class MethodsViewLabelProvider implements ITableLabelProvider {

	private TypesView _typesView;

	public MethodsViewLabelProvider(TypesView tv) {
		_typesView = tv;
	}

	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex != 0) {
			return null;
		}
		if (element instanceof EOperation) {
			return EmfaticOutlineBuilder.getOperationImage();
		}
		if (element instanceof EAttribute) {
			return EmfaticOutlineBuilder.getAttributeImage();
		}
		if (element instanceof EReference) {
			return EmfaticOutlineBuilder.getReferenceImage();
		}
		return null;
	}

	public String getColumnText(Object element, int columnIndex) {
		if (columnIndex == 0) {
			boolean prependClassName = _typesView.isSortingByDefiningType();
			String res = labelFor(element, prependClassName);
			return res;
		}
		return null;
	}

	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return false;
	}

	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub

	}

	public static String labelFor(Object element, boolean prependClassName) {
		if (!(element instanceof EObject)) {
			return "";
		}
		EObject eO = (EObject) element;
		String res = null;
		if (eO instanceof EAttribute) {
			res = Writer.attributeEssentials((EAttribute) eO);
		} else if (eO instanceof EReference) {
			res = Writer.referenceEssentials((EReference) eO);
		} else {
			res = Writer.operationEssentials((EOperation) eO);
		}

		if (prependClassName) {
			EClass declaringClass = null;
			if (eO instanceof EOperation) {
				declaringClass = ((EOperation) eO).getEContainingClass();
			}
			if (eO instanceof EStructuralFeature) {
				declaringClass = ((EStructuralFeature) eO)
						.getEContainingClass();
			}
			if (declaringClass != null) {
				res = "( " + declaringClass.getName() + " )   -   " + res;
			}
		}
		return res;
	}

}
