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

	private void generateInner(IFile genModelFile, IProgressMonitor monitor)
			throws CoreException {
		IPath genModelPath = genModelFile.getFullPath();
		ResourceSet resourceSet = new ResourceSetImpl();
		URI genModelURI = URI.createFileURI(genModelPath.toString());
		Resource r = resourceSet.getResource(genModelURI, true);
		GenModel genModel = (GenModel) r.getContents().get(0);
		IStatus status = genModel.validate();
		if (status.getChildren().length > 0 ) {
			// FIXME add problem markers
			for (IStatus st : status.getChildren()) {
				System.out.println(st);
			}
			return; 
		}
		ExprBuilder eb = new ExprBuilder(genModel);
		String exprBuilderText = eb.toString();
		String exprBuilderFilePath = getExprBuilderFilePath(genModelFile,
				eb._javaClassName);
		writeStringToIPath(exprBuilderFilePath, exprBuilderText);
	}

	private String getExprBuilderFilePath(IFile ecoreFile, String fileName) {
		IPath p = ecoreFile.getFullPath().removeFileExtension()
				.removeLastSegments(1);
		p = p.append(fileName).addFileExtension("java");
		String filePath = p.toString();
		return filePath;
	}

	private void writeStringToIPath(String outFilePath, String fileText)
			throws CoreException {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		IPath path = new Path(outFilePath);
		IFile outFile = workspaceRoot.getFile(path);
		java.io.InputStream in = new ByteArrayInputStream(fileText.getBytes());
		if (outFile.exists())
			outFile.setContents(in, true, false, null);
		else
			outFile.create(in, true, null);
	}

	public void generate(IFile _genModelFile, IProgressMonitor monitor) {
		try {
			generateInner(_genModelFile, monitor);
		} catch (Exception e) {
			org.eclipse.gymnast.generators.embeddeddsl.activator.Activator
					.getDefault().logError(this.getClass().getName(), e);
		}
	}

	public static String fileNameNoExt(IFile _file) {
		String ecoreFileExt = _file.getFileExtension();
		int extLen = ecoreFileExt != null ? ecoreFileExt.length() + 1 : 0;
		String ecoreFileName = _file.getName();
		String fileName = ecoreFileName.substring(0, ecoreFileName.length()
				- extLen);
		return fileName;
	}

}
