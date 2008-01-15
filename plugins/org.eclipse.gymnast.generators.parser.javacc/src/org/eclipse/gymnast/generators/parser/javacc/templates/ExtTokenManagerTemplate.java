package org.eclipse.gymnast.generators.parser.javacc.templates;

import org.eclipse.gymnast.generator.core.generator.GeneratorContext;

public class ExtTokenManagerTemplate {

  protected static String nl;
  public static synchronized ExtTokenManagerTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ExtTokenManagerTemplate result = new ExtTokenManagerTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "/**" + NL + " * ";
  protected final String TEXT_2 = NL + " */" + NL + "" + NL + "package ";
  protected final String TEXT_3 = ";" + NL + "" + NL + "public class Ext";
  protected final String TEXT_4 = "ParserTokenManager extends ";
  protected final String TEXT_5 = "ParserTokenManager {" + NL + "" + NL + "\tprivate final ExtSimpleCharStream _stream;" + NL + "\t" + NL + "\tpublic Ext";
  protected final String TEXT_6 = "ParserTokenManager(ExtSimpleCharStream stream) {" + NL + "\t\tsuper(stream);" + NL + "\t\t_stream = stream;" + NL + "\t}" + NL + "" + NL + "\tprotected Token jjFillToken() {" + NL + "\t\tExtToken t = new ExtToken();" + NL + "\t\tt.kind = jjmatchedKind;" + NL + "\t\tString im = jjstrLiteralImages[jjmatchedKind];" + NL + "\t\tt.image = (im == null) ? input_stream.GetImage() : im;" + NL + "\t\tt.beginLine = input_stream.getBeginLine();" + NL + "\t\tt.beginColumn = input_stream.getBeginColumn();" + NL + "\t\tt.endLine = input_stream.getEndLine();" + NL + "\t\tt.endColumn = input_stream.getEndColumn();" + NL + "\t\tt.tokenOffset = _stream.tokenBeginOffset;" + NL + "\t\treturn t;" + NL + "\t}" + NL + "" + NL + "}";
  protected final String TEXT_7 = NL;

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
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_4);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(TEXT_7);
    return stringBuffer.toString();
  }
}
