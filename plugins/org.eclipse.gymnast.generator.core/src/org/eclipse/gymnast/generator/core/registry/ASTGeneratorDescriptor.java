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

public class ASTGeneratorDescriptor {

	private final String _id;
	private final String _description;
	private final ASTGenerator _astGenerator;
	
	ASTGeneratorDescriptor(String id, String description, ASTGenerator astGenerator) {
		_id = id;
		_description = description;
		_astGenerator = astGenerator;
	}

	public String getId() {
		return _id;
	}
	
	public String getDescription() {
		return _description;
	}
	
	public ASTGenerator getASTGenerator() {
		return _astGenerator;
	}
	
}
