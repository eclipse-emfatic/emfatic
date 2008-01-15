/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.generators.parser.lpg.resources;

import java.io.InputStream;

public class LPGFiles {

	public static InputStream getInputStream(String fileName) {
		return LPGFiles.class.getResourceAsStream(fileName);
	}
	
}
