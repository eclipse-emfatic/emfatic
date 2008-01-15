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
// $ANTLR : "Gymnast.g" -> "GymnastParser.java"$

package org.eclipse.gymnast.generator.core.parser;

public interface GymnastParserTokenTypes {
	int EOF = 1;
	int NULL_TREE_LOOKAHEAD = 3;
	int LITERAL_language = 4;
	int SEMI = 5;
	int LITERAL_options = 6;
	int LCURLY = 7;
	int RCURLY = 8;
	int EQUALS = 9;
	int ID = 10;
	int STRING_LITERAL = 11;
	int INT_LITERAL = 12;
	int COLON = 13;
	int LITERAL_abstract = 14;
	int LITERAL_container = 15;
	int LITERAL_interface = 16;
	int PIPE = 17;
	int LPAREN = 18;
	int RPAREN = 19;
	int LITERAL_list = 20;
	int STAR = 21;
	int PLUS = 22;
	int LITERAL_sequence = 23;
	int LITERAL_token = 24;
	int QMARK = 25;
	int LSQUARE = 26;
	int RSQUARE = 27;
	int COMMA = 28;
	int DOT = 29;
	int BANG = 30;
	int ESC = 31;
	int DIGIT = 32;
	int WS = 33;
	int SINGLE_LINE_COMMENT = 34;
	int MULTI_LINE_COMMENT = 35;
}
