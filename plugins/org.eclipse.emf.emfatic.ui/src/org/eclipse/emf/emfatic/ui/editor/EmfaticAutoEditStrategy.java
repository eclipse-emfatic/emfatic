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

package org.eclipse.emf.emfatic.ui.editor;

import org.eclipse.emf.emfatic.ui.EmfaticUIPlugin;
import org.eclipse.emf.emfatic.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentCommand;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;

public class EmfaticAutoEditStrategy implements IAutoEditStrategy {

	EmfaticEditor _editor = null;
	private IPreferenceStore preferenceStore;

	public EmfaticAutoEditStrategy(EmfaticEditor editor) {
		_editor = editor;
		preferenceStore = EmfaticUIPlugin.getDefault().getPreferenceStore();
	}

	public void customizeDocumentCommand(IDocument d, DocumentCommand c) {
		if (c.text == null) {
			return;
		}
		if (c.text.endsWith("{")) {
			smartBrace(d, c);
		} else if (c.text.endsWith("\"")) {
			insert(c, "", "\""); 
		} else if (c.text.endsWith("[")) {
			c.text = c.text + "*]";
		}
	}

	/**
	 * @see DefaultIndentLineAutoEditStrategy
	 */
	private void smartBrace(IDocument d, DocumentCommand c) {
		if (!preferenceStore.getBoolean(PreferenceConstants.P_BOOLEAN)) {
			return;
		}
		if (c.offset == -1 || d.getLength() == 0) {
			return;
		}
		String computedIndent = computeIndent(d, c);
		String beforeCaret = "\r\n" + computedIndent + "\t";
		String afterCaret = "\r\n" + computedIndent + "}"; 
		insert(c, beforeCaret, afterCaret);
	}
	
	private void insert (DocumentCommand c, String beforeCaret, String afterCaret) {
		StringBuffer buf = new StringBuffer(c.text);
		buf.append(beforeCaret);
		c.caretOffset = c.offset + beforeCaret.toCharArray().length + 1;
		c.shiftsCaret = false;
		buf.append(afterCaret);
		c.length = 0;
		c.text = buf.toString();
	}

	private String computeIndent(IDocument d, DocumentCommand c) {
		String res = "";
		try {
			int p = (c.offset == d.getLength() ? c.offset - 1 : c.offset);
			IRegion info = d.getLineInformationOfOffset(p);
			int start = info.getOffset();

			// find white spaces
			int end = findEndOfWhiteSpace(d, start, c.offset);

			if (end > start) {
				res = d.get(start, end - start);
			}
		} catch (BadLocationException excp) {
			// stop work
		}
		return res;
	}

	/**
	 * Returns the first offset greater than <code>offset</code> and smaller
	 * than <code>end</code> whose character is not a space or tab character.
	 * If no such offset is found, <code>end</code> is returned.
	 * 
	 * @param document
	 *            the document to search in
	 * @param offset
	 *            the offset at which searching start
	 * @param end
	 *            the offset at which searching stops
	 * @return the offset in the specified range whose character is not a space
	 *         or tab
	 * @exception BadLocationException
	 *                if position is an invalid range in the given document
	 */
	protected int findEndOfWhiteSpace(IDocument document, int offset, int end) throws BadLocationException {
		while (offset < end) {
			char c = document.getChar(offset);
			if (c != ' ' && c != '\t') {
				return offset;
			}
			offset++;
		}
		return end;
	}

}
