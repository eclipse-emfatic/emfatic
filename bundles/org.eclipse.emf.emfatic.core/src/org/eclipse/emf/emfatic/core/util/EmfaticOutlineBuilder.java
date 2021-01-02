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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.common.notify.AdapterFactory;
import org.eclipse.emf.ecore.EcoreFactory;
import org.eclipse.emf.ecore.provider.EcoreEditPlugin;
import org.eclipse.emf.ecore.provider.EcoreItemProviderAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedImage;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.ui.provider.ExtendedImageRegistry;
import org.eclipse.emf.emfatic.core.generator.ecore.GenerationPhase;
import org.eclipse.emf.emfatic.core.generator.ecore.TokenText;
import org.eclipse.emf.emfatic.core.generator.ecore.TokenTextBlankSep;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Annotation;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Attribute;
import org.eclipse.emf.emfatic.core.lang.gen.ast.BoundExceptWildcard;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ClassDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.ClassMemberDecls;
import org.eclipse.emf.emfatic.core.lang.gen.ast.CommaListBoundExceptWild;
import org.eclipse.emf.emfatic.core.lang.gen.ast.CompUnit;
import org.eclipse.emf.emfatic.core.lang.gen.ast.DataTypeDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNode;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EmfaticASTNodeVisitor;
import org.eclipse.emf.emfatic.core.lang.gen.ast.EnumDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.KeyEqualsValue;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Operation;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Param;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Params;
import org.eclipse.emf.emfatic.core.lang.gen.ast.Reference;
import org.eclipse.emf.emfatic.core.lang.gen.ast.SubPackageDecl;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TopLevelDecls;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TypeParam;
import org.eclipse.emf.emfatic.core.lang.gen.ast.TypeParamsInfo;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.outline.IOutlineBuilder;
import org.eclipse.gymnast.runtime.core.outline.OutlineNode;
import org.eclipse.swt.graphics.Image;


public class EmfaticOutlineBuilder implements IOutlineBuilder {

	CompUnit compUnit = null;
	Map<ASTNode, OutlineNode> a2o = new HashMap<ASTNode, OutlineNode>();

	public EmfaticOutlineBuilder() {
	}

	public OutlineNode[] buildOutline(ASTNode parseRoot) {
		a2o.clear();
		if (parseRoot instanceof CompUnit) {
			if (parseRoot != null && ((CompUnit) parseRoot).getPackageDecl() != null) {
				compUnit = (CompUnit) parseRoot;
				Image image = getPackageImage();
				String packageName = GenerationPhase.getIDText(compUnit.getPackageDecl().getName());
				OutlineNode outlineNode = new OutlineNode(compUnit.getPackageDecl(), packageName, image);
				a2o.put(compUnit.getPackageDecl().getName(), outlineNode);
				a2o.put(compUnit.getPackageDecl().getPackage_KW(), outlineNode);
				a2o.put(compUnit.getPackageDecl().getSemi(), outlineNode);
				doPackageContents(outlineNode, compUnit.getTopLevelDecls());
				compUnit.setCst2Outline(a2o);
				OutlineNode[] outlineNodes = new OutlineNode[] { outlineNode };
				return outlineNodes;
			}
		}
		return null;
	}

	private void doPackageContents(final OutlineNode parentNode, TopLevelDecls topLevelDecls) {
		(new EmfaticASTNodeVisitor() {

			// TODO MapEntryDecl is not being shown in outline

			private void addAttribute(Attribute c, OutlineNode outlineNode) {
				Attribute at = (Attribute) c;
				String strTypeWithMulti = TokenTextBlankSep.Get(c.getTypeWithMulti());
				String strName = GenerationPhase.getIDText(at.getName());
				OutlineNode n = new OutlineNode(at.getName(), strName + " : " + strTypeWithMulti, EmfaticOutlineBuilder
						.getAttributeImage());
				a2o.put(at, n);
				ASTNode[] atAnns = at.getAnnotations().getChildren();
				annotate(atAnns, n);
				outlineNode.addChild(n);
			}

			private void addClassDeclMembers(ClassDecl classDecl, OutlineNode outlineNode) {
				ClassMemberDecls ms = classDecl.getClassMemberDecls();

				ASTNode[] ans = classDecl.getAnnotations().getChildren();
				annotate(ans, outlineNode);

				for (ASTNode c : ms.getChildren()) {
					if (c instanceof Attribute) {
						addAttribute((Attribute) c, outlineNode);
					}
					if (c instanceof Reference) {
						addReference((Reference) c, outlineNode);
					}
					if (c instanceof Operation) {
						addOperation((Operation) c, outlineNode);
					}
				}
			}

			private void addOperation(Operation c, OutlineNode outlineNode) {
				Operation o = (Operation) c;
				String strResultType = TokenTextBlankSep.Get(o.getResType());
				String strName = GenerationPhase.getIDText(o.getName());
				OutlineNode n = new OutlineNode(o.getName(), strName + " : " + strResultType, EmfaticOutlineBuilder
						.getOperationImage());
				a2o.put(o, n);
				ASTNode[] oAnns = o.getAnnotations().getChildren();
				annotate(oAnns, n);
				outlineNode.addChild(n);
				// eGenericExceptionTypes
				CommaListBoundExceptWild excs = c.getExceptions();
				addExceptions(excs, n);
				// TODO add eGenericReturnType
				TypeParamsInfo tpi = c.getTypeParamsInfo();
				addTypeParams(tpi, n);
				addParams(o.getParams(), n);
			}

			private void addExceptions(CommaListBoundExceptWild excs, OutlineNode n) {
				if (excs == null) {
					return;
				}
				for (ASTNode en : excs.getChildren()) {
					if (en instanceof BoundExceptWildcard) {
						BoundExceptWildcard bew = (BoundExceptWildcard) en;
						OutlineNode e = null;
						String strText = TokenText.Get(bew);
						if (bew.getOneOrMoreTypeArgs() != null) {
							// generic exception
							e = new OutlineNode(en, strText, getGenericExceptionImage());
						} else {
							e = new OutlineNode(en, strText);
						}
						a2o.put(bew, e);
						n.addChild(e);
					}
				}
			}

			private void addParams(Params params, OutlineNode on) {
				if (params == null) {
					return;
				}
				ASTNode[] ps = params.getChildren();
				for (ASTNode pn : ps) {
					if (pn instanceof Param) {
						Param p = (Param) pn;
						String strTypeWithMulti = TokenTextBlankSep.Get(p.getTypeWithMulti());
						String strName = GenerationPhase.getIDText(p.getName());
						OutlineNode n = new OutlineNode(p, strName + " : " + strTypeWithMulti, EmfaticOutlineBuilder
								.getParamImage());
						a2o.put(p, n);
						on.addChild(n);
					}
				}
			}

			private void addReference(Reference c, OutlineNode outlineNode) {
				Reference r = (Reference) c;
				String strTypeWithMulti = TokenTextBlankSep.Get(c.getTypeWithMulti());
				String strName = GenerationPhase.getIDText(r.getName());
				OutlineNode n = new OutlineNode(r.getName(), strName + " : " + strTypeWithMulti, EmfaticOutlineBuilder
						.getReferenceImage());
				a2o.put(r, n);
				ASTNode[] rAnns = r.getAnnotations().getChildren();
				annotate(rAnns, n);
				outlineNode.addChild(n);
			}

			private void addTypeParams(TypeParamsInfo tpi, OutlineNode outlineNode) {
				if (tpi == null || tpi.getOneOrMoreTypeParams() == null) {
					return;
				}
				ASTNode[] tps = tpi.getOneOrMoreTypeParams().getChildren();
				for (ASTNode tpn : tps) {
					if (tpn instanceof TypeParam) {
						TypeParam tp = (TypeParam) tpn;
						String strTypeVarName = GenerationPhase.getIDText(tp.getTypeVarName());
						OutlineNode n = new OutlineNode(tp, strTypeVarName, EmfaticOutlineBuilder.getTypeParamImage());
						a2o.put(tp, n);
						outlineNode.addChild(n);
						// add bounds as children of the node for the type param
						if (tp.getTypeBoundsInfo() != null
								&& tp.getTypeBoundsInfo().getOneOrMoreTypeParamBounds() != null) {
							ASTNode[] boundNodes = tp.getTypeBoundsInfo().getOneOrMoreTypeParamBounds().getChildren();
							for (ASTNode bound : boundNodes) {
								String boundLabel = TokenTextBlankSep.Get((EmfaticASTNode) bound);
								OutlineNode n2 = new OutlineNode(bound, boundLabel);
								a2o.put(bound, n2);
								n.addChild(n2);
							}
						}
					}
				}

			}

			private void annotate(ASTNode[] as, OutlineNode on) {
				// each annotation will be added as a child
				for (ASTNode astNode : as) {
					Annotation astAnn = (Annotation) astNode;
					String strSource = TokenText.Get(astAnn.getSource());
					OutlineNode n = new OutlineNode(astNode, strSource, EmfaticOutlineBuilder.getAnnotationImage());
					a2o.put(astAnn, n);
					for (ASTNode kv : astAnn.getKeyEqualsValueList().getChildren()) {
						if (kv instanceof KeyEqualsValue) {
							String strKey = kv.getChild(0).getChild(0).getFirstChild().getText();
							String strValue = kv.getChild(2).getChild(0).getText();
							String strKV = strKey + " = " + strValue;
							OutlineNode n2 = new OutlineNode(kv, strKV, null);
							a2o.put(kv, n2);
							n.addChild(n2);
						}
					}
					on.addChild(n);
				}
			}

			public boolean beginVisit(ClassDecl classDecl) {
				String name = classDecl.getName().getText();
				Image image = EmfaticOutlineBuilder.getClassImage();
				OutlineNode outlineNode = new OutlineNode(classDecl, name, image);
				a2o.put(classDecl, outlineNode);
				addTypeParams(classDecl.getTypeParamsInfo(), outlineNode);
				addClassDeclMembers(classDecl, outlineNode);
				// TODO decide whether to add supertypes
				parentNode.addChild(outlineNode);
				return false;
			}

			public boolean beginVisit(DataTypeDecl dataTypeDecl) {
				String name = dataTypeDecl.getName().getText();
				Image image = EmfaticOutlineBuilder.getDataTypeImage();
				OutlineNode outlineNode = new OutlineNode(dataTypeDecl, name, image);
				a2o.put(dataTypeDecl, outlineNode);
				parentNode.addChild(outlineNode);
				return false;
			}

			public boolean beginVisit(EnumDecl enumDecl) {
				String name = enumDecl.getName().getText();
				Image image = EmfaticOutlineBuilder.getEnumImage();
				OutlineNode outlineNode = new OutlineNode(enumDecl, name, image);
				a2o.put(enumDecl, outlineNode);
				parentNode.addChild(outlineNode);
				return false;
			}

			public boolean beginVisit(SubPackageDecl subPackageDecl) {
				String name = GenerationPhase.getIDText(subPackageDecl.getName());
				Image image = EmfaticOutlineBuilder.getPackageImage();
				OutlineNode outlineNode = new OutlineNode(subPackageDecl, name, image);

				a2o.put(subPackageDecl.getName(), outlineNode);
				a2o.put(subPackageDecl.getPackage_KW(), outlineNode);
				a2o.put(subPackageDecl.getLcurly(), outlineNode);

				parentNode.addChild(outlineNode);
				doPackageContents(outlineNode, subPackageDecl.getTopLevelDecls());
				return false;
			}

		}).visit(topLevelDecls);
	}

	private static AdapterFactory getAdapterFactory() {
		if (_adapterFactory == null)
			_adapterFactory = new EcoreItemProviderAdapterFactory();
		return _adapterFactory;
	}

	static Image getPackageImage() {
		if (_packageImage == null) {
			Object ePackage = EcoreFactory.eINSTANCE.createEPackage();
			_packageImage = getImage(ePackage);
		}
		return _packageImage;
	}

	static Image getClassImage() {
		if (_classImage == null) {
			Object ePackage = EcoreFactory.eINSTANCE.createEClass();
			_classImage = getImage(ePackage);
		}
		return _classImage;
	}

	static Image getEnumImage() {
		if (_enumImage == null) {
			Object ePackage = EcoreFactory.eINSTANCE.createEEnum();
			_enumImage = getImage(ePackage);
		}
		return _enumImage;
	}

	static Image getDataTypeImage() {
		if (_dataTypeImage == null) {
			Object ePackage = EcoreFactory.eINSTANCE.createEDataType();
			_dataTypeImage = getImage(ePackage);
		}
		return _dataTypeImage;
	}

	public static Image getOperationImage() {
		if (_operationImage == null) {
			Object eOperation = EcoreFactory.eINSTANCE.createEOperation();
			_operationImage = getImage(eOperation);
		}
		return _operationImage;
	}

	public static Image getAnnotationImage() {
		if (_annotationImage == null) {
			Object eAnnotation = EcoreFactory.eINSTANCE.createEAnnotation();
			_annotationImage = getImage(eAnnotation);
		}
		return _annotationImage;
	}

	public static Image getAttributeImage() {
		if (_attributeImage == null) {
			Object eAttribute = EcoreFactory.eINSTANCE.createEAttribute();
			_attributeImage = getImage(eAttribute);
		}
		return _attributeImage;
	}

	public static Image getReferenceImage() {
		if (_referenceImage == null) {
			Object eReference = EcoreFactory.eINSTANCE.createEReference();
			_referenceImage = getImage(eReference);
		}
		return _referenceImage;
	}

	static Image getTypeParamImage() {
		if (_typeParamImage == null) {
			Object etp = EcoreFactory.eINSTANCE.createETypeParameter();
			_typeParamImage = getImage(etp);
		}
		return _typeParamImage;
	}

	static Image getParamImage() {
		if (_paramImage == null) {
			Object ep = EcoreFactory.eINSTANCE.createEParameter();
			_paramImage = getImage(ep);
		}
		return _paramImage;
	}

	static Image getGenericExceptionImage() {
		if (_genericExceptionImage == null) {
			_genericExceptionImage = (Image) EcoreEditPlugin.INSTANCE.getImage("full/obj16/EGenericException");
		}
		return _genericExceptionImage;
	}

	static Image getGenericElementTypeImage() {
		if (_genericElementTypeImage == null) {
			_genericElementTypeImage = (Image) EcoreEditPlugin.INSTANCE.getImage("full/obj16/EGenericElementType");
		}
		return _genericElementTypeImage;
	}

	static Image getGenericSuperTypeImage() {
		if (_genericSuperTypeImage == null) {
			_genericSuperTypeImage = (Image) EcoreEditPlugin.INSTANCE.getImage("full/obj16/EGenericSuperType");
		}
		return _genericSuperTypeImage;
	}

	static Image getGenericWildcardImage() {
		if (_genericWildcardImage == null) {
			_genericWildcardImage = (Image) EcoreEditPlugin.INSTANCE.getImage("full/obj16/EGenericWildcard");
		}
		return _genericWildcardImage;
	}

	private static Image getImage(Object eObject) {
		IItemLabelProvider itemLabelProvider = (IItemLabelProvider) getAdapterFactory().adapt(eObject,
				org.eclipse.emf.edit.provider.IItemLabelProvider.class);
		Object imageObject = itemLabelProvider.getImage(eObject);
		Image image = ExtendedImageRegistry.getInstance().getImage(imageObject);
		return image;
	}

	private static EcoreItemProviderAdapterFactory _adapterFactory;

	private static Image _packageImage;

	private static Image _classImage;

	private static Image _enumImage;

	private static Image _dataTypeImage;

	private static Image _operationImage;

	private static Image _annotationImage;

	private static Image _attributeImage;

	private static Image _referenceImage;

	private static Image _typeParamImage;

	private static Image _paramImage;

	private static Image _genericExceptionImage;

	private static Image _genericElementTypeImage;

	private static Image _genericSuperTypeImage;

	private static Image _genericWildcardImage;

	/**
	 * This overlays the second image on the first.
	 */
	public static Image overlayImage(Image first, Image second) {
		List<Image> images = new ArrayList<Image>(2);
		images.add(first);
		images.add(second);
		ComposedImage imageObject = new ComposedImage(images);
		Image image = (Image) imageObject.getImages().get(0); // ExtendedImageRegistry.getInstance().getImage(imageObject);
		return image;
	}

}
