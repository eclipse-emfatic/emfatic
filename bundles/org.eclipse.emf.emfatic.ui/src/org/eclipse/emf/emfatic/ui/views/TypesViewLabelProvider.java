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

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.emfatic.core.generics.util.GenericsUtil;
import org.eclipse.emf.emfatic.ui.EmfaticUIPlugin;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

class TypesViewLabelProvider extends LabelProvider {

	public String getText(Object obj) {
		EClass eC = (EClass) obj;
		String res = GenericsUtil.getText(GenericsUtil.getEGenericType(eC));
		return res;
	}

	public Image getImage(Object obj) {
		String imgName = "";
		if (obj instanceof EClass) {
			EClass eC = (EClass) obj;
			imgName += eC.isInterface() ? "int_" : "class_";
			imgName += eC.isAbstract() ? "abs" : "obj";
		}
		if (!imgName.equals("")) {
			return EmfaticUIPlugin.getImage("typesView/" + imgName);
		}
		String imageKey = ISharedImages.IMG_OBJ_ELEMENT;
		imageKey = ISharedImages.IMG_OBJ_FOLDER;
		return PlatformUI.getWorkbench().getSharedImages().getImage(imageKey);
	}
}
