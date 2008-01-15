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

package org.eclipse.gymnast.generators.ast.primordial.ui;

import org.eclipse.gymnast.generators.ast.primordial.Activator;
import org.eclipse.ui.texteditor.templates.TemplatePreferencePage;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class GymnastTemplatePreferencePage extends TemplatePreferencePage {
	
	public GymnastTemplatePreferencePage() {
		super();
		
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setTemplateStore(Activator.getDefault().getTemplateStore());
		setContextTypeRegistry(Activator.getDefault().getContextTypeRegistry());
	}
	
	protected boolean isShowFormatterSetting() {
		return false;
	}
	
	public boolean performOk() {
		boolean ok = super.performOk();
		
		Activator.getDefault().savePluginPreferences();
		
		return ok;
	}
	
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		if (visible) {
			setTitle("Gymnast Code Templates");
		}
	}
	
}
