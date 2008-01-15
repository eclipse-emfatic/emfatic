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

import java.util.ArrayList;

import org.eclipse.gymnast.generator.core.ast.GymnastASTNodeVisitor;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.ast.SimpleExpr;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class LiteralCollector extends GymnastASTNodeVisitor {
	
	private ArrayList _literals = new ArrayList();
	
	public LiteralCollector(Rule rule, GeneratorContext context) {
		visit(rule);
	}
	
	public String[] getLiterals() {
		return (String[]) _literals.toArray(new String[_literals.size()]);
	}
	
	public boolean beginVisit(SimpleExpr simpleExpr) {
		String value = simpleExpr.getValue().getText();
		if (value.startsWith("\"")) {
			_literals.add(value);
		}
		
		return false;
	}
}
