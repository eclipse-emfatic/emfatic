/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.examples.game.ui;

import org.eclipse.gymnast.examples.game.lang.gen.parser.GameParserDriver;
import org.eclipse.gymnast.runtime.core.parser.IParser;
import org.eclipse.gymnast.runtime.ui.editor.LDTCodeScanner;
import org.eclipse.gymnast.runtime.ui.editor.LDTSourceViewerConfiguration;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.source.ISourceViewer;

public class GameSourceViewerConfiguration extends LDTSourceViewerConfiguration {
    
    private final GameTextHover _textHover;
    
    public GameSourceViewerConfiguration(GameEditor editor) {
        super(editor);
        _textHover = new GameTextHover(editor);
    }
    
    public LDTCodeScanner createCodeScanner() {
        return new GameCodeScanner();
    }
    
    public IParser getParser() {
        return new GameParserDriver();
    }
    
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType) {
        return _textHover;
    }
    
    public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
        return _textHover;
    }

}
