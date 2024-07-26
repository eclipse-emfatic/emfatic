/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/


package org.eclipse.emf.emfatic.core.generator.emfatic;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.ETypedElement;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.emfatic.core.generics.util.GenericsUtil;
import org.eclipse.emf.emfatic.core.util.EmfaticAnnotationMap;
import org.eclipse.emf.emfatic.core.util.EmfaticBasicTypes;
import org.eclipse.emf.emfatic.core.util.EmfaticKeywords;

public class Writer {

	/**
	 * @deprecated Use {@link #write(Resource)} instead.
	 */
	@Deprecated
	public String write(Resource ecoreResource, IProgressMonitor monitor, IFile ecoreFile) {
		return write(ecoreResource);
	}

	public String write(Resource ecoreResource) {
		_buf = new StringBuffer();

		if (ecoreResource.getContents().size() > 1) {
			final EPackage top = EcoreFactory.eINSTANCE.createEPackage();
			top.setName("top");
			top.setNsPrefix("top");
			top.setNsURI("top");
			for (EObject oldTop : ecoreResource.getContents()) {
				if (oldTop instanceof EPackage) {
					top.getESubpackages().add((EPackage) oldTop);
				}
			}
			ecoreResource.getContents().clear();
			ecoreResource.getContents().add(top);
		}

		EPackage mainPackage = (EPackage) ecoreResource.getContents().get(0);

		_processingEcore = initProcessingEcore(mainPackage);
		_annotationMap = new EmfaticAnnotationMap();
		writeMainPackage(mainPackage);
		return _buf.toString();
	}

	private boolean initProcessingEcore(EPackage mainPackage) {
		EPackage ecore = EcorePackage.eINSTANCE;
		if (!ecore.getName().equals(mainPackage.getName()))
			return false;
		if (!ecore.getNsPrefix().equals(mainPackage.getNsPrefix()))
			return false;
		return ecore.getNsURI().equals(mainPackage.getNsURI());
	}

	private void writeMainPackage(EPackage ePackage) {
		String name = escape(ePackage.getName());
		writeAnnotations(ePackage, 0, false);
		writeNamespaceInfo(ePackage, 0);
		writeln("package " + name + ";");
		writeln();
		writeImports(ePackage);
		writePackageContents(ePackage, 0);
	}

	private void writeImports(EPackage ePackage) {
		if (_processingEcore)
			return;
		Hashtable<Resource, Resource> resourceTable = new Hashtable<Resource, Resource>();
		for (TreeIterator<EObject> ti = ePackage.eAllContents(); ti.hasNext();) {
			EObject eObject = ti.next();
			if (eObject instanceof ETypedElement) {
				ETypedElement eTypedElement = (ETypedElement) eObject;
				addExternalResource(eTypedElement.getEType(), ePackage,
						resourceTable);
			}
			if (eObject instanceof EClass) {
				EClass eClass = (EClass) eObject;
				EClass superType;
				for (Iterator<EClass> i = eClass.getESuperTypes().iterator(); i
						.hasNext(); addExternalResource(superType, ePackage,
						resourceTable))
					superType = (EClass) i.next();

			}
			if (eObject instanceof EOperation) {
				EOperation eOperation = (EOperation) eObject;
				EClassifier exceptionType;
				for (Iterator<EClassifier> i = eOperation.getEExceptions().iterator(); i
						.hasNext(); addExternalResource(exceptionType,
						ePackage, resourceTable))
					exceptionType = (EClassifier) i.next();

			}
		}

		String uri;
		for (Iterator<Resource> ri = resourceTable.values().iterator(); ri.hasNext(); writeln("import \""
				+ uri + "\";")) {
			uri = ri.next().getURI().toString();
		}

		if (resourceTable.size() > 0)
			writeln();
	}

	private void addExternalResource(EClassifier referencedType,
			EPackage localMainPackage, Hashtable<Resource, Resource> resourceTable) {
		if (referencedType == null)
			return;
		Resource resource = referencedType.eResource();
		if (resource == null)
			return;
		if (localMainPackage.eResource().equals(resource))
			return;
		if (EcorePackage.eINSTANCE.equals(referencedType.getEPackage()))
			return;
		if (!resourceTable.containsKey(resource))
			resourceTable.put(resource, resource);
	}

	private void writeSubPackage(EPackage ePackage, int indentLevel) {
		String name = escape(ePackage.getName());
		writeAnnotations(ePackage, indentLevel, false);
		writeNamespaceInfo(ePackage, indentLevel);
		writeln("package " + name + " {", indentLevel);
		writePackageContents(ePackage, indentLevel + 1);
		writeln("}", indentLevel);
		writeln();
	}

	private void writeNamespaceInfo(EPackage ePackage, int indentLevel) {
		if (ePackage.getNsURI() == null && ePackage.getNsPrefix() == null)
			return;
		write("@namespace(", indentLevel);
		if (ePackage.getNsURI() != null) {
			write("uri=\"" + ePackage.getNsURI() + "\"");
			if (ePackage.getNsPrefix() != null)
				write(", ");
		}
		if (ePackage.getNsPrefix() != null)
			write("prefix=\"" + ePackage.getNsPrefix() + "\"");
		writeln(")");
	}

	private void writePackageContents(EPackage ePackage, int indentLevel) {
		for (Iterator<EClassifier> ic = ePackage.getEClassifiers().iterator(); ic.hasNext();) {
			Object o = ic.next();
			if (o instanceof EClass) {
				EClass eClass = (EClass) o;
				writeClass(eClass, indentLevel);
			} else if (o instanceof EEnum) {
				EEnum eEnum = (EEnum) o;
				writeEnum(eEnum, indentLevel);
			} else if (o instanceof EDataType) {
				EDataType eDataType = (EDataType) o;
				writeDataType(eDataType, indentLevel);
			}
		}

		EPackage subPackage;
		for (Iterator<EPackage> ip = ePackage.getESubpackages().iterator(); ip.hasNext(); writeSubPackage(
				subPackage, indentLevel))
			subPackage = (EPackage) ip.next();

	}

	private void writeClass(EClass eClass, int indentLevel) {
		writeAnnotations(eClass, indentLevel, false);
		indent(indentLevel);
		if (eClass.isAbstract())
			write("abstract ");
		if (eClass.isInterface())
			write("interface ");
		else
			write("class ");
		String name = escape(eClass.getName());
		write(name);
		boolean firstSuperType = true;

		writeTypeParams(eClass.getETypeParameters(), eClass);

		for (Iterator<EGenericType> ist = eClass.getEGenericSuperTypes()
				.iterator(); ist.hasNext();) {
			if (firstSuperType) {
				write(" extends ");
				firstSuperType = false;
			} else {
				write(", ");
			}
			EGenericType superType = ist.next();
			String superTypeName = getTypeName(superType, eClass);
			write(superTypeName);
		}

		if (eClass.getInstanceClassName() != null) {
			String instanceClassName = quoteIfNecessary(eClass
					.getInstanceClassName());
			write(" : " + instanceClassName);
		}
		writeln(" {");
		for (Iterator<EOperation> iop = eClass.getEOperations().iterator(); iop
				.hasNext();) {
			EOperation o = iop.next();
			writeOperation(o, indentLevel + 1, eClass);
		}

		for (Iterator<EStructuralFeature> isf = eClass.getEStructuralFeatures()
				.iterator(); isf.hasNext();) {
			EStructuralFeature o2 = isf.next();
			if (o2 instanceof EAttribute)
				writeAttribute((EAttribute) o2, indentLevel + 1);
			else if (o2 instanceof EReference)
				writeReference((EReference) o2, indentLevel + 1);
		}

		writeln("}", indentLevel);
		writeln();
	}

	private void writeTypeParams(EList<ETypeParameter> tps, EClass eClass) {
		Iterator<ETypeParameter> tpsIter = tps.iterator();
		if (tps.size() > 0) {
			write(" <");
		}
		while (tpsIter.hasNext()) {
			ETypeParameter etp = tpsIter.next();
			write(getTypeName(etp, eClass));
			if (tpsIter.hasNext()) {
				write(", ");
			}
		}
		if (tps.size() > 0) {
			write("> ");
		}
	}

	private String getTypeName(ETypeParameter etp, EClass context) {
		String res = escape(etp.getName());
		if (etp.getEBounds().size() > 0) {
			res += " extends ";
			Iterator<EGenericType> iterB = etp.getEBounds().iterator();
			while (iterB.hasNext()) {
				EGenericType b = iterB.next();
				res += getTypeName(b, context);
				if (iterB.hasNext()) {
					res += " & ";
				}
			}
		}
		return res;
	}

	private void writeAttribute(EAttribute eAttr, int indentLevel) {
		String name = escape(eAttr.getName());
		String modifiers = getModifiers(eAttr);
		String type = getTypeExpr(eAttr, eAttr.getEContainingClass());
		String defaultValueExpr = "";
		if (eAttr.getDefaultValueLiteral() != null)
			if (EcorePackage.eINSTANCE.getEBoolean().equals(eAttr.getEType()))
				defaultValueExpr = " = " + eAttr.getDefaultValueLiteral();
			else if (EcorePackage.eINSTANCE.getEInt().equals(eAttr.getEType()))
				defaultValueExpr = " = " + eAttr.getDefaultValueLiteral();
			else if (EcorePackage.eINSTANCE.getEChar().equals(eAttr.getEType()))
				defaultValueExpr = " = '" + eAttr.getDefaultValueLiteral()
						+ "'";
			else
				defaultValueExpr = " = \"" + eAttr.getDefaultValueLiteral()
						+ "\"";
		writeAnnotations(eAttr, indentLevel, true);
		writeln(modifiers + "attr " + type + " " + name + defaultValueExpr
				+ ";", indentLevel);
	}

	private void writeReference(EReference eRef, int indentLevel) {
		String refKind;
		if (eRef.isContainment())
			refKind = "val ";
		else if (eRef.isContainer())
			refKind = "ref ";
		else
			refKind = "ref ";
		String name = escape(eRef.getName());
		String modifiers = getModifiers(eRef);
		String type = getTypeExpr(eRef, eRef.getEContainingClass());
		writeAnnotations(eRef, indentLevel, true);
		writeln(modifiers + refKind + type + " " + name + ";", indentLevel);
	}

	private void writeOperation(EOperation eOp, int indentLevel, EClass context) {

		String name = escape(eOp.getName());
		String modifiers = getModifiers(eOp);
		String type = "void";
		if (eOp.getEGenericType() != null) {
			type = getTypeExpr(eOp, eOp.getEContainingClass());
		}
		writeAnnotations(eOp, indentLevel, true);

		write(modifiers + "op ", indentLevel);
		writeTypeParams(eOp.getETypeParameters(), context);
		write(type + " " + name + "(");
		for (Iterator<EParameter> ip = eOp.getEParameters().iterator(); ip
				.hasNext();) {
			EParameter eParam = ip.next();
			//String paramDoc = EcoreUtil.getDocumentation(eParam);
			//if (paramDoc != null)
			//	write("?\"" + paramDoc + "\" ");
			writeAnnotations(eParam, 0, false);
			String paramMods = getModifiers(eParam);
			String paramName = escape(eParam.getName());
			String paramType = getTypeExpr(eParam, eOp.getEContainingClass());
			write(paramMods + paramType + " " + paramName);
			if (ip.hasNext())
				write(", ");
		}

		write(")");
		Iterator<EGenericType> ie = eOp.getEGenericExceptions().iterator();
		if (ie.hasNext()) {
			write(" throws ");
			while (ie.hasNext()) {
				EGenericType exceptionType = ie.next();
				String exceptionTypeName = getTypeName(exceptionType, eOp
						.getEContainingClass());
				write(exceptionTypeName);
				if (ie.hasNext())
					write(", ");
			}
		}
		writeln(";");
	}

	private void writeEnum(EEnum eEnum, int indentLevel) {
		// TODO can an enum declare type parameters
		writeAnnotations(eEnum, indentLevel, false);
		write("enum ", indentLevel);
		String name = escape(eEnum.getName());
		write(name);
		writeln(" {");
		for (Iterator<EEnumLiteral> iel = eEnum.getELiterals().iterator(); iel.hasNext(); writeln(";")) {
			EEnumLiteral eLit = (EEnumLiteral) iel.next();
			writeAnnotations(eLit, indentLevel + 1, true);
			write(escape(eLit.getName()), indentLevel + 1);
			write(" = ");
			write(Integer.toString(eLit.getValue()));
		}

		writeln("}", indentLevel);
		writeln();
	}

	private void writeDataType(EDataType eDataType, int indentLevel) {
		// TODO can a datatype declare type parameters
		writeAnnotations(eDataType, indentLevel, false);
		indent(indentLevel);
		if (!eDataType.isSerializable())
			write("transient ");
		write("datatype ");
		String name = escape(eDataType.getName());
		write(name);
		write(" : ");
		String instanceClassName = quoteIfNecessary(eDataType
				.getInstanceClassName());
		write(instanceClassName);
		writeln(";");
		writeln();
	}

	private void writeAnnotations(EModelElement eModelElement, int indentLevel,
			boolean initialNewline) {
		List<EAnnotation> annotations = eModelElement.getEAnnotations();
		if (annotations.isEmpty())
			return;
		if (initialNewline)
			writeln();
		EAnnotation eAnnotation;
		for (Iterator<EAnnotation> i = annotations.iterator(); i.hasNext(); writeAnnotation(
				eAnnotation, indentLevel))
			eAnnotation = (EAnnotation) i.next();

	}

	private void writeAnnotation(EAnnotation eAnnotation, int indentLevel) {
		boolean firstDetail = true;
		String sourceURI = eAnnotation.getSource();
		int detailsCount = eAnnotation.getDetails().size();
		String sourceLabel = _annotationMap.getLabelForSourceURI(sourceURI,
				detailsCount);
		String outputSourceURI = sourceLabel == null ? sourceURI : sourceLabel;
		Iterator<Entry<String, String>> i = eAnnotation.getDetails().iterator();
		if (!i.hasNext()) {
			writeln("@" + quoteIfNecessary(outputSourceURI), indentLevel);
			return;
		}
		write("@" + quoteIfNecessary(outputSourceURI) + "(", indentLevel);
		int index = -1;
		while (i.hasNext()) {
			index++;
			java.util.Map.Entry<String, String> mapEntry = i.next();
			String key = (String) mapEntry.getKey();
			String value = (String) mapEntry.getValue();
			String implicitKey = _annotationMap.getImplicitKeyName(sourceURI,
					index, detailsCount);
			String expr = null;
			if (implicitKey != null)
				expr = quote(value);
			else
				expr = quoteIfNecessary(key) + "=" + quote(value);
			if (firstDetail) {
				write(expr);
				firstDetail = false;
			} else {
				write(", " + expr);
			}
			if ("http://www.eclipse.org/emf/2004/EmfaticAnnotationMap"
					.equals(eAnnotation.getSource()))
				_annotationMap.addMapping(key, value);
		}
		writeln(")");
	}

	private String getModifiers(ETypedElement eTypedElement) {
		StringBuffer sb = new StringBuffer();
		if (!eTypedElement.isUnique())
			sb.append("!unique ");
		if (!eTypedElement.isOrdered())
			sb.append("!ordered ");
		if (eTypedElement instanceof EStructuralFeature)
			getModifiersHelper((EStructuralFeature) eTypedElement, sb);
		return sb.toString();
	}

	private void getModifiersHelper(EStructuralFeature eStructuralFeature,
			StringBuffer sb) {
		if (!eStructuralFeature.isChangeable())
			sb.append("readonly ");
		if (eStructuralFeature.isVolatile())
			sb.append("volatile ");
		if (eStructuralFeature.isTransient())
			sb.append("transient ");
		if (eStructuralFeature.isUnsettable())
			sb.append("unsettable ");
		if (eStructuralFeature.isDerived())
			sb.append("derived ");
		if (eStructuralFeature instanceof EAttribute) {
			EAttribute eAttr = (EAttribute) eStructuralFeature;
			if (eAttr.isID())
				sb.append("id ");
		}
		if (eStructuralFeature instanceof EReference) {
			EReference eRef = (EReference) eStructuralFeature;
			if (!eRef.isResolveProxies())
				sb.append("!resolve ");
		}
	}

	private String getTypeExpr(ETypedElement eTypedElement, EClass context) {
		String typeName = getTypeName(eTypedElement.getEGenericType(), context);
		String multiplicity = getMultiplicity(eTypedElement);
		String oppositeName = "";
		if (eTypedElement instanceof EReference) {
			EReference eRef = (EReference) eTypedElement;
			EReference opposite = eRef.getEOpposite();
			if (opposite != null)
				oppositeName = "#" + escape(opposite.getName());
		}
		return typeName + multiplicity + oppositeName;
	}

	private String getTypeName(EGenericType type, EClass context) {
		if (type == null) {
			return "null";
		}
		if (GenericsUtil.isRefToTypeParam(type)) {
			String typeName = escape(type.getETypeParameter().getName());
			return typeName;
		}
		if (GenericsUtil.isWildcard(type)) {
			String res = "?";
			if (GenericsUtil.isUnboundedWildcard(type)) {
				return res;
			}
			if (GenericsUtil.isUpperBoundedWildcard(type)) {
				EGenericType ub = type.getEUpperBound();
				res += " extends " + getTypeName(ub, context);
			} else {
				EGenericType lb = type.getELowerBound();
				res += " super " + getTypeName(lb, context);
			}
			return res;
		}
		/*
		 * now we know that type is either a parameterized type, a raw type
		 * reference, or a reference to a non-generic classifier
		 */
		if (GenericsUtil.isParameterizedType(type)) {
			String res = "";
			res += getClassifierName(type.getEClassifier(), context);
			res += "<";
			Iterator<EGenericType> iterTA = type.getETypeArguments().iterator();
			while (iterTA.hasNext()) {
				EGenericType ta = iterTA.next();
				res += getTypeName(ta, context);
				if (iterTA.hasNext()) {
					res += ", ";
				}
			}
			res += ">";
			return res;
		}
		String res = "";
		res += getClassifierName(type.getEClassifier(), context);
		return res;
	}

	private String getClassifierName(EClassifier type, EClass context) {
		if (type == null)
			return "null";
		if (_processingEcore)
			return type.getName();
		String basicTypeName = EmfaticBasicTypes.LookupBasicTypeName(type);
		if (basicTypeName != null)
			return basicTypeName;
		String qualifiedName = getQualifiedName(type, context);
		if (qualifiedName == null)
			return "null";
		else
			return escape(qualifiedName);
	}

	private String getQualifiedName(EClassifier type, EClass context) {
		String qName = type.getName();
		for (EPackage currPackage = type.getEPackage(); currPackage != null
				&& !EcoreUtil.isAncestor(currPackage, context); currPackage = currPackage
				.getESuperPackage())
			qName = currPackage.getName() + "." + qName;
		return qName;
	}

	private String getMultiplicity(ETypedElement eTypedElement) {
		int lowerBound = eTypedElement.getLowerBound();
		int upperBound = eTypedElement.getUpperBound();
		String multiplicity;
		if (lowerBound == 0 && upperBound == -1)
			multiplicity = "*";
		else if (lowerBound == 0 && upperBound == 1)
			multiplicity = null;
		else if (lowerBound == 1 && upperBound == -1)
			multiplicity = "+";
		else if (lowerBound == upperBound)
			multiplicity = getBoundString(lowerBound);
		else
			multiplicity = getBoundString(lowerBound) + ".."
					+ getBoundString(upperBound);
		if (multiplicity == null)
			multiplicity = "";
		else
			multiplicity = "[" + multiplicity + "]";
		return multiplicity;
	}

	private String getBoundString(int multiplicityBound) {
		if (multiplicityBound == -1)
			return "*";
		if (multiplicityBound == -2)
			return "?";
		else
			return Integer.toString(multiplicityBound);
	}

	private static String escape(String identifier) {
		return EmfaticKeywords.Escape(identifier);
	}

	private String quote(String s) {
		if (_escapeQuotes == null)
			_escapeQuotes = Pattern.compile("\"");
		String s2 = _escapeQuotes.matcher(s).replaceAll("\\\\\"");
		return '"' + s2 + '"';
	}

	private String quoteIfNecessary(String stringLiteralOrQualifiedID) {
		if (stringLiteralOrQualifiedID == null
				|| stringLiteralOrQualifiedID.equals(""))
			return "\"\"";
		for (int i = 0; i < stringLiteralOrQualifiedID.length(); i++) {
			char c = stringLiteralOrQualifiedID.charAt(i);
			if (i == 0) {
				if (!Character.isJavaIdentifierStart(c))
					return quote(stringLiteralOrQualifiedID);
			} else if (!Character.isJavaIdentifierPart(c) && c != '.')
				return quote(stringLiteralOrQualifiedID);
		}

		if (EmfaticKeywords.IsKeyword(stringLiteralOrQualifiedID))
			return quote(stringLiteralOrQualifiedID);
		else
			return stringLiteralOrQualifiedID;
	}

	private void writeln() {
		writeln("");
	}

	private void writeln(String s) {
		writeln(s, 0);
	}

	private void writeln(String s, int indentLevel) {
		indent(indentLevel);
		_buf.append(s + "\n");
	}

	private void write(String s) {
		_buf.append(s);
	}

	private void write(String s, int indentLevel) {
		indent(indentLevel);
		_buf.append(s);
	}

	private void indent(int tabs) {
		StringBuffer sb = new StringBuffer(tabs);
		for (int i = 0; i < tabs; i++)
			sb.append("\t");

		write(sb.toString());
	}

	private StringBuffer _buf;

	private boolean _processingEcore;

	private EmfaticAnnotationMap _annotationMap;

	private Pattern _escapeQuotes;

	public static String stringify(EObject ecoreDecl) {
		Writer w = new Writer();
		EPackage mainPackage = getRootEPackage(ecoreDecl);
		if (mainPackage != null) {
			w._processingEcore = w.initProcessingEcore(mainPackage);
		}
		w._annotationMap = new EmfaticAnnotationMap();
		w._buf = new StringBuffer();
		if (ecoreDecl instanceof EClass) {
			w.writeClass((EClass) ecoreDecl, 0);
		}
		if (ecoreDecl instanceof EDataType) {
			w.writeDataType((EDataType) ecoreDecl, 0);
		}
		if (ecoreDecl instanceof EEnum) {
			w.writeEnum((EEnum) ecoreDecl, 0);
		}
		if (ecoreDecl instanceof EStructuralFeature) {
			String res = stringify((EStructuralFeature) ecoreDecl);
			return res;
		}
		if (ecoreDecl instanceof ETypeParameter) {
			String res = GenericsUtil.getText((ETypeParameter) ecoreDecl);
			return res;
		}
		if (ecoreDecl instanceof EOperation) {
			EOperation eO = (EOperation) ecoreDecl;
			w.writeOperation((EOperation) eO, 0, eO.getEContainingClass());
		}
		String res = w._buf.toString();
		return res;
	}

	private static EPackage getRootEPackage(EObject eo) {
		EPackage root = null;
		if (eo instanceof EClassifier) {
			root = ((EClassifier) eo).getEPackage();
		}
		if (eo instanceof EStructuralFeature) {
			root = ((EStructuralFeature) eo).getEContainingClass()
					.getEPackage();
		}
		if (eo instanceof ETypeParameter) {
			eo = ((ETypeParameter) eo).eContainer();
			root = getRootEPackage(eo);
			return root;
		}
		if (root == null) {
			return null;
		}
		while (root.getESuperPackage() != null) {
			root = root.getESuperPackage();
		}
		return root;
	}

	public static String stringify(EStructuralFeature sf) {
		Writer w = new Writer();
		EPackage mainPackage = (EPackage) sf.getEContainingClass()
				.getEPackage();
		while (mainPackage.getESuperPackage() != null) {
			mainPackage = mainPackage.getESuperPackage();
		}
		w._processingEcore = w.initProcessingEcore(mainPackage);
		w._annotationMap = new EmfaticAnnotationMap();
		w._buf = new StringBuffer();
		if (sf instanceof EAttribute) {
			w.writeAttribute((EAttribute) sf, 0);
		}
		if (sf instanceof EReference) {
			w.writeReference((EReference) sf, 0);
		}
		String res = w._buf.toString();
		return res;
	}
	public static String referenceEssentials(EReference eRef) {
		Writer w = new Writer();
		String name = escape(eRef.getName());
		String type = w.getTypeExpr(eRef, eRef.getEContainingClass());
		String res = type + " " + name ;
		return res;
	}

	public static String attributeEssentials(EAttribute eAttr) {
		Writer w = new Writer();
		String name = escape(eAttr.getName());
		String type = w.getTypeExpr(eAttr, eAttr.getEContainingClass());
		String defaultValueExpr = "";
		if (eAttr.getDefaultValueLiteral() != null)
			if (EcorePackage.eINSTANCE.getEBoolean().equals(eAttr.getEType()))
				defaultValueExpr = " = " + eAttr.getDefaultValueLiteral();
			else if (EcorePackage.eINSTANCE.getEInt().equals(eAttr.getEType()))
				defaultValueExpr = " = " + eAttr.getDefaultValueLiteral();
			else if (EcorePackage.eINSTANCE.getEChar().equals(eAttr.getEType()))
				defaultValueExpr = " = '" + eAttr.getDefaultValueLiteral()
						+ "'";
			else
				defaultValueExpr = " = \"" + eAttr.getDefaultValueLiteral()
						+ "\"";
		String res = type + " " + name + defaultValueExpr ;
		return res;
	}

	public static String operationEssentials(EOperation eOp) {
		Writer w = new Writer();
		w._buf = new StringBuffer();
		String name = escape(eOp.getName());
		String type = "void";
		if (eOp.getEGenericType() != null) {
			type = w.getTypeExpr(eOp, eOp.getEContainingClass());
		}
		
		w.writeTypeParams(eOp.getETypeParameters(), eOp.getEContainingClass());
		w.write(type + " " + name + "(");
		for (Iterator<EParameter> ip = eOp.getEParameters().iterator(); ip
				.hasNext();) {
			EParameter eParam = ip.next();
			String paramDoc = EcoreUtil.getDocumentation(eParam);
			if (paramDoc != null)
				w.write("?\"" + paramDoc + "\" ");
			String paramMods = w.getModifiers(eParam);
			String paramName = escape(eParam.getName());
			String paramType = w.getTypeExpr(eParam, eOp.getEContainingClass());
			w.write(paramMods + paramType + " " + paramName);
			if (ip.hasNext())
				w.write(", ");
		}

		w.write(")");
		Iterator<EGenericType> ie = eOp.getEGenericExceptions().iterator();
		if (ie.hasNext()) {
			w.write(" throws ");
			while (ie.hasNext()) {
				EGenericType exceptionType = ie.next();
				String exceptionTypeName = w.getTypeName(exceptionType, eOp
						.getEContainingClass());
				w.write(exceptionTypeName);
				if (ie.hasNext())
					w.write(", ");
			}
		}
		return w._buf.toString();
	}

}
