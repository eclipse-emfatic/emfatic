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

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.emfatic.core.generator.ecore.TokenText;
import org.eclipse.emf.emfatic.core.generator.emfatic.Writer;
import org.eclipse.emf.emfatic.core.lang.gen.ast.AbstractModifier;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ClassKind;
import org.eclipse.emf.emfatic.core.lang.gen.ast.CompUnit;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNode;
import org.eclipse.emf.emfatic.core.lang.gen.ast.OptNegatedModifier;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ReferenceKind;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TransientModifier;
import org.eclipse.emf.emfatic.core.util.EmfaticKeywords;
import org.eclipse.emf.emfatic.ui.editor.EmfaticEditor.ReferedEcoreDecl;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

public class EmfaticTextHover implements ITextHover {

	EmfaticTextHover(EmfaticEditor editor) {
		_editor = editor;
		_hoverClasses = (new Class[] { OptNegatedModifier.class,
				AbstractModifier.class,
				ClassKind.class,
				TransientModifier.class,
				ReferenceKind.class });
	}

	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if (hoverRegion != null) {
			ASTNode rootNode = _editor.getParseRoot();
			if (rootNode != null) {
				/*
				 * hovers for modifiers ASTNode hoverNode =
				 * rootNode.getNodeAt(hoverRegion.getOffset(),
				 * hoverRegion.getLength(), _hoverClasses, true); if (hoverNode !=
				 * null) { return getHoverInfo(hoverNode); }
				 */
				CompUnit compUnit = (CompUnit) rootNode;
				String msg = tryTypeHover(compUnit, hoverRegion.getOffset());
				if (msg != null)
					return msg;
			}
		}
		return null;
	}

	private String tryTypeHover(CompUnit compUnit, int offset) {
		ReferedEcoreDecl red = _editor.getReferedEcoreDecl(offset, 0);
		if (red == null || red.node == null || red.ecoreDecl == null) {
			return null;
		}
		EObject ecoreDecl = red.ecoreDecl;
		String msg = Writer.stringify(ecoreDecl);
		return msg;
	}

	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return new Region(offset, 0);
	}

	private String getHoverInfo(ASTNode hoverNode) {
		if (hoverNode instanceof OptNegatedModifier) {
			OptNegatedModifier node = (OptNegatedModifier) hoverNode;
			String modifierText = node.getModifier().getText();
			String hoverText = EmfaticKeywords.GetHoverText(modifierText);
			return composeHoverText(hoverText, node.getBang() != null, hoverNode);
		}
		if (hoverNode instanceof AbstractModifier) {
			AbstractModifier node = (AbstractModifier) hoverNode;
			String hoverText = EmfaticKeywords.GetHoverText(node.getAbstract_KW().getText());
			return composeHoverText(hoverText, false, hoverNode);
		}
		if (hoverNode instanceof ClassKind) {
			String hoverText = EmfaticKeywords.GetHoverText(hoverNode.getText());
			return composeHoverText(hoverText, false, hoverNode);
		}
		if (hoverNode instanceof TransientModifier) {
			String hoverText = "EDataType.isSerializable() == <F>";
			return composeHoverText(hoverText, false, hoverNode);
		}
		if (hoverNode instanceof ReferenceKind) {
			String hoverText = EmfaticKeywords.GetHoverText(hoverNode.getText());
			return composeHoverText(hoverText, false, hoverNode);
		} else {
			return null;
		}
	}

	private String composeHoverText(String hoverText, boolean isNegated, ASTNode hoverNode) {
		if (hoverText == null)
			return null;
		String composedText = null;
		if (hoverText.indexOf("<T>") != -1) {
			if (!isNegated)
				composedText = hoverText.replaceFirst("<T>", "true");
			else
				composedText = hoverText.replaceFirst("<T>", "false");
		} else if (hoverText.indexOf("<F>") != -1)
			if (!isNegated)
				composedText = hoverText.replaceFirst("<F>", "false");
			else
				composedText = hoverText.replaceFirst("<F>", "true");
		if (composedText != null) {
			String tokenText = TokenText.Get((EmfaticASTNode) hoverNode);
			return "'" + tokenText + "' means " + composedText;
		} else {
			return null;
		}
	}

	private final EmfaticEditor _editor;
	private final Class _hoverClasses[];
}
