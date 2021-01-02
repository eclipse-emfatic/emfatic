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

package org.eclipse.gymnast.runtime.ui.outline;

import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.outline.OutlineNode;
import org.eclipse.gymnast.runtime.ui.editor.LDTEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.views.contentoutline.ContentOutlinePage;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class LDTContentOutlinePage extends ContentOutlinePage {
	
	private LDTEditor _editor;
	private IEditorInput _input;
	private boolean _isDisposed;
	
	public LDTContentOutlinePage(LDTEditor editor) {
		_editor = editor;
	}
	
	public void createControl(Composite parent) {
		super.createControl(parent);
		
		TreeViewer treeViewer = getTreeViewer();
		LDTOutlineConfiguration outlineConfiguration = _editor.getOutlineConfiguration();
		
		treeViewer.setContentProvider(outlineConfiguration.createOutlineContentProvider());
		treeViewer.setLabelProvider(outlineConfiguration.createOutlineLabelProvide());
		treeViewer.addSelectionChangedListener(this);
		
		if (_input != null) treeViewer.setInput(_input);
	}
	
	public void dispose() {
		_isDisposed = true;
		
		super.dispose();
	}
	
	public void selectionChanged(SelectionChangedEvent event) {
		super.selectionChanged(event);

		ISelection selection= event.getSelection();
		if (selection.isEmpty())
			_editor.resetHighlightRange();
		else {
			try {
				IStructuredSelection ssel = (IStructuredSelection)selection;
				OutlineNode node = (OutlineNode)ssel.getFirstElement();
				highlight(node.getASTNode(), true);
			}
			catch (Exception ex)
			{
				_editor.resetHighlightRange();	
			}
		}
	}
	
	private void highlight(ASTNode node, boolean moveCursor) {
		if (node != null){
			
			int offset = node.getRangeStart();
			int length = node.getRangeLength();
			
			if (offset == -1) {
				_editor.resetHighlightRange();
			} 
			else {
				_editor.setHighlightRange(offset, length, moveCursor);
				// _editor.selectAndReveal(offset, length);
			}
		}
	}
	
	public void select(ASTNode node) {
		if (node != null) {
			TreeViewer viewer = getTreeViewer();
			viewer.removeSelectionChangedListener(this);
			viewer.setSelection(new StructuredSelection(node));
			highlight(node, false);
			viewer.reveal(node);
			viewer.addSelectionChangedListener(this);
		}
	}
	
	public void setInput(IEditorInput input) {
		_input = input;
		update();
	}
	
	public void update() {
		if (_isDisposed) return;
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				updateHelper();
			}
		});
	}
	
	private void updateHelper() {
		if (_isDisposed) return;
		
		TreeViewer treeViewer = getTreeViewer();
		
		if (treeViewer != null) {
			Control control = treeViewer.getControl();
			if (control != null && !control.isDisposed()) {
				control.setRedraw(false);
				treeViewer.setInput(_input);
				treeViewer.expandAll();
				control.setRedraw(true);
			}
		}
	}

}
