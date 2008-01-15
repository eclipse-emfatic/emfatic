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

import java.util.Comparator;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class ProposalsComparator implements Comparator<ICompletionProposal> {
    
    /**
     * Compares two ICompletionProposals according to their display Strings
     * 
     * @return same as String.compareToIgnoreCase()
     */
    public int compare(ICompletionProposal p1, ICompletionProposal p2) {
        return p1.getDisplayString().compareToIgnoreCase(p2.getDisplayString());
    }
}
