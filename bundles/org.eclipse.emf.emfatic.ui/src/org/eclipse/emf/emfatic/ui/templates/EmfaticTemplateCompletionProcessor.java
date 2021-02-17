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

package org.eclipse.emf.emfatic.ui.templates;

import java.util.ArrayList;
import java.util.Collections;

import org.eclipse.emf.emfatic.ui.EmfaticUIPlugin;
import org.eclipse.jdt.ui.text.java.CompletionProposalComparator;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateCompletionProcessor;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.swt.graphics.Image;

/**
 * 
 * @author miguel.garcia@tuhh.de
 */
public class EmfaticTemplateCompletionProcessor extends
		TemplateCompletionProcessor {

	TemplateContextType context;

	/**
	 * 
	 */
	public EmfaticTemplateCompletionProcessor(String contextName) {
		super();
		this.context = EmfaticUIPlugin.getDefault()
				.getEmfaticContextTypeRegistry().getContextType(contextName);
	}

	/**
	 * Returns the templates valid for the context type specified by
	 * <code>contextTypeId</code>. This implementation always returns the
	 * shared TemplateStore.
	 * 
	 * @param contextTypeId
	 *            the context type id
	 * @return the templates valid for this context type id
	 */
	protected Template[] getTemplates(String contextTypeId) {
		return EmfaticUIPlugin.getDefault().getEmfaticTemplateStore()
				.getTemplates();
	}

	/**
	 * Returns the context type that can handle template insertion at the given
	 * region in the viewer's document.
	 * 
	 * @param viewer
	 *            the text viewer
	 * @param region
	 *            the region into the document displayed by viewer
	 * @return the context type that can handle template expansion for the given
	 *         location, or <code>null</code> if none exists
	 */
	protected TemplateContextType getContextType(ITextViewer viewer,
			IRegion region) {
		// return EmfaticUIPlugin.getDefault().getTexContextTypeRegistry()
		// .getContextType(EmfaticContextType.EMFATIC_CONTEXT_TYPE);
		return context;
	}

	/**
	 * Returns an image for the given template. This implementation always
	 * returns the same icon (called "template").
	 * 
	 * @param template
	 *            template
	 * @return image for the given template
	 */
	protected Image getImage(Template template) {
		return EmfaticUIPlugin.getImage("template");
	}

	/**
	 * Adds all available templates to the given list, available meaning here
	 * that the templates match the found prefix.
	 * 
	 * @param viewer
	 *            The viewer associated with this editor
	 * @param documentOffset
	 *            The offset in the document where the completions hould take
	 *            place
	 * @param prefix
	 *            The prefix of the completion string
	 * @return An <code>ArrayList</code> containing the
	 *         <code>ICompletionProposals</code>
	 */
	public ArrayList<ICompletionProposal> addTemplateProposals(
			ITextViewer viewer, int documentOffset, String prefix) {

		ArrayList<ICompletionProposal> propList = new ArrayList<ICompletionProposal>();

		ICompletionProposal[] templateProposals = computeCompletionProposals(
				viewer, documentOffset);

		for (int j = 0; j < templateProposals.length; j++) {
			ICompletionProposal proposal = templateProposals[j];
			if (proposal.getDisplayString().startsWith(prefix)) {
				propList.add(templateProposals[j]);
			}
		}
		Collections.sort(propList, new CompletionProposalComparator());

		return propList;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return new char[] { '@' };
	}

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer,
			int offset) {
		try {
			if (viewer.getDocument().getChar(offset - 1) == '@') {
				ICompletionProposal[] res = super.computeCompletionProposals(
						viewer, offset);
				return res;
			}
		} catch (BadLocationException e) {
		}
		return new ICompletionProposal[] {};
	}
}
