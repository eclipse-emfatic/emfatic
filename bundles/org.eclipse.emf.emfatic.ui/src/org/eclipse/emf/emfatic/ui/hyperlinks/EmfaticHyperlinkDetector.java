/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.hyperlinks;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Attribute;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ClassDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.DataTypeDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNode;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EnumDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Operation;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Reference;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TypeParam;
import org.eclipse.emf.emfatic.ui.editor.EmfaticEditor;
import org.eclipse.emf.emfatic.ui.editor.EmfaticEditor.ReferedEcoreDecl;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;

public class EmfaticHyperlinkDetector implements IHyperlinkDetector {

	private EmfaticEditor _editor;

	public EmfaticHyperlinkDetector(EmfaticEditor editor) {
		_editor = editor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.hyperlink.IHyperlinkDetector#detectHyperlinks(org.eclipse.jface.text.ITextViewer,
	 *      org.eclipse.jface.text.IRegion, boolean)
	 */
	public IHyperlink[] detectHyperlinks(ITextViewer textViewer, IRegion region, boolean canShowMultipleHyperlinks) {
		if (region == null) {
			return null;
		}
		ReferedEcoreDecl red = _editor.getReferedEcoreDecl(region.getOffset(), region.getLength());
		if (red == null || red.node == null || red.ecoreDecl == null) {
			return null; 
		}
		EObject ecoreDecl = red.ecoreDecl;
		EmfaticASTNode node = red.node;
		EmfaticASTNode landingPlace = getLandingPlace(ecoreDecl, _editor);
		if (landingPlace == null) {
			return null; 
		}
		IRegion underlineRegion = new org.eclipse.jface.text.Region(node.getRangeStart(), node.getRangeLength());
		return new IHyperlink[] { new EmfaticHyperlink(_editor, underlineRegion, landingPlace) };
	}

	public static EmfaticASTNode getLandingPlace(EObject ecoreDecl, EmfaticEditor editor) {
		ASTNode cstDecl = editor.getCstDecl2EcoreAST().getInv(ecoreDecl);
		if (cstDecl == null || !(cstDecl instanceof EmfaticASTNode)) {
			return null;
		}
		EmfaticASTNode landingPlace = (EmfaticASTNode) cstDecl;
		if (cstDecl instanceof ClassDecl) {
			landingPlace = ((ClassDecl) cstDecl).getName();
		}
		if (cstDecl instanceof DataTypeDecl) {
			landingPlace = ((DataTypeDecl) cstDecl).getName();
		}
		if (cstDecl instanceof EnumDecl) {
			landingPlace = ((EnumDecl) cstDecl).getName();
		}
		if (cstDecl instanceof TypeParam) {
			landingPlace = ((TypeParam) cstDecl).getTypeVarName();
		}
		if (cstDecl instanceof Reference) {
			landingPlace = ((Reference) cstDecl).getName();
		}
		if (cstDecl instanceof Attribute) {
			landingPlace = ((Attribute) cstDecl).getName();
		}
		if (cstDecl instanceof Operation) {
			landingPlace = ((Operation) cstDecl).getName();
		}
		return landingPlace;
	}

}
