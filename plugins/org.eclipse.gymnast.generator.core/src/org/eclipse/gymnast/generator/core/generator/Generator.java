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

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.gymnast.generator.core.ast.CompUnit;
import org.eclipse.gymnast.generator.core.parser.ParserDriver;
import org.eclipse.gymnast.generator.core.registry.ASTGeneratorDescriptor;
import org.eclipse.gymnast.generator.core.registry.GeneratorRegistry;
import org.eclipse.gymnast.generator.core.registry.ParserGeneratorDescriptor;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;
import org.eclipse.gymnast.runtime.core.parser.ParseMessage;
import org.eclipse.gymnast.runtime.core.util.IReporter;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class Generator {
	
	public void generate(IFile astFile, IProgressMonitor monitor, IReporter reporter) {
		try {
			GeneratorContext context = new GeneratorContext(astFile, monitor, reporter);
			GeneratorUtil util = context.getUtil();
			util.report("Gymnast run started at " + context.getBeginTimestamp());
			
			CompUnit compUnit = parse(context);
			context.initCompUnit(compUnit);
			
			GrammarInfo info = new GrammarInfo(context);
			context.initGrammarInfo(info);
			
			context.processOptions();
			context.initJavaPackages();
			
			final GeneratorRegistry generatorRegistry = GeneratorRegistry.getInstance();
			
			final String astGeneratorId = context.getASTGeneratorId();
			ASTGeneratorDescriptor astGeneratorDescriptor = generatorRegistry.getASTGeneratorDescriptor(astGeneratorId);
			if (astGeneratorDescriptor != null) {
				astGeneratorDescriptor.getASTGenerator().generateAST(context);
			} else {
				util.reportWarning("No AST generator found for id: " + astGeneratorId);
			}
			
			final String parserGeneratorId = context.getParserGeneratorId();
			ParserGeneratorDescriptor parserGeneratorDescriptor = generatorRegistry.getParserGeneratorDescriptor(parserGeneratorId);
			if (parserGeneratorDescriptor != null) {
				parserGeneratorDescriptor.getParserGenerator().generateParser(context);
			} else {
				util.reportWarning("No parser generator found for id: " + parserGeneratorId);
			}
			
			context.initEndTime();
			util.report("Gymnast run finished normally at " + context.getEndTimestamp());
		}
		catch (Exception ex) {
			reporter.reportError(ex);
		}
	}
	
	private CompUnit parse(GeneratorContext context) throws Exception {
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(context.getASTFile().getContents()));
		
		ParserDriver parser = new ParserDriver();
		ParseContext parseContext = parser.parse(reader);
		
		if (parseContext.getMessageCount() > 0) {
			ParseMessage[] msgs = parseContext.getMessages();
			for (int i = 0; i < msgs.length; i++) {
				context.getUtil().reportError("parse message: " + msgs[i].getMessage());
			}
			throw new Exception("Errors in parse!");
		}
		
		CompUnit parseRoot = (CompUnit)parseContext.getParseRoot();
		if (parseRoot == null) {
			throw new Exception("Incomplete input!");
		}
		
		return parseRoot;
	}
	
}
