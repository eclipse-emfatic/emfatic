/*********************************************************************
* Copyright (c) 2008 The University of York.
*
* This program and the accompanying materials are made
* available under the terms of the Eclipse Public License 2.0
* which is available at https://www.eclipse.org/legal/epl-2.0/
*
* SPDX-License-Identifier: EPL-2.0
**********************************************************************/
package org.eclipse.emf.emfatic.ui.preferences;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.emfatic.ui.editor.HighlightingManager;
import org.eclipse.emf.emfatic.ui.editor.ThemeChangeListener;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class EmfaticSyntaxColoringPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	protected HighlightingManager highlightingManager;
	protected IPreferenceStore preferenceStore;
	protected List<String> colorPreferences;
	protected Map<String, String> preferenceLabels;
	protected List<EmfaticColorFieldEditor> colorFieldEditors;

	@Override
	public void init(IWorkbench workbench) {
		highlightingManager = new HighlightingManager();
		colorPreferences = HighlightingManager.COLOR_PREFERENCES;
		preferenceStore = highlightingManager.getPreferenceStore();

		preferenceLabels = new HashMap<>();
		preferenceLabels.put(HighlightingManager.COMMENT_COLOR_PREF, "Comments: ");
		preferenceLabels.put(HighlightingManager.LITERAL_COLOR_PREF, "Literals: ");
		preferenceLabels.put(HighlightingManager.ID_COLOR_PREF, "Ids: ");
		preferenceLabels.put(HighlightingManager.NORMAL_KEYWORDS_COLOR_PREF, "Normal keywords: ");
		preferenceLabels.put(HighlightingManager.SPECIAL_KEYWORDS_COLOR_PREF, "Special keywords: ");
		preferenceLabels.put(HighlightingManager.BASIC_TYPES_COLOR_PREF, "Basic types: ");

		// This listener is necessary because, when themes change, for those color
		//   selectors that stored the default value of the old theme, this default
		//   value is treated as a custom one if the user clicks "Apply and Close".
		//   So, when themes change the value of the color selector is overriden,
		//     either with the custom value of the preferences page, or with the
		//     default value of the new theme
		workbench.getThemeManager().addPropertyChangeListener(themeChangeListener);
	}

	protected ThemeChangeListener themeChangeListener = new ThemeChangeListener() {
		@Override
		public void themeChange() {
			highlightingManager.initialiseDefaultColors();
			for (EmfaticColorFieldEditor fieldEditor : colorFieldEditors) {
				if (preferenceStore.contains(fieldEditor.getPreferenceName())) {
					fieldEditor.load();
				} else {
					fieldEditor.loadDefault();
				}
			}
		}
	};

	@Override
	protected void createFieldEditors() {
		// generate field editors sorted by label
		List<String> sortedPreferences = new ArrayList<>(colorPreferences);
		sortedPreferences.sort(new Comparator<String>() {
			@Override
			public int compare(String pref1, String pref2) {
				return preferenceLabels.get(pref1).compareTo(preferenceLabels.get(pref2));
			}
		});
		
		colorFieldEditors = new ArrayList<>();
		for (String preference : sortedPreferences) {
			EmfaticColorFieldEditor fieldEditor = new EmfaticColorFieldEditor(preference,
					preferenceLabels.get(preference), getFieldEditorParent());
			colorFieldEditors.add(fieldEditor);
			addField(fieldEditor);
		}
	}

	@Override
	public IPreferenceStore getPreferenceStore() {
		return preferenceStore;
	}
}
