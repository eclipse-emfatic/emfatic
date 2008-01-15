package org.eclipse.gymnast.generators.parser.javacc.templates;

import org.eclipse.gymnast.generator.core.generator.GeneratorContext;

public class ExtTokenTemplate {

  protected static String nl;
  public static synchronized ExtTokenTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ExtTokenTemplate result = new ExtTokenTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "/**" + NL + " * ";
  protected final String TEXT_2 = NL + " */" + NL + "" + NL + "package ";
  protected final String TEXT_3 = ";" + NL + "" + NL + "public class ExtToken extends Token {" + NL + "" + NL + "\t/**" + NL + "\t * The start position of the token in the input stream." + NL + "\t */" + NL + "\tpublic int tokenOffset;" + NL + "" + NL + "}";
  protected final String TEXT_4 = NL;

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
    stringBuffer.append(TEXT_4);
    return stringBuffer.toString();
  }
}
