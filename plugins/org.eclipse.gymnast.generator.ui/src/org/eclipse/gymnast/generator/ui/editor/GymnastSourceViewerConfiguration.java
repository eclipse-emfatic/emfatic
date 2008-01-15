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

import org.eclipse.gymnast.generator.core.parser.ParserDriver;
import org.eclipse.gymnast.generator.ui.editor.syntax.GymnastCodeScanner;
import org.eclipse.gymnast.runtime.core.parser.IParser;
import org.eclipse.gymnast.runtime.ui.editor.LDTCodeScanner;
import org.eclipse.gymnast.runtime.ui.editor.LDTEditor;
import org.eclipse.gymnast.runtime.ui.editor.LDTSourceViewerConfiguration;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class GymnastSourceViewerConfiguration extends LDTSourceViewerConfiguration {
	
	public GymnastSourceViewerConfiguration(LDTEditor editor) {
		super(editor);
	}
	
	public LDTCodeScanner createCodeScanner() {
		return new GymnastCodeScanner();
	}

	public IParser getParser() {
		return new ParserDriver();
	}
	
}
