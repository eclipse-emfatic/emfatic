/*********************************************************************
 * Copyright (c) 2024 The University of York.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 **********************************************************************/
package org.eclipse.emf.emfatic.compare.ecore;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

public class EcoreToEmfaticViewerCreator implements IViewerCreator {

	@Override
	public Viewer createViewer(Composite parent, CompareConfiguration config) {
		return new EcoreToEmfaticViewer(parent, config);
	}

}
