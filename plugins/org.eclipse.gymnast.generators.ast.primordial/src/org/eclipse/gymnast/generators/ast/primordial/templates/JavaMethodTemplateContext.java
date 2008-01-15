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
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariableResolver;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class JavaMethodTemplateContext extends GymnastTemplateContext {
	
	private final String _typeName;
	private final String _methodName;
	
	public JavaMethodTemplateContext(String templateID, String typeName, String methodName, GeneratorContext generatorContext) {
		super(GymnastContextType.JavaMethod, templateID, generatorContext);
		
		_typeName = typeName;
		_methodName = methodName;
	}
	
	public String getTypeName() {
	    return _typeName;
	}
	public String getMethodName() {
	    return "ctor".equals(_methodName) ? getTypeName() : _methodName ;
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
            if (context instanceof JavaMethodTemplateContext) {
                JavaMethodTemplateContext c = (JavaMethodTemplateContext)context;
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
	    
	    protected abstract String resolve(JavaMethodTemplateContext c, GeneratorContext gc);
	}
	
	static class MethodNameResolver extends Resolver {
		
		MethodNameResolver() {
			super("methodName", "Name of this method");
		}
		
	    protected String resolve(JavaMethodTemplateContext c, GeneratorContext gc) {
            return c.getMethodName();
        }
	}
	
	static class TypeNameResolver extends Resolver {
		
	    TypeNameResolver() {
			super("typeName", "Name of the type containing this method");
		}
		
	    protected String resolve(JavaMethodTemplateContext c, GeneratorContext gc) {
            return c.getTypeName();
        }
	}
	
}
