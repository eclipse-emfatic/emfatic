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

package org.eclipse.gymnast.generator.ui.editor.syntax;

import org.eclipse.gymnast.runtime.ui.editor.LDTCodeScanner;
import org.eclipse.gymnast.runtime.ui.util.LDTColorProvider;
import org.eclipse.swt.SWT;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class GymnastCodeScanner extends LDTCodeScanner {
	
	public void initKeywords() {
		addKeywords(new String[]{"language", "options"}, LDTColorProvider.BLUE);
		
		addKeywords(
				new String[]{"abstract", "container", "interface", "list", "sequence", "token"},
				LDTColorProvider.RED,
				LDTColorProvider.YELLOW,
				SWT.BOLD
				);
	}
	
}
