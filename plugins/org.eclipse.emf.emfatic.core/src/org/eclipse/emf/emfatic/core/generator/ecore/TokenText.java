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

package org.eclipse.emf.emfatic.core.generator.ecore;

import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNode;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNodeVisitor;

/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class TokenText extends EmfaticASTNodeVisitor {
    
    private StringBuffer _buf = new StringBuffer();

    public static String Get(EmfaticASTNode node) {
        TokenText tt = new TokenText();
        tt.visit(node);
        return tt._buf.toString();
    }
    
    public boolean beginVisit(EmfaticASTNode node) {
        String text = node.getText();
        if (text != null) _buf.append(text);
        return true;
    }
}
