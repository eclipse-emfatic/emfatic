/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.runtime.ui.editor;

import java.io.StringReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.gymnast.runtime.core.parser.IParser;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;
import org.eclipse.gymnast.runtime.core.util.MarkerUtil;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class LDTReconcilingStrategy implements IReconcilingStrategy {
	
	private LDTEditor _editor;
	private IDocument _document;
	
	public LDTReconcilingStrategy(LDTEditor editor) {
		_editor = editor;
	}

	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		updateEditorParseRoot();
	}

	public void reconcile(IRegion partition) {
		updateEditorParseRoot();
	}

	public void setDocument(IDocument document) {
		_document = document;
		updateEditorParseRoot();
	}
	
	private void updateEditorParseRoot() {
		try {
			ParseContext parseContext = parse();
			if (parseContext != null) {
				_editor.setParseRoot(parseContext.getParseRoot());
			}
			else {
				_editor.setParseRoot(null);
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
			_editor.setParseRoot(null);
		}
	}
	
	private ParseContext parse() {
		
		IFile file = _editor.getFile();
		
		if (file == null) {
			return null;
		}

		MarkerUtil.clearMarkers(file);
		
		String input = _document.get();
		StringReader reader = new StringReader(input);
		
		IParser parser = _editor.getParser();
		ParseContext parseContext = parser.parse(reader);
		
		MarkerUtil.placeMarkers(file, parseContext);
		
		return parseContext;
	}
	
}
