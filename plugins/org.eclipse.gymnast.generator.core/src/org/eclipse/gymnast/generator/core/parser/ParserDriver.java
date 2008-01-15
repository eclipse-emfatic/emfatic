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

package org.eclipse.gymnast.generator.core.parser;

import java.io.Reader;

import org.eclipse.gymnast.generator.core.ast.CompUnit;
import org.eclipse.gymnast.runtime.core.parser.IParser;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;
import org.eclipse.gymnast.runtime.core.parser.ParseError;
import org.eclipse.gymnast.runtime.core.parser.ParseMessage;

import antlr.MismatchedCharException;
import antlr.MismatchedTokenException;
import antlr.NoViableAltException;
import antlr.NoViableAltForCharException;
import antlr.RecognitionException;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class ParserDriver implements IParser {
	
	public ParseContext parse(Reader input) {
		GymnastLexer lexer = new GymnastLexer(input);
		lexer.setColumn(0);
		lexer.setTabSize(1);

		GymnastParser parser = new GymnastParser(lexer);
		ParseContext parseContext = new ParseContext();
		parser.setParseContext(parseContext);

		try{
			CompUnit compUnit = parser.compUnit();
			if (compUnit != null) {
				parseContext.setParseRoot(compUnit);
			}
			
			if (parseContext.getMessageCount() == 0) {
				System.out.println("Parse OK!");
			}
			else {
				ParseMessage[] msgs = parseContext.getMessages();
				for (int i = 0; i < msgs.length; i++) {
					System.err.println(msgs[i].getMessage());
				}
			}
		}
		catch (Exception ex){
		    ex.printStackTrace();
		}
		
		return parseContext;
	}
	
	static ParseError createParseError(RecognitionException ex) {
		String message = ex.getMessage();
		int offset = ex.getColumn();
		int length = 0;

		if (ex instanceof MismatchedCharException) {
			length = 1;
		} else if (ex instanceof MismatchedTokenException) {
			MismatchedTokenException ex2 = (MismatchedTokenException) ex;
			if ((ex2.token != null) && (ex2.token.getText() != null)) {
				length = ex2.token.getText().length();
			}
		} else if (ex instanceof NoViableAltException) {
			NoViableAltException ex2 = (NoViableAltException) ex;
			if ((ex2.token != null) && (ex2.token.getText() != null)) {
				length = ex2.token.getText().length();
			}
		} else if (ex instanceof NoViableAltForCharException) {
			length = 1;
		}
		
		return new ParseError(message, offset, length);
	}
}
