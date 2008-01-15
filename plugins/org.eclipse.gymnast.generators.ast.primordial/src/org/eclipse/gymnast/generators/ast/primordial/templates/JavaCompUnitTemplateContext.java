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

import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.generators.ast.primordial.JavaCompUnitBuilder;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateVariableResolver;



/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class JavaCompUnitTemplateContext extends GymnastTemplateContext {

    private final JavaCompUnitBuilder _compUnitBuilder;
    
    public JavaCompUnitTemplateContext(String typeID, JavaCompUnitBuilder compUnitBuilder, GeneratorContext generatorContext) {
        super(GymnastContextType.JavaCompUnit, typeID, generatorContext);
        
        _compUnitBuilder = compUnitBuilder;
    }
    
    public JavaCompUnitTemplateContext(TemplateContextType contextType, String typeID, JavaCompUnitBuilder compUnitBuilder, GeneratorContext generatorContext) {
        super(contextType, typeID, generatorContext);
        
        _compUnitBuilder = compUnitBuilder;
    }

	public JavaCompUnitBuilder getCompUnitBuilder() {
	    return _compUnitBuilder;
	}
	public String getTypeName() {
	    return getCompUnitBuilder().getTypeName();
	}
	
	//
	//
	//
	
	static abstract class Resolver extends TemplateVariableResolver {
	    Resolver(String type, String description) {
	        super(type, description);
	    }
	    
	    protected String resolve(TemplateContext context) {
	        String text = null;
            if (context instanceof JavaCompUnitTemplateContext) {
                JavaCompUnitTemplateContext c = (JavaCompUnitTemplateContext)context;
                GeneratorContext gc = c.getGeneratorContext();
                text = resolve(c, gc);
            }
            
            if (text == null) {
                return "${" + this.getType() + "}";
            }
            else {
                return text;
            }
	    }
	    
	    protected abstract String resolve(JavaCompUnitTemplateContext c, GeneratorContext gc);
	}
	
	static class TypeNameResolver extends Resolver {
		
	    TypeNameResolver() {
			super("typeName", "Name of this type");
		}
		
	    protected String resolve(JavaCompUnitTemplateContext c, GeneratorContext gc) {
            return c.getTypeName();
        }
	}
	
	static class Visitor_acceptImpl_Resolver extends Resolver {
		
	    Visitor_acceptImpl_Resolver() {
			super("ASTVisitorClass_acceptImpl", "acceptImpl method for the visitor framework");
		}
		
	    protected String resolve(JavaCompUnitTemplateContext c, GeneratorContext gc) {
		    try {
		        JavaMethodTemplateContext tc = new JavaMethodTemplateContext(getType(), c.getTypeName(), "acceptImpl", gc);
		        String s = tc.eval();
		        return s;
		    }
		    catch (Exception ex) {
		        return "";
		    }
	    }
	}
    
}
