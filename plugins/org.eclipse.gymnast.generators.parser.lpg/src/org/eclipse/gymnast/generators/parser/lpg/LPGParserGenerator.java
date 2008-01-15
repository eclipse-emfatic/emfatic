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

package org.eclipse.gymnast.generators.parser.lpg;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.generator.core.registry.ParserGenerator;
import org.eclipse.gymnast.generators.parser.lpg.resources.LPGFiles;
import org.eclipse.gymnast.generators.parser.lpg.templates.KWLexerTemplate;
import org.eclipse.gymnast.generators.parser.lpg.templates.LexerTemplate;
import org.eclipse.gymnast.generators.parser.lpg.templates.ParserTemplate;
import org.eclipse.jdt.core.IPackageFragment;

public class LPGParserGenerator extends ParserGenerator {

	private static final String LPG_EXE = "C:\\eclipse\\emfatic\\lpg\\sourceforge\\lpgdistribution\\lpgexe\\lpg.exe";
	
	public void generateParser(GeneratorContext context) throws Exception {
		IPackageFragment parserPackageFragment = context.getParserPackage();
		
		//
		// KeywordLexer
		//
		copyLPGFile(context, "KeywordTemplateB.g");
		copyLPGFile(context, "KWLexerMap.g");
		IFile kwLexerFile = writeKWLexerFile(context);
		invokeLPG(context, kwLexerFile);
		
		//
		// Lexer
		//
		copyLPGFile(context, "LexerTemplateB.g");
		IFile lexerFile = writeLexerFile(context);
		invokeLPG(context, lexerFile);
		
		//
		// Parser
		//
		copyLPGFile(context, "dtParserTemplateB.g");
		IFile parserFile = writeParserFile(context);
		invokeLPG(context, parserFile);
		
		parserPackageFragment.getResource().refreshLocal(IResource.DEPTH_ONE, null);
	}
	
	private IFile writeKWLexerFile(GeneratorContext context) throws Exception {
		String fileName = context.getASTName() + "KWLexer.g";
		context.getUtil().report("Writing LPG KWLexer: " + fileName);
		
		KWLexerTemplate template = new KWLexerTemplate();
		template.init(context);
		String fileText = template.generate();
		
		IPath parserPackagePath = getProjectRelativePath(context);
		IPath filePath = parserPackagePath.append(fileName);
		IFile file = context.getProject().getFile(filePath);
		writeFile(file, fileText);
		return file;
	}
	
	private IFile writeLexerFile(GeneratorContext context) throws Exception {
		String fileName = context.getASTName() + "Lexer.g";
		context.getUtil().report("Writing LPG Lexer: " + fileName);
		
		// start with the generated part
		LexerTemplate template = new LexerTemplate();
		template.init(context);
		StringBuffer fileText = new StringBuffer();
		fileText.append(template.generate());
		
		// append the user supplied part
		IPath astFolderPath = context.getASTFile().getProjectRelativePath().removeLastSegments(1);
		String userLexerFileName = context.getASTName() + "Lexer.lpg";
		IPath userLexerFilePath = astFolderPath.append(userLexerFileName);
		IFile userLexerFile = context.getProject().getFile(userLexerFilePath);
		if (userLexerFile.exists()) {
			fileText.append("\n");
			BufferedReader reader = new BufferedReader(new InputStreamReader(userLexerFile.getContents()));
			String line = reader.readLine();
			while(line != null) {
				fileText.append(line + "\n");
				line = reader.readLine();
			}
		}
		
		IPath parserPackagePath = getProjectRelativePath(context);
		IPath filePath = parserPackagePath.append(fileName);
		IFile file = context.getProject().getFile(filePath);
		writeFile(file, fileText.toString());
		return file;
	}
	
	private IFile writeParserFile(GeneratorContext context) throws Exception {
		String fileName = context.getASTName() + "Parser.g";
		context.getUtil().report("Writing LPG Parser: " + fileName);
		
		ParserTemplate template = new ParserTemplate();
		template.init(context);
		String fileText = template.generate();
		
		IPath parserPackagePath = getProjectRelativePath(context);
		IPath filePath = parserPackagePath.append(fileName);
		IFile file = context.getProject().getFile(filePath);
		writeFile(file, fileText);
		return file;
	}
	
	private IFile copyLPGFile(GeneratorContext context, String fileName) throws Exception {
		context.getUtil().report("Copying LPG file: " + fileName);
		
		IPath parserPackagePath = getProjectRelativePath(context);
		IPath filePath = parserPackagePath.append(fileName);
		IFile file = context.getProject().getFile(filePath);
		
		InputStream input = LPGFiles.getInputStream(fileName);
		writeFile(file, input);
		return file;
	}
	
	private IPath getProjectRelativePath(GeneratorContext context) {
		return context.getParserPackage().getResource().getProjectRelativePath();
	}
	
	private void invokeLPG(GeneratorContext context, IFile lpgFile) throws Exception {
		if (lpgFile == null) return;
		
		context.getUtil().report("Invoking LPG on file: " + lpgFile.getName());
		
		IPackageFragment parserPackageFragment = context.getParserPackage();
		File parserPackageDir = parserPackageFragment.getResource().getLocation().toFile();
		
		String command = LPG_EXE + " " + lpgFile.getName();
		
		Process process = Runtime.getRuntime().exec(command, null, parserPackageDir);
		ProcessOutputReader inputReader = new ProcessOutputReader(process.getInputStream());
		ProcessOutputReader errorReader = new ProcessOutputReader(process.getErrorStream());
		process.waitFor();
		
		String sdtOut = inputReader.getData();
		if (sdtOut.trim().length() > 0) {
			context.getUtil().report(sdtOut);
		}
		String sdtErr = errorReader.getData();
		if (sdtErr.trim().length() > 0) {
			context.getUtil().reportError(sdtErr);
		}
	}
	
}
