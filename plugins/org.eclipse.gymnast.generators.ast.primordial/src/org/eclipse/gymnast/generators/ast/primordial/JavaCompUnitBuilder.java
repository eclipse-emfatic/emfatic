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

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.generator.core.generator.GeneratorUtil;
import org.eclipse.gymnast.generators.ast.primordial.templates.JavaCompUnitTemplateContext;



/**
 * 
 * @author cjdaly@us.ibm.com
 */
public abstract class JavaCompUnitBuilder {
    
    private final String _typeName;
    private final String _templateTypeID;
	protected final GeneratorContext _context;
	protected final GeneratorUtil _util;
    
    public JavaCompUnitBuilder(String typeName, String templateTypeID, GeneratorContext context) {
        _typeName = typeName;
        _templateTypeID = templateTypeID;
		_context = context;
		_util = context.getUtil();
    }
    
    public String getTypeName() {
        return _typeName;
    }
    
	public String getTemplateTypeID() {
	    return _templateTypeID;
	}
	
	
	public abstract IFile getCompUnit() throws Exception;
	
	public void build() throws Exception {
	    _util.report("Building Type: " + getTypeName());
	    
        IFile compUnit = getCompUnit();
        
        String text = evalTemplate();
        if (text == null) {
            _util.reportError("failed to evaluate template!");
        }
        else {
            InputStream inputStream = new ByteArrayInputStream(text.getBytes());
	        
	        if (compUnit.exists()) {
	            compUnit.setContents(inputStream, true, false, null);
	        }
	        else {
	            compUnit.create(inputStream, true, null);
	        }
        }
        
        buildDone();
	}
	
	public void buildDone() {
	}
	
	private String evalTemplate() {
	    try {
	        JavaCompUnitTemplateContext tc = createTemplateContext();
	        String s = tc.eval();
	        return s;
	    }
	    catch (Exception ex) {
	        _util.reportError(ex);
	        return null;
	    }
	}
	
	protected JavaCompUnitTemplateContext createTemplateContext() {
	    return new JavaCompUnitTemplateContext(getTemplateTypeID(), this, _context);
	}
}
