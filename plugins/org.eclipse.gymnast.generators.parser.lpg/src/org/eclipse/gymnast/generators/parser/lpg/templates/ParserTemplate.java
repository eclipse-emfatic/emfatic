package org.eclipse.gymnast.generators.parser.lpg.templates;

import org.eclipse.gymnast.generator.core.generator.GeneratorContext;

public class ParserTemplate {

  protected static String nl;
  public static synchronized ParserTemplate create(String lineSeparator)
  {
    nl = lineSeparator;
    ParserTemplate result = new ParserTemplate();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "--" + NL + "-- ";
  protected final String TEXT_2 = NL + "--" + NL + "" + NL + "%options escape=$" + NL + "%options var=nt" + NL + "%options la=2 " + NL + "%options fp=";
  protected final String TEXT_3 = "Parser" + NL + "%options prefix=TK_" + NL + "%options table=java" + NL + "%options em" + NL + "%options scopes" + NL + "%options noserialize" + NL + "%options template=dtParserTemplateB.g" + NL + "%options package=";
  protected final String TEXT_4 = NL + "%options import_terminals=";
  protected final String TEXT_5 = "Lexer.g" + NL + "" + NL + "$Define" + NL + "    --" + NL + "    -- Definition of macros used in the parser template" + NL + "    --" + NL + "    $package_declaration /.package $package;./" + NL + "    $import_classes " + NL + "    /.import java.util.ArrayList;" + NL + "    import legAst.*;" + NL + "    ./" + NL + "    $action_class /.$file_prefix./" + NL + "    $prs_stream_class /.PrsStream./" + NL + "    $ast_class /.Ast./" + NL + "$End" + NL + "" + NL + "$Terminals" + NL + "    SEMICOLON         ::= ;" + NL + "    ASSIGN            ::= =" + NL + "    LEFT_BRACKET      ::= [" + NL + "    RIGHT_BRACKET     ::= ]" + NL + "    PLUS              ::= +" + NL + "    MINUS             ::= -" + NL + "    DIVIDE            ::= /" + NL + "    STAR              ::= *" + NL + "    LEFT_PARENTHESIS  ::= (" + NL + "    RIGHT_PARENTHESIS ::= )" + NL + "$End" + NL + "" + NL + "$Identifier" + NL + "    IDENTIFIER" + NL + "$End" + NL + "" + NL + "$Keywords" + NL + "    IF THEN ELSE END WHILE DO BREAK" + NL + "$End" + NL + "" + NL + "$Eof" + NL + "   EOF_SYMBOL" + NL + "$End" + NL + "" + NL + "$Error" + NL + "    ERROR_SYMBOL" + NL + "$End" + NL + "" + NL + "$Eol" + NL + "   SEMICOLON" + NL + "$End" + NL + "" + NL + "$Start" + NL + "    start" + NL + "$End" + NL + "" + NL + "$Names" + NL + "    array_declaration ::= 'array declaration'" + NL + "    term ::= subexpression" + NL + "    factor ::= subexpression" + NL + "$End" + NL + "" + NL + "$RULES" + NL + "    start ::= initialize block" + NL + "        /.$BeginJava" + NL + "                    $setResult(block);" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    initialize ::= $empty" + NL + "        /.$BeginJava" + NL + "                    System.out.println(\"****Begin Parser: \");" + NL + "                    System.out.flush();" + NL + "                    $setResult(null);" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    block ::= $empty" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstBlock($getLeftSpan(), $getRightSpan()));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    block ::= block statement" + NL + "        /.$BeginJava" + NL + "                    block.addStatement(statement);" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    statement ::= variable = expression ;" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstAssignmentStatement($getLeftSpan(), $getRightSpan(), variable, expression));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    statement ::= IF expression THEN block$trueBlock ELSE block$falseBlock END IF ;" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstIfStatement($getLeftSpan(), $getRightSpan(), expression, trueBlock, falseBlock));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    statement ::= WHILE expression DO block END WHILE ;" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstWhileStatement($getLeftSpan(), $getRightSpan(), expression, block));" + NL + "          $EndJava" + NL + "        ./" + NL + "    statement ::= BREAK ;" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstBreakStatement($getLeftSpan(), $getRightSpan()));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    statement ::= array_declaration ;" + NL + "        /.$NoAction./" + NL + "" + NL + "    array_declaration ::= identifier [$LEFT_BRACKET ]" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstArrayDeclarationStatement($getLeftSpan()," + NL + "                                                                $getRightSpan()," + NL + "                                                                identifier," + NL + "                                                                new AstDimension($getLeftSpan($LEFT_BRACKET), $getRightSpan())));" + NL + "" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    array_declaration ::= array_declaration [$LEFT_BRACKET ]" + NL + "        /.$BeginJava" + NL + "                    array_declaration.addDimension(new AstDimension($getLeftSpan($LEFT_BRACKET), $getRightSpan()));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    expression -> term" + NL + "" + NL + "    expression ::= expression +$PLUS term" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstPlusExpression($getLeftSpan(), $getRightSpan(), expression, PLUS, term));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    expression ::= expression -$MINUS term" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstMinusExpression($getLeftSpan(), $getRightSpan(), expression, MINUS, term));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    term -> factor" + NL + "" + NL + "    term ::= term /$DIVIDE factor" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstDivideExpression($getLeftSpan(), $getRightSpan(), term, DIVIDE, factor));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    term ::= term *$STAR factor" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstMultiplyExpression($getLeftSpan(), $getRightSpan(), term, STAR, factor));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    factor -> variable" + NL + "" + NL + "    factor ::= CONSTANT$CONSTANT" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstConstantExpression($getLeftSpan(), $getRightSpan(), CONSTANT));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    factor ::= ( expression )" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstParenthesizedExpression($getLeftSpan(), $getRightSpan(), expression));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    variable ::= identifier" + NL + "        /.$BeginJava" + NL + "                    $setResult(new AstVariableExpression($getLeftSpan(), $getRightSpan(), identifier));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "    variable ::= variable [$LEFT_BRACKET expression ]" + NL + "        /.$BeginJava" + NL + "                    variable.addSubscript(new AstSubscript($getLeftSpan($LEFT_BRACKET), $getRightSpan(), expression));" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "     identifier ::= IDENTIFIER$id" + NL + "        /.$BeginJava" + NL + "                    $setResult(id);" + NL + "          $EndJava" + NL + "        ./" + NL + "" + NL + "$End" + NL + "" + NL + "$Types" + NL + "    Object ::= initialize" + NL + "    IToken ::= identifier" + NL + "    AstStmt ::= statement" + NL + "    AstBlock ::= start | block" + NL + "    AstVariableExpression ::= variable" + NL + "    AstArrayDeclarationStatement ::= array_declaration" + NL + "    AstExpr ::= expression" + NL + "              | term" + NL + "              | factor" + NL + "$End";
  protected final String TEXT_6 = NL;

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
    stringBuffer.append(TEXT_6);
    return stringBuffer.toString();
  }
}
