package org.eclipse.gymnast.generators.ecore.convert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnum;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.eclipse.gymnast.generator.core.generator.Util;

public class MyEcoreUtil {

	public static boolean isWellFormed(EObject root) {
		Diagnostician diagnostician = new Diagnostician();
		final Diagnostic diagnostic = diagnostician.validate(root);
		boolean res = diagnostic.getSeverity() == Diagnostic.OK;
		return res;
	}

	public static <T extends EObject> T clone(T input) {
		EcoreUtil.Copier copier = new EcoreUtil.Copier();
		T cloned = (T) (copier.copy(input));
		copier.copyReferences();
		return cloned;
	}

	public static GenModel generateGenModel(IPath genModelPath,
			EPackage ePackage, String basePackage, String prefix, IProject proj)
			throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		URI genModelURI = URI.createFileURI(genModelPath.toString());
		Resource genModelResource = Resource.Factory.Registry.INSTANCE
				.getFactory(genModelURI).createResource(genModelURI);
		GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
		genModelResource.getContents().add(genModel);
		resourceSet.getResources().add(genModelResource);
		genModel.setModelDirectory("/" + proj.getName() + "/src");
		genModel.getForeignModel().add(ePackage.getName());
		genModel.initialize(Collections.singleton(ePackage));
		genModel.setComplianceLevel(GenJDKLevel.JDK50_LITERAL);
		GenPackage genPackage = (GenPackage) genModel.getGenPackages().get(0);
		genModel.setModelName(genModelURI.trimFileExtension().lastSegment()
				+ "GenModel");
		genPackage.setPrefix(prefix);
		genPackage.setBasePackage(basePackage);
		genModelResource.save(Collections.EMPTY_MAP);
		return genModel;
	}

	public static EClass newClass(EPackage ownerPackage, String name,
			boolean isAbstract, EClass eSuper) {
		EClass c = EcoreFactory.eINSTANCE.createEClass();
		c.setName(capitalized(name));
		c.setAbstract(isAbstract);
		c.setInterface(isAbstract);
		if (eSuper != null) {
			c.getESuperTypes().add(eSuper);
		}
		if (ownerPackage != null) {
			ownerPackage.getEClassifiers().add(c);
		}
		return c;
	}

	private static String capitalized(String str) {
		String res = str.substring(0, 1).toUpperCase() + str.substring(1);
		return res;
	}

	public static EPackage newPackage(EList<? super EPackage> contents,
			String name, String ns) {
		EPackage ePackage = EcoreFactory.eINSTANCE.createEPackage();
		ePackage.setName(name);
		ePackage.setNsPrefix(ns);
		ePackage.setNsURI(ns);
		contents.add(ePackage);
		return ePackage;
	}

	public static String nonCollidingName(String suggestedName, EPackage eP) {
		String newName = suggestedName;
		int counter = 2;
		while (eP.getEClassifier(newName) != null) {
			newName = suggestedName + counter;
			counter++;
		}
		return newName;
	}

	public static String nonCollidingName(String suggestedName, EClass eC) {
		String newName = suggestedName;
		int counter = 2;
		while (eC.getEStructuralFeature(newName) != null) {
			newName = suggestedName + counter;
			counter++;
		}
		return newName;
	}

	public static EClass newContainerOf(EPackage ownerPackage, String name,
			EClass itemType, int lowerBound, int upperBound) {
		EClass res = newClass(ownerPackage, name, false, null);
		EReference eR = EcoreFactory.eINSTANCE.createEReference();
		eR.setLowerBound(lowerBound);
		eR.setUpperBound(upperBound);
		eR.setContainment(true);
		eR.setEType(itemType);
		String itemsName = Util.toLowercaseName(itemType.getName()) + "s";
		eR.setName(itemsName);
		res.getEStructuralFeatures().add(eR);
		return res;
	}

	/*
	 * private static String firstLowercase(String name) { String res =
	 * name.substring(0, 1).toLowerCase(); if (name.length() > 1) { res +=
	 * name.substring(1); } return res; }
	 */
	public static EAttribute newAttribute(String name, EClass ownerClass,
			EClassifier type) {
		name = Util.toLowercaseName(name);
		name = nonCollidingName(name, ownerClass);
		EAttribute eA = EcoreFactory.eINSTANCE.createEAttribute();
		eA.setName(name);
		ownerClass.getEStructuralFeatures().add(eA);
		eA.setEType(type);
		return eA;
	}

	public static EReference newReference(String name, EClass ownerClass,
			EClass type) {
		name = Util.toLowercaseName(name);
		name = nonCollidingName(name, ownerClass);
		EReference eR = EcoreFactory.eINSTANCE.createEReference();
		eR.setName(name);
		ownerClass.getEStructuralFeatures().add(eR);
		eR.setEType(type);
		return eR;
	}

	public static void genJavaFromGenModel(GenModel genModel) {
		genModel.setCanGenerate(true);

		// Create the generator and set the model-level input object.
		Generator generator = new Generator();
		generator.setInput(genModel);

		// Generator model code.
		generator.generate(genModel,
				GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE,
				new BasicMonitor.Printing(System.out));
	}

	public static EEnum newEnum(EPackage ownerPackage, String suggestedName,
			List<String> values) {
		String newName = nonCollidingName(capitalized(suggestedName),
				ownerPackage);
		EEnum e = EcoreFactory.eINSTANCE.createEEnum();
		e.setName(newName);
		ownerPackage.getEClassifiers().add(e);
		int incr = 0;
		for (String s : values) {
			EEnumLiteral eL = EcoreFactory.eINSTANCE.createEEnumLiteral();
			eL.setName(s);
			eL.setValue(incr++);
			e.getELiterals().add(eL);
		}
		return e;
	}

	public static List<EClass> getSubTypesOfInPackage(EClass eC, EPackage eP) {
		List<EClass> res = new ArrayList<EClass>();
		for (EClassifier cand : eP.getEClassifiers()) {
			if (cand instanceof EClass) {
				if (!(eC.equals(cand)) && eC.isSuperTypeOf((EClass) cand)) {
					res.add((EClass) cand);
				}
			}
		}
		return res;
	}

	public static List<EClass> getSubTypesOf(EClass eC) {
		EPackage rootP = eC.getEPackage();
		while (rootP.getESuperPackage() != null) {
			rootP = rootP.getESuperPackage();
		}
		List<EClass> res = new ArrayList<EClass>();
		getSubTypesOfInner(eC, rootP, res);
		return res;
	}

	private static void getSubTypesOfInner(EClass eC, EPackage eP,
			List<EClass> subTypesSoFar) {
		subTypesSoFar.addAll(getSubTypesOfInPackage(eC, eP));
		for (EPackage subP : eP.getESubpackages()) {
			getSubTypesOfInner(eC, subP, subTypesSoFar);
		}
	}

	/*
	 * derived from GenModelPackage source annotation, which results in one
	 * operation has at most one annotation body
	 */
	public static final String BODY_ANNOTATION_SOURCE = GenModelPackage.eNS_URI;

	public static EAnnotation newAnnotation(EModelElement object,
			String source, String key, String value) {

		EAnnotation eAnnotation = object.getEAnnotation(source);
		if (eAnnotation != null && !eAnnotation.getDetails().isEmpty()) {
			String prevValue = eAnnotation.getDetails().get(0).getValue();
			String currValue = prevValue + "\n" + value;
			eAnnotation.getDetails().get(0).setValue(currValue);
		} else {
			eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
			eAnnotation.setSource(source);
			eAnnotation.getDetails().put(key, value);
		}
		object.getEAnnotations().add(eAnnotation);
		return eAnnotation;
	}

	public static String getEcoreFQN(EClassifier eC) {
		String res = eC.getName();
		EPackage eP = eC.getEPackage();
		while (eP != null) {
			res = eP.getName() + "." + res;
			eP = eP.getESuperPackage();
		}
		return res;
	}

	public static String getEcoreFQN(EStructuralFeature eSF) {
		String res = getEcoreFQN(eSF.getEContainingClass());
		res += "." + eSF.getName();
		return res;
	}

	/**
	 * Cut&pasted from the online article Discover the Eclipse Modeling
	 * Framework (EMF) and Its Dynamic Capabilities
	 * http://www.devx.com/Java/Article/29093/1763/page/2
	 * 
	 * @param fileLocation
	 * @param rootPackage
	 * @return
	 * @throws IOException
	 */
	public static Resource serializeEcoreToFile(String fileLocation,
			EPackage rootPackage) throws IOException {

		// create resource set and resource
		ResourceSet resourceSet = new ResourceSetImpl();

		// Register Ecore resource factory
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put("ecore", new EcoreResourceFactoryImpl());

		Resource resource = resourceSet.createResource(URI
				.createFileURI(fileLocation));
		// add the root object to the resource
		resource.getContents().add(rootPackage);
		// serialize resource – you can specify also serialization
		// options
		resource.save(null);

		return resource;

	}

	public static EOperation newOperation(String suggestedName,
			EClass ownerClass, EClassifier returnType) {
		String newName = nonCollidingName(suggestedName, ownerClass);
		EOperation op = EcoreFactory.eINSTANCE.createEOperation();
		op.setName(Util.toLowercaseName(newName));
		ownerClass.getEOperations().add(op);
		if (returnType != null) {
			op.setEType(returnType);
		}
		return op;
	}

	public static EPackage loadEcoreFile(IFile ecoreFile) {
		ResourceSetImpl resourceSet = new ResourceSetImpl();
		String ecoreFilePath = ecoreFile.getFullPath().toString();
		URI uri = URI.createPlatformResourceURI(ecoreFilePath);
		Resource ecoreResource = resourceSet.getResource(uri, true);
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
		return mainPackage;
	}

}
