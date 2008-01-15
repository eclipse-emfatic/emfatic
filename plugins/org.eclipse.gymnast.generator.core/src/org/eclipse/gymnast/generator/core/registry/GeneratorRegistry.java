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

package org.eclipse.gymnast.generator.core.registry;

import java.util.Collection;
import java.util.HashMap;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;

public class GeneratorRegistry {
	
	private static final String PARSER_GENERATORS_EXTENSION_POINT_ID = "org.eclipse.gymnast.generator.core.parserGenerators";
	private static final String PARSER_GENERATOR_ELEMENT_NAME = "parserGenerator";
	
	private static final String AST_GENERATORS_EXTENSION_POINT_ID = "org.eclipse.gymnast.generator.core.astGenerators";
	private static final String AST_GENERATOR_ELEMENT_NAME = "astGenerator";
	
	private static final String ID_ATTR_NAME = "id";
	private static final String ID_ATTR_DESCRIPTION = "description";
	private static final String CLASS_ATTR_NAME = "class";
	
	private static GeneratorRegistry _instance;
	
	private final HashMap _idToASTGeneratorDescriptor;
	private final HashMap _idToParserGeneratorDescriptor;
	
	
	private GeneratorRegistry() {
		_idToASTGeneratorDescriptor = new HashMap();
		_idToParserGeneratorDescriptor = new HashMap();
		init();
	}
	
	public static GeneratorRegistry getInstance() {
		if (_instance == null) {
			_instance = new GeneratorRegistry();
		}
		return _instance;
	}
	
	public ASTGeneratorDescriptor getASTGeneratorDescriptor(String id) {
		return (ASTGeneratorDescriptor)_idToASTGeneratorDescriptor.get(id);
	}
	
	public ASTGeneratorDescriptor[] getASTGeneratorDescriptors() {
		Collection values = _idToASTGeneratorDescriptor.values();
		return (ASTGeneratorDescriptor[]) values.toArray(new ASTGeneratorDescriptor[values.size()]);
	}

	public ParserGeneratorDescriptor getParserGeneratorDescriptor(String id) {
		return (ParserGeneratorDescriptor)_idToParserGeneratorDescriptor.get(id);
	}
	
	public ParserGeneratorDescriptor[] getParserGeneratorDescriptors() {
		Collection values = _idToParserGeneratorDescriptor.values();
		return (ParserGeneratorDescriptor[]) values.toArray(new ParserGeneratorDescriptor[values.size()]);
	}
	
	private void init() {
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		
		IConfigurationElement[] astGeneratorElements =
			registry.getConfigurationElementsFor(AST_GENERATORS_EXTENSION_POINT_ID);
		
		for (int i = 0; i < astGeneratorElements.length; i++) {
			IConfigurationElement element = astGeneratorElements[i];
			if (AST_GENERATOR_ELEMENT_NAME.equals(element.getName())) {
				initASTGenerator(element);
			}
		}
		
		IConfigurationElement[] parserGeneratorElements =
			registry.getConfigurationElementsFor(PARSER_GENERATORS_EXTENSION_POINT_ID);
		
		for (int i = 0; i < parserGeneratorElements.length; i++) {
			IConfigurationElement element = parserGeneratorElements[i];
			if (PARSER_GENERATOR_ELEMENT_NAME.equals(element.getName())) {
				initParserGenerator(element);
			}
		}
	}
	
	private void initASTGenerator(IConfigurationElement astGeneratorElement) {
		String id = astGeneratorElement.getAttribute(ID_ATTR_NAME);
		if (id == null) {
			// TODO: report error!
			return;
		}
		
		String description = astGeneratorElement.getAttribute(ID_ATTR_DESCRIPTION);
		
		try {
			ASTGenerator generator = (ASTGenerator)astGeneratorElement.createExecutableExtension(CLASS_ATTR_NAME);
			ASTGeneratorDescriptor descriptor = new ASTGeneratorDescriptor(id, description, generator);
			_idToASTGeneratorDescriptor.put(id, descriptor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void initParserGenerator(IConfigurationElement parserGeneratorElement) {
		String id = parserGeneratorElement.getAttribute(ID_ATTR_NAME);
		if (id == null) {
			// TODO: report error!
			return;
		}
		
		String description = parserGeneratorElement.getAttribute(ID_ATTR_DESCRIPTION);
		
		try {
			ParserGenerator generator = (ParserGenerator)parserGeneratorElement.createExecutableExtension(CLASS_ATTR_NAME);
			ParserGeneratorDescriptor descriptor = new ParserGeneratorDescriptor(id, description, generator);
			_idToParserGeneratorDescriptor.put(id, descriptor);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
