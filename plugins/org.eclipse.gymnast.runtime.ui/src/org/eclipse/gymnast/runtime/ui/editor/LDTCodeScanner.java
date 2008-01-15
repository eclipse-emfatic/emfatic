/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.runtime.ui.editor;

import java.util.ArrayList;

import org.eclipse.gymnast.runtime.ui.util.LDTColorProvider;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.IWhitespaceDetector;
import org.eclipse.jface.text.rules.IWordDetector;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WhitespaceRule;
import org.eclipse.jface.text.rules.WordRule;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public abstract class LDTCodeScanner extends RuleBasedScanner {
	
	private LDTColorProvider _colorProvider;

	private ArrayList<IRule> _rules = new ArrayList<IRule>();
	private WordRule _idRule;
	
	public void dispose() {
		_colorProvider.dispose();
	}
	
	public LDTCodeScanner() {
		_colorProvider = new LDTColorProvider();
		
		initSetup();
	}
	
	public IToken getLiteralToken() {
		return makeToken(LDTColorProvider.DARK_BLUE, null, SWT.BOLD);
	}
	
	public void initLiterals() {
	    IToken literalToken = getLiteralToken();
	    addRule(new SingleLineRule("\"", "\"", literalToken));
	    addRule(new SingleLineRule("'", "'", literalToken));
	}
	
	public void initKeywords() {
	}
	
	public IToken getCommentToken() {
	    return makeToken(LDTColorProvider.DARK_BLUE, LDTColorProvider.GREY1, SWT.BOLD);
	}
	
	public void initComments() {
		IToken commentToken = getCommentToken();
		addRule(new EndOfLineRule("//", commentToken));
		addRule(new MultiLineRule("/*", "*/", commentToken));
	}
	
	public void initSetup() {
		_rules = new ArrayList<IRule>();
		
		// whitespace
		addRule(new WhitespaceRule(new WhitespaceDetector()));
		
		_idRule = new WordRule(new IDDetector(), makeToken(getIdColor()));
		addRule(_idRule);

		initLiterals();
		
		initKeywords();
		
		initComments();
		
		IRule[] result= new IRule[_rules.size()];
		_rules.toArray(result);
		setRules(result);
	}
	
	protected void addRule(IRule rule) {
		_rules.add(rule);
	}
	
	protected void addKeywords(String[] keywords, RGB color) {
		for (int i= 0; i < keywords.length; i++) {
			_idRule.addWord(keywords[i], makeToken(color));
		}
	}
	protected void addKeywords(String[] keywords, RGB foregroundColor, RGB backgroundColor, int style) {
		for (int i= 0; i < keywords.length; i++) {
			_idRule.addWord(keywords[i], makeToken(foregroundColor, backgroundColor, style));
		}
	}
	
	public RGB getIdColor() {
		return LDTColorProvider.BLACK;
	}
	
	protected IToken makeToken(RGB foregroundColor) {
		TextAttribute attr = new TextAttribute(_colorProvider.getColor(foregroundColor));
		return new Token(attr);
	}
	protected IToken makeToken(RGB foregroundColor, RGB backgroundColor, int style) {
		TextAttribute attr = new TextAttribute(
			_colorProvider.getColor(foregroundColor),
			_colorProvider.getColor(backgroundColor),
			style
		);
		return new Token(attr);
	}
	
	public static class WhitespaceDetector implements IWhitespaceDetector {
		public boolean isWhitespace(char c) {
			return Character.isWhitespace(c);
		}
	}
	
	public static class TokenIDDetector implements IWordDetector {
		public boolean isWordPart(char c) {
			if (Character.isUpperCase(c)) return true;
			if (c == '_') return true;
			return false;
		}
		
		public boolean isWordStart(char c) {
			if (Character.isUpperCase(c)) return true;
			if (c == '_') return true;
			return false;
		}
	}
	
	public static class IDDetector implements IWordDetector {
		public boolean isWordPart(char c) {
			return Character.isUnicodeIdentifierPart(c);
		}
		
		public boolean isWordStart(char c) {
			return Character.isUnicodeIdentifierStart(c);
		}
	}
}
