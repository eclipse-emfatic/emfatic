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

package org.eclipse.gymnast.runtime.ui.views.parsetree;

import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public class ParseTreeView extends ViewPart implements ISelectionListener, IParseTreeChangedListener {

	private TreeViewer _viewer;
	private ParseTreeViewerContentProvider _viewerContentProvider;
	private ParseTreeLabelProvider _labelProvider;
	private IParseTreeViewInput _input;
	private boolean _isDisposed = false;
	
	private static ParseTreeView _instance;
	public static ParseTreeView getInstance() {
		return _instance;
	}
	
	public ParseTreeView() {
		super();
		_viewerContentProvider = new ParseTreeViewerContentProvider();
		_labelProvider = new ParseTreeLabelProvider();
		
		if (_instance != null) {
			System.err.println("More than one ParseTreeView!");
		}
		_instance = this;
	}

	public void createPartControl(Composite parent) {
		_viewer = new TreeViewer(parent);
		_viewer.setContentProvider(_viewerContentProvider);
		_viewer.setLabelProvider(_labelProvider);
		getSite().getPage().addSelectionListener(this);
		getSite().setSelectionProvider(_viewer);
	}

	public void setFocus() {
	}
	
	private void changeInput(IParseTreeViewInput newInput) {
		if (_input == newInput) return;
		
		if (_input != null) {
			_input.removeParseTreeChangedListener(this);
		}
		
		_input = newInput;
		if (_input != null) {
			_input.addParseTreeChangedListener(this);
			
		}
		
		if (_viewer.getContentProvider() != null) {
		    _viewer.setInput(_input);
			_viewer.expandToLevel(2);
		}
	}

	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
	    
	    if (_isDisposed) return;
	    
		if (part instanceof IParseTreeViewInput) {
			
			IParseTreeViewInput newInput = (IParseTreeViewInput)part;
			changeInput(newInput);
		}
		else if (part == this) {
			if (selection instanceof IStructuredSelection) {
				IStructuredSelection sel = (IStructuredSelection)selection;
				Object el = sel.getFirstElement();
				if ((el != null) && (el instanceof ASTNode)) {
					if (_input != null) _input.selectNode((ASTNode)el);
				}
			}
		}
		else {
			changeInput(null);
		}

	}
	
	public void parseTreeChanged(final ASTNode[] changedNodes) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if ((changedNodes == null) || (changedNodes.length == 0)) {
				    if (!_viewer.getControl().isDisposed()) {
				        _viewer.refresh();
				    }
				}
				else {
					for (int i=0; i < changedNodes.length; i++) {
						ASTNode n = changedNodes[i];
						_viewer.refresh(n);
					}
				}
			}
		});
	}
	
	public void selectNode(ASTNode node) {
		if (node == null) {
			_viewer.setSelection(null);
			return;
		}

		_viewer.setSelection(new StructuredSelection(node), true);
		if (_input != null) _input.selectNode(node);
	}

	public void dispose() {
		if (_input != null) {
			_input.removeParseTreeChangedListener(this);
		}
		_instance = null;
		_isDisposed = true;
		
		super.dispose();
	}

}
