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

import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.generator.core.generator.LiteralCollector;
import org.eclipse.gymnast.runtime.core.templates.ext.ExtTemplateContext;
import org.eclipse.gymnast.runtime.core.templates.ext.ExtTemplateContextType;
import org.eclipse.gymnast.runtime.core.templates.ext.ExtTemplateVariableResolver;
import org.eclipse.jface.text.templates.TemplateContext;



/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class ForeachKeywordLiteral {
    
    public static final String ID = "foreach_KeywordLiteral";
    
    public static class Resolver extends ExtTemplateVariableResolver.NestedIterator {
        Resolver() {
	        super("^"+ID, "iterate over keyword literals in the rule");
	    }
	    
        protected ExtTemplateContext getNestedContext(TemplateContext outerContext, Iterator iterator, Object iteratorObject) {
            JavaRuleCompUnitTemplateContext c = (JavaRuleCompUnitTemplateContext)outerContext;
            GeneratorContext gc = c.getGeneratorContext();
            
            String literal = (String)iteratorObject;
            String unquoted = gc.getUtil().removeSurroundingQuotes(literal);
            
            Context nc = new Context();
            nc.setVariable("literal", unquoted);
            nc.setVariable("literal_UC", unquoted.toUpperCase());
            
            return nc;
        }
        
        protected Iterator getIterator(TemplateContext outerContext) {
            if (!(outerContext instanceof JavaRuleCompUnitTemplateContext)) {
                return null;
            }
            
            JavaRuleCompUnitTemplateContext c = (JavaRuleCompUnitTemplateContext)outerContext;
            GeneratorContext gc = c.getGeneratorContext();
            String[] literals = new LiteralCollector(c.getRule(), gc).getLiterals();
            
            return Arrays.asList(literals).iterator();
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
