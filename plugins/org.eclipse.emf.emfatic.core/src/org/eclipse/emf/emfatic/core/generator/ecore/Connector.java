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

package org.eclipse.emf.emfatic.core.generator.ecore;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.emfatic.core.generics.util.GenericsUtil;
import org.eclipse.emf.emfatic.core.generics.util.OneToManyMap;
import org.eclipse.emf.emfatic.core.generics.util.OneToOneMap;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Attribute;
import org.eclipse.emf.emfatic.core.lang.gen.ast.BoundExceptWildcard;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ClassDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.CompUnit;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNodeVisitor;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ImportStmt;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ImportStmts;
import org.eclipse.emf.emfatic.core.lang.gen.ast.MapEntryDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Multiplicity;
import org.eclipse.emf.emfatic.core.lang.gen.ast.OneOrMoreTypeArgs;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Operation;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Param;
import org.eclipse.emf.emfatic.core.lang.gen.ast.QualifiedID;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Reference;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ResultType;
import org.eclipse.emf.emfatic.core.lang.gen.ast.SubPackageDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TopLevelDecls;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TypeArg;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TypeParam;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TypeParamsInfo;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TypeWithMulti;
import org.eclipse.emf.emfatic.core.lang.gen.ast.VoidContainer;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Wildcard;
import org.eclipse.emf.emfatic.core.util.EmfaticBasicTypes;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;
import org.omg.CORBA._PolicyStub;



/**
 * 
 * @author cjdaly@us.ibm.com
 * @author miguel.garcia@tuhh.de
 */
 public class Connector extends GenerationPhase {

	private OneToOneMap<ASTNode, EObject> cstDecl2EcoreAST = null;
	private OneToManyMap<EObject, ASTNode> ecoreDecl2cstUse = new OneToManyMap<EObject, ASTNode>();
	protected URI uri = null;
	
	public Connector(Builder b) {
		this.cstDecl2EcoreAST = b.getCstDecl2EcoreASTMap();
	}

	public void connect(ParseContext parseContext, Resource resource, IProgressMonitor monitor) {
		uri = resource.getURI();
		ecoreDecl2cstUse.clear();
		initParseContext(parseContext);
		CompUnit compUnit = (CompUnit) parseContext.getParseRoot();
		_importedPackages = new ArrayList<EPackage>();
		_importedPackages.add(EcorePackage.eINSTANCE);
		doImports(compUnit.getImportStmts());
		EPackage mainPackage = (EPackage) resource.getContents().get(0);
		doPackage(compUnit.getTopLevelDecls(), mainPackage);
		compUnit.setMaps(cstDecl2EcoreAST, ecoreDecl2cstUse);
	}

	private void doImports(ImportStmts importStmts) {
		final ResourceSet resourceSet = new ResourceSetImpl();
		// make sure to first try to load plugin resources from the workspace....
		// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=249635 comment 12
		resourceSet.getURIConverter().getURIMap().putAll(EcorePlugin.computePlatformURIMap());

		(new EmfaticASTNodeVisitor() {

			public boolean beginVisit(ImportStmt importStmt) {
				String uriText = getValue(importStmt.getUri());
				URI uri = URI.createURI(uriText);
				Resource resource = tryLoadResource(uri);
				if (resource != null) {
					EPackage ePackage = (EPackage) resource.getContents().get(0);
					_importedPackages.add(ePackage);
				} else {
					logError(new EmfaticSemanticError.ImportNotFound(importStmt));
				}
				return false;
			}

			private Resource tryLoadResource(URI uri) {
				try {
					if (uri != null && Connector.this.uri != null && uri.isRelative()) {
						uri = uri.resolve(Connector.this.uri);
					}
					
					Resource resource = resourceSet.getResource(uri, true);
					if (resource != null && resource.isLoaded()) {
						return resource;
					}
				} catch (Exception ex) {
					System.err.println(ex.getMessage());
					
				}
				return null;
			}

		}).visit(importStmts);
	}

	private void doPackage(TopLevelDecls topLevelDecls, final EPackage ePackage) {
		(new EmfaticASTNodeVisitor() {

			public boolean beginVisit(SubPackageDecl subPackageDecl) {
				String subPackageName = getIDText(subPackageDecl.getName());
				EPackage subPackage = getSubPackage(ePackage, subPackageName);
				doPackage(subPackageDecl.getTopLevelDecls(), subPackage);
				return false;
			}

			public boolean beginVisit(ClassDecl classDecl) {
				String className = getIDText(classDecl.getName());
				EClass eClass = (EClass) ePackage.getEClassifier(className);
				doClass(classDecl, eClass);
				return false;
			}

			public boolean beginVisit(MapEntryDecl mapEntryDecl) {
				String className = getIDText(mapEntryDecl.getName());
				EClass eClass = (EClass) ePackage.getEClassifier(className);
				doMapEntry(mapEntryDecl, eClass);
				return false;
			}

		}).visit(topLevelDecls);
	}

	private void doClass(ClassDecl classDecl, final EClass eClass) {
		final EPackage ePackage = eClass.getEPackage();

		// resolve the bounds for each type param
		if (classDecl.getTypeParamsInfo() != null) {
			addBoundsToTypeParams(classDecl.getTypeParamsInfo(), eClass.getETypeParameters(), ePackage);
		}

		if (classDecl.getSuperTypes() != null)
			(new EmfaticASTNodeVisitor() {

				public boolean beginVisit(BoundExceptWildcard bew) {
					List<ETypeParameter> visibleTPs = eClass.getETypeParameters();
					// add AST nodes for super types
					EGenericType superType = resolve(ePackage, bew, visibleTPs);
					if (superType != null) {
						placeBewInBigMap(bew, superType);
						EClassifier superClass = superType.getEClassifier();
						if (GenericsUtil.isRefToClassifier(superType)) {
							if (superClass instanceof EClass) {
								eClass.getEGenericSuperTypes().add(superType);
							} else {
								QualifiedID qualifiedID = bew.getRawTNameOrTVarOrParamzedTName();
								logError(new EmfaticSemanticError.IllegalSuperClassKind(qualifiedID));
							}
						} else {
							eClass.getEGenericSuperTypes().add(superType);
						}
					} else {
						QualifiedID qualifiedID = bew.getRawTNameOrTVarOrParamzedTName();
						logError(new EmfaticSemanticError.IllegalSuperClassKind(qualifiedID));
					}
					return false;
				}

			}).visit(classDecl.getSuperTypes());
		(new EmfaticASTNodeVisitor() {

			public boolean beginVisit(Attribute attribute) {
				String attrName = getIDText(attribute.getName());
				EAttribute eAttr = (EAttribute) eClass.getEStructuralFeature(attrName);
				doAttribute(attribute, eAttr, ePackage);
				return false;
			}

			public boolean beginVisit(Reference reference) {
				String refName = getIDText(reference.getName());
				EReference eRef = (EReference) eClass.getEStructuralFeature(refName);
				doReference(reference, eRef, ePackage);
				return false;
			}

			public boolean beginVisit(Operation operation) {
				String opName = getIDText(operation.getName());
				EOperation eOp = getOperation(eClass, opName);
				doOperation(operation, eOp, ePackage);
				return false;
			}

		}).visit(classDecl.getClassMemberDecls());
	}

	private void addBoundsToTypeParams(final TypeParamsInfo tpi, final List<ETypeParameter> visibleTPs,
			final EPackage ePackage) {
		new EmfaticASTNodeVisitor() {
			@Override
			public boolean beginVisit(TypeParam tp) {
				TokenText.Get(tp.getTypeVarName()).trim();
				if (tp.getTypeBoundsInfo() != null) {
					for (ASTNode tbN : tp.getTypeBoundsInfo().getOneOrMoreTypeParamBounds().getChildren()) {
						if (tbN instanceof BoundExceptWildcard) {
							BoundExceptWildcard bew = (BoundExceptWildcard) tbN;
							EGenericType eTPBound = resolve(ePackage, bew, visibleTPs);
							placeBewInBigMap(bew, eTPBound);
							String tvName = getIDText(tp.getTypeVarName());
							ETypeParameter eTP = lookup(tvName, visibleTPs);
							eTP.getEBounds().add(eTPBound);
						}
					}
				}
				return false;
			}
		}.visit(tpi.getOneOrMoreTypeParams());
	}

	private void doMapEntry(MapEntryDecl mapEntryDecl, EClass eClass) {
		doMapEntryPart("key", mapEntryDecl.getKey(), eClass);
		doMapEntryPart("value", mapEntryDecl.getValue(), eClass);
	}

	private void doMapEntryPart(String partName, TypeWithMulti partTypeWithMulti, EClass eClass) {
		QualifiedID qualifiedID = getQualifiedID(partTypeWithMulti);
		Multiplicity multiplicity = partTypeWithMulti.getMultiplicity();
		EClassifier refType = resolve(eClass.getEPackage(), qualifiedID);
		BoundExceptWildcard bew = partTypeWithMulti.getName();
		EGenericType eRefType = resolve(eClass.getEPackage(), bew, eClass.getETypeParameters());
		if (refType != null) {
			placeBewInBigMap(bew, eRefType);
			if (refType instanceof EDataType) {
				EAttribute eAttr = EcoreFactory.eINSTANCE.createEAttribute();
				eAttr.setName(partName);
				eAttr.setEGenericType(eRefType);
				setMultiplicity(multiplicity, eAttr);
				eClass.getEStructuralFeatures().add(eAttr);
				/*
				 * this correspondence is necessary in addition to that for type
				 * of attr
				 */
				cstDecl2EcoreAST.put(partTypeWithMulti.getParent(), eAttr);
			} else {
				EReference eRef = EcoreFactory.eINSTANCE.createEReference();
				eRef.setName(partName);
				eRef.setEGenericType(eRefType);
				setMultiplicity(multiplicity, eRef);
				eClass.getEStructuralFeatures().add(eRef);
				/*
				 * this correspondence is necessary in addition to that for type
				 * of attr
				 */
				cstDecl2EcoreAST.put(partTypeWithMulti.getParent(), eRef);
			}
		} else {
			logError(new EmfaticSemanticError.IllegalAttributeKind(qualifiedID));
		}
	}

	private QualifiedID getQualifiedID(TypeWithMulti twm) {
		QualifiedID qualifiedID = twm.getName().getRawTNameOrTVarOrParamzedTName();
		return qualifiedID;
	}

	private void doAttribute(Attribute attribute, EAttribute eAttr, EPackage ePackage) {
		BoundExceptWildcard bew = attribute.getTypeWithMulti().getName();
		List<ETypeParameter> visibleTPs = eAttr.getEContainingClass().getETypeParameters();
		EGenericType refType = resolve(ePackage, bew, visibleTPs);
		if (refType != null) {
			EClassifier refedClassifier = refType.getEClassifier();
			/*
			 * refType may refer to a type param. However if it refers to an
			 * EClassifier it better be an EDataType!
			 */
			if ((refedClassifier != null) && !(refedClassifier instanceof EDataType)) {
				QualifiedID qid = bew.getRawTNameOrTVarOrParamzedTName();
				logError(new EmfaticSemanticError.IllegalAttributeKind(qid));
			} else {
				eAttr.setEGenericType(refType);
				placeBewInBigMap(bew, refType);
			}
		}
		String defaultValue = getValue(attribute.getDefaultValueExpr());
		if (defaultValue != null)
			eAttr.setDefaultValueLiteral(defaultValue);
	}

	private void doReference(Reference reference, EReference eRef, EPackage ePackage) {
		BoundExceptWildcard bew = reference.getTypeWithMulti().getName();
		List<ETypeParameter> visibleTPs = eRef.getEContainingClass().getETypeParameters();
		EGenericType refType = resolve(ePackage, bew, visibleTPs);
		QualifiedID qualifiedID = bew.getRawTNameOrTVarOrParamzedTName();
		if (refType != null) {
			placeBewInBigMap(bew, refType);
			eRef.setEGenericType(refType);
			// compute oppositeName if not pointing to a type param
			EClassifier refedClassifier = refType.getEClassifier();
			if ("val".equals(reference.getReferenceKind().getText())) {
				eRef.setContainment(true);
			}
			if ((refedClassifier != null) && (refedClassifier instanceof EClass)) {
				String oppositeName = null;
				if (reference.getOppositeName() != null) {
					oppositeName = getIDText(reference.getOppositeName());
					EClass refTypeClass = (EClass) refedClassifier;
					EReference opposite = (EReference) refTypeClass.getEStructuralFeature(oppositeName);
					ecoreDecl2cstUse.put(opposite, reference.getOppositeName());
					eRef.setEOpposite(opposite);
				}
			}
		} else {
			logError(new EmfaticSemanticError.NameResolutionFailure(qualifiedID));
		}
	}

	private void doOperation(final Operation operation, final EOperation eOp, final EPackage ePackage) {

		// resolve the bounds for each type param
		if (operation.getTypeParamsInfo() != null) {
			List<ETypeParameter> visibleTPs = visibleTypeParams(eOp);
			addBoundsToTypeParams(operation.getTypeParamsInfo(), visibleTPs, ePackage);
		}

		ResultType t = operation.getResType();
		if (t instanceof VoidContainer) {
			// the return type of eOp is void
			eOp.setEType(null);
		} else {
			TypeWithMulti twm = (TypeWithMulti) t;
			BoundExceptWildcard bew = twm.getName();
			List<ETypeParameter> visibleTPs = visibleTypeParams(eOp);
			EGenericType refType = resolve(ePackage, bew, visibleTPs);
			if (refType != null) {
				placeBewInBigMap(bew, refType);
				eOp.setEGenericType(refType);
			}
		}
		if (operation.getParams() != null)
			(new EmfaticASTNodeVisitor() {

				public boolean beginVisit(Param param) {
					BoundExceptWildcard bew = param.getTypeWithMulti().getName();
					List<ETypeParameter> visibleTPs = visibleTypeParams(eOp);
					EGenericType paramRefType = resolve(ePackage, bew, visibleTPs);
					if (paramRefType != null) {
						placeBewInBigMap(bew, paramRefType);
						String paramName = getIDText(param.getName());
						EParameter eParam = getParam(eOp, paramName);
						if (eParam != null)
							eParam.setEGenericType(paramRefType);
					}
					return false;
				}

			}).visit(operation.getParams());
		if (operation.getExceptions() != null)
			(new EmfaticASTNodeVisitor() {

				public boolean beginVisit(BoundExceptWildcard bew) {
					List<ETypeParameter> visibleTPs = visibleTypeParams(eOp);
					EGenericType exceptionType = resolve(ePackage, bew, visibleTPs);
					if (exceptionType != null) {
						placeBewInBigMap(bew, exceptionType);
						eOp.getEGenericExceptions().add(exceptionType);
					}
					return false;
				}

			}).visit(operation.getExceptions());
	}

	/**
	 * (a) the type params declared for the operation plus (b) only those from
	 * the type params section of the class whose name is not among those in (a)
	 */
	public static List<ETypeParameter> visibleTypeParams(EOperation eOp) {
		List<ETypeParameter> visibleTPs = new LinkedList<ETypeParameter>();
		visibleTPs.addAll(eOp.getETypeParameters());
		for (ETypeParameter classTP : eOp.getEContainingClass().getETypeParameters()) {
			boolean hiddenByOpLevelDecl = false;
			for (ETypeParameter visibleTP : visibleTPs) {
				if (visibleTP.getName().equals(classTP.getName())) {
					hiddenByOpLevelDecl = true;
				}
			}
			if (!hiddenByOpLevelDecl) {
				visibleTPs.add(classTP);
			}
		}
		return visibleTPs;
	}

	private EClassifier resolve(EPackage context, QualifiedID qualifiedID) {
		TokenText.Get(qualifiedID);
		EClassifier eClassifier = resolveHelper(context, qualifiedID);
		if (eClassifier == null)
			logError(new EmfaticSemanticError.NameResolutionFailure(qualifiedID));
		return eClassifier;
	}

	private EClassifier resolveHelper(EPackage context, QualifiedID qualifiedID) {
		String rawQIDText = TokenText.Get(qualifiedID);
		if (rawQIDText.indexOf('.') == -1 && rawQIDText.indexOf('~') == -1) {
			/*
			 * before looking among built-in types, we look for a classifier in
			 * the same package
			 */
			EClassifier atSamePackage = context.getEClassifier(rawQIDText);
			if (atSamePackage != null) {
				return atSamePackage;
			}

			EClassifier basicType = EmfaticBasicTypes.LookupBasicType(rawQIDText);
			if (basicType != null)
				return basicType;
		}
		String qidText = getIDText(qualifiedID);
		String idParts[] = qidText.split("\\.");
		for (EPackage currentContext = context; currentContext != null; currentContext = currentContext
				.getESuperPackage()) {
			EClassifier eClassifier = resolveHelper(currentContext, idParts);
			if (eClassifier != null)
				return eClassifier;
		}

		for (Iterator<EPackage> i = _importedPackages.iterator(); i.hasNext();) {
			EPackage importedPackage = i.next();
			String importedPackageName = importedPackage.getName();
			if (importedPackageName.length() < qidText.length()) {
				String qidTextStart = qidText.substring(0, importedPackageName.length());
				if (qidTextStart.equals(importedPackageName) && qidText.charAt(importedPackageName.length()) == '.') {
					String qidTextEnd = qidText.substring(importedPackageName.length() + 1);
					String qidTextEndParts[] = qidTextEnd.split("\\.");
					EClassifier eClassifier = resolveHelper(importedPackage, qidTextEndParts);
					if (eClassifier != null)
						return eClassifier;
				}
			}
		}

		return null;
	}

	private EClassifier resolveHelper(EPackage context, String idParts[]) {
		if (idParts.length == 1)
			return context.getEClassifier(idParts[0]);
		EPackage subPackage = getSubPackage(context, idParts[0]);
		if (subPackage != null) {
			String subParts[] = removeFirst(idParts);
			return resolveHelper(subPackage, subParts);
		} else {
			return null;
		}
	}

	/**
	 * The production rule for BoundExceptWildcard (in Gymnast syntax) is:
	 * 
	 * sequence boundExceptWildcard : rawTNameOrTVarOrParamzedTName=qualifiedID
	 * (LT oneOrMoreTypeArgs GT)? ;
	 * 
	 * The qualified name may stand for a type variable, a reference to a
	 * non-generic type, a raw reference to a generic type, or a parameterized
	 * type. Scopes are to be searched in that order.
	 * 
	 */
	private EGenericType resolve(EPackage context, BoundExceptWildcard bew, List<ETypeParameter> typeParamsInScope) {
		QualifiedID qualifiedID = bew.getRawTNameOrTVarOrParamzedTName();
		String rawQIDText = TokenText.Get(qualifiedID);
		if (bew.getOneOrMoreTypeArgs() == null) {
			/*
			 * no type args: either a ref to a type var, ref to non-generic
			 * type, or raw reference to generic type
			 */
			ETypeParameter refedTP = lookup(rawQIDText, typeParamsInScope);
			if (refedTP != null) {
				EGenericType egt = GenericsUtil.getRefToTypeParam(refedTP);
				return egt;
			}
			// fall back to
			EClassifier eClassifier = resolveHelper(context, qualifiedID);
			if (eClassifier == null) {
				logError(new EmfaticSemanticError.NameResolutionFailure(qualifiedID));
				return null;
			}
			return GenericsUtil.getEGenericType(eClassifier);
		} else {
			// there are type args, it has to be a parameterized type
			EClassifier eClassifier = resolveHelper(context, qualifiedID);
			if (eClassifier == null) {
				logError(new EmfaticSemanticError.NameResolutionFailure(qualifiedID));
				return null;
			} else {
				EGenericType res = GenericsUtil.getEGenericType(eClassifier);
				// make AST nodes for type args, wildcards are allowed
				OneOrMoreTypeArgs oomtas = bew.getOneOrMoreTypeArgs();
				for (ASTNode taN : oomtas.getChildren()) {
					if (taN instanceof TypeArg) {
						EGenericType newTypeArg = null;
						if (taN instanceof BoundExceptWildcard) {
							// recursive call
							newTypeArg = resolve(context, (BoundExceptWildcard) taN, typeParamsInScope);
						} else {
							// not recursive call
							newTypeArg = resolve(context, (Wildcard) taN, typeParamsInScope);
						}
						if (newTypeArg != null) {
							res.getETypeArguments().add(newTypeArg);
						}
					}
				}
				return res;
			}
		}
	}

	private EGenericType resolve(EPackage context, Wildcard w, List<ETypeParameter> typeParamsInScope) {
		EGenericType res = null;
		if (w.getBoundExceptWildcard() == null) {
			// unbounded wildcard
			res = GenericsUtil.getUnboundedWildcard();
			return res;
		} else {
			BoundExceptWildcard bew = w.getBoundExceptWildcard();
			EGenericType b = resolve(context, bew, typeParamsInScope);
			// in case b == null, error reporting already performed by resolve
			if (b != null) {
				String kw = w.getExtendsOrSuper().getText().toLowerCase().trim();
				if (kw.equals("extends")) {
					res = GenericsUtil.getUpperBoundedWildcard(b);
				} else {
					res = GenericsUtil.getLowerBoundedWildcard(b);
				}
			}
			return res;
		}
	}

	private String[] removeFirst(String idParts[]) {
		String subParts[] = new String[idParts.length - 1];
		for (int i = 0; i < subParts.length; i++)
			subParts[i] = idParts[i + 1];

		return subParts;
	}

	private ArrayList<EPackage> _importedPackages;

	public static ETypeParameter lookup(String tvName, List<ETypeParameter> etps) {
		for (ETypeParameter tp : etps) {
			if (tp.getName().equals(tvName)) {
				return tp;
			}
		}
		return null;
	}

//	private static boolean isClassifier(EGenericType egt) {
//		if (egt == null) {
//			return false;
//		}
//		boolean res = (egt.getEClassifier() != null) && (egt.getETypeArguments().size() == 0)
//				&& (egt.getETypeParameter() == null);
//		return res;
//	}

	private void placeBewInBigMap(final BoundExceptWildcard bew, final EGenericType gt) {
		EObject ecoreDecl = getEcoreDeclForTypeRef(gt);
		ecoreDecl2cstUse.put(ecoreDecl, bew.getRawTNameOrTVarOrParamzedTName());
		/*
		 * doing the above is not enough as we want hyperlinks also to detect
		 * the nodes for (parts of) type arguments
		 */
		if (bew.getOneOrMoreTypeArgs() == null) {
			return;
		}
		int etaIndex = -1;
		for (int i = 0; i < bew.getOneOrMoreTypeArgs().getChildren().length; i++) {
			ASTNode taN = bew.getOneOrMoreTypeArgs().getChildren()[i];
			if (taN instanceof TypeArg) {
				etaIndex++;
				EGenericType taE = gt.getETypeArguments().get(etaIndex);
				if (taN instanceof Wildcard) {
					Wildcard taWN = (Wildcard) taN;
					BoundExceptWildcard bew2 = taWN.getBoundExceptWildcard();
					if (bew2 != null) {
						EGenericType taB = null;
						if (taWN.getExtendsOrSuper().getText().trim().toLowerCase().equals("extends")) {
							taB = taE.getEUpperBound();
						} else {
							taB = taE.getELowerBound();
						}
						placeBewInBigMap(bew2, taB);
					}
				} else {
					BoundExceptWildcard taBewN = (BoundExceptWildcard) taN;
					placeBewInBigMap(taBewN, taE);
				}
			}
		}
	}

	public static EObject getEcoreDeclForTypeRef(EGenericType gt) {
		EObject ecoreDecl = null;
		if (GenericsUtil.isRefToClassifier(gt)) {
			ecoreDecl = gt.getEClassifier();
		} else {
			ecoreDecl = gt.getETypeParameter();
		}
		return ecoreDecl;
	}

//	private ASTNode getCstDeclForTypeRef(EGenericType gt) {
//		ASTNode cstDecl = null;
//		if (GenericsUtil.isRefToClassifier(gt)) {
//			EClassifier c = gt.getEClassifier();
//			cstDecl = cstDecl2EcoreAST.getInv(c);
//		} else {
//			ETypeParameter tp = gt.getETypeParameter();
//			cstDecl = cstDecl2EcoreAST.getInv(tp);
//		}
//		return cstDecl;
//	}

}
