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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.emfatic.core.lang.gen.parser.EmfaticParserDriver;
import org.eclipse.emf.emfatic.ui.contentassist.CascadedContentAssistProcessor;
import org.eclipse.emf.emfatic.ui.contentassist.EmfaticContentAssistProcessor;
import org.eclipse.emf.emfatic.ui.contentassist.EmfaticKeywordContentAssistProcessor;
import org.eclipse.emf.emfatic.ui.hyperlinks.EmfaticHyperlinkDetector;
import org.eclipse.emf.emfatic.ui.partition.EmfaticPartitionScanner;
import org.eclipse.emf.emfatic.ui.templates.EmfaticContextType;
import org.eclipse.emf.emfatic.ui.templates.EmfaticTemplateCompletionProcessor;
import org.eclipse.gymnast.runtime.core.parser.IParser;
import org.eclipse.gymnast.runtime.ui.editor.LDTCodeScanner;
import org.eclipse.gymnast.runtime.ui.editor.LDTSourceViewerConfiguration;
import org.eclipse.jface.text.DefaultIndentLineAutoEditStrategy;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.presentation.PresentationReconciler;
import org.eclipse.jface.text.rules.DefaultDamagerRepairer;
import org.eclipse.jface.text.source.DefaultAnnotationHover;
import org.eclipse.jface.text.source.IAnnotationHover;
import org.eclipse.jface.text.source.ISourceViewer;


public class EmfaticSourceViewerConfiguration extends
		LDTSourceViewerConfiguration {

	EmfaticCodeScanner _emfaticCodeScanner = null;
	EmfaticEditor _editor = null;

	public EmfaticSourceViewerConfiguration(EmfaticEditor editor) {
		super(editor);
		_editor = editor;
		_textHover = new EmfaticTextHover(editor);
	}

	public LDTCodeScanner createCodeScanner() {
		if (_emfaticCodeScanner == null) {
			_emfaticCodeScanner = new EmfaticCodeScanner();
		}
		return _emfaticCodeScanner;
	}

	public IParser getParser() {
		return new EmfaticParserDriver(URI.createPlatformResourceURI(_editor.getFile().getFullPath().toPortableString(), true));
	}

	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType) {
		return _textHover;
	}

	public ITextHover getTextHover(ISourceViewer sourceViewer,
			String contentType, int stateMask) {
		return _textHover;
	}

	private final EmfaticTextHover _textHover;

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		return EmfaticPartitionScanner.contentTypes();
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer arg0) {
		PresentationReconciler reconciler = new PresentationReconciler();
		DefaultDamagerRepairer dr = new DefaultDamagerRepairer(createCodeScanner());

		// i.e., IDocument.DEFAULT_CONTENT_TYPE
		reconciler.setDamager(dr, "__dftl_partition_content_type");
		reconciler.setRepairer(dr, "__dftl_partition_content_type");

		setDR(reconciler, dr, EmfaticPartitionScanner.packagePart);
		setDR(reconciler, dr, EmfaticPartitionScanner.importPart);
		setDR(reconciler, dr, EmfaticPartitionScanner.annotationPart);
		setDR(reconciler, dr, EmfaticPartitionScanner.subPackagePart);
		setDR(reconciler, dr, EmfaticPartitionScanner.attrPart);
		setDR(reconciler, dr, EmfaticPartitionScanner.refPart);
		setDR(reconciler, dr, EmfaticPartitionScanner.valrPart);
		setDR(reconciler, dr, EmfaticPartitionScanner.opPart);
		setDR(reconciler, dr, EmfaticPartitionScanner.datatypePart);
		setDR(reconciler, dr, EmfaticPartitionScanner.enumPart);
		setDR(reconciler, dr, EmfaticPartitionScanner.mapentryPart);
		setDR(reconciler, dr, EmfaticPartitionScanner.classHeadingPart);
		setDR(reconciler, dr, EmfaticPartitionScanner.ifaceHeadingPart);
		setDR(reconciler, dr, EmfaticPartitionScanner.singleLineComment);
		
		DefaultDamagerRepairer cr = new DefaultDamagerRepairer(
				createCodeScanner()) {
			@Override
			public IRegion getDamageRegion(ITypedRegion partition, DocumentEvent e,
					boolean documentPartitioningChanged) {
				return partition;
			}
		};
		
		setDR(reconciler, cr, EmfaticPartitionScanner.multiLineComment);
		
		
		return reconciler;
	}

	private void setDR(PresentationReconciler reconciler,
			DefaultDamagerRepairer dr, String docPart) {
		reconciler.setDamager(dr, docPart);
		reconciler.setRepairer(dr, docPart);

	}

	@Override
	public IAnnotationHover getAnnotationHover(ISourceViewer sourceViewer) {
		if (_annotationHover == null) {
			// TODO use HTMLAnnotationHover instead
			// TODO use ProjectionAnnotationHover instead, so that summarizing annotations are shown
			_annotationHover = new DefaultAnnotationHover();
		}
		return _annotationHover;
	}

	DefaultAnnotationHover _annotationHover = null;

	/*
	 * hyperlinks
	 * 
	 * http://orangevolt.com/wordpress/archives/2005/01/05/howto-enable-hyperlinking-in-text-editor/
	 * 
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getHyperlinkDetectors(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public IHyperlinkDetector[] getHyperlinkDetectors(ISourceViewer sourceViewer) {
		IHyperlinkDetector[] inheritedDetectors = super
				.getHyperlinkDetectors(sourceViewer);

		if (_editor == null) {
			return inheritedDetectors;
		}

		int inheritedDetectorsLength = inheritedDetectors != null ? inheritedDetectors.length
				: 0;
		IHyperlinkDetector[] detectors = new IHyperlinkDetector[inheritedDetectorsLength + 1];
		detectors[0] = new EmfaticHyperlinkDetector(_editor);
		for (int i = 0; i < inheritedDetectorsLength; i++) {
			detectors[i + 1] = inheritedDetectors[i];
		}

		return detectors;
	}

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(
			ISourceViewer sourceViewer, String contentType) {
		List<IAutoEditStrategy> s = new ArrayList<IAutoEditStrategy>();
		IAutoEditStrategy[] vonOben = super.getAutoEditStrategies(sourceViewer,
				contentType);
		for (IAutoEditStrategy autoEditStrategy : vonOben) {
			s.add(autoEditStrategy);
		}
		s.add(new DefaultIndentLineAutoEditStrategy());
		s.add(new EmfaticAutoEditStrategy(_editor));
		// TODO add partition type-specific auto edit strategies
		IAutoEditStrategy[] res = s.toArray(new IAutoEditStrategy[0]);
		return res;
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		ContentAssistant ca = new ContentAssistant();
		ca.enableAutoActivation(true);
		ca.setAutoActivationDelay(500);
		ca.setProposalPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		ca
				.setContextInformationPopupOrientation(IContentAssistant.CONTEXT_INFO_BELOW);
		ca
				.setInformationControlCreator(getInformationControlCreator(sourceViewer));

		CascadedContentAssistProcessor masterCap = new CascadedContentAssistProcessor();
		
		IContentAssistProcessor cap = new EmfaticTemplateCompletionProcessor(
				EmfaticContextType.EMFATIC_CONTEXT_TYPE);
		masterCap.add(cap);
		registerForAllEmfaticContentTypes(ca, masterCap);

		cap = new EmfaticContentAssistProcessor(_editor);
		masterCap.add(cap);
		
		cap = new EmfaticKeywordContentAssistProcessor(_editor);
		masterCap.add(cap);
		
		return ca;
	}

	private void registerForAllEmfaticContentTypes(ContentAssistant ca,
			IContentAssistProcessor cap) {
		ca.setContentAssistProcessor(cap, IDocument.DEFAULT_CONTENT_TYPE);

		ca.setContentAssistProcessor(cap, EmfaticPartitionScanner.packagePart);
		ca.setContentAssistProcessor(cap, EmfaticPartitionScanner.importPart);
		ca.setContentAssistProcessor(cap,
				EmfaticPartitionScanner.annotationPart);
		ca.setContentAssistProcessor(cap,
				EmfaticPartitionScanner.subPackagePart);
		ca.setContentAssistProcessor(cap, EmfaticPartitionScanner.attrPart);
		ca.setContentAssistProcessor(cap, EmfaticPartitionScanner.refPart);
		ca.setContentAssistProcessor(cap, EmfaticPartitionScanner.valrPart);
		ca.setContentAssistProcessor(cap, EmfaticPartitionScanner.opPart);
		ca.setContentAssistProcessor(cap, EmfaticPartitionScanner.datatypePart);
		ca.setContentAssistProcessor(cap, EmfaticPartitionScanner.enumPart);
		ca.setContentAssistProcessor(cap, EmfaticPartitionScanner.mapentryPart);
		ca.setContentAssistProcessor(cap,
				EmfaticPartitionScanner.classHeadingPart);
		ca.setContentAssistProcessor(cap,
				EmfaticPartitionScanner.ifaceHeadingPart);
		ca.setContentAssistProcessor(cap,
				EmfaticPartitionScanner.multiLineComment);
		ca.setContentAssistProcessor(cap,
				EmfaticPartitionScanner.singleLineComment);

	}
	
	@Override
	public ITextDoubleClickStrategy getDoubleClickStrategy(
			ISourceViewer sourceViewer, String contentType) {
		// TODO Auto-generated method stub
		return super.getDoubleClickStrategy(sourceViewer, contentType);
	}
	
	@Override
	public IAnnotationHover getOverviewRulerAnnotationHover(
			ISourceViewer sourceViewer) {
		// TODO Auto-generated method stub
		return super.getOverviewRulerAnnotationHover(sourceViewer);
	}

}