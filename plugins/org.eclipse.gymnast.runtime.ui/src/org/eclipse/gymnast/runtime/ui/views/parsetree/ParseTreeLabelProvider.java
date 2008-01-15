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

package org.eclipse.gymnast.runtime.ui.views.parsetree;

import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class ParseTreeLabelProvider extends LabelProvider {
	
	Image _errImage;
	Image _okImage;

	public ParseTreeLabelProvider() {
		super();
		_errImage = ImageDescriptor.createFromFile(this.getClass(), "private_co.gif").createImage();
		_okImage = ImageDescriptor.createFromFile(this.getClass(), "public_co.gif").createImage();
	}
	
	public String getText(Object element) {
		if (element instanceof ASTNode) {
			ASTNode node = (ASTNode)element;
			String nodeText = node.getText();
			if (nodeText != null) return node.getTypeName() + " \"" + nodeText + "\"";
			else return node.getTypeName();
		}
		else return super.getText(element);
	}
	
	public Image getImage(Object element) {
		if (element instanceof ASTNode) {
			ASTNode node = (ASTNode)element;
			if (node.getText() == null) return _errImage;
			else return _okImage;
		}
		return null;
	}

}
