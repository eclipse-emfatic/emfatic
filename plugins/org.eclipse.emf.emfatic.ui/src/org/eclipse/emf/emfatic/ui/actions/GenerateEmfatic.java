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

package org.eclipse.emf.emfatic.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.Diagnostician;
import org.eclipse.emf.emfatic.core.generator.emfatic.EmfaticGenerator;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class GenerateEmfatic implements IObjectActionDelegate {
	private class GenerateEmfaticJob extends Job {
		final private IFile _file;

		protected IStatus run(IProgressMonitor monitor) {

			boolean ok = ecoreValidate(_file, monitor);
			if (ok) {
				EmfaticGenerator generator = new EmfaticGenerator();
				generator.generate(_file, monitor);
			}
			_job = null;
			return Status.OK_STATUS;
		}

		GenerateEmfaticJob(IFile file) {
			super("Generating Emfatic Source for " + file.getName());
			_file=file;
		}
	}

	public GenerateEmfatic() {
	}

	public void setActivePart(IAction iaction, IWorkbenchPart iworkbenchpart) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
		_file = null;
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object selElem = sel.getFirstElement();
			if (selElem instanceof IFile)
				_file = (IFile) selElem;
		}
	}

	public void run(IAction action) {
		if (_file != null && _job == null) {
			_job = new GenerateEmfaticJob(_file);
			// we might create a new file in the container
			_job.setRule(_file.getParent());
			_job.schedule();
		}
	}

	private IFile _file;
	volatile private GenerateEmfaticJob _job;

	public static boolean ecoreValidate(IFile ecoreFile, IProgressMonitor progressMonitor) {
		String ecoreFilePath = ecoreFile.getFullPath().toString();
		URI uri = URI.createPlatformResourceURI(ecoreFilePath, false);
		ResourceSet resourceSet = new ResourceSetImpl();
		Resource ecoreResource = resourceSet.getResource(uri, true);
		if (ecoreFile != null) {
			progressMonitor.beginTask("ecoreValidating ...", 1);
			EObject rootPackage = ecoreResource.getContents().get(0);
			Diagnostician diagnostician = new Diagnostician();
			final Diagnostic diagnostic = diagnostician.validate(rootPackage);
			progressMonitor.worked(1);
			// display as markers
			if (progressMonitor.isCanceled()) {
				handleDiagnostic(ecoreFile, Diagnostic.CANCEL_INSTANCE);
			} else {
				handleDiagnostic(ecoreFile, diagnostic);
			}
			boolean res = diagnostic.getSeverity() < Diagnostic.ERROR;
			return res;
		}
		return false;
	}

	private static void handleDiagnostic(IFile ecoreFile, Diagnostic diagnostic) {

//		int severity = diagnostic.getSeverity();
		if (diagnostic.getSeverity() == Diagnostic.OK) {
			// TODO no error
		}

		deleteMarkers(ecoreFile, false, IResource.DEPTH_ZERO);
		for (Diagnostic childDiagnostic : diagnostic.getChildren()) {
			try {
				createMarkers(ecoreFile, childDiagnostic, null);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void createMarkers(IResource resource, Diagnostic diagnostic, Diagnostic parentDiagnostic)
			throws CoreException {
		if (resource != null && resource.exists()) {
			IMarker marker = resource.createMarker(getMarkerID());
			int severity = diagnostic.getSeverity();
			if (severity < Diagnostic.WARNING) {
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
			} else if (severity < Diagnostic.ERROR) {
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_WARNING);
			} else {
				marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			}

			String message = diagnostic.getMessage();
			if (message != null) {
				marker.setAttribute(IMarker.MESSAGE, message);
			}

		}
	}

	protected static String getMarkerID() {
		return "org.eclipse.core.resources.problemmarker";
	}

	protected static void deleteMarkers(IResource resource, boolean includeSubtypes, int depth) {
		if (resource != null && resource.exists()) {
			try {
				resource.deleteMarkers(getMarkerID(), includeSubtypes, depth);
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
