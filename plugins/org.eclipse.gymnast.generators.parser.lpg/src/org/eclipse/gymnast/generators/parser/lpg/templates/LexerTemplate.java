package org.eclipse.gymnast.generators.parser.lpg.templates;

import org.eclipse.gymnast.generator.core.generator.GeneratorContext;

public class LexerTemplate {

  protected static String nl;
  public static synchronized LexerTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    LexerTemplate result = new LexerTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "--" + NL + "-- ";
  protected final String TEXT_2 = NL + "--" + NL + "%options fp=";
  protected final String TEXT_3 = "Lexer" + NL + "%options prefix=Char_" + NL + "%options single-productions" + NL + "%options noserialize" + NL + "%options package=";
  protected final String TEXT_4 = NL + "%options template=LexerTemplateB.g" + NL + "%options filter=";
  protected final String TEXT_5 = "KWLexer.g" + NL + "%options export_terminals=(\"";
  protected final String TEXT_6 = "Parsersym.java\", \"TK_\")" + NL + "" + NL + "$Define" + NL + "    --" + NL + "    -- Definition of macros used in the lexer template" + NL + "    --" + NL + "    $package_declaration /.package $package;./" + NL + "    $import_classes /.import java.util.*;./" + NL + "    $action_class /.$file_prefix./" + NL + "    $prs_stream_class /.PrsStream./" + NL + "    $eof_token /.$_EOF_SYMBOL./" + NL + "" + NL + "    --" + NL + "    -- Definition of macro used in the included file LexerBasicMapB.g" + NL + "    --" + NL + "    $kw_lexer_class /.$";
  protected final String TEXT_7 = "KWLexer./" + NL + "" + NL + "$End" + NL + "" + NL + "$Headers" + NL + "    --" + NL + "    -- Additional methods for the action class not provided in the template" + NL + "    --" + NL + "    /." + NL + "        //" + NL + "        // The Lexer contains an array of characters as the input stream to be parsed." + NL + "        // There are methods to retrieve and classify characters." + NL + "        // The lexparser \"token\" is implemented simply as the index of the next character in the array." + NL + "        // The Lexer extends the abstract class LpgLexStream with an implementation of the abstract" + NL + "        // method getKind.  The template defines the Lexer class and the lexer() method." + NL + "        // A driver creates the action class, \"Lexer\", passing an Option object to the constructor." + NL + "        // The Option object gives access to the input character arrary, the file name and other options." + NL + "        //" + NL + "        Option option;" + NL + "        $kw_lexer_class kwLexer;" + NL + "        boolean printTokens;" + NL + "        private final static int ECLIPSE_TAB_VALUE = 4;" + NL + "" + NL + "        public $action_class(Option option) throws java.io.IOException" + NL + "        {" + NL + "            this(option.getFileName(), ECLIPSE_TAB_VALUE);" + NL + "            this.option = option;" + NL + "            this.printTokens = option.printTokens();" + NL + "            this.kwLexer = new $kw_lexer_class(getInputChars(), $_IDENTIFIER);" + NL + "        }" + NL + "" + NL + "        final void makeToken(int kind)" + NL + "        {" + NL + "            int startOffset = $getToken(1)," + NL + "                endOffset = $getRightSpan();" + NL + "            $prs_stream.makeToken(startOffset, endOffset, kind);" + NL + "            if (printTokens) printValue(startOffset, endOffset);" + NL + "        }" + NL + "" + NL + "        final void skipToken()" + NL + "        {" + NL + "            if (printTokens) printValue($getToken(1), $getRightSpan());" + NL + "        }" + NL + "        " + NL + "        final void checkForKeyWord()" + NL + "        {" + NL + "            int startOffset = $getToken(1)," + NL + "                endOffset = $getRightSpan()," + NL + "            kwKind = kwLexer.lexer(startOffset, endOffset);" + NL + "            $prs_stream.makeToken(startOffset, endOffset, kwKind);" + NL + "            if(printTokens) printValue(startOffset, endOffset);" + NL + "        }" + NL + "        " + NL + "        final void printValue(int startOffset, int endOffset)" + NL + "        {" + NL + "            String s = new String(getInputChars(), startOffset, endOffset - startOffset + 1);" + NL + "            System.out.print(s);" + NL + "        }" + NL + "" + NL + "        public final static int tokenKind[] =" + NL + "        {" + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_HT," + NL + "            Char_LF," + NL + "            Char_CtlCharNotWS," + NL + "            Char_FF," + NL + "            Char_CR," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_CtlCharNotWS," + NL + "            Char_Space," + NL + "            Char_Exclamation," + NL + "            Char_DoubleQuote," + NL + "            Char_Sharp," + NL + "            Char_DollarSign," + NL + "            Char_Percent," + NL + "            Char_Ampersand," + NL + "            Char_SingleQuote," + NL + "            Char_LeftParen," + NL + "            Char_RightParen," + NL + "            Char_Star," + NL + "            Char_Plus," + NL + "            Char_Comma," + NL + "            Char_Minus," + NL + "            Char_Dot," + NL + "            Char_Slash," + NL + "            Char_0," + NL + "            Char_1," + NL + "            Char_2," + NL + "            Char_3," + NL + "            Char_4," + NL + "            Char_5," + NL + "            Char_6," + NL + "            Char_7," + NL + "            Char_8," + NL + "            Char_9," + NL + "            Char_Colon," + NL + "            Char_SemiColon," + NL + "            Char_LessThan," + NL + "            Char_Equal," + NL + "            Char_GreaterThan," + NL + "            Char_QuestionMark," + NL + "            Char_AtSign," + NL + "            Char_A," + NL + "            Char_B," + NL + "            Char_C," + NL + "            Char_D," + NL + "            Char_E," + NL + "            Char_F," + NL + "            Char_G," + NL + "            Char_H," + NL + "            Char_I," + NL + "            Char_J," + NL + "            Char_K," + NL + "            Char_L," + NL + "            Char_M," + NL + "            Char_N," + NL + "            Char_O," + NL + "            Char_P," + NL + "            Char_Q," + NL + "            Char_R," + NL + "            Char_S," + NL + "            Char_T," + NL + "            Char_U," + NL + "            Char_V," + NL + "            Char_W," + NL + "            Char_X," + NL + "            Char_Y," + NL + "            Char_Z," + NL + "            Char_LeftBracket," + NL + "            Char_BackSlash," + NL + "            Char_RightBracket," + NL + "            Char_Caret," + NL + "            Char__," + NL + "            Char_BackQuote," + NL + "            Char_a," + NL + "            Char_b," + NL + "            Char_c," + NL + "            Char_d," + NL + "            Char_e," + NL + "            Char_f," + NL + "            Char_g," + NL + "            Char_h," + NL + "            Char_i," + NL + "            Char_j," + NL + "            Char_k," + NL + "            Char_l," + NL + "            Char_m," + NL + "            Char_n," + NL + "            Char_o," + NL + "            Char_p," + NL + "            Char_q," + NL + "            Char_r," + NL + "            Char_s," + NL + "            Char_t," + NL + "            Char_u," + NL + "            Char_v," + NL + "            Char_w," + NL + "            Char_x," + NL + "            Char_y," + NL + "            Char_z," + NL + "            Char_LeftBrace," + NL + "            Char_VerticalBar," + NL + "            Char_RightBrace," + NL + "            Char_Tilde," + NL + "            Char_AfterASCII, // for all chars in range 128..65534" + NL + "            Char_EOF         // for '\\uffff' or 65535 " + NL + "        };" + NL + "                " + NL + "        public final int getKind(int i)  // Classify character at ith location" + NL + "        {" + NL + "            char c = (i >= getStreamLength() ? '\\uffff' : getCharValue(i));" + NL + "            return (c < 128 // ASCII Character" + NL + "                      ? tokenKind[c]" + NL + "                      : c == '\\uffff'" + NL + "                           ? Char_EOF" + NL + "                           : Char_AfterASCII);" + NL + "        }" + NL + "    ./" + NL + "$End" + NL;
  protected final String TEXT_8 = NL;

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
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_5);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_6);
    stringBuffer.append( _context.getASTName() );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(TEXT_8);
    return stringBuffer.toString();
  }
}