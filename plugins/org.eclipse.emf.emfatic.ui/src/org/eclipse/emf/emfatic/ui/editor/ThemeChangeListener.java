/*********************************************************************
* Copyright (c) 2008 The University of York.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.emf.emfatic.ui.editor;

import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;

public abstract class ThemeChangeListener implements IPropertyChangeListener {

	protected String activeTheme = null;
	
	public static String getActiveTheme() {
		try {
			IThemeEngine engine = (IThemeEngine)
			    Display.getDefault().getData("org.eclipse.e4.ui.css.swt.theme");
			return engine.getActiveTheme().getId();
		}
		catch (Exception ex) {
			return PlatformUI.getWorkbench().getThemeManager().getCurrentTheme().getId();
		}
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (getActiveTheme().equals(activeTheme)) return;
		else {
			activeTheme = getActiveTheme();
			themeChange();
		}
	}

	public abstract void themeChange();
}
