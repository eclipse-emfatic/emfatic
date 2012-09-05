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

package org.eclipse.emf.emfatic.ui.partition;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.jface.text.rules.FastPartitioner;
import org.eclipse.jface.text.rules.IPartitionTokenScanner;

/**
 * Simple extension of DefaultPartitioner with printPartitions() method to assist with printing out partition
 * information
 * 
 */
public class DebugPartitioner extends FastPartitioner
{

	public DebugPartitioner(IPartitionTokenScanner scanner, String[] legalContentTypes)
	{
		super(scanner, legalContentTypes);
	}

	@Override
	public ITypedRegion[] computePartitioning(int offset, int length, boolean includeZeroLengthPartitions)
	{
		return super.computePartitioning(offset, length, includeZeroLengthPartitions);
	}

	@Override
	public void connect(IDocument document, boolean delayInitialization)
	{
		super.connect(document, delayInitialization);
		printPartitions(document);
	}

	public void printPartitions(IDocument document)
	{
		StringBuffer buffer = new StringBuffer();

		ITypedRegion[] partitions = computePartitioning(0, document.getLength());
		for (int i = 0; i < partitions.length; i++)
		{
			try
			{
				String text = document.get(partitions[i].getOffset(), partitions[i].getLength());
				String[] lines = text.split("\n");
				String newText = ""; 
				for (String line : lines) {
					if (!line.trim().equals("")) {
						newText += line + "\n";
					}
				}
				if (!newText.trim().equals("")) {
					buffer.append("Partition type: " + partitions[i].getType() + ", offset: " + partitions[i].getOffset()
							+ ", length: " + partitions[i].getLength() + "\n");
					buffer.append(newText);
					buffer.append("---------------------------\n");
				}
			}
			catch (BadLocationException e)
			{
				//e.printStackTrace();
			}
		}
	}
}