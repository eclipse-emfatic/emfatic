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

import java.text.DateFormat;
import java.util.Date;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gymnast.generator.core.ast.CompUnit;
import org.eclipse.gymnast.runtime.core.util.IReporter;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.JavaCore;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class GeneratorContext {
    
    private final String _toolName = "Gymnast";
    private String _generatedByText;
	
	private Date _beginTime;
	private String _beginTimestamp;
	private Date _endTime;
	private String _endTimestamp;
	
	private IFile _astFile;
	private GeneratorUtil _util;
	private boolean _isReportingVerbose = true;
	
	private CompUnit _compUnit;
	private GrammarInfo _grammarInfo;
	
	private String _astName;
	
	private String _astGeneratorId;
	private String _parserGeneratorId;
	private String _parserPackageName;
	private String _astPackageName;
	private String _lexerFileName;
	private String _astBaseClassName;
	private String _astBaseClassBaseName;
	private String _astTokenClassName;
	private String _astVisitorClassName;
	private final String _ldtASTNodePackageName = "org.eclipse.gymnast.runtime.core.ast";
	private final String _ldtASTNodeClassName = "ASTNode";
	private final String _ldtParserPackageName = "org.eclipse.gymnast.runtime.core.parser";

	private IJavaProject _javaProject;
	private IPackageFragment _astPackage;
	private IPackageFragment _parserPackage;
	
	public GeneratorContext(IFile astFile, IProgressMonitor monitor, IReporter reporter) throws Exception {
		_beginTime = new Date();
		_beginTimestamp = DateFormat.getDateTimeInstance().format(_beginTime);
		
		_astFile = astFile;
		
		_util = new GeneratorUtil(this, monitor, reporter);
		
		IProject proj = getProject();
		
		if (!proj.hasNature("org.eclipse.jdt.core.javanature")) {
			throw new Exception("no java project nature!");
		}
		
		_javaProject = JavaCore.create(proj);
	}
	
	public void initCompUnit(CompUnit compUnit) {
		_compUnit = compUnit;
	}
	
	public void initGrammarInfo(GrammarInfo grammarInfo) {
		_grammarInfo = grammarInfo;
		_astName = grammarInfo.getLanguageName();
	}
	
	void processOptions() throws Exception {
		
		_astGeneratorId = _grammarInfo.getOptionValue("astGenerator");
		if (_astGeneratorId == null) {
			_astGeneratorId = "primordial";
		}
		
		_astPackageName = _grammarInfo.getOptionValue("astPackageName");
		if (_astPackageName == null) {
			// get the path of the ast file and remove the filename
			// .../src/org/foo/lang/lang.g -> .../src/org/foo/lang
			IPath absPath = _astFile.getFullPath().removeLastSegments(1);
			IPackageFragment frag = _javaProject.findPackageFragment(absPath);
			if (frag != null) {
				_astPackageName = frag.getElementName() + ".gen.ast";
			}
			else {
				_astPackageName = ""; // global package ... will this work?
			}
		}
		
		_parserGeneratorId = _grammarInfo.getOptionValue("parserGenerator");
		if (_parserGeneratorId == null) {
			_parserGeneratorId = "antlr";
		}
		
		_parserPackageName = _grammarInfo.getOptionValue("parserPackageName");
		if (_parserPackageName == null) {
			// get the path of the ast file and remove the filename
			// .../src/org/foo/lang/lang.g -> .../src/org/foo/lang
			IPath absPath = _astFile.getFullPath().removeLastSegments(1);
			IPackageFragment frag = _javaProject.findPackageFragment(absPath);
			if (frag != null) {
				_parserPackageName = frag.getElementName() + ".gen.parser";
			}
			else {
				_parserPackageName = ""; // global package ... will this work?
			}
		}

		_lexerFileName = _grammarInfo.getOptionValue("lexerFileName");
		if (_lexerFileName == null) {
			_lexerFileName = getASTName() + "Lexer.g";
		}
		
		_astBaseClassName = _grammarInfo.getOptionValue("astBaseClassName");
		if (_astBaseClassName == null) {
			_astBaseClassName = _astName + "ASTNode";
		}
		
		_astBaseClassBaseName = _grammarInfo.getOptionValue("astBaseClassBaseName");
		if (_astBaseClassBaseName == null) {
			_astBaseClassBaseName = "ASTNodeImpl";
		}
		
		_astTokenClassName = _grammarInfo.getOptionValue("astTokenClassName");
		if (_astTokenClassName == null) {
			_astTokenClassName = _astName + "TokenNode";
		}
		
		_astVisitorClassName = _grammarInfo.getOptionValue("astVisitorClassName");
		if (_astVisitorClassName == null) {
			_astVisitorClassName = _astName + "ASTNodeVisitor";
		}
	}
	
	void initJavaPackages() throws Exception {
		_astPackage = _util.findOrCreatePackage(_javaProject, _astPackageName);
		checkExists(_astPackage, "AST package", _astPackageName);
		
		_parserPackage = _util.findOrCreatePackage(_javaProject, _parserPackageName);
		checkExists(_parserPackage, "parser package", _parserPackageName);
	}
	private void checkExists(IJavaElement el, String kind, String name) throws Exception {
		if ((el == null) || (!el.exists())) {
			throw new Exception("Could not get " + kind + " (name: " + name + ")");
		}
	}
	
	void initEndTime() {
		_endTime = new Date();
		_endTimestamp = DateFormat.getDateTimeInstance().format(_endTime);
	}
	
	//
	// data accessors
	//
	
	public String getToolName() {
	    return _toolName;
	}
	
	public String getGeneratedByText() {
	    if (_generatedByText == null) {
	        _generatedByText = "@generated by " + getToolName() + " from "
                    + getASTFile().getName() + " on " + getBeginTimestamp();
	    }
	    return _generatedByText;
	}
	
	public String getBeginTimestamp() {
		return _beginTimestamp;
	}
	public String getEndTimestamp() {
		return _endTimestamp;
	}

	
	public IProject getProject() {
		return _astFile.getProject();
	}
	public IJavaProject getJavaProject() {
	    return _javaProject;
	}
	public IPackageFragment getASTPackage() {
		return _astPackage;
	}
	public IPackageFragment getParserPackage() {
		return _parserPackage;
	}
	
	public IFile getASTFile() {
		return _astFile;
	}
	public GeneratorUtil getUtil() {
		return _util;
	}
	public boolean isReportingVerbose() {
		return _isReportingVerbose;
	}
	public int getVerbosity() {
		return 5;
	}
	
	public String getASTName() {
		return _astName;
	}
	
	public String getASTGeneratorId() {
		return _astGeneratorId;
	}
	public String getParserGeneratorId() {
		return _parserGeneratorId;
	}
	public String getParserPackageName() {
		return _parserPackageName;
	}
	public String getASTPackageName() {
		return _astPackageName;
	}
	public String getLexerFileName() {
		return _lexerFileName;
	}
	public String getASTBaseClassName() {
		return _astBaseClassName;
	}
	public String getASTBaseClassBaseName() {
		return _astBaseClassBaseName;
	}
	public String getASTTokenClassName() {
		return _astTokenClassName;
	}
	public String getASTVisitorClassName() {
		return _astVisitorClassName;
	}
	
	public String getLDT_ASTNodePackageName() {
		return _ldtASTNodePackageName;
	}
	public String getLDT_ASTNodeClassName() {
		return _ldtASTNodeClassName;
	}
	public String getLDT_ParserPackageName() {
		return _ldtParserPackageName;
	}
	public String getEntryRuleName() {
	    return _grammarInfo.getEntryRuleName();
	}
	public String getEntryRuleClassName() {
	    return _grammarInfo.getEntryRuleClassName();
	}
	
	
	public GrammarInfo getGrammarInfo() {
		return _grammarInfo;
	}
	public CompUnit getCompUnit() {
		return _compUnit;
	}
	
}
