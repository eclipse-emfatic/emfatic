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

package org.eclipse.gymnast.generator.ui.editor;

import org.eclipse.gymnast.runtime.ui.editor.LDTEditor;
import org.eclipse.gymnast.runtime.ui.editor.LDTSourceViewerConfiguration;

/**
 * @author cjdaly@us.ibm.com
 *
 */
public class GymnastEditor extends LDTEditor {
	
	public GymnastEditor( ) {
		// addParseTreeChangedListener(new GymnastParseTreeChangeListener(this));
	}
	
	protected LDTSourceViewerConfiguration createSourceViewerConfiguration() {
		return new GymnastSourceViewerConfiguration(this);
	}
	
}
