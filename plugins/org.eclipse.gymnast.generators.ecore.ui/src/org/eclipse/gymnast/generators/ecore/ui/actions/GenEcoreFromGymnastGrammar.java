package org.eclipse.gymnast.generators.ecore.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.gymnast.generators.ecore.convert.*;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class GenEcoreFromGymnastGrammar implements IObjectActionDelegate {

	private IFile _file;
	private GenerateEcoreFromGymnastGrammarJob _job;

	
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
			_job = new GenerateEcoreFromGymnastGrammarJob();
			_job.schedule();
		}
	}
	
	private class GenerateEcoreFromGymnastGrammarJob extends Job {
		private GenerateEcoreFromGymnastGrammarJob() {
			super("Generating Ecore Model for " + _file.getName());
		}
		
		protected IStatus run(IProgressMonitor monitor) {

		    EcoreGeneratorFromGymnastGrammar generator = new EcoreGeneratorFromGymnastGrammar();
			generator.generate(_file, monitor);
			
			_job = null;
			return Status.OK_STATUS;
		}
	}
	
}
