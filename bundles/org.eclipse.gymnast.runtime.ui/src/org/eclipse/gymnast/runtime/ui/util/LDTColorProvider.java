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

package org.eclipse.gymnast.runtime.ui.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * @author cjdaly@us.ibm.com
 *
 */
public class LDTColorProvider {
	
	public static final RGB RED = new RGB(255, 0, 0);
	public static final RGB GREEN = new RGB(0, 255, 0);
	public static final RGB BLUE = new RGB(0, 0, 255);
	
	public static final RGB DARK_RED = new RGB(128, 0, 0);
	public static final RGB DARK_GREEN = new RGB(0, 128, 0);
	public static final RGB DARK_BLUE = new RGB(0, 0, 128);
	
	public static final RGB YELLOW = new RGB(255, 255, 0);
	public static final RGB AQUA = new RGB(0, 255, 255);
	public static final RGB FUCHSIA = new RGB(255, 0, 255);
	
	public static final RGB BLACK = new RGB(0, 0, 0);
	public static final RGB GREY3 = new RGB(32, 32, 32);
	public static final RGB GREY2 = new RGB(64, 64, 64);
	public static final RGB GREY1 = new RGB(128, 128, 128);
	public static final RGB WHITE = new RGB(255, 255, 255);
	
	private Map<RGB, Color> fColorTable = new HashMap<RGB, Color>(10);

	/**
	 * Release all of the color resources held onto by the receiver.
	 */	
	public void dispose() {
		Iterator<Color> e = fColorTable.values().iterator();
		while (e.hasNext())
			 ((Color) e.next()).dispose();
	}

	/**
	 * Return the Color that is stored in the Color table as rgb.
	 */
	public Color getColor(RGB rgb) {
		if (rgb == null) return null;
		
		Color color = (Color) fColorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			fColorTable.put(rgb, color);
		}
		return color;
	}
}
