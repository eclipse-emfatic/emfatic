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

import java.util.Iterator;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariable;
import org.eclipse.jface.text.templates.TemplateVariableResolver;

/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class ExtTemplateVariableResolver extends TemplateVariableResolver {

    public ExtTemplateVariableResolver() {
        super();
    }
    public ExtTemplateVariableResolver(String type, String description) {
        super(type, description);
    }
    
    public static abstract class Nested extends ExtTemplateVariableResolver {
        public Nested() {
            super();
        }
        
        public Nested(String type, String description) {
            super(type, description);
        }
        
        protected abstract ExtTemplateContext getNestedContext(TemplateContext outerContext);
        
        public void resolve(TemplateVariable variable, TemplateContext context) {
            if (!(variable instanceof ExtTemplateVariable.Nested)) {
                super.resolve(variable, context);
                return;
            }
            
            ExtTemplateVariable.Nested v = (ExtTemplateVariable.Nested)variable;
            
            String nestedInputText = v.getNestedInputText();
            ExtTemplateContext nestedContext = getNestedContext(context);

            Template nestedTemplate = createNestedTemplate(nestedContext, nestedInputText);
            try {
                TemplateBuffer buf = nestedContext.evaluate(nestedTemplate);
                v.setValue(buf.getString());
            }
            catch (Exception ex) {
                // TODO: handle this
            }
        }
        
        protected Template createNestedTemplate(ExtTemplateContext nestedContext, String nestedInputText) {
        	final String name = "nested template name";
        	final String description = "nested template description";
        	final String contextTypeId = nestedContext.getContextType().getId();
        	final String pattern = nestedInputText;
        	final boolean isAutoInsertable = true;
        	
            Template t = new Template(name, description, contextTypeId, pattern, isAutoInsertable);
            return t;
        }
    }
    
    public static abstract class NestedIterator extends ExtTemplateVariableResolver {
        public NestedIterator() {
            super();
        }
        
        public NestedIterator(String type, String description) {
            super(type, description);
        }
        
        protected abstract ExtTemplateContext getNestedContext(TemplateContext outerContext, Iterator iterator, Object iteratorObject);
        protected abstract Iterator getIterator(TemplateContext outerContext);
        
        public void resolve(TemplateVariable variable, TemplateContext context) {
            if (!(variable instanceof ExtTemplateVariable.Nested)) {
                super.resolve(variable, context);
                return;
            }
            
            ExtTemplateVariable.Nested v = (ExtTemplateVariable.Nested)variable;
            
            String nestedInputText = v.getNestedInputText();
            StringBuffer sb = new StringBuffer();

            Iterator i = getIterator(context);
            if (i == null) return;
            
            while (i.hasNext()) {
                Object o = i.next();
                ExtTemplateContext nestedContext = getNestedContext(context, i, o);

	            Template nestedTemplate = createNestedTemplate(nestedContext, nestedInputText);
	            try {
	                TemplateBuffer buf = nestedContext.evaluate(nestedTemplate);
	                sb.append(buf.getString());
	            }
	            catch (Exception ex) {
	                // TODO: handle this
	            }
            }
            
            v.setValue(sb.toString());
        }
        
        protected Template createNestedTemplate(ExtTemplateContext nestedContext, String nestedInputText) {
        	final String name = "nested template name";
        	final String description = "nested template description";
        	final String contextTypeId = nestedContext.getContextType().getId();
        	final String pattern = nestedInputText;
        	final boolean isAutoInsertable = true;
        	
            Template t = new Template(name, description, contextTypeId, pattern, isAutoInsertable);
            return t;
        }
    }
    
    public static class Simple extends ExtTemplateVariableResolver {
        public static final String TYPE = ExtTemplateVariable.Simple.TYPE;
        
        public Simple() {
            super(TYPE, "Replace the template variable with a fixed value");
        }
        
        public void resolve(TemplateVariable variable, TemplateContext context) {
            if (!(variable instanceof ExtTemplateVariable.Simple)) {
                super.resolve(variable, context);
                return;
            }
            
            ExtTemplateVariable.Simple v = (ExtTemplateVariable.Simple)variable;
            String value = v.getReplacementValue();
            v.setValue(value);
        }
    }
    
}
