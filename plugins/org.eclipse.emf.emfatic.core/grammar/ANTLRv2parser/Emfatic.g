
header
{
package org.eclipse.emf.emfatic.core.lang.gen.parser;
import org.eclipse.emf.emfatic.core.lang.gen.ast.*;
import org.eclipse.gymnast.runtime.core.parser.*;
import org.eclipse.gymnast.runtime.core.ast.*;
}

class EmfaticParser extends Parser;

options {
  k=3;

}
{
    private ParseError createParseError(RecognitionException ex) {
        return EmfaticParserDriver.createParseError(ex);
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

compUnit returns [ CompUnit retVal = null ]
:
{ PackageDecl packageDecl = null; ImportStmts importStmts = null; TopLevelDecls topLevelDecls = null; }
  packageDecl=packageDecl importStmts=importStmts topLevelDecls=topLevelDecls EOF
{ retVal = new CompUnit(packageDecl, importStmts, topLevelDecls); }
;

packageDecl returns [ PackageDecl retVal = null ]
:
{ Annotations annotations = null; QualifiedID name = null; }
  annotations=annotations package_KW:"package" name=qualifiedID semi:SEMI 
{ retVal = new PackageDecl(annotations, createTokenInfo(package_KW), name, createTokenInfo(semi)); }
;

qualifiedID returns [ QualifiedID retVal = new QualifiedID() ]
:
{ QidSeparator qidSeparator = null; }
  id1:ID { retVal.addChild(createTokenInfo(id1)); } 
  ( qidSeparator=qidSeparator idn:ID { retVal.addChild(qidSeparator); retVal.addChild(createTokenInfo(idn)); } )*
;

qidSeparator returns [ QidSeparator retVal = null ]
{ Token tok = LT(1); }
: ( DOT
  | DOLLAR
  )
{ retVal = new QidSeparator(createTokenInfo(tok)); }
;

stringLiteralOrQualifiedID returns [ StringLiteralOrQualifiedID retVal = null ]
:
  ( retVal=stringLiteralContainer
  | retVal=qualifiedIDContainer
  )
;

stringLiteralContainer returns [ StringLiteralContainer retVal = null ]
:
  string_literal:STRING_LITERAL 
{ retVal = new StringLiteralContainer(createTokenInfo(string_literal)); }
;

qualifiedIDContainer returns [ QualifiedIDContainer retVal = null ]
:
{ QualifiedID qualifiedID = null; }
  qualifiedID=qualifiedID 
{ retVal = new QualifiedIDContainer(qualifiedID); }
;

annotations returns [ Annotations retVal = new Annotations() ]
:
{ Annotation annotation = null; }
  ( annotation=annotation { retVal.addChild(annotation); } )*
;

annotation returns [ Annotation retVal = null ]
:
{ StringLiteralOrQualifiedID source = null; KeyEqualsValueList keyEqualsValueList = null; }
  at:AT source=stringLiteralOrQualifiedID lparen:LPAREN keyEqualsValueList=keyEqualsValueList rparen:RPAREN 
{ retVal = new Annotation(createTokenInfo(at), source, createTokenInfo(lparen), keyEqualsValueList, createTokenInfo(rparen)); }
;

keyEqualsValueList returns [ KeyEqualsValueList retVal = new KeyEqualsValueList() ]
:
{ KeyEqualsValue kv1 = null; KeyEqualsValue kvn = null; }
  kv1=keyEqualsValue { retVal.addChild(kv1); } 
  ( comma:COMMA kvn=keyEqualsValue { retVal.addChild(createTokenInfo(comma)); retVal.addChild(kvn); } )*
;

keyEqualsValue returns [ KeyEqualsValue retVal = null ]
:
{ StringLiteralOrQualifiedID key = null; StringLiteralOrQualifiedID value = null; }
  key=stringLiteralOrQualifiedID equals:EQUALS value=stringLiteralOrQualifiedID 
{ retVal = new KeyEqualsValue(key, createTokenInfo(equals), value); }
;

importStmts returns [ ImportStmts retVal = new ImportStmts() ]
:
{ ImportStmt importStmt = null; }
  ( importStmt=importStmt { retVal.addChild(importStmt); } )*
;

importStmt returns [ ImportStmt retVal = null ]
:
{ StringLiteralOrQualifiedID uri = null; }
  import_KW:"import" ( alias:ID equals:EQUALS )? uri=stringLiteralOrQualifiedID semi:SEMI 
{ retVal = new ImportStmt(createTokenInfo(import_KW), createTokenInfo(alias), createTokenInfo(equals), uri, createTokenInfo(semi)); }
;

topLevelDecls returns [ TopLevelDecls retVal = new TopLevelDecls() ]
:
{ TopLevelDecl topLevelDecl = null; }
  ( topLevelDecl=topLevelDecl { retVal.addChild(topLevelDecl); } )*
;

topLevelDecl returns [ TopLevelDecl retVal = null ]
:
{ Annotations annotations = null; }
annotations=annotations 
  ( retVal=subPackageDecl[annotations]
  | retVal=classDecl[annotations]
  | retVal=dataTypeDecl[annotations]
  | retVal=enumDecl[annotations]
  | retVal=mapEntryDecl[annotations]
  )
;

subPackageDecl [ Annotations annotations ]  returns [ SubPackageDecl retVal = null ]
:
{ TopLevelDecls topLevelDecls = null; }
  package_KW:"package" name:ID lcurly:LCURLY topLevelDecls=topLevelDecls rcurly:RCURLY 
{ retVal = new SubPackageDecl(annotations, createTokenInfo(package_KW), createTokenInfo(name), createTokenInfo(lcurly), topLevelDecls, createTokenInfo(rcurly)); }
;

classDecl [ Annotations annotations ]  returns [ ClassDecl retVal = null ]
:
{ AbstractModifier abstractModifier = null; ClassKind classKind = null; TypeParamsInfo typeParamsInfo = null; CommaListBoundExceptWild superTypes = null; BoundExceptWildcard instClassName = null; ClassMemberDecls classMemberDecls = null; }
  ( abstractModifier=abstractModifier )? classKind=classKind name:ID ( typeParamsInfo=typeParamsInfo )? ( extends_KW:"extends" superTypes=commaListBoundExceptWild )? ( colon:COLON instClassName=boundExceptWildcard )? lcurly:LCURLY classMemberDecls=classMemberDecls rcurly:RCURLY 
{ retVal = new ClassDecl(annotations, abstractModifier, classKind, createTokenInfo(name), typeParamsInfo, createTokenInfo(extends_KW), superTypes, createTokenInfo(colon), instClassName, createTokenInfo(lcurly), classMemberDecls, createTokenInfo(rcurly)); }
;

commaListBoundExceptWild returns [ CommaListBoundExceptWild retVal = new CommaListBoundExceptWild() ]
:
{ BoundExceptWildcard tb1 = null; BoundExceptWildcard tbn = null; }
  tb1=boundExceptWildcard { retVal.addChild(tb1); } 
  ( comma:COMMA tbn=boundExceptWildcard { retVal.addChild(createTokenInfo(comma)); retVal.addChild(tbn); } )*
;

abstractModifier returns [ AbstractModifier retVal = null ]
:
  abstract_KW:"abstract" 
{ retVal = new AbstractModifier(createTokenInfo(abstract_KW)); }
;

classKind returns [ ClassKind retVal = null ]
{ Token tok = LT(1); }
: ( "class"
  | "interface"
  )
{ retVal = new ClassKind(createTokenInfo(tok)); }
;

typeParamsInfo returns [ TypeParamsInfo retVal = null ]
:
{ OneOrMoreTypeParams oneOrMoreTypeParams = null; }
  lt:LT oneOrMoreTypeParams=oneOrMoreTypeParams gt:GT 
{ retVal = new TypeParamsInfo(createTokenInfo(lt), oneOrMoreTypeParams, createTokenInfo(gt)); }
;

oneOrMoreTypeParams returns [ OneOrMoreTypeParams retVal = new OneOrMoreTypeParams() ]
:
{ TypeParam tp1 = null; TypeParam tpn = null; }
  tp1=typeParam { retVal.addChild(tp1); } 
  ( comma:COMMA tpn=typeParam { retVal.addChild(createTokenInfo(comma)); retVal.addChild(tpn); } )*
;

typeParam returns [ TypeParam retVal = null ]
:
{ TypeBoundsInfo typeBoundsInfo = null; }
  typeVarName:ID ( typeBoundsInfo=typeBoundsInfo )? 
{ retVal = new TypeParam(createTokenInfo(typeVarName), typeBoundsInfo); }
;

typeBoundsInfo returns [ TypeBoundsInfo retVal = null ]
:
{ OneOrMoreTypeParamBounds oneOrMoreTypeParamBounds = null; }
  extends_KW:"extends" oneOrMoreTypeParamBounds=oneOrMoreTypeParamBounds 
{ retVal = new TypeBoundsInfo(createTokenInfo(extends_KW), oneOrMoreTypeParamBounds); }
;

oneOrMoreTypeParamBounds returns [ OneOrMoreTypeParamBounds retVal = new OneOrMoreTypeParamBounds() ]
:
{ BoundExceptWildcard tb1 = null; BoundExceptWildcard tbn = null; }
  tb1=boundExceptWildcard { retVal.addChild(tb1); } 
  ( amp:AMP tbn=boundExceptWildcard { retVal.addChild(createTokenInfo(amp)); retVal.addChild(tbn); } )*
;

boundExceptWildcard returns [ BoundExceptWildcard retVal = null ]
:
{ QualifiedID rawTNameOrTVarOrParamzedTName = null; OneOrMoreTypeArgs oneOrMoreTypeArgs = null; }
  rawTNameOrTVarOrParamzedTName=qualifiedID ( lt:LT oneOrMoreTypeArgs=oneOrMoreTypeArgs gt:GT )? 
{ retVal = new BoundExceptWildcard(rawTNameOrTVarOrParamzedTName, createTokenInfo(lt), oneOrMoreTypeArgs, createTokenInfo(gt)); }
;

oneOrMoreTypeArgs returns [ OneOrMoreTypeArgs retVal = new OneOrMoreTypeArgs() ]
:
{ TypeArg ta1 = null; TypeArg tan = null; }
  ta1=typeArg { retVal.addChild(ta1); } 
  ( comma:COMMA tan=typeArg { retVal.addChild(createTokenInfo(comma)); retVal.addChild(tan); } )*
;

typeArg returns [ TypeArg retVal = null ]
:
  ( retVal=boundExceptWildcard
  | retVal=wildcard
  )
;

wildcard returns [ Wildcard retVal = null ]
:
{ ExtendsOrSuper extendsOrSuper = null; BoundExceptWildcard boundExceptWildcard = null; }
  qmark:QMARK ( extendsOrSuper=extendsOrSuper boundExceptWildcard=boundExceptWildcard )? 
{ retVal = new Wildcard(createTokenInfo(qmark), extendsOrSuper, boundExceptWildcard); }
;

extendsOrSuper returns [ ExtendsOrSuper retVal = null ]
{ Token tok = LT(1); }
: ( "extends"
  | "super"
  )
{ retVal = new ExtendsOrSuper(createTokenInfo(tok)); }
;

classMemberDecls returns [ ClassMemberDecls retVal = new ClassMemberDecls() ]
:
{ ClassMemberDecl classMemberDecl = null; }
  ( classMemberDecl=classMemberDecl { retVal.addChild(classMemberDecl); } )*
;

classMemberDecl returns [ ClassMemberDecl retVal = null ]
:
{ Annotations annotations = null; Modifiers modifiers = null; }
annotations=annotations modifiers=modifiers 
  ( retVal=attribute[annotations, modifiers]
  | retVal=reference[annotations, modifiers]
  | retVal=operation[annotations, modifiers]
  )
;

attribute [ Annotations annotations, Modifiers modifiers ]  returns [ Attribute retVal = null ]
:
{ TypeWithMulti typeWithMulti = null; DefaultValueExpr defaultValueExpr = null; }
  attr_KW:"attr" typeWithMulti=typeWithMulti name:ID ( equals:EQUALS defaultValueExpr=defaultValueExpr )? semi:SEMI 
{ retVal = new Attribute(annotations, modifiers, createTokenInfo(attr_KW), typeWithMulti, createTokenInfo(name), createTokenInfo(equals), defaultValueExpr, createTokenInfo(semi)); }
;

typeWithMulti returns [ TypeWithMulti retVal = null ]
:
{ BoundExceptWildcard name = null; Multiplicity multiplicity = null; }
  name=boundExceptWildcard ( multiplicity=multiplicity )? 
{ retVal = new TypeWithMulti(name, multiplicity); }
;

multiplicity returns [ Multiplicity retVal = null ]
:
{ MultiplicityExpr multiplicityExpr = null; }
  lsquare:LSQUARE ( multiplicityExpr=multiplicityExpr )? rsquare:RSQUARE 
{ retVal = new Multiplicity(createTokenInfo(lsquare), multiplicityExpr, createTokenInfo(rsquare)); }
;

multiplicityExpr returns [ MultiplicityExpr retVal = null ]
:
{ SimpleMultiplicityExpr lowerBound = null; SimpleMultiplicityExpr upperBound = null; }
  lowerBound=simpleMultiplicityExpr ( dot_dot:DOT_DOT upperBound=simpleMultiplicityExpr )? 
{ retVal = new MultiplicityExpr(lowerBound, createTokenInfo(dot_dot), upperBound); }
;

simpleMultiplicityExpr returns [ SimpleMultiplicityExpr retVal = null ]
{ Token tok = LT(1); }
: ( STAR
  | PLUS
  | QMARK
  | INT_LITERAL
  )
{ retVal = new SimpleMultiplicityExpr(createTokenInfo(tok)); }
;

reference [ Annotations annotations, Modifiers modifiers ]  returns [ Reference retVal = null ]
:
{ ReferenceKind referenceKind = null; TypeWithMulti typeWithMulti = null; }
  referenceKind=referenceKind typeWithMulti=typeWithMulti ( hash:HASH oppositeName:ID )? name:ID semi:SEMI 
{ retVal = new Reference(annotations, modifiers, referenceKind, typeWithMulti, createTokenInfo(hash), createTokenInfo(oppositeName), createTokenInfo(name), createTokenInfo(semi)); }
;

referenceKind returns [ ReferenceKind retVal = null ]
{ Token tok = LT(1); }
: ( "ref"
  | "val"
  )
{ retVal = new ReferenceKind(createTokenInfo(tok)); }
;

modifiers returns [ Modifiers retVal = new Modifiers() ]
:
{ OptNegatedModifier optNegatedModifier = null; }
  ( optNegatedModifier=optNegatedModifier { retVal.addChild(optNegatedModifier); } )*
;

optNegatedModifier returns [ OptNegatedModifier retVal = null ]
:
{ Modifier modifier = null; }
  ( bang:BANG )? modifier=modifier 
{ retVal = new OptNegatedModifier(createTokenInfo(bang), modifier); }
;

modifier returns [ Modifier retVal = null ]
{ Token tok = LT(1); }
: ( "readonly"
  | "volatile"
  | "transient"
  | "unsettable"
  | "derived"
  | "unique"
  | "ordered"
  | "resolve"
  | "id"
  )
{ retVal = new Modifier(createTokenInfo(tok)); }
;

defaultValueExpr returns [ DefaultValueExpr retVal = null ]
:
  ( retVal=boolExpr
  | retVal=intExpr
  | retVal=stringExpr
  | retVal=charExpr
  )
;

boolExpr returns [ BoolExpr retVal = null ]
:
{ TrueOrFalse trueOrFalse = null; }
  trueOrFalse=trueOrFalse 
{ retVal = new BoolExpr(trueOrFalse); }
;

trueOrFalse returns [ TrueOrFalse retVal = null ]
{ Token tok = LT(1); }
: ( "true"
  | "false"
  )
{ retVal = new TrueOrFalse(createTokenInfo(tok)); }
;

intExpr returns [ IntExpr retVal = null ]
:
  ( minus:MINUS )? int_literal:INT_LITERAL 
{ retVal = new IntExpr(createTokenInfo(minus), createTokenInfo(int_literal)); }
;

stringExpr returns [ StringExpr retVal = null ]
:
  string_literal:STRING_LITERAL 
{ retVal = new StringExpr(createTokenInfo(string_literal)); }
;

charExpr returns [ CharExpr retVal = null ]
:
  char_literal:CHAR_LITERAL 
{ retVal = new CharExpr(createTokenInfo(char_literal)); }
;

operation [ Annotations annotations, Modifiers modifiers ]  returns [ Operation retVal = null ]
:
{ TypeParamsInfo typeParamsInfo = null; ResultType resType = null; Params params = null; CommaListBoundExceptWild exceptions = null; }
  op_KW:"op" ( typeParamsInfo=typeParamsInfo )? resType=resultType name:ID lparen:LPAREN ( params=params )? rparen:RPAREN ( throws_KW:"throws" exceptions=commaListBoundExceptWild )? semi:SEMI 
{ retVal = new Operation(annotations, modifiers, createTokenInfo(op_KW), typeParamsInfo, resType, createTokenInfo(name), createTokenInfo(lparen), params, createTokenInfo(rparen), createTokenInfo(throws_KW), exceptions, createTokenInfo(semi)); }
;

resultType returns [ ResultType retVal = null ]
:
  ( retVal=typeWithMulti
  | retVal=voidContainer
  )
;

voidContainer returns [ VoidContainer retVal = null ]
:
  void_KW:"void" 
{ retVal = new VoidContainer(createTokenInfo(void_KW)); }
;

params returns [ Params retVal = new Params() ]
:
{ Param p1 = null; Param pn = null; }
  p1=param { retVal.addChild(p1); } 
  ( comma:COMMA pn=param { retVal.addChild(createTokenInfo(comma)); retVal.addChild(pn); } )*
;

param returns [ Param retVal = null ]
:
{ Annotations leadingAnnotations = null; Modifiers modifiers = null; TypeWithMulti typeWithMulti = null; Annotations trailingAnnotations = null; }
  leadingAnnotations=annotations modifiers=modifiers typeWithMulti=typeWithMulti name:ID trailingAnnotations=annotations 
{ retVal = new Param(leadingAnnotations, modifiers, typeWithMulti, createTokenInfo(name), trailingAnnotations); }
;

dataTypeDecl [ Annotations annotations ]  returns [ DataTypeDecl retVal = null ]
:
{ TransientModifier transientModifier = null; StringLiteralOrQualifiedID instClassName = null; }
  ( transientModifier=transientModifier )? datatype_KW:"datatype" name:ID colon:COLON instClassName=stringLiteralOrQualifiedID semi:SEMI 
{ retVal = new DataTypeDecl(annotations, transientModifier, createTokenInfo(datatype_KW), createTokenInfo(name), createTokenInfo(colon), instClassName, createTokenInfo(semi)); }
;

transientModifier returns [ TransientModifier retVal = null ]
:
  transient_KW:"transient" 
{ retVal = new TransientModifier(createTokenInfo(transient_KW)); }
;

enumDecl [ Annotations annotations ]  returns [ EnumDecl retVal = null ]
:
{ EnumLiterals enumLiterals = null; }
  enum_KW:"enum" name:ID lcurly:LCURLY enumLiterals=enumLiterals rcurly:RCURLY 
{ retVal = new EnumDecl(annotations, createTokenInfo(enum_KW), createTokenInfo(name), createTokenInfo(lcurly), enumLiterals, createTokenInfo(rcurly)); }
;

enumLiterals returns [ EnumLiterals retVal = new EnumLiterals() ]
:
{ EnumLiteral enumLiteral = null; }
  ( enumLiteral=enumLiteral { retVal.addChild(enumLiteral); } )*
;

enumLiteral returns [ EnumLiteral retVal = null ]
:
{ Annotations leadingAnnotations = null; Annotations trailingAnnotations = null; }
  leadingAnnotations=annotations name:ID ( equals:EQUALS val:INT_LITERAL )? trailingAnnotations=annotations semi:SEMI 
{ retVal = new EnumLiteral(leadingAnnotations, createTokenInfo(name), createTokenInfo(equals), createTokenInfo(val), trailingAnnotations, createTokenInfo(semi)); }
;

mapEntryDecl [ Annotations annotations ]  returns [ MapEntryDecl retVal = null ]
:
{ TypeWithMulti key = null; TypeWithMulti value = null; }
  mapentry_KW:"mapentry" name:ID colon:COLON key=typeWithMulti minus_gt:MINUS_GT value=typeWithMulti semi:SEMI 
{ retVal = new MapEntryDecl(annotations, createTokenInfo(mapentry_KW), createTokenInfo(name), createTokenInfo(colon), key, createTokenInfo(minus_gt), value, createTokenInfo(semi)); }
;


class EmfaticLexer extends Lexer;

options
{
	testLiterals=false;
	k=3;
	// charVocabulary='\003'..'\377';
	charVocabulary='\u0000'..'\uFFFE';
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

STAR    : '*';
PLUS    : '+';
MINUS   : '-';
EQUALS  : '=';

QMARK   : '?';
BANG    : '!';
DOLLAR  : '$';
HASH    : '#';
AT      : '@';

DOT_DOT  : "..";
MINUS_GT : "->";
GT_LT    : "><";
LT_GT    : "<>";

STRING_LITERAL : '"' (ESC | ~'"')* '"' ;
CHAR_LITERAL   : '\'' (ESC | ~'\'') '\'';

// TODO: The (DIGIT DIGIT DIGIT) is cheesy it should be something like
//       ( options { warnWhenFollowAmbig = false; } : (DIGIT)+ )
//       but I don't want to deal with parsing that yet.
protected ESC :
'\\' ('t' | 'f' | 'r' | 'n' | '"' | '\'' | '\\' | (DIGIT DIGIT DIGIT) )
;

ID options { testLiterals=true; }
: ('~')? ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '_' | DIGIT)* ;

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

AMP: '&';
LT:'<';
GT:'>';

