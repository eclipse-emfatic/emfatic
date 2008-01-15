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

/**
 * @author cjdaly@us.ibm.com
 *
 */
public class Util {
	
	//
	// Helpers for string manipulation scenarios
	//
	
	public static String removeSurroundingQuotes(String text) {
		return text.replaceAll("\"", "");
	}
	
	/**
	 * Uppercase the first letter of the name to make a good Java class
	 * name (so "myName" -> "MyName")
	 * @param name name to be uppercased
	 * @return name with first character uppercased
	 */
	public static String toUppercaseName(String name) {
		String val;
		char firstChar = Character.toUpperCase(name.charAt(0));
		if (name.length() > 1) {
			val = firstChar + name.substring(1);
		}
		else {
			val = Character.toString(firstChar);
		}
		return val;
	}
	
	/**
	 * Lowercase the first letter of the name to make a good Antlr rule
	 * name (so "MyName" -> "myName")
	 * @param name name to be lowercased
	 * @return name with first character lowercased
	 */
	public static String toLowercaseName(String name) {
		String val;
		char firstChar = Character.toLowerCase(name.charAt(0));
		if (name.length() > 1) {
			val = firstChar + name.substring(1);
		}
		else {
			val = Character.toString(firstChar);
		}
		return val;
	}
	
	/**
	 * Find all of the uppercase letters in the name and return a new
	 * String with just those letters lowercased (so "MyName" -> "mn")
	 * If there are no uppercase letters in the input String then return
	 * the input String.
	 * @param name name to be abbreviated
	 * @return abbreviated name
	 */
	public static String toAbbreviatedName(String name) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < name.length(); i++) {
			char c = name.charAt(i);
			if (Character.isUpperCase(c)) {
				sb.append(Character.toLowerCase(c));
			}
		}
		if (sb.length() == 0) return name;
		else return sb.toString();
		
	}
	
}
