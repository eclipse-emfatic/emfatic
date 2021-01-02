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

package org.eclipse.gymnast.runtime.core.parser;

/**
 * @author cjdaly@us.ibm.com
 *
 */
public class ParseWarning extends ParseMessage {
	
	public ParseWarning(String message, int offset, int length) {
		super(message, offset, length);
	}
	
	public ParseWarning(String message, int offset) {
		super(message, offset);
	}
	
	protected ParseWarning() {
		// invoker must call init()!
	}
	
}