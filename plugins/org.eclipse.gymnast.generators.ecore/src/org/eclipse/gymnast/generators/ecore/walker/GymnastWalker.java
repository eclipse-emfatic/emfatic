package org.eclipse.gymnast.generators.ecore.walker;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.gymnast.generator.core.ast.AltRule;
import org.eclipse.gymnast.generator.core.ast.Alts;
import org.eclipse.gymnast.generator.core.ast.Attrs;
import org.eclipse.gymnast.generator.core.ast.CompUnit;
import org.eclipse.gymnast.generator.core.ast.Expr;
import org.eclipse.gymnast.generator.core.ast.Grammar;
import org.eclipse.gymnast.generator.core.ast.HeaderSection;
import org.eclipse.gymnast.generator.core.ast.Id;
import org.eclipse.gymnast.generator.core.ast.ListRule;
import org.eclipse.gymnast.generator.core.ast.OptSubSeq;
import org.eclipse.gymnast.generator.core.ast.Option;
import org.eclipse.gymnast.generator.core.ast.Seq;
import org.eclipse.gymnast.generator.core.ast.SeqRule;
import org.eclipse.gymnast.generator.core.ast.SimpleExpr;
import org.eclipse.gymnast.generator.core.ast.TokenRule;
import org.eclipse.gymnast.generator.core.parser.ParserDriver;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;

/**
 * 
 * A GymnastWalker navigates the full Abstract Syntax Tree (AST) of a grammar.
 * It implements the accept operation of the Visitor pattern for a
 * GymnastASTNode object. In this way the accept operation need not be part of
 * GymnastASTNode. <br>
 * <br>
 * 
 * This walker navigates the grammar AST post-order: for each node children are
 * visited, and then those results as well as the current node are given to a
 * handler method. <br>
 * <br>
 * 
 * Users must subclass GymnastSwitch and call walk(grammarRoot, visitor) where
 * grammarRoot is of type org.eclipse.gymnast.generator.core.ast.CompUnit <br>
 * <br>
 * Example usage: <br>
 * <br>
 * 
 * <code>
 * class MyVisitor extends GrammarSwitch { .... }<br>
 *  
 * MyVisitor v = new MyVisitor (); <br>
 * GymnastWalker w = new GymnastWalker(); <br>
 * Object result = w.walk(myGrammar, v);<br>
 * </code>
 * 
 * <br>
 * <br>
 * See Also: GymnastSwitch
 * 
 * 
 * 
 * @author <a href="http://www.sts.tu-harburg.de/~mi.garcia/">Miguel Garcia</a>
 * 
 */
public class GymnastWalker<T> {

	public T walk(org.eclipse.gymnast.generator.core.ast.CompUnit root, GymnastSwitch<T> v) {
		T resHeader = walkHeader(root.getHeaderSection(), v);
		T resGrammar = walkGrammar(root.getGrammar(), v);
		T res = v.handleRoot(root, resHeader, resGrammar);
		return res;
	}

	/**
	 * BufferedReader reader = new BufferedReader(new
	 * InputStreamReader(context.getASTFile().getContents()));
	 */
	public T walk(Reader grammar, GymnastSwitch<T> v) {

		ParserDriver parser = new ParserDriver();
		ParseContext parseContext = parser.parse(grammar);

		CompUnit parseRoot = (CompUnit) parseContext.getParseRoot();
		if (parseRoot == null) {
			return null;
		}

		return walk(parseRoot, v);
	}

	public T walkGrammarContents(String grammarContents, GymnastSwitch<T> v) {
		StringReader sr = new StringReader(grammarContents);
		return walk(sr, v);
	}

	public T walkFile(String fileLocation, GymnastSwitch<T> v) {
		try {
			FileReader fr = new FileReader(fileLocation);
			return walk(fr, v);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	private T walkGrammar(Grammar grammar, GymnastSwitch<T> v) {
		ASTNode[] rules = grammar.getChildren();
		List<T> resTokenRules = new ArrayList<T>();
		List<T> resSeqRules = new ArrayList<T>();
		List<T> resAltRules = new ArrayList<T>();
		List<T> resListRules = new ArrayList<T>();
		for (ASTNode rule : rules) {
			if (rule instanceof TokenRule) {
				TokenRule tr = (TokenRule) rule;
				resTokenRules.add(walkTokenRule(tr, v));
			} else if (rule instanceof SeqRule) {
				SeqRule sr = (SeqRule) rule;
				resSeqRules.add(walkSeqRule(sr, v));
			} else if (rule instanceof AltRule) {
				AltRule ar = (AltRule) rule;
				resAltRules.add(walkAltRule(ar, v));
			} else if (rule instanceof ListRule) {
				ListRule lr = (ListRule) rule;
				resListRules.add(walkListRule(lr, v));
			}
		}
		return v.handleGrammar(grammar, resTokenRules, resSeqRules, resAltRules, resListRules);
	}

	private T walkListRule(ListRule lr, GymnastSwitch<T> v) {
		String name = lr.getDecl().getName().getText();
		List<String> attrs = getAttrsIfAny(lr.getDecl().getAttrs());
		SimpleExpr se1 = lr.getBody().getListExpr();
		String opt_e1Name = unNullName(se1);
		String e1 = se1.getValue().getText();
		String separator = "";
		if (lr.getBody().getSeparator() != null) {
			separator = lr.getBody().getSeparator().getValue().getText();
		}
		String e2 = "";
		SimpleExpr se2 = lr.getBody().getListExpr2();
		String opt_e2Name = unNullName(se2);
		if (se2 != null) {
			e2 = se2.getValue().getText();
		}
		int lowerBound = lr.getBody().getListMark().getText().equals("+") ? 1 : 0;

		return v.handleListRule(lr, attrs, name, e1, separator, e2, lowerBound, opt_e1Name, opt_e2Name);
	}

	private String unNullName(SimpleExpr se) {
		String res = "";
		if (se != null && se.getName() != null) {
			res = se.getName().getText();
		}
		return res;
	}

	private T walkAltRule(AltRule ar, GymnastSwitch<T> v) {
		String strKind = ar.getDecl().getKind().getText();
		AltRuleKind k = AltRuleKind.ABSTRACT_RK;
		if (strKind.equals("container")) {
			k = AltRuleKind.CONTAINER_RK;
		} else if (strKind.equals("interface")) {
			k = AltRuleKind.INTERFACE_RK;
		}
		String name = ar.getDecl().getName().getText();
		List<String> attrs = getAttrsIfAny(ar.getDecl().getAttrs());
		List<String> alts = getAlts(ar.getBody().getAlts());
		List<T> resPreSeqs = new ArrayList<T>();
		List<T> resPostSeqs = new ArrayList<T>();
		if (ar.getBody().getPreSeq() != null) {
			Seq preSeq = ar.getBody().getPreSeq().getSeq();
			for (org.eclipse.gymnast.runtime.core.ast.ASTNode c : preSeq.getChildren()) {
				if (c instanceof Expr) {
					Expr e = (Expr) c;
					resPreSeqs.addAll(walkExprInPreOrPostSeqInAltRule(ar, true, e, v));
				}
			}
		}
		if (ar.getBody().getPostSeq() != null) {
			Seq postSeq = ar.getBody().getPostSeq().getSeq();
			for (org.eclipse.gymnast.runtime.core.ast.ASTNode c : postSeq.getChildren()) {
				if (c instanceof Expr) {
					Expr e = (Expr) c;
					resPreSeqs.addAll(walkExprInPreOrPostSeqInAltRule(ar, true, e, v));
				}
			}
		}
		return v.handleAltRule(ar, name, alts, attrs, resPreSeqs, resPostSeqs);
	}

	private Collection<? extends T> walkExprInPreOrPostSeqInAltRule(AltRule ar, boolean isOptional, Expr e,
			GymnastSwitch<T> v) {
		isOptional = isOptional || (e instanceof OptSubSeq);
		List<T> res = new ArrayList<T>();
		if (e instanceof OptSubSeq) {
			OptSubSeq optSubSeq = (OptSubSeq) e;
			ASTNode[] nodes = optSubSeq.getSeq().getChildren();
			for (ASTNode node : nodes) {
				if (node instanceof Expr) {
					Expr innerE = (Expr) node;
					res.addAll(walkExprInPreOrPostSeqInAltRule(ar, isOptional, innerE, v));
				}
			}
		} else {
			SimpleExpr se = (SimpleExpr) e;
			List<String> attrs = getAttrsIfAny(se.getAttrs());
			String optFieldName = unNull(se.getName());
			String value = se.getValue().getText();
			res.add(v.handleSimpleExprInPreOrPostInAltRule(ar, se, isOptional, optFieldName, value, attrs, res.size()));
		}
		return res;
	}

	private T walkSeqRule(SeqRule sr, GymnastSwitch<T> v) {
		String name = sr.getDecl().getName().getText();
		List<String> attrs = getAttrsIfAny(sr.getDecl().getAttrs());
		ASTNode[] exprNodes = sr.getBody().getChildren();
		List<T> resSimpleExprs = new ArrayList<T>();
		for (ASTNode node : exprNodes) {
			if (node instanceof Expr) {
				Expr e = (Expr) node;
				resSimpleExprs.addAll(walkExprInSeqRule(sr, false, e, v));
			}
		}
		return v.handleSeqRule(sr, name, attrs, resSimpleExprs);
	}

	private List<T> walkExprInSeqRule(SeqRule sr, boolean isOptional, Expr e, GymnastSwitch<T> v) {
		isOptional = isOptional || (e instanceof OptSubSeq);
		List<T> res = new ArrayList<T>();
		if (e instanceof OptSubSeq) {
			OptSubSeq optSubSeq = (OptSubSeq) e;
			ASTNode[] nodes = optSubSeq.getSeq().getChildren();
			for (ASTNode node : nodes) {
				if (node instanceof Expr) {
					Expr innerE = (Expr) node;
					res.addAll(walkExprInSeqRule(sr, isOptional, innerE, v));
				}
			}
		} else {
			SimpleExpr se = (SimpleExpr) e;
			List<String> attrs = getAttrsIfAny(se.getAttrs());
			String optFieldName = unNull(se.getName());
			String value = se.getValue().getText();
			res.add(v.handleSimpleExprInRule(sr, se, isOptional, optFieldName, value, attrs, -1));
		}
		return res;
	}

	private String unNull(Id name) {
		if (name == null) {
			return "";
		}
		return name.getText();
	}

	private List<String> getAttrsIfAny(Attrs attrs) {
		List<String> res = new ArrayList<String>();
		if (attrs != null) {
			if (attrs.getChild(1) != null) {
				ASTNode[] attrNodes = attrs.getChild(1).getChildren();
				for (ASTNode attrNode : attrNodes) {
					String str = attrNode.getText();
					if (str != null && !(",".equals(str)) && !("[".equals(str)) && !("]".equals(str))) {
						res.add(attrNode.getText());
					}
				}
			}
		}
		return res;
	}

	private T walkTokenRule(TokenRule tr, GymnastSwitch<T> v) {
		String tokenRuleName = tr.getDecl().getName().getText();
		List<String> attrs = getAttrsIfAny(tr.getDecl().getAttrs());
		List<String> alts = getAlts(tr.getBody());
		return v.handleTokenRule(tr, tokenRuleName, attrs, alts);
	}

	private List<String> getAlts(Alts body) {
		ASTNode[] altNodes = body.getChildren();
		List<String> alts = new ArrayList<String>();
		for (ASTNode altNode : altNodes) {
			if (altNode instanceof SimpleExpr) {
				SimpleExpr alt = (SimpleExpr) altNode;
				String str = alt.getValue().getText();
				alts.add(str);
			}
		}
		return alts;
	}

	private T walkHeader(HeaderSection headerSection, GymnastSwitch<T> v) {
		String languageName = headerSection.getName().getText();
		Map<String, String> resOptions = new HashMap<String, String>();
		if (headerSection.getOptionsSection() != null) {
			ASTNode[] opts = headerSection.getOptionsSection().getOptionList().getChildren();
			for (ASTNode child : opts) {
				if (child instanceof Option) {
					Option opt = (Option) child;
					String optName = opt.getName().getText();
					String optValue = opt.getValue().getText();
					/*
					 * FIXME only the last of a set of duplicate options is
					 * kept, the others are overwritten silently
					 */
					resOptions.put(optName, optValue);
				}
			}
		}
		return v.handleHeader(headerSection, languageName, resOptions);
	}

}
