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

package org.eclipse.emf.emfatic.core.util;

import java.util.Hashtable;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;

public class EmfaticBasicTypes {

	public EmfaticBasicTypes() {
	}

	private static void initBasicTypes() {
		_nameToType = new Hashtable<String, EClassifier>();
		_typeToName = new Hashtable<EClassifier, String>();

		EcorePackage ecore = EcorePackage.eINSTANCE;
		
		/*
		 * alternate notation , will be understood when given in .emf however
		 * when importing from .ecore the Emfatic-preferred names will be
		 * generated (see below). This because the entries in the map _typeToName are
		 * overwritten (same keys)
		 */
		add("EBoolean", ecore.getEBoolean());
		add("EByte", ecore.getEByte());
		add("EChar", ecore.getEChar());
		add("EDouble", ecore.getEDouble());
		add("EFloat", ecore.getEFloat());
		add("EInt", ecore.getEInt());
		add("ELong", ecore.getELong());
		add("EShort", ecore.getEShort());
		add("EBooleanObject", ecore.getEBooleanObject());
		add("EByteObject", ecore.getEByteObject());
		add("ECharacterObject", ecore.getECharacterObject());
		add("EDoubleObject", ecore.getEDoubleObject());
		add("EFloatObject", ecore.getEFloatObject());
		add("EIntegerObject", ecore.getEIntegerObject());
		add("ELongObject", ecore.getELongObject());
		add("EShortObject", ecore.getEShortObject());
		add("EDate", ecore.getEDate());
		add("EString", ecore.getEString());
		add("EJavaClass", ecore.getEJavaClass());

		
		add("boolean", ecore.getEBoolean());
		add("byte", ecore.getEByte());
		add("char", ecore.getEChar());
		add("double", ecore.getEDouble());
		add("float", ecore.getEFloat());
		add("int", ecore.getEInt());
		add("long", ecore.getELong());
		add("short", ecore.getEShort());
		add("Boolean", ecore.getEBooleanObject());
		add("Byte", ecore.getEByteObject());
		add("Character", ecore.getECharacterObject());
		add("Double", ecore.getEDoubleObject());
		add("Float", ecore.getEFloatObject());
		add("Integer", ecore.getEIntegerObject());
		add("Long", ecore.getELongObject());
		add("Short", ecore.getEShortObject());
		add("Date", ecore.getEDate());
		add("String", ecore.getEString());
		add("Object", ecore.getEJavaObject());
		add("Class", ecore.getEJavaClass());
		add("EObject", ecore.getEObject());
		add("EClass", ecore.getEClass());

		// External types defined by Ecore
		add("EDate", ecore.getEDate());
		add("EBigInteger", ecore.getEBigInteger());
		add("EBigDecimal", ecore.getEBigDecimal());
		add("EResource", ecore.getEResource());
		add("EResourceSet", ecore.getEResourceSet());
		add("EEnumerator", ecore.getEEnumerator());
		add("EEList", ecore.getEEList());
		add("ETreeIterator", ecore.getETreeIterator());
		add("EJavaObject", ecore.getEJavaObject());


		_array = (String[]) _typeToName.values().toArray(new String[_typeToName.size()]);
		_isInitialized = true;
	}

	private static void add(String name, EClassifier type) {
		_nameToType.put(name, type);
		_typeToName.put(type, name);
	}

	public static String[] GetBasicTypeNames() {
		if (!_isInitialized)
			initBasicTypes();
		return (String[]) _array.clone();
	}

	public static EClassifier LookupBasicType(String name) {
		if (!_isInitialized)
			initBasicTypes();
		return (EClassifier) _nameToType.get(name);
	}

	public static String LookupBasicTypeName(EClassifier eClassifier) {
		if (!_isInitialized)
			initBasicTypes();
		return (String) _typeToName.get(eClassifier);
	}

	private static boolean _isInitialized = false;
	private static Hashtable<String, EClassifier> _nameToType;
	private static Hashtable<EClassifier, String> _typeToName;
	private static String _array[];

}
