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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNode;
import org.eclipse.emf.emfatic.ui.editor.EmfaticEditor;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

public class EmfaticContentAssistProcessor implements IContentAssistProcessor {

	private EmfaticEditor _editor = null;
	private ProposalsComparator proposalsComparator = new ProposalsComparator();

	public EmfaticContentAssistProcessor(EmfaticEditor editor) {
		_editor = editor;
	}

	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {

		ASTNode nodeAtCursor = _editor.getNodeAtCursor();
		if (nodeAtCursor == null) {
			return null;   
		}
		ASTNode node = _editor.getClosestEnclosingASTNodeAt(nodeAtCursor.getRangeStart(), EmfaticASTNode.class); 
		String word = node.getText();

		List<ICompletionProposal> returnProposals = new ArrayList<ICompletionProposal>();
		
		ICompletionProposal lonely = new CompletionProposal(	
				word,					//replacementString
				node.getRangeStart(), 	//replacementOffset the offset of the text to be replaced
				node.getRangeLength(),	//replacementLength the length of the text to be replaced
				word.length(),	//cursorPosition the position of the cursor following the insert relative to replacementOffset
				null, 			//image to display
				word, 			//displayString the string to be displayed for the proposal
				null,			//contentInformation the context information associated with this proposal
				"" );			//additional proposal info
		returnProposals.add(lonely);
		
		ICompletionProposal[] proposals = new ICompletionProposal[returnProposals
				.size()];
		returnProposals.toArray(proposals);

		Arrays.sort(proposals, proposalsComparator);
		return proposals;
	}

	public IContextInformation[] computeContextInformation(ITextViewer viewer,
			int offset) {
		return null;
	}

	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] {  };
	}

	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

	public String getErrorMessage() {
		return null;
	}

}
