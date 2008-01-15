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

package org.eclipse.gymnast.runtime.core.util;

import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;
import org.eclipse.gymnast.runtime.core.parser.ParseError;
import org.eclipse.gymnast.runtime.core.parser.ParseMessage;
import org.eclipse.gymnast.runtime.core.parser.ParseWarning;
import org.eclipse.ui.texteditor.MarkerUtilities;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class MarkerUtil {
	
	public static void updateMarkers(IFile file, ParseContext parseContext) {
		clearMarkers(file);
		placeMarkers(file, parseContext);
	}
	
	public static String getMarkerType() {
		return IMarker.PROBLEM;
	}
	
	public static void clearMarkers(final IFile file) {
	    try {
	        file.deleteMarkers(getMarkerType(), true, IResource.DEPTH_INFINITE);
	    }
		catch (Exception ex) {
		    ex.printStackTrace();
		}
	}
	
	public static void placeMarkers(IFile file, ParseContext parseContext) {
		try {
			ParseMessage[] parseMessages = parseContext.getMessages();
			for (int i = 0; i < parseMessages.length; i++) {
				createMarker(file, parseMessages[i]);
			}
		}
		catch (Exception ex) {
		    ex.printStackTrace();
		}
	}
	
	private static void createMarker(IFile file, ParseMessage message) {

		HashMap map = new HashMap();
		
		int offset = message.getOffset();
		int length = message.getLength();
		
		map.put(IMarker.MESSAGE, message.getMessage());
		
		if (message instanceof ParseError) {
			map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_ERROR));
		}
		else if (message instanceof ParseWarning) {
			map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_WARNING));
		}
		else {
			map.put(IMarker.SEVERITY, new Integer(IMarker.SEVERITY_INFO));
		}
		
		map.put(IMarker.CHAR_START, new Integer(offset));
		map.put(IMarker.CHAR_END, new Integer(offset + length));
		map.put(IMarker.TRANSIENT, new Boolean(true));
			
		try {
			MarkerUtilities.createMarker(file, map, getMarkerType());
		}
		catch (CoreException ex) {
		    ex.printStackTrace();
		}
		
	}

}
