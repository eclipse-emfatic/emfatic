// $ANTLR : "Game.g" -> "GameParser.java"$

package org.eclipse.gymnast.examples.game.lang.gen.parser;
import org.eclipse.gymnast.examples.game.lang.gen.ast.Command;
import org.eclipse.gymnast.examples.game.lang.gen.ast.CommandList;
import org.eclipse.gymnast.examples.game.lang.gen.ast.Direction;
import org.eclipse.gymnast.examples.game.lang.gen.ast.Move;
import org.eclipse.gymnast.examples.game.lang.gen.ast.OptReps;
import org.eclipse.gymnast.examples.game.lang.gen.ast.Shoot;
import org.eclipse.gymnast.runtime.core.ast.TokenInfo;
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

public class GameParser extends antlr.LLkParser       implements GameParserTokenTypes
 {

    private ParseError createParseError(RecognitionException ex) {
        return GameParserDriver.createParseError(ex);
    }

    private TokenInfo createTokenInfo(Token tok) {
        if (tok == null) return null;
        else return new TokenInfo(tok.getText(), tok.getColumn(), tok.getType());
    }


	private ParseContext _parseContext;
	public void setParseContext(ParseContext parseContext) {
		_parseContext = parseContext;
	}
	
    public void reportError(RecognitionException ex) {
        if (_parseContext != null) {
        	_parseContext.addParseMessage(createParseError(ex));
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


protected GameParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public GameParser(TokenBuffer tokenBuf) {
  this(tokenBuf,1);
}

protected GameParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public GameParser(TokenStream lexer) {
  this(lexer,1);
}

public GameParser(ParserSharedInputState state) {
  super(state,1);
  tokenNames = _tokenNames;
}

	public final CommandList  commandList() throws RecognitionException, TokenStreamException {
		 CommandList retVal = new CommandList() ;
		
		
		try {      // for error handling
			Command command = null;
			{
			_loop3:
			do {
				if ((LA(1)==INT_LITERAL||LA(1)==LITERAL_fire||LA(1)==LITERAL_move)) {
					command=command();
					retVal.addChild(command);
				}
				else {
					break _loop3;
				}
				
			} while (true);
			}
			match(Token.EOF_TYPE);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return retVal;
	}
	
	public final Command  command() throws RecognitionException, TokenStreamException {
		 Command retVal = null ;
		
		
		try {      // for error handling
			OptReps optReps = null;
			optReps=optReps();
			{
			switch ( LA(1)) {
			case LITERAL_fire:
			{
				retVal=shoot(optReps);
				break;
			}
			case LITERAL_move:
			{
				retVal=move(optReps);
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
			recover(ex,_tokenSet_1);
		}
		return retVal;
	}
	
	public final OptReps  optReps() throws RecognitionException, TokenStreamException {
		 OptReps retVal = null ;
		
		Token  reps = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case INT_LITERAL:
			{
				reps = LT(1);
				match(INT_LITERAL);
				break;
			}
			case LITERAL_fire:
			case LITERAL_move:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new OptReps(createTokenInfo(reps));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return retVal;
	}
	
	public final Shoot  shoot(
		 OptReps optReps 
	) throws RecognitionException, TokenStreamException {
		 Shoot retVal = null ;
		
		Token  fire_KW = null;
		Token  semi = null;
		
		try {      // for error handling
			fire_KW = LT(1);
			match(LITERAL_fire);
			semi = LT(1);
			match(SEMI);
			retVal = new Shoot(optReps, createTokenInfo(fire_KW), createTokenInfo(semi));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return retVal;
	}
	
	public final Move  move(
		 OptReps optReps 
	) throws RecognitionException, TokenStreamException {
		 Move retVal = null ;
		
		Token  move_KW = null;
		Token  semi = null;
		
		try {      // for error handling
			Direction direction = null;
			move_KW = LT(1);
			match(LITERAL_move);
			direction=direction();
			semi = LT(1);
			match(SEMI);
			retVal = new Move(optReps, createTokenInfo(move_KW), direction, createTokenInfo(semi));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return retVal;
	}
	
	public final Direction  direction() throws RecognitionException, TokenStreamException {
		 Direction retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_left:
			{
				match(LITERAL_left);
				break;
			}
			case LITERAL_right:
			{
				match(LITERAL_right);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new Direction(createTokenInfo(tok));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_3);
		}
		return retVal;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"INT_LITERAL",
		"\"fire\"",
		"SEMI",
		"\"move\"",
		"\"left\"",
		"\"right\"",
		"ID",
		"DIGIT",
		"WS",
		"SINGLE_LINE_COMMENT"
	};
	
	private static final long[] mk_tokenSet_0() {
		long[] data = { 2L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_0 = new BitSet(mk_tokenSet_0());
	private static final long[] mk_tokenSet_1() {
		long[] data = { 178L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 160L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 64L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	
	}
