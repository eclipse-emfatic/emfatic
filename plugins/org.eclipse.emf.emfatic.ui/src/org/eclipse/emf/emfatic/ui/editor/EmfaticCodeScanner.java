/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/


package org.eclipse.emf.emfatic.ui.editor;

import org.eclipse.emf.emfatic.core.util.EmfaticBasicTypes;
import org.eclipse.emf.emfatic.core.util.EmfaticKeywords;
import org.eclipse.gymnast.runtime.ui.editor.LDTCodeScanner;
import org.eclipse.gymnast.runtime.ui.util.LDTColorProvider;
import org.eclipse.jface.text.rules.MultiLineRule;

public class EmfaticCodeScanner extends LDTCodeScanner
{

    public EmfaticCodeScanner()
    {
    }

    public void initKeywords()
    {
        addKeywords(EmfaticKeywords.GetNormalKeywords(), LDTColorProvider.BLUE);
        addKeywords(EmfaticKeywords.GetSpecialKeywords(), LDTColorProvider.DARK_BLUE, null, 1);
        addKeywords(EmfaticBasicTypes.GetBasicTypeNames(), LDTColorProvider.DARK_GREEN);
    }

    public void initLiterals()
    {
        org.eclipse.jface.text.rules.IToken literalToken = getLiteralToken();
        addRule(new MultiLineRule("\"", "\"", literalToken, '\\'));
    }
}
