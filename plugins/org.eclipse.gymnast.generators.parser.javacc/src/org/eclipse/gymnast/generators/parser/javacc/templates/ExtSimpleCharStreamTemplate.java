package org.eclipse.gymnast.generators.parser.javacc.templates;

import org.eclipse.gymnast.generator.core.generator.GeneratorContext;

public class ExtSimpleCharStreamTemplate {

  protected static String nl;
  public static synchronized ExtSimpleCharStreamTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ExtSimpleCharStreamTemplate result = new ExtSimpleCharStreamTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "/**" + NL + " * ";
  protected final String TEXT_2 = NL + " */" + NL + "" + NL + "package ";
  protected final String TEXT_3 = ";" + NL + "" + NL + "public class ExtSimpleCharStream extends SimpleCharStream {" + NL + "" + NL + "\t/**" + NL + "\t * The current character position in the input stream." + NL + "\t */" + NL + "\tpublic int inputOffset = -1;" + NL + "\t" + NL + "\t/**" + NL + "\t * The start position of the current token in the input stream." + NL + "\t */" + NL + "\tpublic int tokenBeginOffset;" + NL + "\t" + NL + "\t" + NL + "\tpublic ExtSimpleCharStream(java.io.Reader dstream) {" + NL + "\t\tsuper(dstream);" + NL + "\t}" + NL + "\t" + NL + "\tpublic char BeginToken() throws java.io.IOException {" + NL + "\t\ttokenBegin = -1;" + NL + "\t\ttokenBeginOffset = -1;" + NL + "\t\t" + NL + "\t\tchar c = readChar();" + NL + "\t\t" + NL + "\t\ttokenBegin = bufpos;" + NL + "\t\ttokenBeginOffset = inputOffset;" + NL + "" + NL + "\t\treturn c;" + NL + "\t}" + NL + "\t" + NL + "\tprotected void UpdateLineColumn(char c) {" + NL + "\t\tinputOffset++;" + NL + "\t\tsuper.UpdateLineColumn(c);" + NL + "\t}" + NL + "\t" + NL + "}";
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
