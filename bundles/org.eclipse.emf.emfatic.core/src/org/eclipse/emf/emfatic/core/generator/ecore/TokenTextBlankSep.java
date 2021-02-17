/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.core.generator.ecore;

import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNode;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNodeVisitor;


public class TokenTextBlankSep extends EmfaticASTNodeVisitor
{

    public TokenTextBlankSep()
    {
        _buf = new StringBuffer();
    }

    public static String Get(EmfaticASTNode node)
    {
        TokenTextBlankSep tt = new TokenTextBlankSep();
        tt.visit(node);
        return tt._buf.toString();
    }

    public boolean beginVisit(EmfaticASTNode node)
    {
        String text = node.getText();
        if(text != null) {
        	String sepPre = "";
        	String sepPost = ""; 
        	String prevText = _buf.toString() +  text; 
        	if (prevText.endsWith("extends")
        			|| prevText.endsWith("super")) {
				sepPost = " ";
			} 
        	if (text.equals("extends") || text.equals("super") ) {
        		sepPre = " ";
			}
            _buf.append(sepPre + text + sepPost);
        }
        return true;
    }

    private StringBuffer _buf;
    
   
}
