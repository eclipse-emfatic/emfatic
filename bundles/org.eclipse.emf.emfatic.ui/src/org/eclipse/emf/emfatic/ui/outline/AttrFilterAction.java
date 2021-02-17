/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui.outline;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;

public class AttrFilterAction extends Action {
	private AttrFilter _filter;
	private TreeViewer _treeViewer;

	public AttrFilterAction(String string, int asCheckBox, AttrFilter filter, TreeViewer treeViewer) {
		super(string, asCheckBox);
		_filter = filter;
		_treeViewer = treeViewer;
	}

	public void run() {
		if (isChecked()) {
			setText("Show Attributes");
			setToolTipText("Show Attributes");
			if (_filter == null) {
				_filter = new AttrFilter();
			}
			_treeViewer.addFilter(_filter);
			// TODO avoid flicker
			_treeViewer.expandAll();
		} else {
			setText("Hide Attributes");
			setToolTipText("Hide Attributes");
			_treeViewer.removeFilter(_filter);
			// TODO avoid flicker
			_treeViewer.expandAll();
		}
	}

}
