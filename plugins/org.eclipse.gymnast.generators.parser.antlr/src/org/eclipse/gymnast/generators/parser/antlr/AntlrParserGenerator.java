/*******************************************************************************
 * Copyright (c) 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.generators.parser.antlr;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.generator.core.registry.ParserGenerator;
import org.eclipse.gymnast.generators.parser.antlr.templates.ParserDriverTemplate;
import org.eclipse.jdt.core.IPackageFragment;

public class AntlrParserGenerator extends ParserGenerator {

	public void generateParser(GeneratorContext context) throws Exception {
		IFile grammarFile = writeGrammarFile(context);
		
		AntlrDriver antlrDriver = new AntlrDriver();
		antlrDriver.invokeAntlr(grammarFile, context.getUtil().getReporter());
		
		IPackageFragment parserPackageFragment = context.getParserPackage();
		writeParserDriver(context, parserPackageFragment);
	}
	
	private IFile writeGrammarFile(GeneratorContext context) throws Exception {

		String grammarFileName = context.getASTName() + ".g";
		context.getUtil().report("Writing Antlr grammar: " + grammarFileName);
		
		AntlrGrammarWriter grammarWriter = new AntlrGrammarWriter(context);
		StringBuffer sb = grammarWriter.writeGrammar();
		
		IPath astFolderPath = context.getASTFile().getProjectRelativePath().removeLastSegments(1);

		IPath lexerFilePath = astFolderPath.append(context.getLexerFileName());
		IFile lexerFile = context.getProject().getFile(lexerFilePath);
		if (lexerFile.exists()) {
			sb.append("\n");
			BufferedReader reader = new BufferedReader(new InputStreamReader(lexerFile.getContents()));
			String line = reader.readLine();
			while(line != null) {
				sb.append(line + "\n");
				line = reader.readLine();
			}
		}
		
		IPackageFragment parserPackageFragment = context.getParserPackage();
		IPath parserPackagePath = parserPackageFragment.getResource().getProjectRelativePath();
		IPath grammarFilePath = parserPackagePath.append(grammarFileName);
		IFile grammarFile = context.getProject().getFile(grammarFilePath);
		
		String grammarFileText = sb.toString();
		InputStream in = new ByteArrayInputStream(grammarFileText.getBytes());
		
		if (grammarFile.exists()) {
			grammarFile.setContents(in, true, false, null);
		}
		else {
			grammarFile.create(in, true, null);
		}
		
		return grammarFile;
	}
	
	private void writeParserDriver(GeneratorContext context, IPackageFragment parserPackageFragment) throws Exception {
		ParserDriverTemplate parserDriverTemplate = new ParserDriverTemplate();
		parserDriverTemplate.init(context);
		String fileText = parserDriverTemplate.generate();
		
		IFolder parserPackageFolder = (IFolder)parserPackageFragment.getResource();
        IFile parserDriverFile = parserPackageFolder.getFile(context.getASTName() + "ParserDriver.java");
        
        InputStream in = new ByteArrayInputStream(fileText.getBytes());
        
        if (parserDriverFile.exists()) {
        	parserDriverFile.setContents(in, true, false, null);
		}
		else {
			parserDriverFile.create(in, true, null);
		}
	}
	
}
