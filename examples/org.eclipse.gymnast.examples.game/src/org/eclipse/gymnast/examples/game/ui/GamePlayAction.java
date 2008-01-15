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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.gymnast.examples.game.lang.gen.ast.CommandList;
import org.eclipse.gymnast.examples.game.lang.gen.ast.GameASTNodeVisitor;
import org.eclipse.gymnast.examples.game.lang.gen.ast.Move;
import org.eclipse.gymnast.examples.game.lang.gen.ast.OptReps;
import org.eclipse.gymnast.examples.game.lang.gen.ast.Shoot;
import org.eclipse.gymnast.examples.game.lang.gen.parser.GameParserDriver;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class GamePlayAction implements IObjectActionDelegate {
    
    private IFile _file;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		_file = null;
		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection)selection;
			Object selElem = sel.getFirstElement();
			if (selElem instanceof IFile) {
				_file = (IFile)selElem;
			}
		}
	}
	
	public void run(IAction action) {
	    if (_file != null) {
	        try {
	    		BufferedReader reader = new BufferedReader(new InputStreamReader(_file.getContents()));
	    		GameParserDriver parser = new GameParserDriver();
	    		ParseContext parseContext = parser.parse(reader);
	    		
	    		if (parseContext.hasErrors()) {
	    		    System.out.println("Error in parse!");
	    		}
	    		else {
	    		    ASTNode root = parseContext.getParseRoot();
	    		    if (root instanceof CommandList) {
	    		        play((CommandList)root);
	    		    }
	    		}
	        }
	        catch (Exception ex) {
	            ex.printStackTrace();
	        }
	    }
	}
	
	private void play(CommandList commandList) {
	    new GameASTNodeVisitor() {
	        
	        public boolean beginVisit(Move move) {
	            String text = move.getDirection().getText().toUpperCase();
	            int reps = getReps(move.getOptReps());
	            println(text, reps);
                return false;
            }
	        public boolean beginVisit(Shoot shoot) {
	            String text = shoot.getFire_KW().getText().toUpperCase();
	            int reps = getReps(shoot.getOptReps());
	            println(text, reps);
                return false;
            }
	        
	    }.visit(commandList);
	}
	
	private int getReps(OptReps optReps) {
	    if (optReps.getReps() == null) return 1;
	    String reps = optReps.getReps().getText();
	    return Integer.parseInt(reps);
	}
	
	private void println(String text, int times) {
	    for (int i = 0; i < times; i++) {
	        System.out.print(text + " ");
	    }
	    System.out.println();
	}
    
}
