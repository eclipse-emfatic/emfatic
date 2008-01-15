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

import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.generator.ASTUtil;
import org.eclipse.gymnast.generator.core.generator.GeneratorContext;
import org.eclipse.gymnast.generator.core.generator.GeneratorUtil;
import org.eclipse.gymnast.generators.ast.primordial.ASTRuleCompUnitBuilder;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateVariableResolver;



/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class JavaRuleCompUnitTemplateContext extends JavaCompUnitTemplateContext {
    
    private ASTRuleCompUnitBuilder _ruleCompUnitBuilder;
	private String _baseClassName;
	private String[] _baseInterfaceNames;
    
    public JavaRuleCompUnitTemplateContext(String typeID, ASTRuleCompUnitBuilder ruleCompUnitBuilder, GeneratorContext generatorContext) {
        super(GymnastContextType.JavaRuleCompUnit, typeID, ruleCompUnitBuilder, generatorContext);
        
        _ruleCompUnitBuilder = ruleCompUnitBuilder;

        
        GeneratorContext context = generatorContext;
        GeneratorUtil util = context.getUtil();
        Rule rule = ruleCompUnitBuilder.getRule();
		
		_baseClassName = util.getRuleBaseClassName(rule);
		_baseInterfaceNames = util.getRuleBaseInterfaceNames(rule);
    }
    
	public ASTRuleCompUnitBuilder getRuleCompUnitBuilder() {
	    return _ruleCompUnitBuilder;
	}
	
	public Rule getRule() {
	    return _ruleCompUnitBuilder.getRule();
	}
	
	public String getRuleName() {
	    return _ruleCompUnitBuilder.getRuleName();
	}
	
	public String getBaseClassName() {
		return _baseClassName;
	}
	
	public String[] getBaseInterfaceNames() {
	    if (_baseInterfaceNames == null) return null;
		else return (String[])_baseInterfaceNames.clone();
	}
	
	public boolean isAbstract() {
	    return ASTUtil.isAbstract(getRule());
	}
	public boolean isInterface() {
	    return ASTUtil.isInterface(getRule());
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
            if (context instanceof JavaRuleCompUnitTemplateContext) {
                JavaRuleCompUnitTemplateContext c = (JavaRuleCompUnitTemplateContext)context;
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
	    
	    protected abstract String resolve(JavaRuleCompUnitTemplateContext c, GeneratorContext gc);
	}
	
	static class ExtendsResolver extends Resolver {
		
	    ExtendsResolver() {
			super("extends", "auto calculate the extends clause");
		}
		
	    protected String resolve(JavaRuleCompUnitTemplateContext c, GeneratorContext gc) {
	        String baseClassName = c.getBaseClassName();
	        if ((baseClassName == null) || "".equals(baseClassName)) {
	            return "";
	        }
	        else {
	            return "extends " + baseClassName;
	        }
        }
	}
	
	static class ImplementsResolver extends Resolver {
		
	    ImplementsResolver() {
			super("implements", "auto calculate the implements clause");
		}
		
	    protected String resolve(JavaRuleCompUnitTemplateContext c, GeneratorContext gc) {
	        String[] baseInterfaceNames = c.getBaseInterfaceNames();
	        
	        if ((baseInterfaceNames == null) ||(baseInterfaceNames.length == 0)) {
	            return "";
	        }
	        else {
	            StringBuffer sb = new StringBuffer("implements ");
	            sb.append(baseInterfaceNames[0]);
				for (int i = 1; i < baseInterfaceNames.length; i++) {
					sb.append(", " + baseInterfaceNames[i]);
				}
	            return sb.toString();
	        }
        }
	}
	
	static class TypeKindResolver extends Resolver {
		
	    TypeKindResolver() {
			super("typeKind", "auto calculate whether the type is an abstract class, normal class or interface");
		}
		
	    protected String resolve(JavaRuleCompUnitTemplateContext c, GeneratorContext gc) {
	        if (c.isInterface()) {
	            return "interface";
	        }
	        else {
	            if (c.isAbstract()) {
	                return "abstract class";
	            }
	            else {
	                return "class";
	            }
	        }
        }
	}

}
