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

package org.eclipse.emf.emfatic.ui.contentassist;

import java.util.TreeSet;

import org.eclipse.emf.emfatic.core.util.EmfaticKeywords;
import org.eclipse.emf.emfatic.ui.editor.EmfaticEditor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ContextInformation;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationPresenter;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class EmfaticKeywordContentAssistProcessor implements
		IContentAssistProcessor {

	//private EmfaticEditor _editor = null;
	private TreeSet<String> proposalList = new TreeSet<String>();
	private IContextInformationValidator fValidator = new Validator();

	public EmfaticKeywordContentAssistProcessor(EmfaticEditor editor) {
//		_editor = editor;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		
		try {
			if (viewer.getDocument().getChar(offset - 1) == '@') {
				return new ICompletionProposal[] {};
			}
		} catch (BadLocationException e) {
			// do nothing
		}

		WordPartDetector wpd = new WordPartDetector(viewer, offset);
		String start = wpd.wordPart;
		proposalList = new TreeSet<String>();
		for (String kw : EmfaticKeywords.GetKeywords()) {
			if (kw.startsWith(start)) {
				proposalList.add(kw);
			}
		}
		ICompletionProposal[] res = turnProposalVectorIntoAdaptedArray(wpd);
		return res;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		// TODO Auto-generated method stub
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] {};
	}

	public char[] getContextInformationAutoActivationCharacters() {
		// TODO Auto-generated method stub
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return fValidator;
	}

	public String getErrorMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * Turns the vector into an Array of ICompletionProposal objects
	 */
	protected ICompletionProposal[] turnProposalVectorIntoAdaptedArray(
			WordPartDetector word) {
		ICompletionProposal[] result = new ICompletionProposal[proposalList
				.size()];

		int index = 0;

		for (String keyWord : proposalList) {

			IContextInformation info = new ContextInformation(keyWord,
					getContentInfoString(keyWord));
			// Creates a new completion proposal.
			result[index] = new CompletionProposal(keyWord, // replacementString
					word.getOffset(), // replacementOffset the offset of the
					// text to be replaced
					word.getLength(), // replacementLength the length of the
					// text to be replaced
					keyWord.length(), // cursorPosition the position of the
					// cursor following the insert relative
					// to replacementOffset
					null, // image to display
					keyWord, // displayString the string to be displayed for
					// the proposal
					null, // contentInformation the context information
					// associated with this proposal
					getContentInfoString(keyWord));
			index++;
		}
		proposalList.clear();
		return result;
	}

	/**
	 * Method getContentInfoString.
	 * 
	 * @param keyWord
	 */
	private String getContentInfoString(String keyWord) {
		return "";
	}

	/**
	 * Simple content assist tip closer. The tip is valid in a range of 5
	 * characters around its popup location.
	 */
	protected static class Validator implements IContextInformationValidator,
			IContextInformationPresenter {

		protected int fInstallOffset;

		/*
		 * @see IContextInformationValidator#isContextInformationValid(int)
		 */
		public boolean isContextInformationValid(int offset) {
			return Math.abs(fInstallOffset - offset) < 5;
		}

		/*
		 * @see IContextInformationValidator#install(IContextInformation,
		 *      ITextViewer, int)
		 */
		public void install(IContextInformation info, ITextViewer viewer,
				int offset) {
			fInstallOffset = offset;
		}

		/*
		 * @see org.eclipse.jface.text.contentassist.IContextInformationPresenter#updatePresentation(int,
		 *      TextPresentation)
		 */
		public boolean updatePresentation(int documentPosition,
				TextPresentation presentation) {
			return false;
		}
	};

}
