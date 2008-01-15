/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.redsquiggles;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class EmfaticRedSquigglerDeltaVisitor implements IResourceDeltaVisitor {
	public boolean visit(IResourceDelta delta) {
		// only interested in changed resources (not added or removed)
		if (delta.getKind() != IResourceDelta.CHANGED)
			return true;
		// not interested in marker changes
		// if ((delta.getFlags() & IResourceDelta.MARKERS) == 0)
		// return true;
		// interested in content changes
		if ((delta.getFlags() & IResourceDelta.CONTENT) == 0)
			return true;
		IResource resource = delta.getResource();
		// only interested in files with the "txt" extension
		if (resource.getType() == IResource.FILE && "emf".equalsIgnoreCase(resource.getFileExtension())) {
			IFile emfFile = (IFile) resource;
			_job = new RedSquigglerJob(emfFile);
			_job.schedule();
		}
		return true;
	}

	private class RedSquigglerJob extends Job {

		protected IStatus run(IProgressMonitor monitor) {
			/*
			 * actually all red squiggles are placed in
			 * EmfaticParserDriver#parse(), thus here only sthg beyond that
			 * should go
			 */
			_job = null;
			return Status.OK_STATUS;
		}

		RedSquigglerJob(IFile emfFile) {
			super("Computing red squiggles (EmfaticRedSquiggler) for " + emfFile.getName());
			this.emfFile = emfFile;
		}

		private IFile emfFile;

	}

	private RedSquigglerJob _job;

}
