/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.runtime.ui.editor;

import java.util.ArrayList;

import org.eclipse.core.resources.IFile;
import org.eclipse.gymnast.runtime.core.ast.ASTNode;
import org.eclipse.gymnast.runtime.core.outline.IOutlineBuilder;
import org.eclipse.gymnast.runtime.core.outline.OutlineNode;
import org.eclipse.gymnast.runtime.core.parser.IParser;
import org.eclipse.gymnast.runtime.ui.outline.LDTContentOutlinePage;
import org.eclipse.gymnast.runtime.ui.outline.LDTOutlineConfiguration;
import org.eclipse.gymnast.runtime.ui.views.parsetree.IParseTreeChangedListener;
import org.eclipse.gymnast.runtime.ui.views.parsetree.IParseTreeViewInput;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextInputListener;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;


/**
 * @author cjdaly@us.ibm.com
 *
 */
public abstract class LDTEditor extends TextEditor implements IParseTreeViewInput {
	
	private LDTSourceViewerConfiguration _sourceViewerConfiguration;
	private LDTOutlineConfiguration _outlineConfiguration;
	
	private ASTNode _parseRoot;
	private LDTContentOutlinePage _outlinePage;
	private SelectionChangedListener _selectionChangedListener;
	private TextInputListener _textInputListener;
	
	public LDTEditor() {
		super();
		
	}
	
	protected void initializeEditor() {
		super.initializeEditor();
		
		_sourceViewerConfiguration = createSourceViewerConfiguration();
		setSourceViewerConfiguration(_sourceViewerConfiguration);
		
		_outlineConfiguration = createOutlineConfiguration();
	}
	
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		
		_selectionChangedListener = createSelectionChangedListener();
		_textInputListener = createTextInputListener();
	}

	public void dispose() {
		super.dispose();
		
		if (_textInputListener != null) {
		    _textInputListener.dispose();
		    _textInputListener = null;
		}
		
		if (_selectionChangedListener != null) {
		    _selectionChangedListener.dispose();
		    _selectionChangedListener = null;
		}
	}
	
	protected abstract LDTSourceViewerConfiguration createSourceViewerConfiguration();
	
	protected LDTOutlineConfiguration createOutlineConfiguration() {
		return new LDTOutlineConfiguration(this);
	}
	
	public LDTOutlineConfiguration getOutlineConfiguration() {
		return _outlineConfiguration;
	}
	
	public IParser getParser() {
		return _sourceViewerConfiguration.getParser();
	}
	
	public IOutlineBuilder getOutlineBuilder() {
		return _outlineConfiguration.getOutlineBuilder();
	}
	
	public IFile getFile() {
		if (getEditorInput() instanceof IFileEditorInput) {
			IFileEditorInput input = (IFileEditorInput)getEditorInput();
			return input.getFile();
		}
		return null;
	}
	
	void setParseRoot(ASTNode parseRoot) {
		_parseRoot = parseRoot;
		
		for (int i = 0; i<_parseTreeChangedListeners.size(); i++) {
			IParseTreeChangedListener listener = (IParseTreeChangedListener)_parseTreeChangedListeners.get(i);
			listener.parseTreeChanged(new ASTNode[] {_parseRoot});
		}
		
		if (_outlinePage != null) {
			_outlinePage.update();
		}
	}
	
	public Object getAdapter(Class adapter) {
		if (IContentOutlinePage.class.equals(adapter)) {
			if (_outlinePage == null) {
					if (getOutlineBuilder() != null) {
					_outlinePage = _outlineConfiguration.createContentOutlinePage();
					IEditorInput input = getEditorInput();
					if (input != null) {
						_outlinePage.setInput(input);
					}
				}
			}
			return _outlinePage;
		}
		return super.getAdapter(adapter);
	}
	
	public OutlineNode[] getOutlineElements() {

		IOutlineBuilder outlineBuilder = getOutlineBuilder();
		OutlineNode[] elements = null;
		
		if (outlineBuilder != null) {
			try {
				elements = outlineBuilder.buildOutline(_parseRoot);
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		
		return elements;
	}
	
	
	//
	// IParseTreeViewInput implementation
	//
	private ArrayList<IParseTreeChangedListener> _parseTreeChangedListeners = new ArrayList<IParseTreeChangedListener>();
	//
	public void addParseTreeChangedListener(IParseTreeChangedListener listener) {
		_parseTreeChangedListeners.add(listener);
	}
	//
	public void removeParseTreeChangedListener(IParseTreeChangedListener listener) {
		_parseTreeChangedListeners.remove(listener);
	}
	//	
	public ASTNode getParseRoot() {
		return _parseRoot;
	}
	//
	public void selectNode(ASTNode node) {
		if (node != null) {
			selectAndReveal(node.getRangeStart(), node.getRangeLength());
		}
	}
	//
	public ASTNode getNodeAtCursor() {
		int cursorPos = getSourceViewer().getTextWidget().getCaretOffset();
		if (_parseRoot != null) {
			return _parseRoot.getNodeAt(cursorPos, 0);
		}
		return null;
	}
	
	//
	//
	//
	
	protected SelectionChangedListener createSelectionChangedListener() {
	    return null;
	    // ISelectionProvider selectionProvider = getSelectionProvider();
		// return new SelectionChangedListener(selectionProvider);
	}
	
	protected class SelectionChangedListener implements ISelectionChangedListener {
	    
	    private ISelectionProvider _selectionProvider;
		
		public SelectionChangedListener(ISelectionProvider selectionProvider) {
		    _selectionProvider = selectionProvider;
			_selectionProvider.addSelectionChangedListener(this);
		}
		
		public void dispose() {
			_selectionProvider.removeSelectionChangedListener(this);
		}

		public void selectionChanged(SelectionChangedEvent event) {
		}
	}
	
	//
	//
	//
	
	protected TextInputListener createTextInputListener() {
	    return null;
	    // ISourceViewer sourceViewer = getSourceViewer();
	    // return new TextInputListener(sourceViewer);
	}
	
	protected class TextInputListener implements ITextInputListener {
	    
	    private ISourceViewer _sourceViewer;
	    
	    public TextInputListener(ISourceViewer sourceViewer) {
	        _sourceViewer = sourceViewer;
	        _sourceViewer.addTextInputListener(this);
	    }
	    
	    public void dispose() {
	        _sourceViewer.removeTextInputListener(this);
	    }
	    
	    public void inputDocumentAboutToBeChanged(IDocument oldInput, IDocument newInput) {
        }
	    
	    public void inputDocumentChanged(IDocument oldInput, IDocument newInput) {
        }
	}
}
