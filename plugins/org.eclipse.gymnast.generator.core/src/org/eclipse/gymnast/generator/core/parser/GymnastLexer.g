
class GymnastLexer extends Lexer;

options
{
	testLiterals=false;
	k=3;
	charVocabulary='\003'..'\377';
}


LCURLY  : '{';
RCURLY  : '}';
LSQUARE : '[';
RSQUARE : ']';
LPAREN  : '(';
RPAREN  : ')';

DOT     : '.';
COMMA   : ',';
COLON   : ':';
SEMI    : ';';
PIPE    : '|';

STAR    : '*';
PLUS    : '+';
EQUALS  : '=';
QMARK   : '?';
BANG    : '!';

STRING_LITERAL : '"' (ESC | ~'"')* '"' ;

// TODO: The (DIGIT DIGIT DIGIT) is cheesy it should be something like
//       ( options { warnWhenFollowAmbig = false; } : (DIGIT)+ )
//       but I don't want to deal with parsing that yet.
protected ESC :
'\\' ('t' | 'f' | 'r' | 'n' | '"' | '\'' | '\\' | (DIGIT DIGIT DIGIT) )
;

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

MULTI_LINE_COMMENT : "/*" ("*/" | (~'!' (~'*' | '*' ~'/')* "*/"))
{ $setType(Token.SKIP); }
;
