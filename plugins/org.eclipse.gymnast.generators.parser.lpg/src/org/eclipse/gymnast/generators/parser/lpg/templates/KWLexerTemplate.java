package org.eclipse.gymnast.generators.parser.lpg.templates;

import org.eclipse.gymnast.generator.core.generator.GeneratorContext;

public class KWLexerTemplate {

  protected static String nl;
  public static synchronized KWLexerTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    KWLexerTemplate result = new KWLexerTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "--" + NL + "-- ";
  protected final String TEXT_2 = NL + "--" + NL + "%options slr" + NL + "%options fp=";
  protected final String TEXT_3 = "KWLexer" + NL + "%options prefix=Char_" + NL + "%options noserialize" + NL + "%options package=";
  protected final String TEXT_4 = NL + "%options template=KeyWordTemplateB.g" + NL + "%options export_terminals=(\"";
  protected final String TEXT_5 = "Parsersym.java\", \"TK_\")" + NL + "" + NL + "$Define" + NL + "    --" + NL + "    -- Definition of macros used in the KeyWordTemplateB.g template" + NL + "    --" + NL + "    -- $import_classes /.import java.util.*;./" + NL + "    -- $action_class /.$file_prefix./" + NL + "    $eof_char /.Char_EOF./" + NL + "" + NL + "$End" + NL + "" + NL + "$Include" + NL + "    KWLexerMap.g" + NL + "$End" + NL + "" + NL + "$Export";
  protected final String TEXT_6 = NL + "  KW_";
  protected final String TEXT_7 = NL + "$End" + NL + "" + NL + "$Eof" + NL + "    EOF" + NL + "$End" + NL + "" + NL + "$Start" + NL + "    KeyWord" + NL + "$End" + NL + "" + NL + "$Rules" + NL + "KeyWord ::=" + NL;
  protected final String TEXT_8 = NL;
  protected final String TEXT_9 = NL + "/.$BeginAction" + NL + "    $setResult($_KW_";
  protected final String TEXT_10 = ");" + NL + "  $EndAction" + NL + "./" + NL;
  protected final String TEXT_11 = NL + "$End";
  protected final String TEXT_12 = NL;

  private GeneratorContext _context;

  public void init(GeneratorContext context) {
    _context = context;
  }
  
  private String space(String keyword) {
    StringBuffer sb = new StringBuffer();
    for (int i = 0; i < keyword.length(); i++) {
      if (i > 0) sb.append(' ');
      sb.append(keyword.charAt(i));
    }
    return sb.toString();
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
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_5);
    
String[] literals = _context.getGrammarInfo().getLiterals();
for (int i = 0; i < literals.length; i++) {

    stringBuffer.append(TEXT_6);
    stringBuffer.append( literals[i] );
    
}

    stringBuffer.append(TEXT_7);
    
for (int i = 0; i < literals.length; i++) {

    stringBuffer.append(TEXT_8);
    stringBuffer.append(i>0?"| ":"  ");
    stringBuffer.append( space(literals[i]) );
    stringBuffer.append(TEXT_9);
    stringBuffer.append( literals[i] );
    stringBuffer.append(TEXT_10);
    
}

    stringBuffer.append(TEXT_11);
    stringBuffer.append(TEXT_12);
    return stringBuffer.toString();
  }
}
