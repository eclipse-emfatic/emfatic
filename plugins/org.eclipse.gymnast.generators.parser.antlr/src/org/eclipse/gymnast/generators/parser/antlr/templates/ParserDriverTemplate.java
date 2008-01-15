package org.eclipse.gymnast.generators.parser.antlr.templates;

import org.eclipse.gymnast.generator.core.generator.GeneratorContext;

public class ParserDriverTemplate {

  protected static String nl;
  public static synchronized ParserDriverTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ParserDriverTemplate result = new ParserDriverTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "/**" + NL + " * ";
  protected final String TEXT_2 = NL + " */" + NL + "" + NL + "package ";
  protected final String TEXT_3 = ";" + NL + "" + NL + "import java.io.Reader;" + NL + "" + NL + "import antlr.MismatchedCharException;" + NL + "import antlr.MismatchedTokenException;" + NL + "import antlr.NoViableAltException;" + NL + "import antlr.NoViableAltForCharException;" + NL + "import antlr.RecognitionException;" + NL + "import antlr.TokenStreamRecognitionException;" + NL + "" + NL + "import ";
  protected final String TEXT_4 = ".*;" + NL + "import ";
  protected final String TEXT_5 = ".*;" + NL + "" + NL + "public class ";
  protected final String TEXT_6 = "ParserDriver implements IParser {" + NL + "" + NL + "    public ParseContext parse(Reader input) {" + NL + "    \t";
  protected final String TEXT_7 = "Lexer lexer = new ";
  protected final String TEXT_8 = "Lexer(input);" + NL + "\t\tlexer.setColumn(0);" + NL + "\t\tlexer.setTabSize(1);" + NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_9 = "Parser parser = new ";
  protected final String TEXT_10 = "Parser(lexer);" + NL + "\t\tParseContext parseContext = new ParseContext();" + NL + "\t\tparser.setParseContext(parseContext);" + NL + "\t\t" + NL + "\t\tparseCompUnit(parser, parseContext);" + NL + "\t" + NL + "\t\tif (parseContext.getMessageCount() == 0) {" + NL + "\t\t\tSystem.out.println(\"Parse OK!\");" + NL + "\t\t}" + NL + "\t\telse {" + NL + "\t\t\tParseMessage[] msgs = parseContext.getMessages();" + NL + "\t\t\tfor (int i = 0; i < msgs.length; i++) {" + NL + "\t\t\t\tSystem.err.println(msgs[i].getMessage());" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\treturn parseContext;" + NL + "    }" + NL + "" + NL + "\tprivate void parseCompUnit(";
  protected final String TEXT_11 = "Parser parser, ParseContext parseContext) {" + NL + "\t    try {" + NL + "\t        ";
  protected final String TEXT_12 = " compUnit = parser.";
  protected final String TEXT_13 = "();" + NL + "\t        parseContext.setParseRoot(compUnit);" + NL + "\t    }" + NL + "\t    catch (RecognitionException rex) {" + NL + "\t        parseContext.addParseMessage(createParseError(rex));" + NL + "\t    }" + NL + "\t    catch (TokenStreamRecognitionException tex) {" + NL + "\t    \tRecognitionException rex = tex.recog;" + NL + "\t        parseContext.addParseMessage(createParseError(rex));" + NL + "\t    }" + NL + "\t    catch (Exception ex) {" + NL + "\t        parseContext.addParseMessage(new ParseError(ex.getMessage(), 1));" + NL + "\t    }" + NL + "\t}" + NL + "" + NL + "\tstatic ParseError createParseError(RecognitionException ex) {" + NL + "\t\tString message = ex.getMessage();" + NL + "\t\tint offset = ex.getColumn();" + NL + "\t\tint length = 0;" + NL + "" + NL + "\t\tif (ex instanceof MismatchedCharException) {" + NL + "\t\t\tlength = 1;" + NL + "\t\t} else if (ex instanceof MismatchedTokenException) {" + NL + "\t\t\tMismatchedTokenException ex2 = (MismatchedTokenException) ex;" + NL + "\t\t\tif ((ex2.token != null) && (ex2.token.getText() != null)) {" + NL + "\t\t\t\tlength = ex2.token.getText().length();" + NL + "\t\t\t}" + NL + "\t\t} else if (ex instanceof NoViableAltException) {" + NL + "\t\t\tNoViableAltException ex2 = (NoViableAltException) ex;" + NL + "\t\t\tif ((ex2.token != null) && (ex2.token.getText() != null)) {" + NL + "\t\t\t\tlength = ex2.token.getText().length();" + NL + "\t\t\t}" + NL + "\t\t} else if (ex instanceof NoViableAltForCharException) {" + NL + "\t\t\tlength = 1;" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\treturn new ParseError(message, offset, length);" + NL + "\t}" + NL + "" + NL + "}";

  private GeneratorContext _context;

  public void init(GeneratorContext context) {
    _context = context;
  }

  public String generate()
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    stringBuffer.append( _context.getGeneratedByText() );
    stringBuffer.append(TEXT_2);
    stringBuffer.append( _context.getParserPackageName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( _context.getASTPackageName() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append( _context.getLDT_ParserPackageName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_9);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_10);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_11);
    stringBuffer.append( _context.getEntryRuleClassName() );
    stringBuffer.append(TEXT_12);
    stringBuffer.append( _context.getEntryRuleName() );
    stringBuffer.append(TEXT_13);
    return stringBuffer.toString();
  }
}