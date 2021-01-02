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
public abstract class ParseMessage {
	
	private String _message;
	private int _offset;
	private int _length;
	
	public ParseMessage(String message, int offset, int length) {
		init(message, offset, length);
	}
	
	public ParseMessage(String message, int offset) {
		init(message, offset, 0);
	}
	
	protected ParseMessage() {
		// invoker must call init()!
	}
	
	protected void init(String message, int offset, int length) {
		_message = message;
		_offset = offset;
		_length = length;
	}
	
	public String getMessage() {
		return _message;
	}
	
	public int getOffset() {
		return _offset;
	}
	
	public int getLength() {
		return _length;
	}

}
