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

package org.eclipse.gymnast.generators.parser.javacc;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.generator.core.registry.ParserGenerator;
import org.eclipse.gymnast.generators.parser.javacc.templates.ExtSimpleCharStreamTemplate;
import org.eclipse.gymnast.generators.parser.javacc.templates.ExtTokenManagerTemplate;
import org.eclipse.gymnast.generators.parser.javacc.templates.ExtTokenTemplate;
import org.eclipse.gymnast.generators.parser.javacc.templates.JavaCCParserTemplate;
import org.eclipse.gymnast.generators.parser.javacc.templates.ParserDriverTemplate;
import org.eclipse.gymnast.runtime.core.util.IReporter;
import org.eclipse.jdt.core.IPackageFragment;

public class JavaCCParserGenerator extends ParserGenerator {

	public void generateParser(GeneratorContext context) throws Exception {
		IPackageFragment parserPackageFragment = context.getParserPackage();
		IFile grammarFile = writeGrammarFile(context, parserPackageFragment);
		invokeJavaCC(context, grammarFile);
		writeParserDriver(context, parserPackageFragment);
		writeExtToken(context, parserPackageFragment);
		writeExtTokenManager(context, parserPackageFragment);
		writeExtSimpleCharStream(context, parserPackageFragment);
	}
	
	private IFile writeGrammarFile(GeneratorContext context, IPackageFragment parserPackageFragment) throws Exception {
		String grammarFileName = context.getASTName() + ".jj";
		context.getUtil().report("Writing JavaCC grammar: " + grammarFileName);
		
		JavaCCParserTemplate parserTemplate = new JavaCCParserTemplate();
		parserTemplate.init(context);
		StringBuffer grammarFileText = new StringBuffer(parserTemplate.generate());
		
		JavaCCGrammarWriter grammarWriter = new JavaCCGrammarWriter(context);
		grammarFileText.append(grammarWriter.writeGrammar());
		
		appendLexerFile(context, grammarFileText);
		
		IPath parserPackagePath = parserPackageFragment.getResource().getProjectRelativePath();
		IPath grammarFilePath = parserPackagePath.append(grammarFileName);
		IFile grammarFile = context.getProject().getFile(grammarFilePath);
		
		writeFile(grammarFile, grammarFileText.toString());
		
		return grammarFile;
	}
	
	private void appendLexerFile(GeneratorContext context, StringBuffer grammarFileText) throws Exception {
		IPath astFolderPath = context.getASTFile().getProjectRelativePath().removeLastSegments(1);

		String lexerFileName = context.getASTName() + "Lexer.jj";
		
		IPath lexerFilePath = astFolderPath.append(lexerFileName);
		IFile lexerFile = context.getProject().getFile(lexerFilePath);
		if (lexerFile.exists()) {
			grammarFileText.append("\n");
			BufferedReader reader = new BufferedReader(new InputStreamReader(lexerFile.getContents()));
			String line = reader.readLine();
			while(line != null) {
				grammarFileText.append(line + "\n");
				line = reader.readLine();
			}
		}
	}
	
	private void invokeJavaCC(GeneratorContext context, IFile grammarFile) throws Exception {
		
		final IReporter reporter = context.getUtil().getReporter();
		reporter.report("Running JavaCC on file: " + grammarFile.getName());
		
		String grammarFilePath = grammarFile.getLocation().toOSString();
		
		String outputFolderPath = grammarFile.getParent().getLocation().toOSString();
		String outputDirectoryArg = "-OUTPUT_DIRECTORY=" + outputFolderPath;
		
		String optionStatic = "-STATIC=false";
		String optionLookahead = "-LOOKAHEAD=3";
		// String optionForceLookaheadCheck = "-FORCE_LA_CHECK=true";
		
		String[] args = new String[] {
				optionLookahead,
				// optionForceLookaheadCheck,
				optionStatic,
				outputDirectoryArg,
				grammarFilePath
				};
		
		// TODO waiting for javacc.jar ... int status = Main.mainProgram(args);
		
		Runtime r = Runtime.getRuntime();
		String argsStr = optionLookahead + " " + optionStatic + " " + outputDirectoryArg + " " + grammarFilePath;
		String cmdLine = "java org.javacc.parser.Main " + argsStr; 
		System.out.println("GymnastGenerator about to run : " + cmdLine);
		Process p = r.exec(cmdLine);
		DataInputStream inStream = new DataInputStream(p.getInputStream());
		String line = inStream.readLine();
		System.out.println("GymnastGenerator JavaCC returned : " + line);
		
		// make sure the JavaCC generated files show in the workspace
		grammarFile.getParent().refreshLocal(IResource.DEPTH_ONE, null);
		
		/* if (status != 0) {
			reporter.reportError("JavaCC returned error code: " + status);
		}
		*/
	}
	
	private void writeParserDriver(GeneratorContext context, IPackageFragment parserPackageFragment) throws Exception {
		ParserDriverTemplate parserDriverTemplate = new ParserDriverTemplate();
		parserDriverTemplate.init(context);
		String fileText = parserDriverTemplate.generate();
		
		IFolder parserPackageFolder = (IFolder)parserPackageFragment.getResource();
        IFile parserDriverFile = parserPackageFolder.getFile(context.getASTName() + "ParserDriver.java");
        
        writeFile(parserDriverFile, fileText);
	}
	
	private void writeExtToken(GeneratorContext context, IPackageFragment parserPackageFragment) throws Exception {
		ExtTokenTemplate extTokenTemplate = new ExtTokenTemplate();
		extTokenTemplate.init(context);
		String fileText = extTokenTemplate.generate();
		
		IFolder parserPackageFolder = (IFolder)parserPackageFragment.getResource();
        IFile extTokenFile = parserPackageFolder.getFile("ExtToken.java");
        
        writeFile(extTokenFile, fileText);
	}
	
	private void writeExtTokenManager(GeneratorContext context, IPackageFragment parserPackageFragment) throws Exception {
		ExtTokenManagerTemplate extTokenManagerTemplate = new ExtTokenManagerTemplate();
		extTokenManagerTemplate.init(context);
		String fileText = extTokenManagerTemplate.generate();
		
		IFolder parserPackageFolder = (IFolder)parserPackageFragment.getResource();
        IFile extTokenManagerFile = parserPackageFolder.getFile("Ext" + context.getASTName() + "ParserTokenManager.java");
        
        writeFile(extTokenManagerFile, fileText);
	}
	
	private void writeExtSimpleCharStream(GeneratorContext context, IPackageFragment parserPackageFragment) throws Exception {
		ExtSimpleCharStreamTemplate extSimpleCharStreamTemplate = new ExtSimpleCharStreamTemplate();
		extSimpleCharStreamTemplate.init(context);
		String fileText = extSimpleCharStreamTemplate.generate();
		
		IFolder parserPackageFolder = (IFolder)parserPackageFragment.getResource();
        IFile extTokenManagerFile = parserPackageFolder.getFile("ExtSimpleCharStream.java");
        
        writeFile(extTokenManagerFile, fileText);
	}
	
}
