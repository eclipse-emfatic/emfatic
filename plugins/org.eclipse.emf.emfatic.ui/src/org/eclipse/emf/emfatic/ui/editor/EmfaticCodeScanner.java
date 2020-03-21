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
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;

public class EmfaticCodeScanner extends LDTCodeScanner
{
    protected HighlightingManager highlightingManager;

    public EmfaticCodeScanner()
    {
    }

    @Override
    public void initSetup() {
        highlightingManager = new HighlightingManager();
        super.initSetup();
    }

    @Override
    public void initKeywords()
    {
        addKeywords(EmfaticKeywords.GetNormalKeywords(), highlightingManager.getNormalKeywordsColor());
        addKeywords(EmfaticKeywords.GetSpecialKeywords(), highlightingManager.getSpecialKeywordsColor(), null, 1);
        addKeywords(EmfaticBasicTypes.GetBasicTypeNames(), highlightingManager.getBasicTypesColor());
    }

    @Override
    public void initLiterals()
    {
        org.eclipse.jface.text.rules.IToken literalToken = getLiteralToken();
        addRule(new MultiLineRule("\"", "\"", literalToken, '\\'));
    }

    @Override
    public IToken getLiteralToken() {
        return makeToken(highlightingManager.getLiteralColor(), null, SWT.NORMAL);
    }

    @Override
    public IToken getCommentToken() {
        return makeToken(highlightingManager.getCommentColor(), null, SWT.NORMAL);
    }

    @Override
    public RGB getIdColor() {
        return highlightingManager.getIdColor();
    }
}
