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
public class ParseError extends ParseMessage {
    
	protected ParseError() {
		// invoker must call init()!
	}
	
	public ParseError(String message, int offset, int length) {
		super(message, offset, length);
	}
	
	public ParseError(String message, int offset) {
		super(message, offset);
	}
	
//	public ParseError(RecognitionException ex) {
//		init(ex);
//	}
//	
//	public ParseError(TokenStreamException ex) {
//	    if (ex instanceof TokenStreamRecognitionException) {
//	        TokenStreamRecognitionException ex2 = (TokenStreamRecognitionException)ex;
//	        RecognitionException rex = ex2.recog;
//	        init(rex);
//	    }
//	}
//	
//	private void init(RecognitionException ex) {
//	    String message = ex.getMessage();
//		int offset = ex.getColumn();
//		int length = 0;
//		
//		if (ex instanceof MismatchedCharException) {
//			length = 1;
//		}
//		else if (ex instanceof MismatchedTokenException) {
//			MismatchedTokenException ex2 = (MismatchedTokenException)ex;
//			if ((ex2.token != null) && (ex2.token.getText() != null)) {
//				length = ex2.token.getText().length();
//			}
//		}
//		else if (ex instanceof NoViableAltException) {
//			NoViableAltException ex2 = (NoViableAltException)ex;
//			if ((ex2.token != null) && (ex2.token.getText() != null)) {
//				length = ex2.token.getText().length();
//			}
//		}
//		else if (ex instanceof NoViableAltForCharException) {
//			length = 1;
//		}
//		
//		init(message, offset, length);
//	}
}
