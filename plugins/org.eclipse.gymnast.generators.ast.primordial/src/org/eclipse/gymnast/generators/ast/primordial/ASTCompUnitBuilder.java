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

package org.eclipse.gymnast.generators.ast.primordial;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;

/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class ASTCompUnitBuilder extends JavaCompUnitBuilder {
    
    public ASTCompUnitBuilder(String typeName, String templateTypeID, GeneratorContext context) {
        super(typeName, templateTypeID, context);
    }
    
    public IFile getCompUnit() throws Exception {
	    IFolder astFolder = (IFolder)_context.getASTPackage().getResource();
        IFile compUnit = astFolder.getFile(getTypeName() + ".java");
        return compUnit;
	}
    
    public void buildDone() {
        _util.ruleDone();
	}

}
