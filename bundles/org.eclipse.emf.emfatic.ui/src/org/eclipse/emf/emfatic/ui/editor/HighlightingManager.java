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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.e4.ui.css.swt.theme.ITheme;
import org.eclipse.e4.ui.css.swt.theme.IThemeEngine;
import org.eclipse.emf.emfatic.ui.EmfaticUIPlugin;
import org.eclipse.gymnast.runtime.ui.util.LDTColorProvider;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

public class HighlightingManager {

	private static final String PREFERENCE_PREFIX = "org.eclipse.emf.emfatic.ui";

	private static String prefixPreference(String preference) {
		return String.format("%s.%s", PREFERENCE_PREFIX, preference);
	}

	public static final RGB COMMENT_COLOR = LDTColorProvider.DARK_GREEN;
	public static final RGB LITERAL_COLOR = LDTColorProvider.DARK_BLUE;
	public static final RGB ID_COLOR = LDTColorProvider.BLACK;
	public static final RGB NORMAL_KEYWORDS_COLOR = LDTColorProvider.BLUE;
	public static final RGB SPECIAL_KEYWORDS_COLOR = LDTColorProvider.DARK_BLUE;
	public static final RGB BASIC_TYPES_COLOR = LDTColorProvider.DARK_GREEN;
	
	public static final RGB COMMENT_COLOR_DARK = new RGB(190, 218, 0);
	public static final RGB LITERAL_COLOR_DARK = new RGB(115, 148, 255);
	public static final RGB ID_COLOR_DARK = LDTColorProvider.WHITE;
	public static final RGB NORMAL_KEYWORDS_COLOR_DARK = new RGB(182, 252, 255);
	public static final RGB SPECIAL_KEYWORDS_COLOR_DARK = new RGB(243, 191, 0);
	public static final RGB BASIC_TYPES_COLOR_DARK = new RGB(118, 167, 37);

	
	public static final String COMMENT_COLOR_PREF = prefixPreference("commentColor");
	public static final String LITERAL_COLOR_PREF = prefixPreference("literalColor");
	public static final String ID_COLOR_PREF = prefixPreference("idColor");
	public static final String NORMAL_KEYWORDS_COLOR_PREF = prefixPreference("normalKeywordsColor");
	public static final String SPECIAL_KEYWORDS_COLOR_PREF = prefixPreference("specialKeywordsColor");
	public static final String BASIC_TYPES_COLOR_PREF = prefixPreference("basicTypesColor");

	public static final List<String> COLOR_PREFERENCES = 
			new ArrayList<> (Arrays.asList(
					COMMENT_COLOR_PREF,
					LITERAL_COLOR_PREF,
					ID_COLOR_PREF,
					NORMAL_KEYWORDS_COLOR_PREF,
					SPECIAL_KEYWORDS_COLOR_PREF,
					BASIC_TYPES_COLOR_PREF));


	protected IPreferenceStore preferenceStore;

	public HighlightingManager() {
		preferenceStore = EmfaticUIPlugin.getDefault().getPreferenceStore();
		initialiseDefaultColors();
	}

	protected boolean areDefaultColorsCorrect() {
		return 
			(isDarkThemeEnabled() &&
				PreferenceConverter.getDefaultColor(preferenceStore, BASIC_TYPES_COLOR_PREF).equals(BASIC_TYPES_COLOR_DARK))
			||
			(!isDarkThemeEnabled() &&
				 PreferenceConverter.getDefaultColor(preferenceStore, BASIC_TYPES_COLOR_PREF).equals(BASIC_TYPES_COLOR));
	}
	
	public IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}

	public void initialiseDefaultColors() {
		// avoid changes if current defaults match the current theme
		if (!areDefaultColorsCorrect()) {
			setDefaults(); 
		}
	}

	public boolean isColorPreference(String preference) {
		return preference.startsWith(PREFERENCE_PREFIX);
	}
	
	protected void setDefaults() {
		if (isDarkThemeEnabled()) {
			PreferenceConverter.setDefault(preferenceStore, COMMENT_COLOR_PREF, COMMENT_COLOR_DARK);
			PreferenceConverter.setDefault(preferenceStore, LITERAL_COLOR_PREF, LITERAL_COLOR_DARK);
			PreferenceConverter.setDefault(preferenceStore, ID_COLOR_PREF, ID_COLOR_DARK);
			PreferenceConverter.setDefault(preferenceStore, NORMAL_KEYWORDS_COLOR_PREF, NORMAL_KEYWORDS_COLOR_DARK);
			PreferenceConverter.setDefault(preferenceStore, SPECIAL_KEYWORDS_COLOR_PREF, SPECIAL_KEYWORDS_COLOR_DARK);
			PreferenceConverter.setDefault(preferenceStore, BASIC_TYPES_COLOR_PREF, BASIC_TYPES_COLOR_DARK);
		} else {
			PreferenceConverter.setDefault(preferenceStore, COMMENT_COLOR_PREF, COMMENT_COLOR);
			PreferenceConverter.setDefault(preferenceStore, LITERAL_COLOR_PREF, LITERAL_COLOR);
			PreferenceConverter.setDefault(preferenceStore, ID_COLOR_PREF, ID_COLOR);
			PreferenceConverter.setDefault(preferenceStore, NORMAL_KEYWORDS_COLOR_PREF, NORMAL_KEYWORDS_COLOR);
			PreferenceConverter.setDefault(preferenceStore, SPECIAL_KEYWORDS_COLOR_PREF, SPECIAL_KEYWORDS_COLOR);
			PreferenceConverter.setDefault(preferenceStore, BASIC_TYPES_COLOR_PREF, BASIC_TYPES_COLOR);
		}
	}

	public static boolean isDarkThemeEnabled() {
		try {
			final Display display = Display.getDefault();
			if (display == null) {
				// We're not in a UI thread: return false for now
				return false;
			}
			final IThemeEngine engine = (IThemeEngine)
			    display.getData("org.eclipse.e4.ui.css.swt.theme");
			final ITheme activeTheme = engine.getActiveTheme();
			return activeTheme != null && "org.eclipse.e4.ui.css.theme.e4_dark".equals(activeTheme.getId());
		}
		catch (Exception ex) {
			return false;
		}
	}

	protected RGB getColor(String preference) {
		return PreferenceConverter.getColor(preferenceStore, preference);
	}

	public RGB getCommentColor() {
		return getColor(COMMENT_COLOR_PREF);
	}

	public RGB getLiteralColor() {
		return getColor(LITERAL_COLOR_PREF);
	}

	public RGB getIdColor() {
		return getColor(ID_COLOR_PREF);
	}

	public RGB getNormalKeywordsColor() {
		return getColor(NORMAL_KEYWORDS_COLOR_PREF);
	}

	public RGB getSpecialKeywordsColor() {
		return getColor(SPECIAL_KEYWORDS_COLOR_PREF);
	}

	public RGB getBasicTypesColor() {
		return getColor(BASIC_TYPES_COLOR_PREF);
	}
}
