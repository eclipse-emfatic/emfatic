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

import org.eclipse.gymnast.examples.game.lang.gen.ast.CommandList;
import org.eclipse.gymnast.examples.game.lang.gen.ast.GameASTNodeVisitor;
import org.eclipse.gymnast.examples.game.lang.gen.ast.Move;
import org.eclipse.gymnast.examples.game.lang.gen.ast.Shoot;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.outline.IOutlineBuilder;
import org.eclipse.gymnast.runtime.core.outline.OutlineNode;

public class GameOutlineBuilder implements IOutlineBuilder {
    
    public OutlineNode[] buildOutline(ASTNode parseRoot) {
        
        if (parseRoot instanceof CommandList) {
            CommandList commandList = (CommandList)parseRoot;
            
            final OutlineNode root = new OutlineNode(commandList, "Commands");
            
            new GameASTNodeVisitor() {
                public boolean beginVisit(Move move) {
					if (move.getDirection() == null) {
						OutlineNode n = new OutlineNode(move, "move ?");
	                    root.addChild(n);
					}
					else {
	                    String text = move.getDirection().getText();
	                    if (move.getOptReps().getReps() != null) {
	                        text += " (x" + move.getOptReps().getReps().getText() + ")";
	                    }
	                    OutlineNode n = new OutlineNode(move, text);
	                    root.addChild(n);
					}
                    return false;
                }
                public boolean beginVisit(Shoot shoot) {
                    String text = shoot.getFire_KW().getText();
                    if (shoot.getOptReps().getReps() != null) {
                        text += " (x" + shoot.getOptReps().getReps().getText() + ")";
                    }
                    OutlineNode n = new OutlineNode(shoot, text);
                    root.addChild(n);
                    return false;
                }
            }.visit(commandList);
            
            return new OutlineNode[] {root};
        }
        
        return null;
    }
}
