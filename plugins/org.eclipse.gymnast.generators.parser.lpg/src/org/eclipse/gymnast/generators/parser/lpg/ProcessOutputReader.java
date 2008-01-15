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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ProcessOutputReader {

	private final InputStream _inputStream;
	private final StringBuffer _buffer;
	private final ReaderThread _readerThread;
	
	public ProcessOutputReader(InputStream inputStream) {
		_inputStream = inputStream;
		_buffer = new StringBuffer();
		_readerThread = new ReaderThread();
		_readerThread.start();
	}
	
	public boolean isFinished() {
		return !_readerThread.isAlive();
	}
	
	public String getData() {
		return _buffer.toString();
	}
	
	private class ReaderThread extends Thread {
		
		public void run() {
			BufferedReader reader = new BufferedReader(new InputStreamReader(_inputStream));
			try {
//				String l = reader.readLine();
//				while (l != null) {
//					System.out.println(l);
//					l = reader.readLine();
//				}
				int c = reader.read();
				while (c != -1) {
					_buffer.append((char)c);
					c = reader.read();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
}
