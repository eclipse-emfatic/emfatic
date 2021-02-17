/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.runtime.ui.editor;

import org.eclipse.gymnast.runtime.core.parser.IParser;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.MonoReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public abstract class LDTSourceViewerConfiguration extends SourceViewerConfiguration {
	
	private LDTEditor _editor;
	
	private MonoReconciler _reconciler;
	private LDTReconcilingStrategy _reconcilingStrategy;
	private LDTCodeScanner _codeScanner;

	public LDTSourceViewerConfiguration(LDTEditor editor) {
		_editor = editor;
		
		_reconcilingStrategy = new LDTReconcilingStrategy(_editor);
		_reconciler = new MonoReconciler(_reconcilingStrategy, false);
		_codeScanner = createCodeScanner();
	}
	
	public LDTCodeScanner createCodeScanner() {
		return null;
	}
	
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {

		if (_codeScanner == null) return super.getPresentationReconciler(sourceViewer);
		
		PresentationReconciler reconciler= new PresentationReconciler();
	
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(_codeScanner);
		reconciler.setDamager(dr, IDocument.DEFAULT_CONTENT_TYPE);
		reconciler.setRepairer(dr, IDocument.DEFAULT_CONTENT_TYPE);

		return reconciler;
	}

	public IReconciler getReconciler(ISourceViewer sourceViewer) {
		return _reconciler;
	}
	
	public abstract IParser getParser();
	
}
