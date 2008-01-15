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

package org.eclipse.gymnast.generators.ast.primordial.templates;

import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.generator.ASTUtil;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.runtime.core.templates.ext.ExtTemplateContext;
import org.eclipse.gymnast.runtime.core.templates.ext.ExtTemplateContextType;
import org.eclipse.gymnast.runtime.core.templates.ext.ExtTemplateVariableResolver;
import org.eclipse.jface.text.templates.TemplateContext;



/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class ForeachRule {
    
    public static final String ID = "foreach_Rule";
    
    public static class Resolver extends ExtTemplateVariableResolver.NestedIterator {
        Resolver() {
	        super("^"+ID, "iterate over rules in the language");
	    }
	    
        protected ExtTemplateContext getNestedContext(TemplateContext outerContext, Iterator iterator, Object iteratorObject) {
            JavaCompUnitTemplateContext c = (JavaCompUnitTemplateContext)outerContext;
            GeneratorContext gc = c.getGeneratorContext();
            
            Rule rule = (Rule)iteratorObject;
            String ruleName = ASTUtil.getName(rule);
            
            Context nc = new Context();
            nc.setVariable("ruleName", gc.getUtil().toUppercaseName(ruleName));
            nc.setVariable("ruleName_LC", ruleName);
            nc.setVariable("ruleName_UC", gc.getUtil().toUppercaseName(ruleName));
            
            String ruleBaseClassName = gc.getUtil().getRuleBaseClassName(rule);
            nc.setVariable("ruleBaseClassName", ruleBaseClassName);
            return nc;
        }
        
        protected Iterator getIterator(TemplateContext outerContext) {
            if (!(outerContext instanceof JavaCompUnitTemplateContext)) {
                return null;
            }
            
            JavaCompUnitTemplateContext c = (JavaCompUnitTemplateContext)outerContext;
            GeneratorContext gc = c.getGeneratorContext();
            
            Rule[] rules = gc.getGrammarInfo().getRules();
            return Arrays.asList(rules).iterator();
        }
    }
    
    public static class Context extends ExtTemplateContext {
        public Context() {
            super(ContextType);
        }
    }
    
    public static final ExtTemplateContextType ContextType =
        new GymnastContextType(ID, ID) {
        
        protected void addResolvers() {
            
        }
    };

}
