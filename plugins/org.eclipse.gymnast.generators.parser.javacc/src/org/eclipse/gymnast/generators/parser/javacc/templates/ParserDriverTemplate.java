package org.eclipse.gymnast.generators.parser.javacc.templates;

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
  protected final String TEXT_3 = ";" + NL + "" + NL + "import java.io.Reader;" + NL + "import ";
  protected final String TEXT_4 = ".*;" + NL + "import ";
  protected final String TEXT_5 = ".*;" + NL + "" + NL + "public class ";
  protected final String TEXT_6 = "ParserDriver implements IParser {" + NL + "" + NL + "    public ParseContext parse(Reader input) {" + NL + "    \t" + NL + "    \tExtSimpleCharStream stream = new ExtSimpleCharStream(input);" + NL + "    \tExt";
  protected final String TEXT_7 = "ParserTokenManager tokenManager = new Ext";
  protected final String TEXT_8 = "ParserTokenManager(stream);" + NL + "    \t";
  protected final String TEXT_9 = "Parser parser = new ";
  protected final String TEXT_10 = "Parser(tokenManager);" + NL + "    \tParseContext parseContext = new ParseContext();" + NL + "    \tparseCompUnit(parser, parseContext);" + NL + "    \t" + NL + "    \tif (parseContext.getMessageCount() == 0) {" + NL + "\t\t\tSystem.out.println(\"Parse OK!\");" + NL + "\t\t}" + NL + "\t\telse {" + NL + "\t\t\tParseMessage[] msgs = parseContext.getMessages();" + NL + "\t\t\tfor (int i = 0; i < msgs.length; i++) {" + NL + "\t\t\t\tSystem.err.println(msgs[i].getMessage());" + NL + "\t\t\t}" + NL + "\t\t}" + NL + "\t\t" + NL + "\t\treturn parseContext;" + NL + "    }" + NL + "    " + NL + "    private void parseCompUnit(";
  protected final String TEXT_11 = "Parser parser, ParseContext parseContext) {" + NL + "    \ttry {" + NL + "\t\t\t";
  protected final String TEXT_12 = " compUnit = parser.";
  protected final String TEXT_13 = "();" + NL + "\t\t\tparseContext.setParseRoot(compUnit);" + NL + "\t\t} catch (ParseException ex) {" + NL + "\t\t\tParseError parseError;" + NL + "\t\t\tToken token = ex.currentToken;" + NL + "\t\t\tif (token instanceof ExtToken) {" + NL + "\t\t\t\tExtToken extToken = (ExtToken) token;" + NL + "\t\t\t\tint offset = extToken.tokenOffset;" + NL + "\t\t\t\tString tokenText = token.image;" + NL + "\t\t\t\tint length = (tokenText == null) ? 0 : tokenText.length();" + NL + "\t\t\t\tparseError = new ParseError(ex.getMessage(), offset, length);" + NL + "\t\t\t}" + NL + "\t\t\telse {" + NL + "\t\t\t\tparseError = new ParseError(ex.getMessage(), 1);" + NL + "\t\t\t}" + NL + "\t\t\tparseContext.addParseMessage(parseError);" + NL + "\t\t}" + NL + "\t\tcatch (TokenMgrError ex) {" + NL + "\t\t\tParseError parseError = new ParseError(ex.getMessage(), 1);" + NL + "\t\t\tparseContext.addParseMessage(parseError);" + NL + "\t\t}" + NL + "    }" + NL + "" + NL + "}";

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