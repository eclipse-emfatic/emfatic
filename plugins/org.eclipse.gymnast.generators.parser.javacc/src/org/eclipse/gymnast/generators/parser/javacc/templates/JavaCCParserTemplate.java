package org.eclipse.gymnast.generators.parser.javacc.templates;

import org.eclipse.gymnast.generator.core.generator.GeneratorContext;

public class JavaCCParserTemplate {

  protected static String nl;
  public static synchronized JavaCCParserTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    JavaCCParserTemplate result = new JavaCCParserTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "/**" + NL + " * ";
  protected final String TEXT_2 = NL + " */" + NL + "" + NL + "PARSER_BEGIN(";
  protected final String TEXT_3 = "Parser)" + NL + "package ";
  protected final String TEXT_4 = ";" + NL + "" + NL + "import ";
  protected final String TEXT_5 = ".*;" + NL + "import ";
  protected final String TEXT_6 = ".*;" + NL + "" + NL + "public class ";
  protected final String TEXT_7 = "Parser {" + NL + "" + NL + "  public TokenInfo createTokenInfo(Token tok) {" + NL + "    if (!(tok instanceof ExtToken)) return null;" + NL + "    " + NL + "    ExtToken extToken = (ExtToken) tok;" + NL + "    return new TokenInfo(tok.image, extToken.tokenOffset, tok.kind);" + NL + "  }" + NL + "" + NL + "}" + NL + "PARSER_END(";
  protected final String TEXT_8 = "Parser)" + NL;
  protected final String TEXT_9 = NL;

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
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_3);
    stringBuffer.append( _context.getParserPackageName() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append( _context.getASTPackageName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( _context.getLDT_ASTNodePackageName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(TEXT_9);
    return stringBuffer.toString();
  }
}