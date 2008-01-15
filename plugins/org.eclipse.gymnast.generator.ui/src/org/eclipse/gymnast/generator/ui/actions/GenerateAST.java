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

package org.eclipse.gymnast.generator.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.gymnast.generator.core.generator.Generator;
import org.eclipse.gymnast.runtime.core.util.IReporter;
import org.eclipse.gymnast.runtime.ui.views.console.ReporterConsole;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class GenerateAST implements IObjectActionDelegate {
	
	private IFile _file;
	private GenerateASTJob _job;
	
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}
	
	public void selectionChanged(IAction action, ISelection selection) {		
		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection)selection;
			Object selElem = sel.getFirstElement();
			if (selElem instanceof IFile) {
				_file = (IFile)selElem;
			}
		}
	}

	public void run(IAction action) {
		if (_job == null) {
			_job = new GenerateASTJob();
			_job.schedule();
		}
	}
	
	private class GenerateASTJob extends Job {
		
		private IReporter _reporter;
		
		private GenerateASTJob() {
			super("Generating AST for " + _file.getName());
			
			_reporter = new ReporterConsole("Generate AST");
		}
		
		protected IStatus run(IProgressMonitor monitor) {
			
			setAutoBuilding(false);
			
			Generator generator = new Generator();
			generator.generate(_file, monitor, _reporter);
			monitor.done();
			_job = null;
			
			setAutoBuilding(true);
			
			return Status.OK_STATUS;
		}
		
		private void setAutoBuilding(boolean value) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			IWorkspaceDescription desc = workspace.getDescription();
			desc.setAutoBuilding(value);

			try {
				workspace.setDescription(desc);
			}
			catch (Exception ex) {
				_reporter.reportError(ex);
			}
		}
		
	}
	
}
