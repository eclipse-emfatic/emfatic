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

package org.eclipse.gymnast.generators.ast.primordial;

import java.util.ArrayList;

import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;

public class BuildManager {

	private final GeneratorContext _context;
	private final ArrayList _compUnitBuilderList;
	
	public BuildManager(GeneratorContext context) {
		_context = context;
		_compUnitBuilderList = new ArrayList();
	}
	
	public void createBuilders() throws Exception {
		
		addBuilder(new ASTCompUnitBuilder(_context.getASTBaseClassName(),
                "ASTBaseClass.cu", _context));
        addBuilder(new ASTCompUnitBuilder(_context.getASTTokenClassName(),
                "ASTTokenClass.cu", _context));
        addBuilder(new ASTCompUnitBuilder(_context.getASTVisitorClassName(),
                "ASTVisitorClass.cu", _context));
        
		Rule[] rules = _context.getGrammarInfo().getRules();
		for (int i = 0; i < rules.length; i++) {
			Rule rule = rules[i];
			ASTRuleCompUnitBuilder builder = new ASTRuleCompUnitBuilder(rule, _context);
			addBuilder(builder);
		}
	}
	
	private void addBuilder(JavaCompUnitBuilder builder) {
	    _compUnitBuilderList.add(builder);
	}
	
	public void build() throws Exception {
	    _context.getUtil().beginRules(_compUnitBuilderList.size());
	    
		for (int i = 0; i < _compUnitBuilderList.size(); i++) {
		    JavaCompUnitBuilder builder = (JavaCompUnitBuilder)_compUnitBuilderList.get(i);
			builder.build();
		}
	}
	
}
