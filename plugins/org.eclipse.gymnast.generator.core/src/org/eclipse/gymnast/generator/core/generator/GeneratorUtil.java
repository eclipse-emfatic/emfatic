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

package org.eclipse.gymnast.generator.core.generator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.ast.SimpleExpr;
import org.eclipse.gymnast.runtime.core.util.IReporter;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class GeneratorUtil {
	
	private GeneratorContext _context;
	private IProgressMonitor _monitor;
	private IReporter _reporter;
	
	GeneratorUtil(GeneratorContext context, IProgressMonitor monitor, IReporter reporter) {
		_context = context;
		_monitor = monitor;
		_reporter = reporter;
	}
	
	
	//
	// Reporting
	//
	
	public IReporter getReporter() {
		return _reporter;
	}
	
	public void report(String message) {
		report(message, 0);
	}
	
	public void report(String message, int verbosity) {
		if (verbosity == 0) {
		    if (message != null) {
		        _monitor.subTask(message);
		    }
		}
		
		if (verbosity <= _context.getVerbosity()) {
			_reporter.report(message, verbosity);
		}
	}
	
	public void reportWarning(String message) {
		_reporter.reportWarning(message);
	}
	
	public void reportError(String message) {
		_reporter.reportError(message);
	}
	
	public void reportError(Exception ex) {
		_reporter.reportError(ex);
	}
	
	
	public void beginRules(int ruleCount) {
		_monitor.beginTask("Processing Rules", ruleCount);
	}
	public void ruleDone() {
		_monitor.worked(1);
	}
	
	//
	// Helpers for string manipulation scenarios
	//
	
	public String removeSurroundingQuotes(String text) {
		return Util.removeSurroundingQuotes(text);
	}
	
	/**
	 * Uppercase the first letter of the name to make a good Java class
	 * name (so "myName" -> "MyName")
	 * @param name name to be uppercased
	 * @return name with first character uppercased
	 */
	public String toUppercaseName(String name) {
		return Util.toUppercaseName(name);
	}
	
	/**
	 * Lowercase the first letter of the name to make a good Antlr rule
	 * name (so "MyName" -> "myName")
	 * @param name name to be lowercased
	 * @return name with first character lowercased
	 */
	public String toLowercaseName(String name) {
		return Util.toLowercaseName(name);
	}
	
	//
	//
	//
	
	public boolean isTokenReference(String type) {
		// In Antlr syntax a reference is a token reference if it is
		// all uppercase characters or if it's a string literal.  Otherwise
		// it's a rule reference.
		// eg:  ruleDef : ruleRef | TOKEN_REF | "literal";
		
		if (type.charAt(0) == '"') {
			// it's a string literal
			return true;
		}
		
		for (int i = 0; i < type.length(); i++) {
			char c = type.charAt(i);
			
			if (Character.isLowerCase(c)) {
				return false;
			}
		}
		return true;
	}
	
	private String getDefaultName(String type) {
		String name = null;
		// construct a reasonable name based on the type
		if (type.charAt(0) == '"') {
			// it's a string literal
			name = removeSurroundingQuotes(type) + "_KW";
		}
		else if (isTokenReference(type)) {
			name = type.toLowerCase();
		}
		else {
			name = type;
		}
		return name;
	}
	
	public String getLabel(SimpleExpr simpleExpr) {
		String rule = simpleExpr.getValue().getText();
		String label = null;
		if (simpleExpr.getName() != null) {
			label = simpleExpr.getName().getText();
		}
		else {
			label = getDefaultName(rule);
		}
		return label;
	}
	
	//
	// Helpers for building doc comments and code body text
	//
	
	void appendLine(StringBuffer sb) {
		sb.append('\n');
	}
	
	void appendLine(StringBuffer sb, int indentLevel, String text) {
		for (int i=0; i<indentLevel; i++) sb.append('\t');
		sb.append(text);
		sb.append('\n');
	}
	
	public String getRuleBaseClassName(Rule rule) {
	    String baseClassName;
	    
		if (ASTUtil.isToken(rule)) {
		    baseClassName = _context.getASTTokenClassName();
		}
		else {
			String[] bases = _context.getGrammarInfo().getAltReferencers(ASTUtil.getName(rule));
			if ((bases == null) || (bases.length == 0)) {
			    baseClassName = _context.getASTBaseClassName();
			}
			else if (bases.length == 1) {
			    baseClassName = toUppercaseName(bases[0]);
			}
			else {
			    baseClassName = _context.getASTBaseClassName();
			}
		}
		
		return baseClassName;
	}
	
	public String[] getRuleBaseInterfaceNames(Rule rule) {
	    String[] baseInterfaceNames = null;
	    
		if (ASTUtil.isToken(rule)) {
		}
		else {
			String[] bases = _context.getGrammarInfo().getAltReferencers(ASTUtil.getName(rule));
			if ((bases == null) || (bases.length == 0)) {
			}
			else if (bases.length == 1) {
			}
			else {
				for (int i = 0; i < bases.length; i++) {
					bases[i] = toUppercaseName(bases[i]);
				}
				baseInterfaceNames = bases;
			}
		}
		
		return baseInterfaceNames;
	}
	
	
	//
	// Helpers for constucting JavaElements
	//
	
	public IPackageFragment findOrCreatePackage(
		IJavaProject project,
		String packageName)
		throws Exception {
		
		IPackageFragmentRoot[] roots = project.getPackageFragmentRoots();
		IPackageFragmentRoot root = null;
		for (int i = 0; i < roots.length; i++) {
			if (roots[i].getKind() == IPackageFragmentRoot.K_SOURCE) {
				root = roots[i];
				break;
			}
		}

		if (root == null) {
			throw new Exception("Can't get source IPackageFragmentRoot!");
		}

		IPackageFragment frag = root.getPackageFragment(packageName);
		if (frag.exists()) {
			report("Found package: " + frag.getElementName(), 2);
			return frag;
		} else {
			frag = root.createPackageFragment(packageName, false, null);
			report("Creating package: " + frag.getElementName(), 2);
			return frag;
		}
	}

}
