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
import org.eclipse.gymnast.generator.core.ast.AltRule;
import org.eclipse.gymnast.generator.core.ast.AltRuleBody;
import org.eclipse.gymnast.generator.core.ast.AltRuleDecl;
import org.eclipse.gymnast.generator.core.ast.AltRuleKind;
import org.eclipse.gymnast.generator.core.ast.AltSeq;
import org.eclipse.gymnast.generator.core.ast.Alts;
import org.eclipse.gymnast.generator.core.ast.Atom;
import org.eclipse.gymnast.generator.core.ast.AttrList;
import org.eclipse.gymnast.generator.core.ast.Attrs;
import org.eclipse.gymnast.generator.core.ast.CompUnit;
import org.eclipse.gymnast.generator.core.ast.Expr;
import org.eclipse.gymnast.generator.core.ast.Grammar;
import org.eclipse.gymnast.generator.core.ast.HeaderSection;
import org.eclipse.gymnast.generator.core.ast.Id;
import org.eclipse.gymnast.generator.core.ast.ListMark;
import org.eclipse.gymnast.generator.core.ast.ListRule;
import org.eclipse.gymnast.generator.core.ast.ListRuleBody;
import org.eclipse.gymnast.generator.core.ast.ListRuleDecl;
import org.eclipse.gymnast.generator.core.ast.OptSubSeq;
import org.eclipse.gymnast.generator.core.ast.Option;
import org.eclipse.gymnast.generator.core.ast.OptionList;
import org.eclipse.gymnast.generator.core.ast.OptionValue;
import org.eclipse.gymnast.generator.core.ast.OptionsSection;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.ast.Seq;
import org.eclipse.gymnast.generator.core.ast.SeqRule;
import org.eclipse.gymnast.generator.core.ast.SeqRuleDecl;
import org.eclipse.gymnast.generator.core.ast.SimpleExpr;
import org.eclipse.gymnast.generator.core.ast.TokenRule;
import org.eclipse.gymnast.generator.core.ast.TokenRuleDecl;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;
import org.eclipse.gymnast.runtime.core.parser.ParseError;
import org.eclipse.gymnast.runtime.core.parser.ParseWarning;

import antlr.NoViableAltException;
import antlr.ParserSharedInputState;
import antlr.RecognitionException;
import antlr.Token;
import antlr.TokenBuffer;
import antlr.TokenStream;
import antlr.TokenStreamException;
import antlr.collections.impl.BitSet;


public class GymnastParser extends antlr.LLkParser       implements GymnastParserTokenTypes
 {

	private ParseContext _parseContext;
	public void setParseContext(ParseContext parseContext) {
		_parseContext = parseContext;
	}
	
    public void reportError(RecognitionException ex) {
        if (_parseContext != null) {
        	_parseContext.addParseMessage(ParserDriver.createParseError(ex));
        }
    }

    public void reportError(String s) {
        if (_parseContext != null) {
        	_parseContext.addParseMessage(new ParseError(s, -1));
        }
    }

    public void reportWarning(String s) {
    	if (_parseContext != null) {
        	_parseContext.addParseMessage(new ParseWarning(s, -1));
        }
    }

protected GymnastParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public GymnastParser(TokenBuffer tokenBuf) {
  this(tokenBuf,2);
}

protected GymnastParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public GymnastParser(TokenStream lexer) {
  this(lexer,2);
}

public GymnastParser(ParserSharedInputState state) {
  super(state,2);
  tokenNames = _tokenNames;
}

	public final CompUnit  compUnit() throws RecognitionException, TokenStreamException {
		 CompUnit retVal = null ;
		
		
		try {      // for error handling
			HeaderSection headerSection = null; Grammar grammar = null;
			headerSection=headerSection();
			grammar=grammar();
			match(Token.EOF_TYPE);
			retVal = new CompUnit(headerSection, grammar);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_0);
		}
		return retVal;
	}
	
	public final HeaderSection  headerSection() throws RecognitionException, TokenStreamException {
		 HeaderSection retVal = null ;
		
		Token  language_KW = null;
		Token  semi = null;
		
		try {      // for error handling
			Id name = null; OptionsSection optionsSection = null;
			language_KW = LT(1);
			match(LITERAL_language);
			name=id();
			semi = LT(1);
			match(SEMI);
			{
			switch ( LA(1)) {
			case LITERAL_options:
			{
				optionsSection=optionsSection();
				break;
			}
			case EOF:
			case LITERAL_abstract:
			case LITERAL_container:
			case LITERAL_interface:
			case LITERAL_list:
			case LITERAL_sequence:
			case LITERAL_token:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new HeaderSection(language_KW, name, semi, optionsSection);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
		return retVal;
	}
	
	public final Grammar  grammar() throws RecognitionException, TokenStreamException {
		 Grammar retVal = new Grammar() ;
		
		
		try {      // for error handling
			Rule rule = null;
			{
			_loop13:
			do {
				if ((_tokenSet_2.member(LA(1)))) {
					rule=rule();
					retVal.addChild(rule);
				}
				else {
					break _loop13;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_0);
		}
		return retVal;
	}
	
	public final Id  id() throws RecognitionException, TokenStreamException {
		 Id retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case ID:
			{
				match(ID);
				break;
			}
			case LITERAL_abstract:
			{
				match(LITERAL_abstract);
				break;
			}
			case LITERAL_container:
			{
				match(LITERAL_container);
				break;
			}
			case LITERAL_interface:
			{
				match(LITERAL_interface);
				break;
			}
			case LITERAL_language:
			{
				match(LITERAL_language);
				break;
			}
			case LITERAL_list:
			{
				match(LITERAL_list);
				break;
			}
			case LITERAL_options:
			{
				match(LITERAL_options);
				break;
			}
			case LITERAL_sequence:
			{
				match(LITERAL_sequence);
				break;
			}
			case LITERAL_token:
			{
				match(LITERAL_token);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new Id(tok);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_3);
		}
		return retVal;
	}
	
	public final OptionsSection  optionsSection() throws RecognitionException, TokenStreamException {
		 OptionsSection retVal = null ;
		
		Token  options_KW = null;
		Token  lcurly = null;
		Token  rcurly = null;
		
		try {      // for error handling
			OptionList optionList = null;
			options_KW = LT(1);
			match(LITERAL_options);
			lcurly = LT(1);
			match(LCURLY);
			optionList=optionList();
			rcurly = LT(1);
			match(RCURLY);
			retVal = new OptionsSection(options_KW, lcurly, optionList, rcurly);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
		return retVal;
	}
	
	public final OptionList  optionList() throws RecognitionException, TokenStreamException {
		 OptionList retVal = new OptionList() ;
		
		
		try {      // for error handling
			Option option = null;
			{
			_loop7:
			do {
				if ((_tokenSet_4.member(LA(1)))) {
					option=option();
					retVal.addChild(option);
				}
				else {
					break _loop7;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_5);
		}
		return retVal;
	}
	
	public final Option  option() throws RecognitionException, TokenStreamException {
		 Option retVal = null ;
		
		Token  equals = null;
		Token  semi = null;
		
		try {      // for error handling
			Id name = null; OptionValue value = null;
			name=id();
			equals = LT(1);
			match(EQUALS);
			value=optionValue();
			semi = LT(1);
			match(SEMI);
			retVal = new Option(name, equals, value, semi);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_6);
		}
		return retVal;
	}
	
	public final OptionValue  optionValue() throws RecognitionException, TokenStreamException {
		 OptionValue retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case ID:
			{
				match(ID);
				break;
			}
			case STRING_LITERAL:
			{
				match(STRING_LITERAL);
				break;
			}
			case INT_LITERAL:
			{
				match(INT_LITERAL);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new OptionValue(tok);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_7);
		}
		return retVal;
	}
	
	public final Rule  rule() throws RecognitionException, TokenStreamException {
		 Rule retVal = null ;
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_abstract:
			case LITERAL_container:
			case LITERAL_interface:
			{
				retVal=altRule();
				break;
			}
			case LITERAL_list:
			{
				retVal=listRule();
				break;
			}
			case LITERAL_sequence:
			{
				retVal=seqRule();
				break;
			}
			case LITERAL_token:
			{
				retVal=tokenRule();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
		return retVal;
	}
	
	public final AltRule  altRule() throws RecognitionException, TokenStreamException {
		 AltRule retVal = null ;
		
		Token  semi = null;
		
		try {      // for error handling
			AltRuleDecl decl = null; AltRuleBody body = null;
			decl=altRuleDecl();
			body=altRuleBody();
			semi = LT(1);
			match(SEMI);
			retVal = new AltRule(decl, body, semi);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
		return retVal;
	}
	
	public final ListRule  listRule() throws RecognitionException, TokenStreamException {
		 ListRule retVal = null ;
		
		Token  semi = null;
		
		try {      // for error handling
			ListRuleDecl decl = null; ListRuleBody body = null;
			decl=listRuleDecl();
			body=listRuleBody();
			semi = LT(1);
			match(SEMI);
			retVal = new ListRule(decl, body, semi);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
		return retVal;
	}
	
	public final SeqRule  seqRule() throws RecognitionException, TokenStreamException {
		 SeqRule retVal = null ;
		
		Token  semi = null;
		
		try {      // for error handling
			SeqRuleDecl decl = null; Seq body = null;
			decl=seqRuleDecl();
			body=seq();
			semi = LT(1);
			match(SEMI);
			retVal = new SeqRule(decl, body, semi);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
		return retVal;
	}
	
	public final TokenRule  tokenRule() throws RecognitionException, TokenStreamException {
		 TokenRule retVal = null ;
		
		Token  semi = null;
		
		try {      // for error handling
			TokenRuleDecl decl = null; Alts body = null;
			decl=tokenRuleDecl();
			body=alts();
			semi = LT(1);
			match(SEMI);
			retVal = new TokenRule(decl, body, semi);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_1);
		}
		return retVal;
	}
	
	public final AltRuleDecl  altRuleDecl() throws RecognitionException, TokenStreamException {
		 AltRuleDecl retVal = null ;
		
		Token  colon = null;
		
		try {      // for error handling
			AltRuleKind kind = null; Id name = null; Attrs attrs = null;
			kind=altRuleKind();
			name=id();
			{
			switch ( LA(1)) {
			case LSQUARE:
			{
				attrs=attrs();
				break;
			}
			case COLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			colon = LT(1);
			match(COLON);
			retVal = new AltRuleDecl(kind, name, attrs, colon);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_8);
		}
		return retVal;
	}
	
	public final AltRuleBody  altRuleBody() throws RecognitionException, TokenStreamException {
		 AltRuleBody retVal = null ;
		
		
		try {      // for error handling
			AltSeq preSeq = null; Alts alts = null; AltSeq postSeq = null;
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				preSeq=altSeq();
				break;
			}
			case LITERAL_language:
			case LITERAL_options:
			case ID:
			case STRING_LITERAL:
			case LITERAL_abstract:
			case LITERAL_container:
			case LITERAL_interface:
			case LITERAL_list:
			case LITERAL_sequence:
			case LITERAL_token:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			alts=alts();
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				postSeq=altSeq();
				break;
			}
			case SEMI:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new AltRuleBody(preSeq, alts, postSeq);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_7);
		}
		return retVal;
	}
	
	public final AltRuleKind  altRuleKind() throws RecognitionException, TokenStreamException {
		 AltRuleKind retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_abstract:
			{
				match(LITERAL_abstract);
				break;
			}
			case LITERAL_container:
			{
				match(LITERAL_container);
				break;
			}
			case LITERAL_interface:
			{
				match(LITERAL_interface);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new AltRuleKind(tok);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_4);
		}
		return retVal;
	}
	
	public final Attrs  attrs() throws RecognitionException, TokenStreamException {
		 Attrs retVal = null ;
		
		Token  lsquare = null;
		Token  rsquare = null;
		
		try {      // for error handling
			AttrList attrList = null;
			lsquare = LT(1);
			match(LSQUARE);
			attrList=attrList();
			rsquare = LT(1);
			match(RSQUARE);
			retVal = new Attrs(lsquare, attrList, rsquare);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_9);
		}
		return retVal;
	}
	
	public final AltSeq  altSeq() throws RecognitionException, TokenStreamException {
		 AltSeq retVal = null ;
		
		Token  lparen = null;
		Token  rparen = null;
		
		try {      // for error handling
			Seq seq = null;
			lparen = LT(1);
			match(LPAREN);
			seq=seq();
			rparen = LT(1);
			match(RPAREN);
			retVal = new AltSeq(lparen, seq, rparen);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_10);
		}
		return retVal;
	}
	
	public final Alts  alts() throws RecognitionException, TokenStreamException {
		 Alts retVal = new Alts() ;
		
		Token  pipe = null;
		
		try {      // for error handling
			SimpleExpr se1 = null; SimpleExpr sen = null;
			se1=simpleExpr();
			retVal.addChild(se1);
			{
			_loop26:
			do {
				if ((LA(1)==PIPE)) {
					pipe = LT(1);
					match(PIPE);
					sen=simpleExpr();
					retVal.addChild(pipe); retVal.addChild(sen);
				}
				else {
					break _loop26;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_11);
		}
		return retVal;
	}
	
	public final SimpleExpr  simpleExpr() throws RecognitionException, TokenStreamException {
		 SimpleExpr retVal = null ;
		
		Token  equals = null;
		
		try {      // for error handling
			Id name = null; Atom value = null; Attrs attrs = null;
			{
			if ((_tokenSet_4.member(LA(1))) && (LA(2)==EQUALS)) {
				name=id();
				equals = LT(1);
				match(EQUALS);
			}
			else if ((_tokenSet_12.member(LA(1))) && (_tokenSet_13.member(LA(2)))) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			value=atom();
			{
			switch ( LA(1)) {
			case LSQUARE:
			{
				attrs=attrs();
				break;
			}
			case LITERAL_language:
			case SEMI:
			case LITERAL_options:
			case ID:
			case STRING_LITERAL:
			case LITERAL_abstract:
			case LITERAL_container:
			case LITERAL_interface:
			case PIPE:
			case LPAREN:
			case RPAREN:
			case LITERAL_list:
			case STAR:
			case PLUS:
			case LITERAL_sequence:
			case LITERAL_token:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new SimpleExpr(name, equals, value, attrs);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_14);
		}
		return retVal;
	}
	
	public final Seq  seq() throws RecognitionException, TokenStreamException {
		 Seq retVal = new Seq() ;
		
		
		try {      // for error handling
			Expr expr = null;
			{
			int _cnt40=0;
			_loop40:
			do {
				if ((_tokenSet_8.member(LA(1)))) {
					expr=expr();
					retVal.addChild(expr);
				}
				else {
					if ( _cnt40>=1 ) { break _loop40; } else {throw new NoViableAltException(LT(1), getFilename());}
				}
				
				_cnt40++;
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_15);
		}
		return retVal;
	}
	
	public final ListRuleDecl  listRuleDecl() throws RecognitionException, TokenStreamException {
		 ListRuleDecl retVal = null ;
		
		Token  list_KW = null;
		Token  colon = null;
		
		try {      // for error handling
			Id name = null; Attrs attrs = null;
			list_KW = LT(1);
			match(LITERAL_list);
			name=id();
			{
			switch ( LA(1)) {
			case LSQUARE:
			{
				attrs=attrs();
				break;
			}
			case COLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			colon = LT(1);
			match(COLON);
			retVal = new ListRuleDecl(list_KW, name, attrs, colon);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_12);
		}
		return retVal;
	}
	
	public final ListRuleBody  listRuleBody() throws RecognitionException, TokenStreamException {
		 ListRuleBody retVal = null ;
		
		Token  lparen = null;
		Token  rparen = null;
		
		try {      // for error handling
			SimpleExpr listExpr = null; SimpleExpr separator = null; SimpleExpr listExpr2 = null; ListMark listMark = null;
			listExpr=simpleExpr();
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				lparen = LT(1);
				match(LPAREN);
				separator=simpleExpr();
				listExpr2=simpleExpr();
				rparen = LT(1);
				match(RPAREN);
				break;
			}
			case STAR:
			case PLUS:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			listMark=listMark();
			retVal = new ListRuleBody(listExpr, lparen, separator, listExpr2, rparen, listMark);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_7);
		}
		return retVal;
	}
	
	public final ListMark  listMark() throws RecognitionException, TokenStreamException {
		 ListMark retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case STAR:
			{
				match(STAR);
				break;
			}
			case PLUS:
			{
				match(PLUS);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new ListMark(tok);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_7);
		}
		return retVal;
	}
	
	public final SeqRuleDecl  seqRuleDecl() throws RecognitionException, TokenStreamException {
		 SeqRuleDecl retVal = null ;
		
		Token  sequence_KW = null;
		Token  colon = null;
		
		try {      // for error handling
			Id name = null; Attrs attrs = null;
			sequence_KW = LT(1);
			match(LITERAL_sequence);
			name=id();
			{
			switch ( LA(1)) {
			case LSQUARE:
			{
				attrs=attrs();
				break;
			}
			case COLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			colon = LT(1);
			match(COLON);
			retVal = new SeqRuleDecl(sequence_KW, name, attrs, colon);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_8);
		}
		return retVal;
	}
	
	public final Expr  expr() throws RecognitionException, TokenStreamException {
		 Expr retVal = null ;
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LPAREN:
			{
				retVal=optSubSeq();
				break;
			}
			case LITERAL_language:
			case LITERAL_options:
			case ID:
			case STRING_LITERAL:
			case LITERAL_abstract:
			case LITERAL_container:
			case LITERAL_interface:
			case LITERAL_list:
			case LITERAL_sequence:
			case LITERAL_token:
			{
				retVal=simpleExpr();
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_16);
		}
		return retVal;
	}
	
	public final TokenRuleDecl  tokenRuleDecl() throws RecognitionException, TokenStreamException {
		 TokenRuleDecl retVal = null ;
		
		Token  token_KW = null;
		Token  colon = null;
		
		try {      // for error handling
			Id name = null; Attrs attrs = null;
			token_KW = LT(1);
			match(LITERAL_token);
			name=id();
			{
			switch ( LA(1)) {
			case LSQUARE:
			{
				attrs=attrs();
				break;
			}
			case COLON:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			colon = LT(1);
			match(COLON);
			retVal = new TokenRuleDecl(token_KW, name, attrs, colon);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_12);
		}
		return retVal;
	}
	
	public final OptSubSeq  optSubSeq() throws RecognitionException, TokenStreamException {
		 OptSubSeq retVal = null ;
		
		Token  lparen = null;
		Token  rparen = null;
		Token  qmark = null;
		
		try {      // for error handling
			Seq seq = null;
			lparen = LT(1);
			match(LPAREN);
			seq=seq();
			rparen = LT(1);
			match(RPAREN);
			qmark = LT(1);
			match(QMARK);
			retVal = new OptSubSeq(lparen, seq, rparen, qmark);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_16);
		}
		return retVal;
	}
	
	public final Atom  atom() throws RecognitionException, TokenStreamException {
		 Atom retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case ID:
			{
				match(ID);
				break;
			}
			case STRING_LITERAL:
			{
				match(STRING_LITERAL);
				break;
			}
			case LITERAL_abstract:
			{
				match(LITERAL_abstract);
				break;
			}
			case LITERAL_container:
			{
				match(LITERAL_container);
				break;
			}
			case LITERAL_interface:
			{
				match(LITERAL_interface);
				break;
			}
			case LITERAL_language:
			{
				match(LITERAL_language);
				break;
			}
			case LITERAL_list:
			{
				match(LITERAL_list);
				break;
			}
			case LITERAL_options:
			{
				match(LITERAL_options);
				break;
			}
			case LITERAL_sequence:
			{
				match(LITERAL_sequence);
				break;
			}
			case LITERAL_token:
			{
				match(LITERAL_token);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new Atom(tok);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_13);
		}
		return retVal;
	}
	
	public final AttrList  attrList() throws RecognitionException, TokenStreamException {
		 AttrList retVal = new AttrList() ;
		
		Token  id1 = null;
		Token  comma = null;
		Token  idn = null;
		
		try {      // for error handling
			id1 = LT(1);
			match(ID);
			retVal.addChild(id1);
			{
			_loop53:
			do {
				if ((LA(1)==COMMA)) {
					comma = LT(1);
					match(COMMA);
					idn = LT(1);
					match(ID);
					retVal.addChild(comma); retVal.addChild(idn);
				}
				else {
					break _loop53;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			consume();
			consumeUntil(_tokenSet_17);
		}
		return retVal;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"language\"",
		"SEMI",
		"\"options\"",
		"LCURLY",
		"RCURLY",
		"EQUALS",
		"ID",
		"STRING_LITERAL",
		"INT_LITERAL",
		"COLON",
		"\"abstract\"",
		"\"container\"",
		"\"interface\"",
		"PIPE",
		"LPAREN",
		"RPAREN",
		"\"list\"",
		"STAR",
		"PLUS",
		"\"sequence\"",
		"\"token\"",
		"QMARK",
		"LSQUARE",
		"RSQUARE",
		"COMMA",
		"DOT",
		"BANG",
		"ESC",
		"DIGIT",
		"WS",
		"SINGLE_LINE_COMMENT",
		"MULTI_LINE_COMMENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 26329090L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 26329088L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 67117600L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 26330192L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 256L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 26330448L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 32L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 26594384L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 33549424L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 26332272L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 262176L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 26332240L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 100650096L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 33541232L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 524320L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 27118704L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 134217728L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	
	}
