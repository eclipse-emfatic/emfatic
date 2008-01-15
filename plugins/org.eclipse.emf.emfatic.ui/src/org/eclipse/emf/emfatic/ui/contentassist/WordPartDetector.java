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

package org.eclipse.emf.emfatic.ui.contentassist;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;

public class WordPartDetector {
	String wordPart = "";
	int docOffset;

	/**
	 * Method WordPartDetector.
	 * 
	 * @param viewer
	 *            is a text viewer
	 * @param documentOffset
	 *            into the SQL document
	 */
	public WordPartDetector(ITextViewer viewer, int documentOffset) {
		docOffset = documentOffset - 1;
		try {
			// find the word that must be finished
			while (docOffset >= viewer.getTopIndexStartOffset()
					&& Character.isLetterOrDigit(viewer.getDocument().getChar(
							docOffset))) {
				docOffset--;
			}
			// we've been one step too far : increase the offset
			docOffset++;
			wordPart = viewer.getDocument().get(docOffset,
					documentOffset - docOffset);
		} catch (BadLocationException e) {
			// do nothing
		}
	}

	/**
	 * Method getString.
	 * 
	 * @return String
	 */
	public String getString() {
		return wordPart;
	}

	public int getLength() {
		return wordPart.length();
	}

	public int getOffset() {
		return docOffset;
	}

}
