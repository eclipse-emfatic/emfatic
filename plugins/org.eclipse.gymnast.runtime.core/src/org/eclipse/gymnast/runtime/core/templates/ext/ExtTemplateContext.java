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

package org.eclipse.gymnast.runtime.core.templates.ext;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateTranslator;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class ExtTemplateContext extends TemplateContext {
    
    public ExtTemplateContext(TemplateContextType contextType) {
        super(contextType);
    }
    
	public boolean canEvaluate(Template template) {
	    return true;
		// return template.getContextTypeId().equals(this.getContextType().getId());
	}
	
	public TemplateBuffer evaluate(Template template) throws BadLocationException, TemplateException {

		if (!canEvaluate(template)) return null;
		
		TemplateTranslator translator = new ExtTemplateTranslator();
		TemplateBuffer buffer = translator.translate(template);
		
		if (buffer == null) return null;

		getContextType().resolve(buffer, this);
		
		return buffer;
	}

}
