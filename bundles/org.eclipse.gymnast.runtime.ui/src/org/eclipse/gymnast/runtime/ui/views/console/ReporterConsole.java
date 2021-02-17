/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.runtime.ui.views.console;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.gymnast.runtime.core.util.IReporter;
import org.eclipse.gymnast.runtime.ui.util.LDTColorProvider;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.MessageConsoleStream;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class ReporterConsole implements IReporter {
	
	private RGB BLACK = new RGB(0,0,0);
	private RGB BLUE = new RGB(0,0,255);
	private RGB RED = new RGB(255,0,0);
	private RGB ORANGE = new RGB(255,128,0);
	
	private final LDTColorProvider _colorProvider;
	private final MessageConsole _console;
	private final MessageConsoleStream _outStream;
	private final MessageConsoleStream _subStream;
	private final MessageConsoleStream _wrnStream;
	private final MessageConsoleStream _errStream;
	
	public ReporterConsole(String name) {
		_colorProvider = new LDTColorProvider();
		
		_console = new MessageConsole(name, null);
		ConsolePlugin.getDefault().getConsoleManager().addConsoles(new IConsole[]{_console});

		_outStream = _console.newMessageStream();
		_outStream.setColor(getColor(BLUE));

		_subStream = _console.newMessageStream();
		_subStream.setColor(getColor(BLACK));
		
		_wrnStream = _console.newMessageStream();
		_wrnStream.setColor(getColor(ORANGE));
		
		_errStream = _console.newMessageStream();
		_errStream.setColor(getColor(RED));
	}
	
	public void report(String message) {
		report(message, 0);
	}
	
	public void report(String message, int level) {
		if (level == 0) {
			_outStream.println(message);
		}
		else {
			_subStream.println(indentMessage(message, level));
		}
	}
	private String indentMessage(String message, int level) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < level; i++) sb.append("  ");
		sb.append(message);
		return sb.toString();
	}
	
	public void reportWarning(String message) {
		_wrnStream.println(message);
	}

	public void reportError(String message) {
		_errStream.println(message);
	}
	public void reportError(Exception ex) {
		StringWriter sw = new StringWriter();
		ex.printStackTrace(new PrintWriter(sw));
		_errStream.println(sw.toString());
	}
	
	private Color getColor(RGB rgb) {
		return _colorProvider.getColor(rgb);
	}
	
}
