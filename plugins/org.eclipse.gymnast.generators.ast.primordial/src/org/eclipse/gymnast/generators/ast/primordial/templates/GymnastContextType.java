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

import org.eclipse.gymnast.runtime.core.templates.ext.ExtTemplateContextType;
import org.eclipse.jface.text.templates.GlobalTemplateVariables;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public abstract class GymnastContextType extends ExtTemplateContextType {
	
	public static final String ID_PREFIX = "org.eclipse.gymnast.generators.ast.primordial.templates";
	
	protected GymnastContextType(String idSuffix, String name) {
		super(ID_PREFIX + "." + idSuffix, name);
		
		addResolvers();
	}
	
	protected abstract void addResolvers();
	
	public static final GymnastContextType JavaMethod = new GymnastContextType(
            "JavaMethod", "Java Method") {

        protected void addResolvers() {
        	
            addResolver(new GymnastTemplateContext.ToolNameResolver());
        	addResolver(new GymnastTemplateContext.GeneratedByResolver());
        	addResolver(new GymnastTemplateContext.BeginTimeResolver());
        	addResolver(new GymnastTemplateContext.ASTFileNameResolver());
        	addResolver(new GymnastTemplateContext.ASTNameResolver());
        	addResolver(new GymnastTemplateContext.ParserPackageNameResolver());
        	addResolver(new GymnastTemplateContext.ASTPackageNameResolver());
        	addResolver(new GymnastTemplateContext.ASTBaseClassNameResolver());
        	addResolver(new GymnastTemplateContext.ASTBaseClassBaseNameResolver());
        	addResolver(new GymnastTemplateContext.ASTTokenClassNameResolver());
        	addResolver(new GymnastTemplateContext.ASTVisitorClassNameResolver());
        	addResolver(new GymnastTemplateContext.LDT_ASTNodePackageNameResolver());
        	addResolver(new GymnastTemplateContext.LDT_ASTNodeClassNameResolver());
        	addResolver(new GymnastTemplateContext.LDT_ParserPackageNameResolver());
        	addResolver(new GymnastTemplateContext.EntryRuleNameResolver());
        	addResolver(new GymnastTemplateContext.EntryRuleClassNameResolver());
        	
        	addResolver(new JavaMethodTemplateContext.MethodNameResolver());
        	addResolver(new JavaMethodTemplateContext.TypeNameResolver());
            
        	addResolver(new GlobalTemplateVariables.User());
        	addResolver(new GlobalTemplateVariables.Dollar());
        }
    };
    
	public static final GymnastContextType JavaCompUnit = new GymnastContextType(
            "JavaCompUnit", "Java CompilationUnit") {

        protected void addResolvers() {
            addResolver(new GymnastTemplateContext.ToolNameResolver());
        	addResolver(new GymnastTemplateContext.GeneratedByResolver());
        	addResolver(new GymnastTemplateContext.BeginTimeResolver());
        	addResolver(new GymnastTemplateContext.ASTFileNameResolver());
        	addResolver(new GymnastTemplateContext.ASTNameResolver());
        	addResolver(new GymnastTemplateContext.ParserPackageNameResolver());
        	addResolver(new GymnastTemplateContext.ASTPackageNameResolver());
        	addResolver(new GymnastTemplateContext.ASTBaseClassNameResolver());
        	addResolver(new GymnastTemplateContext.ASTBaseClassBaseNameResolver());
        	addResolver(new GymnastTemplateContext.ASTTokenClassNameResolver());
        	addResolver(new GymnastTemplateContext.ASTVisitorClassNameResolver());
        	addResolver(new GymnastTemplateContext.LDT_ASTNodePackageNameResolver());
        	addResolver(new GymnastTemplateContext.LDT_ASTNodeClassNameResolver());
        	addResolver(new GymnastTemplateContext.LDT_ParserPackageNameResolver());
        	addResolver(new GymnastTemplateContext.EntryRuleNameResolver());
        	addResolver(new GymnastTemplateContext.EntryRuleClassNameResolver());
        	
        	addResolver(new JavaCompUnitTemplateContext.TypeNameResolver());
        	addResolver(new JavaCompUnitTemplateContext.Visitor_acceptImpl_Resolver());
        	
        	addResolver(new ForeachRule.Resolver());
            
        	addResolver(new GlobalTemplateVariables.User());
        	addResolver(new GlobalTemplateVariables.Dollar());
        }
    };
    
	public static final GymnastContextType JavaRuleCompUnit = new GymnastContextType(
            "JavaRuleCompUnit", "Java CompilationUnit for grammar rules") {

        protected void addResolvers() {
            addResolver(new GymnastTemplateContext.ToolNameResolver());
        	addResolver(new GymnastTemplateContext.GeneratedByResolver());
        	addResolver(new GymnastTemplateContext.BeginTimeResolver());
        	addResolver(new GymnastTemplateContext.ASTFileNameResolver());
        	addResolver(new GymnastTemplateContext.ASTNameResolver());
        	addResolver(new GymnastTemplateContext.ParserPackageNameResolver());
        	addResolver(new GymnastTemplateContext.ASTPackageNameResolver());
        	addResolver(new GymnastTemplateContext.ASTBaseClassNameResolver());
        	addResolver(new GymnastTemplateContext.ASTBaseClassBaseNameResolver());
        	addResolver(new GymnastTemplateContext.ASTTokenClassNameResolver());
        	addResolver(new GymnastTemplateContext.ASTVisitorClassNameResolver());
        	addResolver(new GymnastTemplateContext.LDT_ASTNodePackageNameResolver());
        	addResolver(new GymnastTemplateContext.LDT_ASTNodeClassNameResolver());
        	addResolver(new GymnastTemplateContext.LDT_ParserPackageNameResolver());
        	addResolver(new GymnastTemplateContext.EntryRuleNameResolver());
        	addResolver(new GymnastTemplateContext.EntryRuleClassNameResolver());
        	
        	addResolver(new JavaCompUnitTemplateContext.TypeNameResolver());
        	addResolver(new JavaCompUnitTemplateContext.Visitor_acceptImpl_Resolver());
        	
        	addResolver(new JavaRuleCompUnitTemplateContext.ExtendsResolver());
        	addResolver(new JavaRuleCompUnitTemplateContext.ImplementsResolver());
        	addResolver(new JavaRuleCompUnitTemplateContext.TypeKindResolver());
        	
        	addResolver(new ForeachKeywordLiteral.Resolver());
        	addResolver(new ForeachChildElement.Resolver());
        	
        	addResolver(new GlobalTemplateVariables.User());
        	addResolver(new GlobalTemplateVariables.Dollar());
        }
    };
	
}
