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

package org.eclipse.gymnast.generator.core.registry;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;

public abstract class ParserGenerator {

	public abstract void generateParser(GeneratorContext context) throws Exception;
	
	
	protected void writeFile(IFile file, String fileText) throws CoreException {
		InputStream input = new ByteArrayInputStream(fileText.getBytes());
		writeFile(file, input);
	}
	
	protected void writeFile(IFile file, InputStream input) throws CoreException {
		if (file.exists()) {
			file.setContents(input, true, false, null);
		}
		else {
			file.create(input, true, null);
		}
	}
}
