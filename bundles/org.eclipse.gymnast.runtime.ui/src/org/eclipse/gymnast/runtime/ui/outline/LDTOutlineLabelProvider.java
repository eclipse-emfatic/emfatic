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

package org.eclipse.gymnast.runtime.ui.outline;

import org.eclipse.gymnast.runtime.core.outline.OutlineNode;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class LDTOutlineLabelProvider extends LabelProvider {
	
	public String getText(Object element) {
		if (element instanceof OutlineNode){
			OutlineNode outlineNode = (OutlineNode)element;
			return getOutlineNodeText(outlineNode);
		}
		else {
			return super.getText(element);
		}
	}
	
	public String getOutlineNodeText(OutlineNode outlineNode) {
		String text = outlineNode.getText();
		if (text == null) return "";
		else return text;
	}
	
	public Image getImage(Object element) {
		if (element instanceof OutlineNode){
			OutlineNode outlineNode = (OutlineNode)element;
			return getOutlineNodeImage(outlineNode);
		}
		else {
			return super.getImage(element);
		}
	}
	
	public Image getOutlineNodeImage(OutlineNode outlineNode) {
		return outlineNode.getImage();
	}

}
