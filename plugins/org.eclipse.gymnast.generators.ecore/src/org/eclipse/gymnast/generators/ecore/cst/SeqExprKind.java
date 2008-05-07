package org.eclipse.gymnast.generators.ecore.cst;

/**
 * This classification is independent of whether the item is optional. Every
 * SeqExpr can be declared optional.
 * 
 * In more detail:
 * 
 * An item is classified into one of:
 * 
 * a) constant content (isBuiltInToken, isSurroundedByQuotes)
 * 
 * b) the item refers to a rule having an EClassifier as counterpart
 * 
 * c) can be converted to str or int
 * 
 * d) by now the only case left is for item to be an invocation of a list rule
 * 
 */
public enum SeqExprKind {

	CONSTANT_CONTENT, REFERS_TO_RULE_WITH_ECLASS, REFERS_TO_RULE_WITH_EENUM, KEEP_AS_STR, KEEP_AS_CHR, KEEP_AS_INT, REFERS_TO_LIST_RULE

}
