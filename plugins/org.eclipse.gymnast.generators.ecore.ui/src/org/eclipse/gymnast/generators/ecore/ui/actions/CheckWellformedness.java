package org.eclipse.gymnast.generators.ecore.ui.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.gymnast.generators.ecore.convert.EcoreGeneratorFromGymnastGrammar;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public class CheckWellformedness implements IObjectActionDelegate {

	private IFile _file;
	
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
		if ((_file != null) ) {
			EcoreGeneratorFromGymnastGrammar generator = new EcoreGeneratorFromGymnastGrammar();
			generator.generate(_file, false, new NullProgressMonitor());
		}
	}

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}
	
	
}
