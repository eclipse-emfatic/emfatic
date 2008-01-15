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
import org.eclipse.gymnast.generator.core.generator.RuleRefCollector;
import org.eclipse.gymnast.runtime.core.templates.ext.ExtTemplateContext;
import org.eclipse.gymnast.runtime.core.templates.ext.ExtTemplateContextType;
import org.eclipse.gymnast.runtime.core.templates.ext.ExtTemplateVariableResolver;
import org.eclipse.jface.text.templates.TemplateContext;



/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class ForeachChildElement {
    
    public static final String ID = "foreach_ChildElement";
    
    public static class Resolver extends ExtTemplateVariableResolver.NestedIterator {
        Resolver() {
	        super("^"+ID, "iterate over keyword literals in the rule");
	    }
	    
        protected ExtTemplateContext getNestedContext(TemplateContext outerContext, Iterator iterator, Object iteratorObject) {
            JavaRuleCompUnitTemplateContext c = (JavaRuleCompUnitTemplateContext)outerContext;
            GeneratorContext gc = c.getGeneratorContext();
            RuleRefCollector refs = gc.getGrammarInfo().getRuleRefCollector(c.getRule());
            
            String label = (String)iteratorObject;
            String type = refs.getType(label);
            String childType;
            String childTypeOrToken;
            String initExpr;
            if (gc.getUtil().isTokenReference(type)) {
                childType = gc.getASTTokenClassName();
                childTypeOrToken = "TokenInfo";
                initExpr = "new " + gc.getASTTokenClassName() + "(" + label + ")";
            }
            else {
                childType = gc.getUtil().toUppercaseName(type);
                childTypeOrToken = childType;
                initExpr = label;
            }
            
            Context nc = new Context();
            nc.setVariable("childName", label);
            nc.setVariable("childName_UC", gc.getUtil().toUppercaseName(label));
            nc.setVariable("childType", childType);
            nc.setVariable("childTypeOrToken", childTypeOrToken);
            nc.setVariable("initExpr", initExpr);
            nc.setVariable("comma", iterator.hasNext() ? "," : "");
            
            
            return nc;
        }
        
        protected Iterator getIterator(TemplateContext outerContext) {
            if (!(outerContext instanceof JavaRuleCompUnitTemplateContext)) {
                return null;
            }
            
            JavaRuleCompUnitTemplateContext c = (JavaRuleCompUnitTemplateContext)outerContext;
            GeneratorContext gc = c.getGeneratorContext();
            RuleRefCollector refs = gc.getGrammarInfo().getRuleRefCollector(c.getRule());
            String[] labels = refs.getLabels();
            
            return Arrays.asList(labels).iterator();
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
