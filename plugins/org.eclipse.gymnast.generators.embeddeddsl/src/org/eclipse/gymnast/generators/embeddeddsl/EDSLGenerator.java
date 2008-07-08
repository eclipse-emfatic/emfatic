package org.eclipse.gymnast.generators.embeddeddsl;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gymnast.generators.embeddeddsl.templates.ExprBuilder;

public class EDSLGenerator {

	private void generateInner(IFile genModelFile, IProgressMonitor monitor) throws CoreException {
		IPath genModelPath = genModelFile.getFullPath();
		ResourceSet resourceSet = new ResourceSetImpl();
		String gps = genModelPath.toString();
		URI genModelURI = URI.createPlatformResourceURI(gps, true);
		Resource r = resourceSet.getResource(genModelURI, true);
		GenModel genModel = (GenModel) r.getContents().get(0);
		// EcoreUtil.resolveAll(resourceSet);
		IStatus status = genModel.validate();
		/*
		 * problem markers are added in the accompanying ui plugin, in class
		 * GenerateEDSLFromGenModel
		 */
		if (status.getChildren().length > 0) {
			return;
		}
		ExprBuilder eb = new ExprBuilder(genModel);
		String exprBuilderText = eb.toString();
		String exprBuilderFilePath = getExprBuilderFilePath(genModelFile, eb._javaClassName);
		writeStringToIPath(exprBuilderFilePath, exprBuilderText);
	}

	private String getExprBuilderFilePath(IFile f, String fileName) {
		IPath p = f.getFullPath().removeFileExtension().removeLastSegments(1);
		p = p.append(fileName).addFileExtension("java");
		String filePath = p.toString();
		return filePath;
	}

	private void writeStringToIPath(String outFilePath, String fileText) throws CoreException {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = new Path(outFilePath);
		IFile outFile = workspaceRoot.getFile(path);
		java.io.InputStream in = new ByteArrayInputStream(fileText.getBytes());
		if (outFile.exists())
			outFile.setContents(in, true, false, null);
		else
			outFile.create(in, true, null);
	}

	/**
	 * Generates the expression builder java file by writing it in the same
	 * folder as the first parameter.
	 * 
	 * @param _genModelFile
	 * @param monitor
	 */
	public void generate(IFile _genModelFile, IProgressMonitor monitor) {
		try {
			generateInner(_genModelFile, monitor);
		} catch (Exception e) {
			org.eclipse.gymnast.generators.embeddeddsl.activator.Activator.getDefault().logError(this.getClass().getName(), e);
		}
	}

	/**
	 * @return the filename without extension
	 */
	public static String fileNameNoExt(IFile _file) {
		String fileExt = _file.getFileExtension();
		int extLen = fileExt != null ? fileExt.length() + 1 : 0;
		String fileNameWithExt = _file.getName();
		String fileNameWithoutExt = fileNameWithExt.substring(0, fileNameWithExt.length() - extLen);
		return fileNameWithoutExt;
	}

}
