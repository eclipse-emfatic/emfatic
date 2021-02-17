/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.partition;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class EmfaticPartitionScanner extends RuleBasedPartitionScanner {
	public final static String packagePart = "packagePart";
	public final static String importPart = "importPart";
	public final static String annotationPart = "annotationPart";
	public final static String subPackagePart = "subPackagePart";
	public final static String attrPart = "attrPart";
	public final static String refPart = "refPart";
	public final static String valrPart = "valPart";
	public final static String opPart = "opPart";
	public final static String datatypePart = "datatypePart";
	public final static String enumPart = "enumPart";
	public final static String mapentryPart = "mapentryPart";
	public final static String classHeadingPart = "classHeadingPart";
	public final static String ifaceHeadingPart = "ifaceHeadingPart";
	public final static String multiLineComment = "multiLineComment";
	public final static String singleLineComment = "singleLineComment";

	public static String[] contentTypes() {
		return new String[] { IDocument.DEFAULT_CONTENT_TYPE, packagePart, importPart, annotationPart, subPackagePart,
				attrPart, refPart, valrPart, opPart, datatypePart, enumPart, mapentryPart, classHeadingPart,
				ifaceHeadingPart, multiLineComment, singleLineComment };
	}

	public EmfaticPartitionScanner() {

		IToken packagePartToken = new Token(packagePart); // 1
		IToken importPartToken = new Token(importPart); // 2
		IToken annotationPartToken = new Token(annotationPart); // 3
		IToken subPackagePartToken = new Token(subPackagePart); // 4
		IToken attrPartToken = new Token(attrPart); // 5
		IToken refPartToken = new Token(refPart); // 6
		IToken valrPartToken = new Token(valrPart); // 7
		IToken opPartToken = new Token(opPart); // 8
		IToken datatypePartToken = new Token(datatypePart); // 9
		IToken enumPartToken = new Token(enumPart); // 10
		IToken mapentryPartToken = new Token(mapentryPart); // 11
		IToken classHeadingPartToken = new Token(classHeadingPart); // 12
		IToken ifaceHeadingPartToken = new Token(ifaceHeadingPart); // 13
		IToken multiLineCommentToken = new Token(multiLineComment); // 14
		IToken singleLineCommentToken = new Token(singleLineComment); // 15

		IPredicateRule[] rules = new IPredicateRule[16];

		rules[0] = new NonMatchingRule();
		rules[1] = new SingleLineRule("package", ";", packagePartToken);
		rules[2] = new SingleLineRule("import", ";", importPartToken);
		rules[3] = new MultiLineRule("@", ")", annotationPartToken);
		rules[4] = new SingleLineRule("package", "{", subPackagePartToken);
		rules[5] = new MultiLineRule("attr", ";", attrPartToken);
		rules[6] = new MultiLineRule("ref", ";", refPartToken);
		rules[7] = new MultiLineRule("val", ";", valrPartToken);
		rules[8] = new MultiLineRule("op", ";", opPartToken);
		rules[9] = new MultiLineRule("datatype", ";", datatypePartToken);
		rules[10] = new MultiLineRule("enum", "}", enumPartToken);
		rules[11] = new MultiLineRule("mapentry", ";", mapentryPartToken);
		rules[12] = new MultiLineRule("class", "{", classHeadingPartToken);
		rules[13] = new MultiLineRule("interface", "{", ifaceHeadingPartToken);
		rules[14] = new MultiLineRule("/*", "*/", multiLineCommentToken);
		rules[15] = new EndOfLineRule("//", singleLineCommentToken);

		setPredicateRules(rules);
	}
}
