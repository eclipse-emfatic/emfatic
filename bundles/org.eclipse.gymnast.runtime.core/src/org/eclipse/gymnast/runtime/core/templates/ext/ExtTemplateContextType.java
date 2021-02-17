/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.runtime.core.templates.ext;

import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateTranslator;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class ExtTemplateContextType extends TemplateContextType {
    
    public ExtTemplateContextType() {
        super();
        addResolvers();
        
    }

    public ExtTemplateContextType(String id) {
        super(id);
        addResolvers();
    }

    public ExtTemplateContextType(String id, String name) {
        super(id, name);
        addResolvers();
    }
    
    private void addResolvers() {
        addResolver(new ExtTemplateVariableResolver.Simple());
    }
    
	public void validate(String pattern) throws TemplateException {
		TemplateTranslator translator= new ExtTemplateTranslator();
		TemplateBuffer buffer= translator.translate(pattern);
		if (buffer != null) {
			validateVariables(buffer.getVariables());
		}
	}
}
