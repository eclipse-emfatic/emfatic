// $ANTLR : "Emfatic.g" -> "EmfaticParser.java"$

package org.eclipse.emf.emfatic.core.lang.gen.parser;
import org.eclipse.emf.emfatic.core.lang.gen.ast.*;
import org.eclipse.gymnast.runtime.core.parser.*;
import org.eclipse.gymnast.runtime.core.ast.*;

import antlr.TokenBuffer;
import antlr.TokenStreamException;
import antlr.TokenStreamIOException;
import antlr.ANTLRException;
import antlr.LLkParser;
import antlr.Token;
import antlr.TokenStream;
import antlr.RecognitionException;
import antlr.NoViableAltException;
import antlr.MismatchedTokenException;
import antlr.SemanticException;
import antlr.ParserSharedInputState;
import antlr.collections.impl.BitSet;

public class EmfaticParser extends antlr.LLkParser       implements EmfaticParserTokenTypes
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


protected EmfaticParser(TokenBuffer tokenBuf, int k) {
  super(tokenBuf,k);
  tokenNames = _tokenNames;
}

public EmfaticParser(TokenBuffer tokenBuf) {
  this(tokenBuf,3);
}

protected EmfaticParser(TokenStream lexer, int k) {
  super(lexer,k);
  tokenNames = _tokenNames;
}

public EmfaticParser(TokenStream lexer) {
  this(lexer,3);
}

public EmfaticParser(ParserSharedInputState state) {
  super(state,3);
  tokenNames = _tokenNames;
}

	public final CompUnit  compUnit() throws RecognitionException, TokenStreamException {
		 CompUnit retVal = null ;
		
		
		try {      // for error handling
			PackageDecl packageDecl = null; ImportStmts importStmts = null; TopLevelDecls topLevelDecls = null;
			packageDecl=packageDecl();
			importStmts=importStmts();
			topLevelDecls=topLevelDecls();
			match(Token.EOF_TYPE);
			retVal = new CompUnit(packageDecl, importStmts, topLevelDecls);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_0);
		}
		return retVal;
	}
	
	public final PackageDecl  packageDecl() throws RecognitionException, TokenStreamException {
		 PackageDecl retVal = null ;
		
		Token  package_KW = null;
		Token  semi = null;
		
		try {      // for error handling
			Annotations annotations = null; QualifiedID name = null;
			annotations=annotations();
			package_KW = LT(1);
			match(LITERAL_package);
			name=qualifiedID();
			semi = LT(1);
			match(SEMI);
			retVal = new PackageDecl(annotations, createTokenInfo(package_KW), name, createTokenInfo(semi));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return retVal;
	}
	
	public final ImportStmts  importStmts() throws RecognitionException, TokenStreamException {
		 ImportStmts retVal = new ImportStmts() ;
		
		
		try {      // for error handling
			ImportStmt importStmt = null;
			{
			_loop22:
			do {
				if ((LA(1)==LITERAL_import)) {
					importStmt=importStmt();
					retVal.addChild(importStmt);
				}
				else {
					break _loop22;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_2);
		}
		return retVal;
	}
	
	public final TopLevelDecls  topLevelDecls() throws RecognitionException, TokenStreamException {
		 TopLevelDecls retVal = new TopLevelDecls() ;
		
		
		try {      // for error handling
			TopLevelDecl topLevelDecl = null;
			{
			_loop27:
			do {
				if ((_tokenSet_3.member(LA(1)))) {
					topLevelDecl=topLevelDecl();
					retVal.addChild(topLevelDecl);
				}
				else {
					break _loop27;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_4);
		}
		return retVal;
	}
	
	public final Annotations  annotations() throws RecognitionException, TokenStreamException {
		 Annotations retVal = new Annotations() ;
		
		
		try {      // for error handling
			Annotation annotation = null;
			{
			_loop14:
			do {
				if ((LA(1)==AT)) {
					annotation=annotation();
					retVal.addChild(annotation);
				}
				else {
					break _loop14;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_5);
		}
		return retVal;
	}
	
	public final QualifiedID  qualifiedID() throws RecognitionException, TokenStreamException {
		 QualifiedID retVal = new QualifiedID() ;
		
		Token  id1 = null;
		Token  idn = null;
		
		try {      // for error handling
			QidSeparator qidSeparator = null;
			id1 = LT(1);
			match(ID);
			retVal.addChild(createTokenInfo(id1));
			{
			_loop5:
			do {
				if ((LA(1)==DOT||LA(1)==DOLLAR)) {
					qidSeparator=qidSeparator();
					idn = LT(1);
					match(ID);
					retVal.addChild(qidSeparator); retVal.addChild(createTokenInfo(idn));
				}
				else {
					break _loop5;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_6);
		}
		return retVal;
	}
	
	public final QidSeparator  qidSeparator() throws RecognitionException, TokenStreamException {
		 QidSeparator retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case DOT:
			{
				match(DOT);
				break;
			}
			case DOLLAR:
			{
				match(DOLLAR);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new QidSeparator(createTokenInfo(tok));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return retVal;
	}
	
	public final StringLiteralOrQualifiedID  stringLiteralOrQualifiedID() throws RecognitionException, TokenStreamException {
		 StringLiteralOrQualifiedID retVal = null ;
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case STRING_LITERAL:
			{
				retVal=stringLiteralContainer();
				break;
			}
			case ID:
			{
				retVal=qualifiedIDContainer();
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
			recover(ex,_tokenSet_8);
		}
		return retVal;
	}
	
	public final StringLiteralContainer  stringLiteralContainer() throws RecognitionException, TokenStreamException {
		 StringLiteralContainer retVal = null ;
		
		Token  string_literal = null;
		
		try {      // for error handling
			string_literal = LT(1);
			match(STRING_LITERAL);
			retVal = new StringLiteralContainer(createTokenInfo(string_literal));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		return retVal;
	}
	
	public final QualifiedIDContainer  qualifiedIDContainer() throws RecognitionException, TokenStreamException {
		 QualifiedIDContainer retVal = null ;
		
		
		try {      // for error handling
			QualifiedID qualifiedID = null;
			qualifiedID=qualifiedID();
			retVal = new QualifiedIDContainer(qualifiedID);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_8);
		}
		return retVal;
	}
	
	public final Annotation  annotation() throws RecognitionException, TokenStreamException {
		 Annotation retVal = null ;
		
		Token  at = null;
		Token  lparen = null;
		Token  rparen = null;
		
		try {      // for error handling
			StringLiteralOrQualifiedID source = null; KeyEqualsValueList keyEqualsValueList = null;
			at = LT(1);
			match(AT);
			source=stringLiteralOrQualifiedID();
			lparen = LT(1);
			match(LPAREN);
			keyEqualsValueList=keyEqualsValueList();
			rparen = LT(1);
			match(RPAREN);
			retVal = new Annotation(createTokenInfo(at), source, createTokenInfo(lparen), keyEqualsValueList, createTokenInfo(rparen));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_9);
		}
		return retVal;
	}
	
	public final KeyEqualsValueList  keyEqualsValueList() throws RecognitionException, TokenStreamException {
		 KeyEqualsValueList retVal = new KeyEqualsValueList() ;
		
		Token  comma = null;
		
		try {      // for error handling
			KeyEqualsValue kv1 = null; KeyEqualsValue kvn = null;
			kv1=keyEqualsValue();
			retVal.addChild(kv1);
			{
			_loop18:
			do {
				if ((LA(1)==COMMA)) {
					comma = LT(1);
					match(COMMA);
					kvn=keyEqualsValue();
					retVal.addChild(createTokenInfo(comma)); retVal.addChild(kvn);
				}
				else {
					break _loop18;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		return retVal;
	}
	
	public final KeyEqualsValue  keyEqualsValue() throws RecognitionException, TokenStreamException {
		 KeyEqualsValue retVal = null ;
		
		Token  equals = null;
		
		try {      // for error handling
			StringLiteralOrQualifiedID key = null; StringLiteralOrQualifiedID value = null;
			key=stringLiteralOrQualifiedID();
			equals = LT(1);
			match(EQUALS);
			value=stringLiteralOrQualifiedID();
			retVal = new KeyEqualsValue(key, createTokenInfo(equals), value);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		return retVal;
	}
	
	public final ImportStmt  importStmt() throws RecognitionException, TokenStreamException {
		 ImportStmt retVal = null ;
		
		Token  import_KW = null;
		Token  alias = null;
		Token  equals = null;
		Token  semi = null;
		
		try {      // for error handling
			StringLiteralOrQualifiedID uri = null;
			import_KW = LT(1);
			match(LITERAL_import);
			{
			if ((LA(1)==ID) && (LA(2)==EQUALS)) {
				alias = LT(1);
				match(ID);
				equals = LT(1);
				match(EQUALS);
			}
			else if ((LA(1)==ID||LA(1)==STRING_LITERAL) && (LA(2)==SEMI||LA(2)==DOT||LA(2)==DOLLAR)) {
			}
			else {
				throw new NoViableAltException(LT(1), getFilename());
			}
			
			}
			uri=stringLiteralOrQualifiedID();
			semi = LT(1);
			match(SEMI);
			retVal = new ImportStmt(createTokenInfo(import_KW), createTokenInfo(alias), createTokenInfo(equals), uri, createTokenInfo(semi));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_1);
		}
		return retVal;
	}
	
	public final TopLevelDecl  topLevelDecl() throws RecognitionException, TokenStreamException {
		 TopLevelDecl retVal = null ;
		
		
		try {      // for error handling
			Annotations annotations = null;
			annotations=annotations();
			{
			switch ( LA(1)) {
			case LITERAL_package:
			{
				retVal=subPackageDecl(annotations);
				break;
			}
			case LITERAL_abstract:
			case LITERAL_class:
			case LITERAL_interface:
			{
				retVal=classDecl(annotations);
				break;
			}
			case LITERAL_transient:
			case LITERAL_datatype:
			{
				retVal=dataTypeDecl(annotations);
				break;
			}
			case LITERAL_enum:
			{
				retVal=enumDecl(annotations);
				break;
			}
			case LITERAL_mapentry:
			{
				retVal=mapEntryDecl(annotations);
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
			recover(ex,_tokenSet_12);
		}
		return retVal;
	}
	
	public final SubPackageDecl  subPackageDecl(
		 Annotations annotations 
	) throws RecognitionException, TokenStreamException {
		 SubPackageDecl retVal = null ;
		
		Token  package_KW = null;
		Token  name = null;
		Token  lcurly = null;
		Token  rcurly = null;
		
		try {      // for error handling
			TopLevelDecls topLevelDecls = null;
			package_KW = LT(1);
			match(LITERAL_package);
			name = LT(1);
			match(ID);
			lcurly = LT(1);
			match(LCURLY);
			topLevelDecls=topLevelDecls();
			rcurly = LT(1);
			match(RCURLY);
			retVal = new SubPackageDecl(annotations, createTokenInfo(package_KW), createTokenInfo(name), createTokenInfo(lcurly), topLevelDecls, createTokenInfo(rcurly));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		return retVal;
	}
	
	public final ClassDecl  classDecl(
		 Annotations annotations 
	) throws RecognitionException, TokenStreamException {
		 ClassDecl retVal = null ;
		
		Token  name = null;
		Token  extends_KW = null;
		Token  colon = null;
		Token  lcurly = null;
		Token  rcurly = null;
		
		try {      // for error handling
			AbstractModifier abstractModifier = null; ClassKind classKind = null; TypeParamsInfo typeParamsInfo = null; CommaListBoundExceptWild superTypes = null; BoundExceptWildcard instClassName = null; ClassMemberDecls classMemberDecls = null;
			{
			switch ( LA(1)) {
			case LITERAL_abstract:
			{
				abstractModifier=abstractModifier();
				break;
			}
			case LITERAL_class:
			case LITERAL_interface:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			classKind=classKind();
			name = LT(1);
			match(ID);
			{
			switch ( LA(1)) {
			case LT:
			{
				typeParamsInfo=typeParamsInfo();
				break;
			}
			case LCURLY:
			case LITERAL_extends:
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
			{
			switch ( LA(1)) {
			case LITERAL_extends:
			{
				extends_KW = LT(1);
				match(LITERAL_extends);
				superTypes=commaListBoundExceptWild();
				break;
			}
			case LCURLY:
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
			{
			switch ( LA(1)) {
			case COLON:
			{
				colon = LT(1);
				match(COLON);
				instClassName=boundExceptWildcard();
				break;
			}
			case LCURLY:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			lcurly = LT(1);
			match(LCURLY);
			classMemberDecls=classMemberDecls();
			rcurly = LT(1);
			match(RCURLY);
			retVal = new ClassDecl(annotations, abstractModifier, classKind, createTokenInfo(name), typeParamsInfo, createTokenInfo(extends_KW), superTypes, createTokenInfo(colon), instClassName, createTokenInfo(lcurly), classMemberDecls, createTokenInfo(rcurly));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		return retVal;
	}
	
	public final DataTypeDecl  dataTypeDecl(
		 Annotations annotations 
	) throws RecognitionException, TokenStreamException {
		 DataTypeDecl retVal = null ;
		
		Token  datatype_KW = null;
		Token  name = null;
		Token  colon = null;
		Token  semi = null;
		
		try {      // for error handling
			TransientModifier transientModifier = null; StringLiteralOrQualifiedID instClassName = null;
			{
			switch ( LA(1)) {
			case LITERAL_transient:
			{
				transientModifier=transientModifier();
				break;
			}
			case LITERAL_datatype:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			datatype_KW = LT(1);
			match(LITERAL_datatype);
			name = LT(1);
			match(ID);
			colon = LT(1);
			match(COLON);
			instClassName=stringLiteralOrQualifiedID();
			semi = LT(1);
			match(SEMI);
			retVal = new DataTypeDecl(annotations, transientModifier, createTokenInfo(datatype_KW), createTokenInfo(name), createTokenInfo(colon), instClassName, createTokenInfo(semi));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		return retVal;
	}
	
	public final EnumDecl  enumDecl(
		 Annotations annotations 
	) throws RecognitionException, TokenStreamException {
		 EnumDecl retVal = null ;
		
		Token  enum_KW = null;
		Token  name = null;
		Token  lcurly = null;
		Token  rcurly = null;
		
		try {      // for error handling
			EnumLiterals enumLiterals = null;
			enum_KW = LT(1);
			match(LITERAL_enum);
			name = LT(1);
			match(ID);
			lcurly = LT(1);
			match(LCURLY);
			enumLiterals=enumLiterals();
			rcurly = LT(1);
			match(RCURLY);
			retVal = new EnumDecl(annotations, createTokenInfo(enum_KW), createTokenInfo(name), createTokenInfo(lcurly), enumLiterals, createTokenInfo(rcurly));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		return retVal;
	}
	
	public final MapEntryDecl  mapEntryDecl(
		 Annotations annotations 
	) throws RecognitionException, TokenStreamException {
		 MapEntryDecl retVal = null ;
		
		Token  mapentry_KW = null;
		Token  name = null;
		Token  colon = null;
		Token  minus_gt = null;
		Token  semi = null;
		
		try {      // for error handling
			TypeWithMulti key = null; TypeWithMulti value = null;
			mapentry_KW = LT(1);
			match(LITERAL_mapentry);
			name = LT(1);
			match(ID);
			colon = LT(1);
			match(COLON);
			key=typeWithMulti();
			minus_gt = LT(1);
			match(MINUS_GT);
			value=typeWithMulti();
			semi = LT(1);
			match(SEMI);
			retVal = new MapEntryDecl(annotations, createTokenInfo(mapentry_KW), createTokenInfo(name), createTokenInfo(colon), key, createTokenInfo(minus_gt), value, createTokenInfo(semi));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_12);
		}
		return retVal;
	}
	
	public final AbstractModifier  abstractModifier() throws RecognitionException, TokenStreamException {
		 AbstractModifier retVal = null ;
		
		Token  abstract_KW = null;
		
		try {      // for error handling
			abstract_KW = LT(1);
			match(LITERAL_abstract);
			retVal = new AbstractModifier(createTokenInfo(abstract_KW));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_13);
		}
		return retVal;
	}
	
	public final ClassKind  classKind() throws RecognitionException, TokenStreamException {
		 ClassKind retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_class:
			{
				match(LITERAL_class);
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
			retVal = new ClassKind(createTokenInfo(tok));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return retVal;
	}
	
	public final TypeParamsInfo  typeParamsInfo() throws RecognitionException, TokenStreamException {
		 TypeParamsInfo retVal = null ;
		
		Token  lt = null;
		Token  gt = null;
		
		try {      // for error handling
			OneOrMoreTypeParams oneOrMoreTypeParams = null;
			lt = LT(1);
			match(LT);
			oneOrMoreTypeParams=oneOrMoreTypeParams();
			gt = LT(1);
			match(GT);
			retVal = new TypeParamsInfo(createTokenInfo(lt), oneOrMoreTypeParams, createTokenInfo(gt));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_14);
		}
		return retVal;
	}
	
	public final CommaListBoundExceptWild  commaListBoundExceptWild() throws RecognitionException, TokenStreamException {
		 CommaListBoundExceptWild retVal = new CommaListBoundExceptWild() ;
		
		Token  comma = null;
		
		try {      // for error handling
			BoundExceptWildcard tb1 = null; BoundExceptWildcard tbn = null;
			tb1=boundExceptWildcard();
			retVal.addChild(tb1);
			{
			_loop38:
			do {
				if ((LA(1)==COMMA)) {
					comma = LT(1);
					match(COMMA);
					tbn=boundExceptWildcard();
					retVal.addChild(createTokenInfo(comma)); retVal.addChild(tbn);
				}
				else {
					break _loop38;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_15);
		}
		return retVal;
	}
	
	public final BoundExceptWildcard  boundExceptWildcard() throws RecognitionException, TokenStreamException {
		 BoundExceptWildcard retVal = null ;
		
		Token  lt = null;
		Token  gt = null;
		
		try {      // for error handling
			QualifiedID rawTNameOrTVarOrParamzedTName = null; OneOrMoreTypeArgs oneOrMoreTypeArgs = null;
			rawTNameOrTVarOrParamzedTName=qualifiedID();
			{
			switch ( LA(1)) {
			case LT:
			{
				lt = LT(1);
				match(LT);
				oneOrMoreTypeArgs=oneOrMoreTypeArgs();
				gt = LT(1);
				match(GT);
				break;
			}
			case SEMI:
			case ID:
			case COMMA:
			case LCURLY:
			case COLON:
			case GT:
			case AMP:
			case LSQUARE:
			case HASH:
			case MINUS_GT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new BoundExceptWildcard(rawTNameOrTVarOrParamzedTName, createTokenInfo(lt), oneOrMoreTypeArgs, createTokenInfo(gt));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_16);
		}
		return retVal;
	}
	
	public final ClassMemberDecls  classMemberDecls() throws RecognitionException, TokenStreamException {
		 ClassMemberDecls retVal = new ClassMemberDecls() ;
		
		
		try {      // for error handling
			ClassMemberDecl classMemberDecl = null;
			{
			_loop65:
			do {
				if ((_tokenSet_17.member(LA(1)))) {
					classMemberDecl=classMemberDecl();
					retVal.addChild(classMemberDecl);
				}
				else {
					break _loop65;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		return retVal;
	}
	
	public final OneOrMoreTypeParams  oneOrMoreTypeParams() throws RecognitionException, TokenStreamException {
		 OneOrMoreTypeParams retVal = new OneOrMoreTypeParams() ;
		
		Token  comma = null;
		
		try {      // for error handling
			TypeParam tp1 = null; TypeParam tpn = null;
			tp1=typeParam();
			retVal.addChild(tp1);
			{
			_loop45:
			do {
				if ((LA(1)==COMMA)) {
					comma = LT(1);
					match(COMMA);
					tpn=typeParam();
					retVal.addChild(createTokenInfo(comma)); retVal.addChild(tpn);
				}
				else {
					break _loop45;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_19);
		}
		return retVal;
	}
	
	public final TypeParam  typeParam() throws RecognitionException, TokenStreamException {
		 TypeParam retVal = null ;
		
		Token  typeVarName = null;
		
		try {      // for error handling
			TypeBoundsInfo typeBoundsInfo = null;
			typeVarName = LT(1);
			match(ID);
			{
			switch ( LA(1)) {
			case LITERAL_extends:
			{
				typeBoundsInfo=typeBoundsInfo();
				break;
			}
			case COMMA:
			case GT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new TypeParam(createTokenInfo(typeVarName), typeBoundsInfo);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		return retVal;
	}
	
	public final TypeBoundsInfo  typeBoundsInfo() throws RecognitionException, TokenStreamException {
		 TypeBoundsInfo retVal = null ;
		
		Token  extends_KW = null;
		
		try {      // for error handling
			OneOrMoreTypeParamBounds oneOrMoreTypeParamBounds = null;
			extends_KW = LT(1);
			match(LITERAL_extends);
			oneOrMoreTypeParamBounds=oneOrMoreTypeParamBounds();
			retVal = new TypeBoundsInfo(createTokenInfo(extends_KW), oneOrMoreTypeParamBounds);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		return retVal;
	}
	
	public final OneOrMoreTypeParamBounds  oneOrMoreTypeParamBounds() throws RecognitionException, TokenStreamException {
		 OneOrMoreTypeParamBounds retVal = new OneOrMoreTypeParamBounds() ;
		
		Token  amp = null;
		
		try {      // for error handling
			BoundExceptWildcard tb1 = null; BoundExceptWildcard tbn = null;
			tb1=boundExceptWildcard();
			retVal.addChild(tb1);
			{
			_loop51:
			do {
				if ((LA(1)==AMP)) {
					amp = LT(1);
					match(AMP);
					tbn=boundExceptWildcard();
					retVal.addChild(createTokenInfo(amp)); retVal.addChild(tbn);
				}
				else {
					break _loop51;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		return retVal;
	}
	
	public final OneOrMoreTypeArgs  oneOrMoreTypeArgs() throws RecognitionException, TokenStreamException {
		 OneOrMoreTypeArgs retVal = new OneOrMoreTypeArgs() ;
		
		Token  comma = null;
		
		try {      // for error handling
			TypeArg ta1 = null; TypeArg tan = null;
			ta1=typeArg();
			retVal.addChild(ta1);
			{
			_loop56:
			do {
				if ((LA(1)==COMMA)) {
					comma = LT(1);
					match(COMMA);
					tan=typeArg();
					retVal.addChild(createTokenInfo(comma)); retVal.addChild(tan);
				}
				else {
					break _loop56;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_19);
		}
		return retVal;
	}
	
	public final TypeArg  typeArg() throws RecognitionException, TokenStreamException {
		 TypeArg retVal = null ;
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case ID:
			{
				retVal=boundExceptWildcard();
				break;
			}
			case QMARK:
			{
				retVal=wildcard();
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
			recover(ex,_tokenSet_20);
		}
		return retVal;
	}
	
	public final Wildcard  wildcard() throws RecognitionException, TokenStreamException {
		 Wildcard retVal = null ;
		
		Token  qmark = null;
		
		try {      // for error handling
			ExtendsOrSuper extendsOrSuper = null; BoundExceptWildcard boundExceptWildcard = null;
			qmark = LT(1);
			match(QMARK);
			{
			switch ( LA(1)) {
			case LITERAL_extends:
			case LITERAL_super:
			{
				extendsOrSuper=extendsOrSuper();
				boundExceptWildcard=boundExceptWildcard();
				break;
			}
			case COMMA:
			case GT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new Wildcard(createTokenInfo(qmark), extendsOrSuper, boundExceptWildcard);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_20);
		}
		return retVal;
	}
	
	public final ExtendsOrSuper  extendsOrSuper() throws RecognitionException, TokenStreamException {
		 ExtendsOrSuper retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_extends:
			{
				match(LITERAL_extends);
				break;
			}
			case LITERAL_super:
			{
				match(LITERAL_super);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new ExtendsOrSuper(createTokenInfo(tok));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return retVal;
	}
	
	public final ClassMemberDecl  classMemberDecl() throws RecognitionException, TokenStreamException {
		 ClassMemberDecl retVal = null ;
		
		
		try {      // for error handling
			Annotations annotations = null; Modifiers modifiers = null;
			annotations=annotations();
			modifiers=modifiers();
			{
			switch ( LA(1)) {
			case LITERAL_attr:
			{
				retVal=attribute(annotations, modifiers);
				break;
			}
			case LITERAL_ref:
			case LITERAL_val:
			{
				retVal=reference(annotations, modifiers);
				break;
			}
			case LITERAL_op:
			{
				retVal=operation(annotations, modifiers);
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
			recover(ex,_tokenSet_21);
		}
		return retVal;
	}
	
	public final Modifiers  modifiers() throws RecognitionException, TokenStreamException {
		 Modifiers retVal = new Modifiers() ;
		
		
		try {      // for error handling
			OptNegatedModifier optNegatedModifier = null;
			{
			_loop84:
			do {
				if (((LA(1) >= BANG && LA(1) <= LITERAL_id))) {
					optNegatedModifier=optNegatedModifier();
					retVal.addChild(optNegatedModifier);
				}
				else {
					break _loop84;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_22);
		}
		return retVal;
	}
	
	public final Attribute  attribute(
		 Annotations annotations, Modifiers modifiers 
	) throws RecognitionException, TokenStreamException {
		 Attribute retVal = null ;
		
		Token  attr_KW = null;
		Token  name = null;
		Token  equals = null;
		Token  semi = null;
		
		try {      // for error handling
			TypeWithMulti typeWithMulti = null; DefaultValueExpr defaultValueExpr = null;
			attr_KW = LT(1);
			match(LITERAL_attr);
			typeWithMulti=typeWithMulti();
			name = LT(1);
			match(ID);
			{
			switch ( LA(1)) {
			case EQUALS:
			{
				equals = LT(1);
				match(EQUALS);
				defaultValueExpr=defaultValueExpr();
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
			semi = LT(1);
			match(SEMI);
			retVal = new Attribute(annotations, modifiers, createTokenInfo(attr_KW), typeWithMulti, createTokenInfo(name), createTokenInfo(equals), defaultValueExpr, createTokenInfo(semi));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_21);
		}
		return retVal;
	}
	
	public final Reference  reference(
		 Annotations annotations, Modifiers modifiers 
	) throws RecognitionException, TokenStreamException {
		 Reference retVal = null ;
		
		Token  hash = null;
		Token  oppositeName = null;
		Token  name = null;
		Token  semi = null;
		
		try {      // for error handling
			ReferenceKind referenceKind = null; TypeWithMulti typeWithMulti = null;
			referenceKind=referenceKind();
			typeWithMulti=typeWithMulti();
			{
			switch ( LA(1)) {
			case HASH:
			{
				hash = LT(1);
				match(HASH);
				oppositeName = LT(1);
				match(ID);
				break;
			}
			case ID:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			name = LT(1);
			match(ID);
			semi = LT(1);
			match(SEMI);
			retVal = new Reference(annotations, modifiers, referenceKind, typeWithMulti, createTokenInfo(hash), createTokenInfo(oppositeName), createTokenInfo(name), createTokenInfo(semi));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_21);
		}
		return retVal;
	}
	
	public final Operation  operation(
		 Annotations annotations, Modifiers modifiers 
	) throws RecognitionException, TokenStreamException {
		 Operation retVal = null ;
		
		Token  op_KW = null;
		Token  name = null;
		Token  lparen = null;
		Token  rparen = null;
		Token  throws_KW = null;
		Token  semi = null;
		
		try {      // for error handling
			TypeParamsInfo typeParamsInfo = null; ResultType resType = null; Params params = null; CommaListBoundExceptWild exceptions = null;
			op_KW = LT(1);
			match(LITERAL_op);
			{
			switch ( LA(1)) {
			case LT:
			{
				typeParamsInfo=typeParamsInfo();
				break;
			}
			case ID:
			case LITERAL_void:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			resType=resultType();
			name = LT(1);
			match(ID);
			lparen = LT(1);
			match(LPAREN);
			{
			switch ( LA(1)) {
			case ID:
			case AT:
			case BANG:
			case LITERAL_readonly:
			case LITERAL_volatile:
			case LITERAL_transient:
			case LITERAL_unsettable:
			case LITERAL_derived:
			case LITERAL_unique:
			case LITERAL_ordered:
			case LITERAL_resolve:
			case LITERAL_id:
			{
				params=params();
				break;
			}
			case RPAREN:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			rparen = LT(1);
			match(RPAREN);
			{
			switch ( LA(1)) {
			case LITERAL_throws:
			{
				throws_KW = LT(1);
				match(LITERAL_throws);
				exceptions=commaListBoundExceptWild();
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
			semi = LT(1);
			match(SEMI);
			retVal = new Operation(annotations, modifiers, createTokenInfo(op_KW), typeParamsInfo, resType, createTokenInfo(name), createTokenInfo(lparen), params, createTokenInfo(rparen), createTokenInfo(throws_KW), exceptions, createTokenInfo(semi));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_21);
		}
		return retVal;
	}
	
	public final TypeWithMulti  typeWithMulti() throws RecognitionException, TokenStreamException {
		 TypeWithMulti retVal = null ;
		
		
		try {      // for error handling
			BoundExceptWildcard name = null; Multiplicity multiplicity = null;
			name=boundExceptWildcard();
			{
			switch ( LA(1)) {
			case LSQUARE:
			{
				multiplicity=multiplicity();
				break;
			}
			case SEMI:
			case ID:
			case HASH:
			case MINUS_GT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new TypeWithMulti(name, multiplicity);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
		return retVal;
	}
	
	public final DefaultValueExpr  defaultValueExpr() throws RecognitionException, TokenStreamException {
		 DefaultValueExpr retVal = null ;
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_true:
			case LITERAL_false:
			{
				retVal=boolExpr();
				break;
			}
			case INT_LITERAL:
			case MINUS:
			{
				retVal=intExpr();
				break;
			}
			case STRING_LITERAL:
			{
				retVal=stringExpr();
				break;
			}
			case CHAR_LITERAL:
			{
				retVal=charExpr();
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
			recover(ex,_tokenSet_24);
		}
		return retVal;
	}
	
	public final Multiplicity  multiplicity() throws RecognitionException, TokenStreamException {
		 Multiplicity retVal = null ;
		
		Token  lsquare = null;
		Token  rsquare = null;
		
		try {      // for error handling
			MultiplicityExpr multiplicityExpr = null;
			lsquare = LT(1);
			match(LSQUARE);
			{
			switch ( LA(1)) {
			case QMARK:
			case STAR:
			case PLUS:
			case INT_LITERAL:
			{
				multiplicityExpr=multiplicityExpr();
				break;
			}
			case RSQUARE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			rsquare = LT(1);
			match(RSQUARE);
			retVal = new Multiplicity(createTokenInfo(lsquare), multiplicityExpr, createTokenInfo(rsquare));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_23);
		}
		return retVal;
	}
	
	public final MultiplicityExpr  multiplicityExpr() throws RecognitionException, TokenStreamException {
		 MultiplicityExpr retVal = null ;
		
		Token  dot_dot = null;
		
		try {      // for error handling
			SimpleMultiplicityExpr lowerBound = null; SimpleMultiplicityExpr upperBound = null;
			lowerBound=simpleMultiplicityExpr();
			{
			switch ( LA(1)) {
			case DOT_DOT:
			{
				dot_dot = LT(1);
				match(DOT_DOT);
				upperBound=simpleMultiplicityExpr();
				break;
			}
			case RSQUARE:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new MultiplicityExpr(lowerBound, createTokenInfo(dot_dot), upperBound);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_25);
		}
		return retVal;
	}
	
	public final SimpleMultiplicityExpr  simpleMultiplicityExpr() throws RecognitionException, TokenStreamException {
		 SimpleMultiplicityExpr retVal = null ;
		
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
			case QMARK:
			{
				match(QMARK);
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
			retVal = new SimpleMultiplicityExpr(createTokenInfo(tok));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_26);
		}
		return retVal;
	}
	
	public final ReferenceKind  referenceKind() throws RecognitionException, TokenStreamException {
		 ReferenceKind retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_ref:
			{
				match(LITERAL_ref);
				break;
			}
			case LITERAL_val:
			{
				match(LITERAL_val);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new ReferenceKind(createTokenInfo(tok));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return retVal;
	}
	
	public final OptNegatedModifier  optNegatedModifier() throws RecognitionException, TokenStreamException {
		 OptNegatedModifier retVal = null ;
		
		Token  bang = null;
		
		try {      // for error handling
			Modifier modifier = null;
			{
			switch ( LA(1)) {
			case BANG:
			{
				bang = LT(1);
				match(BANG);
				break;
			}
			case LITERAL_readonly:
			case LITERAL_volatile:
			case LITERAL_transient:
			case LITERAL_unsettable:
			case LITERAL_derived:
			case LITERAL_unique:
			case LITERAL_ordered:
			case LITERAL_resolve:
			case LITERAL_id:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			modifier=modifier();
			retVal = new OptNegatedModifier(createTokenInfo(bang), modifier);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_27);
		}
		return retVal;
	}
	
	public final Modifier  modifier() throws RecognitionException, TokenStreamException {
		 Modifier retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_readonly:
			{
				match(LITERAL_readonly);
				break;
			}
			case LITERAL_volatile:
			{
				match(LITERAL_volatile);
				break;
			}
			case LITERAL_transient:
			{
				match(LITERAL_transient);
				break;
			}
			case LITERAL_unsettable:
			{
				match(LITERAL_unsettable);
				break;
			}
			case LITERAL_derived:
			{
				match(LITERAL_derived);
				break;
			}
			case LITERAL_unique:
			{
				match(LITERAL_unique);
				break;
			}
			case LITERAL_ordered:
			{
				match(LITERAL_ordered);
				break;
			}
			case LITERAL_resolve:
			{
				match(LITERAL_resolve);
				break;
			}
			case LITERAL_id:
			{
				match(LITERAL_id);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new Modifier(createTokenInfo(tok));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_27);
		}
		return retVal;
	}
	
	public final BoolExpr  boolExpr() throws RecognitionException, TokenStreamException {
		 BoolExpr retVal = null ;
		
		
		try {      // for error handling
			TrueOrFalse trueOrFalse = null;
			trueOrFalse=trueOrFalse();
			retVal = new BoolExpr(trueOrFalse);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_24);
		}
		return retVal;
	}
	
	public final IntExpr  intExpr() throws RecognitionException, TokenStreamException {
		 IntExpr retVal = null ;
		
		Token  minus = null;
		Token  int_literal = null;
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case MINUS:
			{
				minus = LT(1);
				match(MINUS);
				break;
			}
			case INT_LITERAL:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			int_literal = LT(1);
			match(INT_LITERAL);
			retVal = new IntExpr(createTokenInfo(minus), createTokenInfo(int_literal));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_24);
		}
		return retVal;
	}
	
	public final StringExpr  stringExpr() throws RecognitionException, TokenStreamException {
		 StringExpr retVal = null ;
		
		Token  string_literal = null;
		
		try {      // for error handling
			string_literal = LT(1);
			match(STRING_LITERAL);
			retVal = new StringExpr(createTokenInfo(string_literal));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_24);
		}
		return retVal;
	}
	
	public final CharExpr  charExpr() throws RecognitionException, TokenStreamException {
		 CharExpr retVal = null ;
		
		Token  char_literal = null;
		
		try {      // for error handling
			char_literal = LT(1);
			match(CHAR_LITERAL);
			retVal = new CharExpr(createTokenInfo(char_literal));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_24);
		}
		return retVal;
	}
	
	public final TrueOrFalse  trueOrFalse() throws RecognitionException, TokenStreamException {
		 TrueOrFalse retVal = null ;
		
		Token tok = LT(1);
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case LITERAL_true:
			{
				match(LITERAL_true);
				break;
			}
			case LITERAL_false:
			{
				match(LITERAL_false);
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			retVal = new TrueOrFalse(createTokenInfo(tok));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_24);
		}
		return retVal;
	}
	
	public final ResultType  resultType() throws RecognitionException, TokenStreamException {
		 ResultType retVal = null ;
		
		
		try {      // for error handling
			{
			switch ( LA(1)) {
			case ID:
			{
				retVal=typeWithMulti();
				break;
			}
			case LITERAL_void:
			{
				retVal=voidContainer();
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
			recover(ex,_tokenSet_7);
		}
		return retVal;
	}
	
	public final Params  params() throws RecognitionException, TokenStreamException {
		 Params retVal = new Params() ;
		
		Token  comma = null;
		
		try {      // for error handling
			Param p1 = null; Param pn = null;
			p1=param();
			retVal.addChild(p1);
			{
			_loop107:
			do {
				if ((LA(1)==COMMA)) {
					comma = LT(1);
					match(COMMA);
					pn=param();
					retVal.addChild(createTokenInfo(comma)); retVal.addChild(pn);
				}
				else {
					break _loop107;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_10);
		}
		return retVal;
	}
	
	public final VoidContainer  voidContainer() throws RecognitionException, TokenStreamException {
		 VoidContainer retVal = null ;
		
		Token  void_KW = null;
		
		try {      // for error handling
			void_KW = LT(1);
			match(LITERAL_void);
			retVal = new VoidContainer(createTokenInfo(void_KW));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_7);
		}
		return retVal;
	}
	
	public final Param  param() throws RecognitionException, TokenStreamException {
		 Param retVal = null ;
		
		Token  name = null;
		
		try {      // for error handling
			Annotations leadingAnnotations = null; Modifiers modifiers = null; TypeWithMulti typeWithMulti = null; Annotations trailingAnnotations = null;
			leadingAnnotations=annotations();
			modifiers=modifiers();
			typeWithMulti=typeWithMulti();
			name = LT(1);
			match(ID);
			trailingAnnotations=annotations();
			retVal = new Param(leadingAnnotations, modifiers, typeWithMulti, createTokenInfo(name), trailingAnnotations);
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_11);
		}
		return retVal;
	}
	
	public final TransientModifier  transientModifier() throws RecognitionException, TokenStreamException {
		 TransientModifier retVal = null ;
		
		Token  transient_KW = null;
		
		try {      // for error handling
			transient_KW = LT(1);
			match(LITERAL_transient);
			retVal = new TransientModifier(createTokenInfo(transient_KW));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_28);
		}
		return retVal;
	}
	
	public final EnumLiterals  enumLiterals() throws RecognitionException, TokenStreamException {
		 EnumLiterals retVal = new EnumLiterals() ;
		
		
		try {      // for error handling
			EnumLiteral enumLiteral = null;
			{
			_loop115:
			do {
				if ((LA(1)==ID||LA(1)==AT)) {
					enumLiteral=enumLiteral();
					retVal.addChild(enumLiteral);
				}
				else {
					break _loop115;
				}
				
			} while (true);
			}
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_18);
		}
		return retVal;
	}
	
	public final EnumLiteral  enumLiteral() throws RecognitionException, TokenStreamException {
		 EnumLiteral retVal = null ;
		
		Token  name = null;
		Token  equals = null;
		Token  val = null;
		Token  semi = null;
		
		try {      // for error handling
			Annotations leadingAnnotations = null; Annotations trailingAnnotations = null;
			leadingAnnotations=annotations();
			name = LT(1);
			match(ID);
			{
			switch ( LA(1)) {
			case EQUALS:
			{
				equals = LT(1);
				match(EQUALS);
				val = LT(1);
				match(INT_LITERAL);
				break;
			}
			case SEMI:
			case AT:
			{
				break;
			}
			default:
			{
				throw new NoViableAltException(LT(1), getFilename());
			}
			}
			}
			trailingAnnotations=annotations();
			semi = LT(1);
			match(SEMI);
			retVal = new EnumLiteral(leadingAnnotations, createTokenInfo(name), createTokenInfo(equals), createTokenInfo(val), trailingAnnotations, createTokenInfo(semi));
		}
		catch (RecognitionException ex) {
			reportError(ex);
			recover(ex,_tokenSet_29);
		}
		return retVal;
	}
	
	
	public static final String[] _tokenNames = {
		"<0>",
		"EOF",
		"<2>",
		"NULL_TREE_LOOKAHEAD",
		"\"package\"",
		"SEMI",
		"ID",
		"DOT",
		"DOLLAR",
		"STRING_LITERAL",
		"AT",
		"LPAREN",
		"RPAREN",
		"COMMA",
		"EQUALS",
		"\"import\"",
		"LCURLY",
		"RCURLY",
		"\"extends\"",
		"COLON",
		"\"abstract\"",
		"\"class\"",
		"\"interface\"",
		"LT",
		"GT",
		"AMP",
		"QMARK",
		"\"super\"",
		"\"attr\"",
		"LSQUARE",
		"RSQUARE",
		"DOT_DOT",
		"STAR",
		"PLUS",
		"INT_LITERAL",
		"HASH",
		"\"ref\"",
		"\"val\"",
		"BANG",
		"\"readonly\"",
		"\"volatile\"",
		"\"transient\"",
		"\"unsettable\"",
		"\"derived\"",
		"\"unique\"",
		"\"ordered\"",
		"\"resolve\"",
		"\"id\"",
		"\"true\"",
		"\"false\"",
		"MINUS",
		"CHAR_LITERAL",
		"\"op\"",
		"\"throws\"",
		"\"void\"",
		"\"datatype\"",
		"\"enum\"",
		"\"mapentry\"",
		"MINUS_GT",
		"GT_LT",
		"LT_GT",
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
		long[] data = { 252203778163377170L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_1 = new BitSet(mk_tokenSet_1());
	private static final long[] mk_tokenSet_2() {
		long[] data = { 252203778163344402L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_2 = new BitSet(mk_tokenSet_2());
	private static final long[] mk_tokenSet_3() {
		long[] data = { 252203778163344400L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_3 = new BitSet(mk_tokenSet_3());
	private static final long[] mk_tokenSet_4() {
		long[] data = { 131074L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_4 = new BitSet(mk_tokenSet_4());
	private static final long[] mk_tokenSet_5() {
		long[] data = { 256986585293140080L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_5 = new BitSet(mk_tokenSet_5());
	private static final long[] mk_tokenSet_6() {
		long[] data = { 288230411107661920L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_6 = new BitSet(mk_tokenSet_6());
	private static final long[] mk_tokenSet_7() {
		long[] data = { 64L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_7 = new BitSet(mk_tokenSet_7());
	private static final long[] mk_tokenSet_8() {
		long[] data = { 30752L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_8 = new BitSet(mk_tokenSet_8());
	private static final long[] mk_tokenSet_9() {
		long[] data = { 256986585293141104L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_9 = new BitSet(mk_tokenSet_9());
	private static final long[] mk_tokenSet_10() {
		long[] data = { 4096L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_10 = new BitSet(mk_tokenSet_10());
	private static final long[] mk_tokenSet_11() {
		long[] data = { 12288L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_11 = new BitSet(mk_tokenSet_11());
	private static final long[] mk_tokenSet_12() {
		long[] data = { 252203778163475474L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_12 = new BitSet(mk_tokenSet_12());
	private static final long[] mk_tokenSet_13() {
		long[] data = { 6291456L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_13 = new BitSet(mk_tokenSet_13());
	private static final long[] mk_tokenSet_14() {
		long[] data = { 18014398510334016L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_14 = new BitSet(mk_tokenSet_14());
	private static final long[] mk_tokenSet_15() {
		long[] data = { 589856L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_15 = new BitSet(mk_tokenSet_15());
	private static final long[] mk_tokenSet_16() {
		long[] data = { 288230411099250784L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_16 = new BitSet(mk_tokenSet_16());
	private static final long[] mk_tokenSet_17() {
		long[] data = { 4785006153040896L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_17 = new BitSet(mk_tokenSet_17());
	private static final long[] mk_tokenSet_18() {
		long[] data = { 131072L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_18 = new BitSet(mk_tokenSet_18());
	private static final long[] mk_tokenSet_19() {
		long[] data = { 16777216L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_19 = new BitSet(mk_tokenSet_19());
	private static final long[] mk_tokenSet_20() {
		long[] data = { 16785408L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_20 = new BitSet(mk_tokenSet_20());
	private static final long[] mk_tokenSet_21() {
		long[] data = { 4785006153171968L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_21 = new BitSet(mk_tokenSet_21());
	private static final long[] mk_tokenSet_22() {
		long[] data = { 4503806054236224L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_22 = new BitSet(mk_tokenSet_22());
	private static final long[] mk_tokenSet_23() {
		long[] data = { 288230410511450208L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_23 = new BitSet(mk_tokenSet_23());
	private static final long[] mk_tokenSet_24() {
		long[] data = { 32L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_24 = new BitSet(mk_tokenSet_24());
	private static final long[] mk_tokenSet_25() {
		long[] data = { 1073741824L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_25 = new BitSet(mk_tokenSet_25());
	private static final long[] mk_tokenSet_26() {
		long[] data = { 3221225472L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_26 = new BitSet(mk_tokenSet_26());
	private static final long[] mk_tokenSet_27() {
		long[] data = { 4785006153039936L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_27 = new BitSet(mk_tokenSet_27());
	private static final long[] mk_tokenSet_28() {
		long[] data = { 36028797018963968L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_28 = new BitSet(mk_tokenSet_28());
	private static final long[] mk_tokenSet_29() {
		long[] data = { 132160L, 0L};
		return data;
	}
	public static final BitSet _tokenSet_29 = new BitSet(mk_tokenSet_29());
	
	}
