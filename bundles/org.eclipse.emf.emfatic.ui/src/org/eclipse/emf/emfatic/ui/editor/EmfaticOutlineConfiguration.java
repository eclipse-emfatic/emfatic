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

import org.eclipse.emf.emfatic.ui.outline.EmfaticContentOutlinePage;
import org.eclipse.emf.emfatic.ui.views.EmfaticOutlineBuilder;
import org.eclipse.gymnast.runtime.core.outline.IOutlineBuilder;
import org.eclipse.gymnast.runtime.ui.outline.LDTContentOutlinePage;
import org.eclipse.gymnast.runtime.ui.outline.LDTOutlineConfiguration;

public class EmfaticOutlineConfiguration extends LDTOutlineConfiguration {

	private EmfaticEditor _editor;

	public EmfaticOutlineConfiguration(EmfaticEditor editor) {
		super(editor);
		_editor = editor;
	}

	public IOutlineBuilder getOutlineBuilder() {
		return new EmfaticOutlineBuilder();
	}

	public LDTContentOutlinePage createContentOutlinePage() {
		return new EmfaticContentOutlinePage(_editor);
	}
	
}
