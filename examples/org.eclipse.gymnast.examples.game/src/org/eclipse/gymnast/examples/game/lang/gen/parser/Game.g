
header
{
package org.eclipse.gymnast.examples.game.lang.gen.parser;
import org.eclipse.gymnast.examples.game.lang.gen.ast.*;
import org.eclipse.gymnast.runtime.core.parser.*;
import org.eclipse.gymnast.runtime.core.ast.*;
}

class GameParser extends Parser;

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

}

commandList returns [ CommandList retVal = new CommandList() ]
:
{ Command command = null; }
  ( command=command { retVal.addChild(command); } )* EOF
;

command returns [ Command retVal = null ]
:
{ OptReps optReps = null; }
optReps=optReps 
  ( retVal=shoot[optReps]
  | retVal=move[optReps]
  )
;

optReps returns [ OptReps retVal = null ]
:
  ( reps:INT_LITERAL )? 
{ retVal = new OptReps(createTokenInfo(reps)); }
;

shoot [ OptReps optReps ]  returns [ Shoot retVal = null ]
:
  fire_KW:"fire" semi:SEMI 
{ retVal = new Shoot(optReps, createTokenInfo(fire_KW), createTokenInfo(semi)); }
;

move [ OptReps optReps ]  returns [ Move retVal = null ]
:
{ Direction direction = null; }
  move_KW:"move" direction=direction semi:SEMI 
{ retVal = new Move(optReps, createTokenInfo(move_KW), direction, createTokenInfo(semi)); }
;

direction returns [ Direction retVal = null ]
{ Token tok = LT(1); }
: ( "left"
  | "right"
  )
{ retVal = new Direction(createTokenInfo(tok)); }
;


class GameLexer extends Lexer;

options
{
	testLiterals=false;
	k=3;
	charVocabulary='\003'..'\377';
}

SEMI    : ';';

ID options { testLiterals=true; }
: ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '_' | DIGIT)* ;

INT_LITERAL : (DIGIT)+ ;

protected DIGIT : '0'..'9';

WS : (' ' | '\t' | '\f' | '\r' | '\n')+ 
{ $setType(Token.SKIP); }
;

SINGLE_LINE_COMMENT : "//" (~('\n'|'\r'))* ('\n'|'\r')?
{ $setType(Token.SKIP); }
;

