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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.emf.emfatic.core.generator.ecore.EcoreGenerator;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author cjdaly@us.ibm.com
 */
public class GenerateEcore implements IObjectActionDelegate {
	
	private IFile _file;
	volatile private GenerateEcoreJob _job;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
	
	public void selectionChanged(IAction action, ISelection selection) {
		_file = null;
		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection)selection;
			Object selElem = sel.getFirstElement();
			if (selElem instanceof IFile) {
				_file = (IFile)selElem; 
			}
		}
	}

	public void run(IAction action) {
		if ((_file != null) && (_job == null)) {
			_job = new GenerateEcoreJob(_file);
			// we might create a new file in the container
			_job.setRule(_file.getParent());
			_job.schedule();
		}
	}
	
	private class GenerateEcoreJob extends Job {
		private IFile _file;
		private GenerateEcoreJob(IFile file) {
			super("Generating Ecore Model for " + file.getName());
			_file=file;
		}
		
		protected IStatus run(IProgressMonitor monitor) {

		    EcoreGenerator generator = new EcoreGenerator();
			generator.generate(_file, monitor);
			
			_job = null;
			return Status.OK_STATUS;
		}
	}
	
}
