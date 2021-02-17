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

import java.util.ArrayList;

import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.TemplateTranslator;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class ExtTemplateTranslator extends TemplateTranslator {
    
    private String _errorMessage;

    public String getErrorMessage() {
        return _errorMessage;
    }
    
    public TemplateBuffer translate(Template template) throws TemplateException {
    	return translate(template.getPattern());
    }
    
    public TemplateBuffer translate(String string) {
        try {
            return new TemplateBuffer(string, findVariables(string));
        }
        catch (Exception ex) {
            _errorMessage = ex.getMessage();
        }
        return null;
    }
    
    private ExtTemplateVariable[] findVariables(String input) throws Exception {
        ArrayList vars = new ArrayList();
        int pos = 0;
        ExtTemplateVariable var = parseVariable(input, pos);
        while (var != null) {
            vars.add(var);
            pos = var.getDefaultOffset() + var.getLength();
            var = parseVariable(input, pos);
        }
        
        return (ExtTemplateVariable[]) vars.toArray(new ExtTemplateVariable[vars.size()]);
    }
    
    private ExtTemplateVariable parseVariable(String input, int startPos) throws Exception {

        boolean isNestingVariable = false;
        
        startPos = input.indexOf('$', startPos);
        if (startPos == -1) return null;

        int pos = startPos+1; // skip the initial '$'
        char c = input.charAt(pos);
        
        switch (c) {
        	case '$' :
        	    // $$ -> $
        	    return new ExtTemplateVariable.Simple("$$", "$", startPos);
        	case '{' :
        	    break;
        	default :
        	    // $ -> $
        	    return new ExtTemplateVariable.Identity("$", startPos);
        }
        
        pos++;
        c = input.charAt(pos);
        
        if (c == '^') {
            isNestingVariable = true;
            pos++;
        }
        String id = parseId(input, pos);
        
        pos += id.length();
        c = input.charAt(pos);
        
        if (c != '}') {
            String varText = input.substring(startPos, pos);
            throw new Exception("Badly formed variable: " + varText);
        }
        
        pos++; // char after '}'
        
        if ("".equals(id)) {
            // if its ${} or ${^} just remove the text
            String value = input.substring(startPos, pos);
            return new ExtTemplateVariable.Removal(value, startPos);
        }
        
        if (isNestingVariable) {
            int endPos = findTerminator(input, pos);
            if (endPos == -1) {
                String varText = input.substring(startPos, pos);
                throw new Exception("Can't find ${^END} terminator for " + varText);
            }
            String varText = input.substring(startPos, endPos);
            String nestedText = input.substring(pos, endPos - _EndMarker.length());
            return new ExtTemplateVariable.Nested('^' + id, varText, nestedText, startPos);
        }
        else {
            String varText = input.substring(startPos, pos);
            return new ExtTemplateVariable(id, varText, startPos);
        }
    }
    
    private final String _EndMarker = "${^END}";

	private int findTerminator(String input, int startPos) {

		int tPos = input.indexOf(_EndMarker, startPos);

		if (tPos != -1) {
			tPos += _EndMarker.length();
			return tPos;
		}
		return -1;
    }
    
    private String parseId(String input, int startPos) {
        int pos = startPos;
        char c = input.charAt(pos);
        while (isIdChar(c)) {
            pos++; c = input.charAt(pos);
        }
        return input.substring(startPos, pos);
    }
    
    private boolean isIdChar(char c) {
        return Character.isUnicodeIdentifierPart(c) || (c == '@') || (c == '.');
    }
    
}
