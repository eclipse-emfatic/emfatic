/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.redsquiggles;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.emfatic.core.lang.gen.ast.CompUnit;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNodeVisitor;
import org.eclipse.emf.emfatic.core.lang.gen.ast.MapEntryDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TopLevelDecl;
import org.eclipse.emf.emfatic.ui.editor.EmfaticEditor;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.ui.views.parsetree.IParseTreeChangedListener;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.ui.texteditor.IDocumentProvider;

public class EmfaticCSTChangeListener implements IParseTreeChangedListener {

	EmfaticEditor _editor;
	public static QualifiedName qnCST = new QualifiedName("de.tuhh.sts.emfatic", "cst");
	public static QualifiedName qnAST = new QualifiedName("de.tuhh.sts.emfatic", "ast");

	public EmfaticCSTChangeListener(EmfaticEditor editor) {
		_editor = editor;
	}

	/**
	 * responsible for folding
	 */
	public void parseTreeChanged(ASTNode[] arg0) {
		/*
		 * TODO arg0 is always null because LDTEditor#setParseRoot gives just
		 * that instead of its _parseRoot
		 */

		/*
		 * see the resource listener EmfaticRedSquiggler for an alternative way
		 * of listening
		 */

		IFile emfFile = _editor.getFile();
		CompUnit compUnit = (CompUnit) _editor.getParseRoot();
		try {
			if (emfFile != null) {
				emfFile.setSessionProperty(qnCST, compUnit);
				emfFile.setSessionProperty(qnAST, null);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		if (compUnit != null) {
			// the ast was set in EmfaticParserDriver#parse()
			EPackage rootPackage = compUnit.getAST();
			try {
				emfFile.setSessionProperty(qnAST, rootPackage);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}

		updateFolding();
		
		return;
	}

	private List<Position> fPositions = new ArrayList<Position>();

	private void updateFolding() {
		fPositions = new ArrayList<Position>();
		CompUnit compUnit = (CompUnit) _editor.getParseRoot();

		if (compUnit == null) {
			return;
		}

		new EmfaticASTNodeVisitor() {
			public boolean beginVisit(TopLevelDecl tld) {
				if (!(tld instanceof MapEntryDecl)) {
					int offset = tld.getRangeStart();
					int length = tld.getRangeLength();
					if (spansSeveralLines(offset, length)) {
						fPositions.add(new Position(offset, length));
					}
				}
				return true;
			}
			/*
			 * comments (including multiline ones) are eaten up by the lexer,
			 * there are no productions in the grammar for comments, and thus
			 * cannot be folded. Once that changes, this is the place to add a
			 * visitor handler which creates a Position to indicate that region
			 * can be folded. 
			 */
		}.visit(compUnit.getTopLevelDecls());

		_editor.updateFoldingStructure(fPositions);

	}

	private boolean spansSeveralLines(final int offset, final int length) {
		IDocumentProvider dp = _editor.getDocumentProvider();
		IDocument doc = dp.getDocument(_editor.getEditorInput());
		if (doc != null) {
			try {
				String[] lines = doc.get(offset, length).split("\n");
				boolean res = lines.length > 2;
				return res;
			} catch (BadLocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

}
