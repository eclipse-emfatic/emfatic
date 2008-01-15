/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.preferences;

import org.eclipse.emf.emfatic.ui.EmfaticUIPlugin;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;

public class EmfaticTemplatesPreferencePage extends TemplatePreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Constructor
	 */
	public EmfaticTemplatesPreferencePage() {
		setPreferenceStore(EmfaticUIPlugin.getDefault().getPreferenceStore());
		setTemplateStore(EmfaticUIPlugin.getDefault().getEmfaticTemplateStore());
		setContextTypeRegistry(EmfaticUIPlugin.getDefault().getEmfaticContextTypeRegistry());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.texteditor.templates.TemplatePreferencePage#isShowFormatterSetting()
	 */
	protected boolean isShowFormatterSetting() {
		return true;
	}

	/**
	 * @see org.eclipse.jface.preference.IPreferencePage#performOk()
	 */
	public boolean performOk() {
		boolean ok = super.performOk();

		if (ok)
			EmfaticUIPlugin.getDefault().savePluginPreferences();

		return ok;
	}
}
