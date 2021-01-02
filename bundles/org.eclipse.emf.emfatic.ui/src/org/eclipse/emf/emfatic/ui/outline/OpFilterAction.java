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

package org.eclipse.emf.emfatic.ui.outline;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

public class OpFilterAction extends Action {
	private OpFilter _filter;
	private TreeViewer _treeViewer;

	public OpFilterAction(String string, int asCheckBox, OpFilter filter, TreeViewer treeViewer) {
		super(string, asCheckBox);
		_filter = filter;
		_treeViewer = treeViewer;
	}

	public void run() {
		if (isChecked()) {
			setText("Show Operations");
			setToolTipText("Show Operations");
			if (_filter == null) {
				_filter = new OpFilter();
			}
			_treeViewer.addFilter(_filter);
			// TODO avoid flicker
			_treeViewer.expandAll();
		} else {
			setText("Hide Operations");
			setToolTipText("Hide Operations");
			_treeViewer.removeFilter(_filter);
			// TODO avoid flicker
			_treeViewer.expandAll();
		}
	}

}
