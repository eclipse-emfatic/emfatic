/* Generated By:JavaCC: Do not edit this line. EmfaticParserConstants.java */
package org.eclipse.emf.emfatic.core.lang.gen.parser;


/**
 * Token literal values and constants.
 * Generated by org.javacc.parser.OtherFilesGen#start()
 */
public interface EmfaticParserConstants {

  /** End of File. */
  int EOF = 0;
  /** RegularExpression Id. */
  int SINGLELINECOMMENT = 1;
  /** RegularExpression Id. */
  int WS = 2;
  /** RegularExpression Id. */
  int PACKAGEKW = 6;
  /** RegularExpression Id. */
  int CLASSKW = 7;
  /** RegularExpression Id. */
  int IMPORTKW = 8;
  /** RegularExpression Id. */
  int ABSTRACTKW = 9;
  /** RegularExpression Id. */
  int INTERFACEKW = 10;
  /** RegularExpression Id. */
  int EXTENDSKW = 11;
  /** RegularExpression Id. */
  int SUPERKW = 12;
  /** RegularExpression Id. */
  int ATTRKW = 13;
  /** RegularExpression Id. */
  int REFKW = 14;
  /** RegularExpression Id. */
  int VALKW = 15;
  /** RegularExpression Id. */
  int READONLYKW = 16;
  /** RegularExpression Id. */
  int VOLATILEKW = 17;
  /** RegularExpression Id. */
  int TRANSIENTKW = 18;
  /** RegularExpression Id. */
  int UNSETTABLEKW = 19;
  /** RegularExpression Id. */
  int DERIVEDKW = 20;
  /** RegularExpression Id. */
  int UNIQUEKW = 21;
  /** RegularExpression Id. */
  int ORDEREDKW = 22;
  /** RegularExpression Id. */
  int RESOLVEKW = 23;
  /** RegularExpression Id. */
  int IDKW = 24;
  /** RegularExpression Id. */
  int TRUEKW = 25;
  /** RegularExpression Id. */
  int FALSEKW = 26;
  /** RegularExpression Id. */
  int OPKW = 27;
  /** RegularExpression Id. */
  int VOIDKW = 28;
  /** RegularExpression Id. */
  int DATATYPEKW = 29;
  /** RegularExpression Id. */
  int ENUMKW = 30;
  /** RegularExpression Id. */
  int MAPENTRYKW = 31;
  /** RegularExpression Id. */
  int THROWSKW = 32;
  /** RegularExpression Id. */
  int DOUBLESLASH = 33;
  /** RegularExpression Id. */
  int DOUBLEQUOTE = 34;
  /** RegularExpression Id. */
  int SINGLEQUOTE = 35;
  /** RegularExpression Id. */
  int BACKSLASH = 36;
  /** RegularExpression Id. */
  int LCURLY = 37;
  /** RegularExpression Id. */
  int RCURLY = 38;
  /** RegularExpression Id. */
  int LSQUARE = 39;
  /** RegularExpression Id. */
  int RSQUARE = 40;
  /** RegularExpression Id. */
  int LPAREN = 41;
  /** RegularExpression Id. */
  int RPAREN = 42;
  /** RegularExpression Id. */
  int DOT = 43;
  /** RegularExpression Id. */
  int COMMA = 44;
  /** RegularExpression Id. */
  int COLON = 45;
  /** RegularExpression Id. */
  int SEMI = 46;
  /** RegularExpression Id. */
  int STAR = 47;
  /** RegularExpression Id. */
  int PLUS = 48;
  /** RegularExpression Id. */
  int MINUS = 49;
  /** RegularExpression Id. */
  int EQUALS = 50;
  /** RegularExpression Id. */
  int QMARK = 51;
  /** RegularExpression Id. */
  int BANG = 52;
  /** RegularExpression Id. */
  int DOLLAR = 53;
  /** RegularExpression Id. */
  int HASH = 54;
  /** RegularExpression Id. */
  int AT = 55;
  /** RegularExpression Id. */
  int DOT_DOT = 56;
  /** RegularExpression Id. */
  int MINUS_GT = 57;
  /** RegularExpression Id. */
  int GT_LT = 58;
  /** RegularExpression Id. */
  int LT_GT = 59;
  /** RegularExpression Id. */
  int AMP = 60;
  /** RegularExpression Id. */
  int LT = 61;
  /** RegularExpression Id. */
  int GT = 62;
  /** RegularExpression Id. */
  int STRING_LITERAL = 63;
  /** RegularExpression Id. */
  int CHAR_LITERAL = 64;
  /** RegularExpression Id. */
  int LETTERORUNDERSCORE = 65;
  /** RegularExpression Id. */
  int LETTERORUNDERSCOREORDIGIT = 66;
  /** RegularExpression Id. */
  int INT_LITERAL = 67;
  /** RegularExpression Id. */
  int DIGIT = 68;
  /** RegularExpression Id. */
  int ESC = 69;
  /** RegularExpression Id. */
  int DIGITDIGITDIGIT = 70;
  /** RegularExpression Id. */
  int ID = 71;

  /** Lexical state. */
  int DEFAULT = 0;
  /** Lexical state. */
  int WithinComment = 1;

  /** Literal token values. */
  String[] tokenImage = {
    "<EOF>",
    "<SINGLELINECOMMENT>",
    "<WS>",
    "\"/*\"",
    "\"*/\"",
    "<token of kind 5>",
    "\"package\"",
    "\"class\"",
    "\"import\"",
    "\"abstract\"",
    "\"interface\"",
    "\"extends\"",
    "\"super\"",
    "\"attr\"",
    "\"ref\"",
    "\"val\"",
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
    "\"op\"",
    "\"void\"",
    "\"datatype\"",
    "\"enum\"",
    "\"mapentry\"",
    "\"throws\"",
    "\"//\"",
    "\"\\\"\"",
    "\"\\\'\"",
    "\"\\\\\"",
    "\"{\"",
    "\"}\"",
    "\"[\"",
    "\"]\"",
    "\"(\"",
    "\")\"",
    "\".\"",
    "\",\"",
    "\":\"",
    "\";\"",
    "\"*\"",
    "\"+\"",
    "\"-\"",
    "\"=\"",
    "\"?\"",
    "\"!\"",
    "\"$\"",
    "\"#\"",
    "\"@\"",
    "\"..\"",
    "\"->\"",
    "\"><\"",
    "\"<>\"",
    "\"&\"",
    "\"<\"",
    "\">\"",
    "<STRING_LITERAL>",
    "<CHAR_LITERAL>",
    "<LETTERORUNDERSCORE>",
    "<LETTERORUNDERSCOREORDIGIT>",
    "<INT_LITERAL>",
    "<DIGIT>",
    "<ESC>",
    "<DIGITDIGITDIGIT>",
    "<ID>",
  };

}
