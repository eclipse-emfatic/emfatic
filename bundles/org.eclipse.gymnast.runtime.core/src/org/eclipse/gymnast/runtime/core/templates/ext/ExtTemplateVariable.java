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

import org.eclipse.jface.text.templates.TemplateVariable;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class ExtTemplateVariable extends TemplateVariable {
    
    public ExtTemplateVariable(String type, String templateVariableText, int offset) {
        super(type, templateVariableText, new int[]{offset});
    }
    
    public int getDefaultOffset() {
        return getOffsets()[0];
    }
    
    public static class Nested extends ExtTemplateVariable {
        private String _nestedInputText;
        
        public Nested(String type, String templateVariableText, String nestedInputText, int offset) {
            super(type, templateVariableText, offset);
            _nestedInputText = nestedInputText;
        }
        
        public String getNestedInputText() {
            return _nestedInputText;
        }
    }
    
    public static class Simple extends ExtTemplateVariable {
        public static final String TYPE = "_simple";
        
        private String _replacementValue;
        
        public Simple(String templateVariableText, String replacementValue, int offset) {
            super(TYPE, templateVariableText, offset);
            _replacementValue = replacementValue;
        }
        
        public String getReplacementValue() {
            return _replacementValue;
        }
    }
    
    public static class Identity extends Simple {
        public Identity(String templateVariableText, int offset) {
            super(templateVariableText, templateVariableText, offset);
        }
    }
    
    public static class Removal extends Simple {
        public Removal(String templateVariableText, int offset) {
            super(templateVariableText, "", offset);
        }
    }
    
}
