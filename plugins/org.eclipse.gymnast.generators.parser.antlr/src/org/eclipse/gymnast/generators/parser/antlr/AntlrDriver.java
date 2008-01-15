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

package org.eclipse.gymnast.generators.parser.antlr;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.gymnast.runtime.core.util.IReporter;

import antlr.FileLineFormatter;
import antlr.Tool;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class AntlrDriver extends Tool {
	
	private IReporter _reporter;
	
	public void invokeAntlr(IFile file, IReporter reporter) {
		_reporter = reporter;
		
		try {
			if (file == null) throw new Exception("no file!");
			
			reporter.report("Running Antlr on file: " + file.getName());
			
			IPath _location = file.getLocation();				
			IPath _folder = _location.removeLastSegments(1);
						
			String[] args = {"-o", 
				_folder.toOSString(),
				_location.toOSString()};

			doEverything(args);
			
			file.getParent().refreshLocal(IResource.DEPTH_ONE, null);				
			
		} catch (Exception ex) {
			reporter.reportError(ex);
		}
	}

	// Don't kill the whole shell just because Antlr can't parse a file!
	public void fatalError(String message) {
		_reporter.reportError("FATAL ERROR: " + message);
		throw new RuntimeException(message);
	}
	
	public void error(String s, String file, int line, int column) {
		String lineInfo = FileLineFormatter.getFormatter().getFormatString(file, line, column);
		_reporter.reportError(lineInfo);
		_reporter.reportError("  " + s);
	}
	
	public void error(String s) {
		_reporter.reportError(s);
	}
	
	public void reportException(Exception e, String message) {
		_reporter.reportError(message);
		_reporter.reportError(e);
	}
	
	public void reportProgress(String message) {
		_reporter.report(message);
	}
	
	public void toolError(String s) {
		_reporter.reportError("TOOL ERROR: " + s);
	}
	
	public void warning(String s, String file, int line, int column) {
		String lineInfo = FileLineFormatter.getFormatter().getFormatString(file, line, column);
		_reporter.reportWarning(lineInfo);
		_reporter.reportWarning("  " + s);
	}
	
	public void warning(String s) {
		_reporter.reportWarning(s);
	}
	
	public void warning(String[] s, String file, int line, int column) {
		if (s == null) return;
		String lineInfo = FileLineFormatter.getFormatter().getFormatString(file, line, column);
		_reporter.reportWarning(lineInfo);
		for (int i = 0; i < s.length; i++) {
			_reporter.reportWarning("  " + s[i]);
		}
	}
	
}
