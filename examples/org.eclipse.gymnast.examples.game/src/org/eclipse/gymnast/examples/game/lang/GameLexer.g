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

