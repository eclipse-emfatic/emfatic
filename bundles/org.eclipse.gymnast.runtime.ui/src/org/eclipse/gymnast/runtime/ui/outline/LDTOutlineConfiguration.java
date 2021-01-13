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

package org.eclipse.gymnast.runtime.ui.outline;

import org.eclipse.gymnast.runtime.core.outline.IOutlineBuilder;
import org.eclipse.gymnast.runtime.ui.editor.LDTEditor;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class LDTOutlineConfiguration {
	
	private LDTEditor _editor;
	
	public LDTOutlineConfiguration(LDTEditor editor) {
		_editor = editor;
	}
	
	public IOutlineBuilder getOutlineBuilder() {
		return null;
	}
	
	public LDTContentOutlinePage createContentOutlinePage() {
		return new LDTContentOutlinePage(_editor);
	}
	
	public LDTOutlineContentProvider createOutlineContentProvider() {
		return new LDTOutlineContentProvider(_editor);
	}
	
	public LDTOutlineLabelProvider createOutlineLabelProvide() {
		return new LDTOutlineLabelProvider();
	}

}
