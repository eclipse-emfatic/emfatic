package org.eclipse.gymnast.generators.ecore.ui.actions;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Collections;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.genmodel.GenJDKLevel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelFactory;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gymnast.generators.ecore.convert.EcoreGeneratorFromGymnastGrammar;
import org.eclipse.gymnast.generators.ecore.convert.MyEcoreUtil;
import org.eclipse.gymnast.generators.ecore.cst.RootCS;
import org.eclipse.gymnast.generators.ecore.ui.Activator;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.osgi.framework.Bundle;

public class GenerateGenModel implements IObjectActionDelegate {

	public GenerateGenModel() {
		// TODO Auto-generated constructor stub
	}

	private IFile _file;
	private boolean _genJava;
	private String _genModelBasePackage;
	private String _genModelPrefix;

	public void selectionChanged(IAction action, ISelection selection) {
		_file = null;

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object selElem = sel.getFirstElement();
			if (selElem instanceof IFile) {
				_file = (IFile) selElem;
			}
		}
	}

	public void run(IAction action) {
		if ((_file != null)) {
			// check preconditions
			RootCS wellFormednessChecker = RootCS.getWellFormednessChecker(_file);
			if (wellFormednessChecker == null) {
				// add problem markers
				EcoreGeneratorFromGymnastGrammar generator = new EcoreGeneratorFromGymnastGrammar();
				generator.generate(_file, false, new NullProgressMonitor());
				return;
			}
			_genModelBasePackage = wellFormednessChecker.getOption_genModelBasePackage();
			_genModelPrefix = wellFormednessChecker.getOption_genModelPrefix();
			// create the code generating operation
			WorkspaceModifyOperation op = buildOperation();
			// run the generate code operation
			try {
				_genJava = action.getId().endsWith("AndJava");
				Shell activeShell = Activator.getDefault().getWorkbench().getActiveWorkbenchWindow().getShell();
				new ProgressMonitorDialog(activeShell).run(true, true, op);
			} catch (InterruptedException e) {
				Activator.getDefault().logError(this.getClass().getName(), e);
			} catch (InvocationTargetException e) {
				Activator.getDefault().logError(this.getClass().getName(), e);
			}
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub

	}

	private WorkspaceModifyOperation buildOperation() {
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor) throws CoreException {
				try {

					monitor.beginTask("Gymnast GenModel Generator - ", 1);
					IPath genModelPath = _file.getFullPath().removeLastSegments(1);
					// TODO check if .ecore exists, report if not
					IFile ecoreFile = (IFile) _file.getParent().findMember(fileNameNoExt(_file) + ".ecore");
					if (ecoreFile == null) {
						EcoreGeneratorFromGymnastGrammar generator = new EcoreGeneratorFromGymnastGrammar();
						generator.generate(_file, monitor);
						ecoreFile = (IFile) _file.getParent().findMember(fileNameNoExt(_file) + ".ecore");
						assert ecoreFile.exists();
					}
					genModelPath = genModelPath.append(fileNameNoExt(_file) + ".genmodel");
					EPackage mainPackage = MyEcoreUtil.loadEcoreFile(ecoreFile);
					try {
						GenModel gm = generateGenModel(genModelPath, mainPackage, _genModelBasePackage,
								_genModelPrefix, _file.getProject());
						if (_genJava) {
							genJavaFromGenModel(gm, monitor);
						}
					} catch (IOException e) {
						Activator.getDefault().logError(this.getClass().getName(), e);
					}

					IContainer selContainer = _genJava ? _file.getProject() : _file.getParent();
					if (selContainer != null) {
						selContainer.refreshLocal(IResource.DEPTH_INFINITE, monitor);
					}

					return;

				} finally {
					monitor.done();
				}
			}

		};
		return op;
	}

	public static String fileNameNoExt(IFile _file) {
		String ecoreFileExt = _file.getFileExtension();
		int extLen = ecoreFileExt != null ? ecoreFileExt.length() + 1 : 0;
		String ecoreFileName = _file.getName();
		String fileName = ecoreFileName.substring(0, ecoreFileName.length() - extLen);
		return fileName;
	}

	public static GenModel generateGenModel(IPath genModelPath, EPackage ePackage, String genModelBasePackage,
			String genModelPrefix, IProject proj) throws IOException {
		ResourceSet resourceSet = new ResourceSetImpl();
		URI genModelURI = URI.createFileURI(genModelPath.toString());
		Resource genModelResource = Resource.Factory.Registry.INSTANCE.getFactory(genModelURI).createResource(
				genModelURI);
		GenModel genModel = GenModelFactory.eINSTANCE.createGenModel();
		genModelResource.getContents().add(genModel);
		resourceSet.getResources().add(genModelResource);
		genModel.setModelDirectory("/" + proj.getName() + "/src");
		genModel.getForeignModel().add(ePackage.getName());
		genModel.initialize(Collections.singleton(ePackage));
		genModel.setComplianceLevel(GenJDKLevel.JDK50_LITERAL);
		GenPackage genPackage = genModel.getGenPackages().get(0);

		// modified
		genModel.setModelName(genModelURI.trimFileExtension().lastSegment());
		// + "GenModel");
		try {
			genModel.setModelPluginID(proj.getDescription().getName());
		} catch (CoreException e1) {
			Activator.getDefault().logError(GenerateGenModel.class.getName(), e1);
		}

		// assignPrefixBasedOnPackageName(genPackage);

		genPackage.setBasePackage(genModelBasePackage);
		genPackage.setPrefix(genModelPrefix);

		genModel.setDynamicTemplates(true);
		genModel.setCodeFormatting(true);

		// dependencies so far
		// genModel.getModelPluginVariables().add("ECLIPSE_OCL = org.eclipse.ocl");
		/* arrange for using hookpoints when generating .java from .genmodel */
		try {
			Bundle thisBundle = Activator.getDefault().getBundle();
			IPath dynamicTemplatesFolder = new Path("templates");
			URL url = FileLocator.find(thisBundle, dynamicTemplatesFolder, null);
			String strURI;
			strURI = url.toURI().toString();
			genModel.setTemplateDirectory(strURI);
		} catch (URISyntaxException e) {
			Activator.getDefault().logError(GenerateGenModel.class.getName(), e);
		}

		genModelResource.save(Collections.EMPTY_MAP);
		return genModel;
	}

	/**
	 * 
	 * recursively sets the prefix for all subpackages
	 * 
	 * @param genPackage
	 */
	private static void assignPrefixBasedOnPackageName(GenPackage genPackage) {
		String prefix = genPackage.getEcorePackage().getName();
		genPackage.setPrefix(prefix);
		for (GenPackage gP : genPackage.getNestedGenPackages()) {
			assignPrefixBasedOnPackageName(gP);
		}
	}

	public static void genJavaFromGenModel(GenModel genModel, IProgressMonitor monitor) {
		genModel.setCanGenerate(true);

		// Create the generator and set the model-level input object.
		Generator generator = new Generator();
		generator.setInput(genModel);

		// Generator model code.
		generator.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, BasicMonitor.toMonitor(monitor));
	}
}
