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

package org.eclipse.gymnast.generators.ast.primordial;

import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.generator.ASTUtil;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.generators.ast.primordial.templates.JavaCompUnitTemplateContext;
import org.eclipse.gymnast.generators.ast.primordial.templates.JavaRuleCompUnitTemplateContext;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class ASTRuleCompUnitBuilder extends ASTCompUnitBuilder{
    
    private final Rule _rule;
    
    public ASTRuleCompUnitBuilder(Rule rule, GeneratorContext context) {
        super(null, null, context);
        _rule = rule;
    }
    
    public Rule getRule() {
        return _rule;
    }
    
	public String getTypeName() {
	    return _util.toUppercaseName(getRuleName());
	}
	
	public String getTemplateTypeID() {
	    
		if (ASTUtil.isAbstract(_rule)) {
			return "ASTAbstractRule.cu";
		}
		else if (ASTUtil.isContainer(_rule)) {
		    return "ASTContainerRule.cu";
		}
		else if (ASTUtil.isList(_rule)) {
		    return "ASTListRule.cu";
		}
		else if (ASTUtil.isSequence(_rule)) {
		    return "ASTSequenceRule.cu";
		}
		else if (ASTUtil.isToken(_rule)) {
		    return "ASTTokenRule.cu";
		}
		else {
			throw new RuntimeException("Unknown rule type: " + ASTUtil.getName(_rule));
		}
	}
	
	public String getRuleName() {
	    return ASTUtil.getName(_rule);
	}
	
	protected JavaCompUnitTemplateContext createTemplateContext() {
	    return new JavaRuleCompUnitTemplateContext(getTemplateTypeID(), this, _context);
	}

}
