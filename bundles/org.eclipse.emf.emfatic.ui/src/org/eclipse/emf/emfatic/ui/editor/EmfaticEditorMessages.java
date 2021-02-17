/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.editor;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class EmfaticEditorMessages {

	private static final String BUNDLE_NAME = "org.eclipse.emf.emfatic.ui.editor.EmfaticEditorMessages"; //$NON-NLS-1$

	private static final ResourceBundle fgResourceBundle = ResourceBundle.getBundle(BUNDLE_NAME);

	private EmfaticEditorMessages() {
	}

	public static String getString(String key) {
		try {
			return fgResourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}

	protected static ResourceBundle getResourceBundle() {
		return fgResourceBundle;
	}
}
