package org.eclipse.gymnast.generators.embeddeddsl.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.gymnast.generators.embeddeddsl.EDSLGenerator;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class GenerateEDSLFromGenModel implements IObjectActionDelegate {

	private IFile _file;
	private GenerateEDSLFromGenModelJob _job;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

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
		if ((_file != null) && (_job == null)) {
			_job = new GenerateEDSLFromGenModelJob();
			_job.schedule();
		}
	}

	private class GenerateEDSLFromGenModelJob extends Job {
		private GenerateEDSLFromGenModelJob() {
			super("Generating Embedded DSL for " + _file.getName());
		}

		protected IStatus run(IProgressMonitor monitor) {

			if (!problemMarkers()) {
				EDSLGenerator generator = new EDSLGenerator();
				generator.generate(_file, monitor);
			}

			_job = null;
			return Status.OK_STATUS;
		}

		private boolean problemMarkers() {
			IFile genModelFile = _file;
			IPath genModelPath = genModelFile.getFullPath();
			ResourceSet resourceSet = new ResourceSetImpl();
			String gps = genModelPath.toString();
			URI genModelURI = URI.createPlatformResourceURI(gps, true);
			Resource r = resourceSet.getResource(genModelURI, true);
			GenModel genModel = (GenModel) r.getContents().get(0);
			// EcoreUtil.resolveAll(resourceSet);
			IStatus status = genModel.validate();
			UtilMarkers.clearMarkers(genModelFile);
			boolean areThereProblems = status.getChildren().length > 0;
			if (areThereProblems) {
				UtilMarkers.createProblemMarker(status, genModelFile, 0, null);
			}
			return areThereProblems;
		}
	}

}
