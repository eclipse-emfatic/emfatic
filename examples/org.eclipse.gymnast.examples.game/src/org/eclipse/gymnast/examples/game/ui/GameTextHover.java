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

import org.eclipse.gymnast.examples.game.lang.gen.ast.Move;
import org.eclipse.gymnast.examples.game.lang.gen.ast.Shoot;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

public class GameTextHover implements ITextHover {
    
    private final GameEditor _editor;
    private final Class[] _hoverClasses;
    
    public GameTextHover(GameEditor editor) {
        _editor = editor;
        _hoverClasses = new Class[] {
                Move.class,
                Shoot.class
        };
    }
    
    public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
        if (hoverRegion != null) {
            ASTNode rootNode = _editor.getParseRoot();
            if (rootNode != null) {
                ASTNode hoverNode = rootNode.getNodeAt(hoverRegion.getOffset(), hoverRegion.getLength(), _hoverClasses, true);
                if (hoverNode != null) return getHoverInfo(hoverNode);
            }
        }
        return null;
    }
    
    public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
        return new Region(offset, 0);
    }
    
    private String getHoverInfo(ASTNode hoverNode) {
        if (hoverNode instanceof Move) {
            return "MOVE!";
        }
        else if (hoverNode instanceof Shoot) {
            return "BANG!";
        }
        else return null;
    }
    
}
