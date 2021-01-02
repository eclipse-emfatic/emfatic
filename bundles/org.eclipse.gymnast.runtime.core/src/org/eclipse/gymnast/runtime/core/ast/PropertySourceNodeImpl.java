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

package org.eclipse.gymnast.runtime.core.ast;

import java.util.Vector;

import org.eclipse.ui.views.properties.IPropertyDescriptor;
import org.eclipse.ui.views.properties.IPropertySource;
import org.eclipse.ui.views.properties.TextPropertyDescriptor;

/**
 * @author cjdaly@us.ibm.com
 *
 */
public abstract class PropertySourceNodeImpl extends ASTNodeImpl implements IPropertySource {

	public PropertySourceNodeImpl() {
		super();
	}

	public PropertySourceNodeImpl(TokenInfo tokenInfo) {
		super(tokenInfo);
	}

	
	//////////////////////////////////////
	/// IPropertySource implementation ///
	//////////////////////////////////////
	
	static private String TEXT_ID = "PropertySourceASTNode.Text";
	static private String OFFSET_ID = "PropertySourceASTNode.Offset";
	
	static private Vector descriptors;	
	static
	{
		descriptors = new Vector();
		descriptors.addElement(new TextPropertyDescriptor(TEXT_ID, "Text"));
		descriptors.addElement(new TextPropertyDescriptor(OFFSET_ID, "Offset"));
	}
	
	public Object getEditableValue() {
		return null;
	}

	public IPropertyDescriptor[] getPropertyDescriptors() {
		return (IPropertyDescriptor[]) descriptors.toArray(new IPropertyDescriptor[descriptors.size()]);
	}

	public Object getPropertyValue(Object id) {
		if (TEXT_ID.equals(id)) {
			if (getText() == null) return "";
			else return getText();
		}
		if (OFFSET_ID.equals(id)) {
			return new Integer(getOffset());
		}
		return null;
	}

	public boolean isPropertySet(Object id) {
		return false;
	}

	public void resetPropertyValue(Object id) {
		
	}

	public void setPropertyValue(Object id, Object value) {
		
	}

}
