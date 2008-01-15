
header
{
package org.eclipse.gymnast.generator.core.parser;
import org.eclipse.gymnast.generator.core.ast.*;
import org.eclipse.gymnast.runtime.core.parser.*;
}

class GymnastParser extends Parser;

options {
  k=2;
}
{
	private ParseContext _parseContext;
	public void setParseContext(ParseContext parseContext) {
		_parseContext = parseContext;
	}
	
    public void reportError(RecognitionException ex) {
        if (_parseContext != null) {
        	_parseContext.addParseMessage(new ParseError(ex));
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

compUnit returns [ CompUnit retVal = null ]
:
{ HeaderSection headerSection = null; Grammar grammar = null; }
  headerSection=headerSection grammar=grammar EOF
{ retVal = new CompUnit(headerSection, grammar); }
;

headerSection returns [ HeaderSection retVal = null ]
:
{ Id name = null; OptionsSection optionsSection = null; }
  language_KW:"language" name=id semi:SEMI ( optionsSection=optionsSection )? 
{ retVal = new HeaderSection(language_KW, name, semi, optionsSection); }
;

optionsSection returns [ OptionsSection retVal = null ]
:
{ OptionList optionList = null; }
  options_KW:"options" lcurly:LCURLY optionList=optionList rcurly:RCURLY 
{ retVal = new OptionsSection(options_KW, lcurly, optionList, rcurly); }
;

optionList returns [ OptionList retVal = new OptionList() ]
:
{ Option option = null; }
  ( option=option { retVal.addChild(option); } )*
;

option returns [ Option retVal = null ]
:
{ Id name = null; OptionValue value = null; }
  name=id equals:EQUALS value=optionValue semi:SEMI 
{ retVal = new Option(name, equals, value, semi); }
;

optionValue returns [ OptionValue retVal = null ]
{ Token tok = LT(1); }
: ( ID
  | STRING_LITERAL
  | INT_LITERAL
  )
{ retVal = new OptionValue(tok); }
;

grammar returns [ Grammar retVal = new Grammar() ]
:
{ Rule rule = null; }
  ( rule=rule { retVal.addChild(rule); } )*
;

rule returns [ Rule retVal = null ]
:
  ( retVal=altRule
  | retVal=listRule
  | retVal=seqRule
  | retVal=tokenRule
  )
;

altRule returns [ AltRule retVal = null ]
:
{ AltRuleDecl decl = null; AltRuleBody body = null; }
  decl=altRuleDecl body=altRuleBody semi:SEMI 
{ retVal = new AltRule(decl, body, semi); }
;

altRuleDecl returns [ AltRuleDecl retVal = null ]
:
{ AltRuleKind kind = null; Id name = null; Attrs attrs = null; }
  kind=altRuleKind name=id ( attrs=attrs )? colon:COLON 
{ retVal = new AltRuleDecl(kind, name, attrs, colon); }
;

altRuleKind returns [ AltRuleKind retVal = null ]
{ Token tok = LT(1); }
: ( "abstract"
  | "container"
  | "interface"
  )
{ retVal = new AltRuleKind(tok); }
;

altRuleBody returns [ AltRuleBody retVal = null ]
:
{ AltSeq preSeq = null; Alts alts = null; AltSeq postSeq = null; }
  ( preSeq=altSeq )? alts=alts ( postSeq=altSeq )? 
{ retVal = new AltRuleBody(preSeq, alts, postSeq); }
;

alts returns [ Alts retVal = new Alts() ]
:
{ SimpleExpr se1 = null; SimpleExpr sen = null; }
  se1=simpleExpr { retVal.addChild(se1); } 
  ( pipe:PIPE sen=simpleExpr { retVal.addChild(pipe); retVal.addChild(sen); } )*
;

altSeq returns [ AltSeq retVal = null ]
:
{ Seq seq = null; }
  lparen:LPAREN seq=seq rparen:RPAREN 
{ retVal = new AltSeq(lparen, seq, rparen); }
;

listRule returns [ ListRule retVal = null ]
:
{ ListRuleDecl decl = null; ListRuleBody body = null; }
  decl=listRuleDecl body=listRuleBody semi:SEMI 
{ retVal = new ListRule(decl, body, semi); }
;

listRuleDecl returns [ ListRuleDecl retVal = null ]
:
{ Id name = null; Attrs attrs = null; }
  list_KW:"list" name=id ( attrs=attrs )? colon:COLON 
{ retVal = new ListRuleDecl(list_KW, name, attrs, colon); }
;

listRuleBody returns [ ListRuleBody retVal = null ]
:
{ SimpleExpr listExpr = null; SimpleExpr separator = null; SimpleExpr listExpr2 = null; ListMark listMark = null; }
  listExpr=simpleExpr ( lparen:LPAREN separator=simpleExpr listExpr2=simpleExpr rparen:RPAREN )? listMark=listMark 
{ retVal = new ListRuleBody(listExpr, lparen, separator, listExpr2, rparen, listMark); }
;

listMark returns [ ListMark retVal = null ]
{ Token tok = LT(1); }
: ( STAR
  | PLUS
  )
{ retVal = new ListMark(tok); }
;

seqRule returns [ SeqRule retVal = null ]
:
{ SeqRuleDecl decl = null; Seq body = null; }
  decl=seqRuleDecl body=seq semi:SEMI 
{ retVal = new SeqRule(decl, body, semi); }
;

seqRuleDecl returns [ SeqRuleDecl retVal = null ]
:
{ Id name = null; Attrs attrs = null; }
  sequence_KW:"sequence" name=id ( attrs=attrs )? colon:COLON 
{ retVal = new SeqRuleDecl(sequence_KW, name, attrs, colon); }
;

seq returns [ Seq retVal = new Seq() ]
:
{ Expr expr = null; }
  ( expr=expr { retVal.addChild(expr); } )+
;

tokenRule returns [ TokenRule retVal = null ]
:
{ TokenRuleDecl decl = null; Alts body = null; }
  decl=tokenRuleDecl body=alts semi:SEMI 
{ retVal = new TokenRule(decl, body, semi); }
;

tokenRuleDecl returns [ TokenRuleDecl retVal = null ]
:
{ Id name = null; Attrs attrs = null; }
  token_KW:"token" name=id ( attrs=attrs )? colon:COLON 
{ retVal = new TokenRuleDecl(token_KW, name, attrs, colon); }
;

expr returns [ Expr retVal = null ]
:
  ( retVal=optSubSeq
  | retVal=simpleExpr
  )
;

optSubSeq returns [ OptSubSeq retVal = null ]
:
{ Seq seq = null; }
  lparen:LPAREN seq=seq rparen:RPAREN qmark:QMARK 
{ retVal = new OptSubSeq(lparen, seq, rparen, qmark); }
;

simpleExpr returns [ SimpleExpr retVal = null ]
:
{ Id name = null; Atom value = null; Attrs attrs = null; }
  ( name=id equals:EQUALS )? value=atom ( attrs=attrs )? 
{ retVal = new SimpleExpr(name, equals, value, attrs); }
;

attrs returns [ Attrs retVal = null ]
:
{ AttrList attrList = null; }
  lsquare:LSQUARE attrList=attrList rsquare:RSQUARE 
{ retVal = new Attrs(lsquare, attrList, rsquare); }
;

attrList returns [ AttrList retVal = new AttrList() ]
:
  id1:ID { retVal.addChild(id1); } 
  ( comma:COMMA idn:ID { retVal.addChild(comma); retVal.addChild(idn); } )*
;

atom returns [ Atom retVal = null ]
{ Token tok = LT(1); }
: ( ID
  | STRING_LITERAL
  | "abstract"
  | "container"
  | "interface"
  | "language"
  | "list"
  | "options"
  | "sequence"
  | "token"
  )
{ retVal = new Atom(tok); }
;

id returns [ Id retVal = null ]
{ Token tok = LT(1); }
: ( ID
  | "abstract"
  | "container"
  | "interface"
  | "language"
  | "list"
  | "options"
  | "sequence"
  | "token"
  )
{ retVal = new Id(tok); }
;



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
