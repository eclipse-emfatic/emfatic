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

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

class TypesViewContentProvider implements ITreeContentProvider {

	TypesView _typesView = null;
	EClass lastShown = null;

	public TypesViewContentProvider(TypesView tv) {
		_typesView = tv;
	}

	public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		// we're not listening for property changes
	}

	public void dispose() {
	}

	public Object[] getElements(Object parent) {
		if (parent instanceof WeakReference) {
			WeakReference wrC = (WeakReference) parent;
			EClass eC = (EClass) wrC.get();
			return new EClass[] { eC };
		}
		if (parent instanceof EClass) {
			EClass eC = (EClass) parent;
			return getChildren(eC);
		}
		return new Object[] {};
	}

	public Object getParent(Object child) {
		EClass eC = (EClass) child;
		if (_typesView.isShowinSuperTypeHierarchy()) {
			if (subClassesOf(eC).length > 0) {
				EClass fstSubtype = subClassesOf(eC)[0];
				return fstSubtype;
			} else {
				return null;
			}
		} else {
			if (eC.getESuperTypes().size() > 0) {
				EClass firstParent = eC.getESuperTypes().get(0);
				return firstParent;
			} else {
				return null;
			}
		}
	}

	public Object[] getChildren(Object parent) {
		EClass eC = (EClass) parent;
		EClass[] res;
		if (_typesView.isShowinSuperTypeHierarchy()) {
			res = superTypesOf(eC);
		} else {
			res = subClassesOf(eC);
		}
		return res;
	}

	private EClass[] superTypesOf(EClass eC) {
		EClass[] res = new EClass[eC.getESuperTypes().size()];
		eC.getESuperTypes().toArray(res);
		return res;
	}

	private EClass[] subClassesOf(EClass eC) {
		Collection<EObject> subClasses = findOpposites(eC,
				EcorePackage.eINSTANCE.getEClass_ESuperTypes());
		EClass[] res = new EClass[subClasses.size()];
		int i = 0;
		for (EObject sc : subClasses) {
			if (sc instanceof EClass) {
				res[i] = (EClass) sc;
				i++;
			}
		}
		return res;
	}

	public boolean hasChildren(Object parent) {
		EClass eC = (EClass) parent;
		boolean res; 
		if (_typesView.isShowinSuperTypeHierarchy()) {
			res = superTypesOf(eC).length != 0;
		} else {
			res = subClassesOf(eC).length != 0;
		}
		return res;
	}

	public static Collection<EObject> findOpposites(final EObject object,
			final EReference outgoingEReference) {
		// see Sec. 13.5.3 Using Cross Referencers on EMF book (1st Ed.)
		Collection<EStructuralFeature.Setting> settings = new EcoreUtil.UsageCrossReferencer(
				object.eResource().getResourceSet()) {

			protected boolean crossReference(EObject eObject,
					EReference eReference, EObject crossReferencedObject) {
				return object == crossReferencedObject
						&& eReference == outgoingEReference;
			}

			public Collection<EStructuralFeature.Setting> findUsage(
					EObject eObject) {
				return super.findUsage(eObject);
			}

		}.findUsage(object);

		List<EObject> references = new ArrayList<EObject>();

		for (EStructuralFeature.Setting setting : settings) {
			if (setting.getEStructuralFeature() == outgoingEReference) {
				references.add(setting.getEObject());
			}
		}
		return references;
	}
}
