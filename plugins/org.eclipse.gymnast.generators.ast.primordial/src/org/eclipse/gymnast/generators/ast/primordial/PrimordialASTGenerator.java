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

package org.eclipse.gymnast.generators.ast.primordial;

import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.generator.core.registry.ASTGenerator;

public class PrimordialASTGenerator extends ASTGenerator {

	public void generateAST(GeneratorContext context) throws Exception {
		
		BuildManager buildManager = new BuildManager(context);
		buildManager.createBuilders();
		buildManager.build();
	}
	
}
