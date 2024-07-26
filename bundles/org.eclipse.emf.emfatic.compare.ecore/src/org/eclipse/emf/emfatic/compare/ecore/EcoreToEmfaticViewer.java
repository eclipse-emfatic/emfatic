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
import org.eclipse.compare.ITypedElement;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.compare.structuremergeviewer.ICompareInputChangeListener;
import org.eclipse.emf.compare.ide.ui.internal.contentmergeviewer.accessor.AccessorAdapter;
import org.eclipse.emf.compare.rcp.ui.internal.contentmergeviewer.accessor.impl.MatchAccessor;
import org.eclipse.emf.compare.rcp.ui.mergeviewer.item.IMergeViewerItem;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.emfatic.core.generator.emfatic.Writer;
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.ITextStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

@SuppressWarnings("restriction")
public class EcoreToEmfaticViewer extends TextMergeViewer {

	protected static class DummyDocument extends AbstractDocument implements ITypedElement {

		protected static class StringStore implements ITextStore {
			private String store = "";

			@Override
			public char get(int offset) {
				return store.charAt(offset);
			}

			@Override
			public String get(int offset, int length) {
				return store.substring(offset, offset + length);
			}

			@Override
			public int getLength() {
				return store.length();
			}

			@Override
			public void replace(int offset, int length, String text) {
				// not allowed
			}

			@Override
			public void set(String text) {
				this.store = text;
			}
		}

		public DummyDocument() {
			setTextStore(new StringStore());
			setLineTracker(new DefaultLineTracker());
		}

		@Override
		public String getName() {
			return "Name";
		}

		@Override
		public Image getImage() {
			// no image
			return null;
		}

		@Override
		public String getType() {
			return "dummy";
		}
	}

	protected static class EmfaticCompareInput implements ICompareInput {

		private final ICompareInput originalInput;
		private final EObject left;
		private final EObject right;
		private final EObject ancestor;

		public EmfaticCompareInput(ICompareInput originalInput, EObject left, EObject right, EObject ancestor) {
			this.originalInput = originalInput;
			this.left = left;
			this.right = right;
			this.ancestor = ancestor;
		}

		@Override
		public String getName() {
			return originalInput.getName();
		}

		@Override
		public Image getImage() {
			return originalInput.getImage();
		}

		@Override
		public int getKind() {
			return originalInput.getKind();
		}

		@Override
		public ITypedElement getAncestor() {
			return ancestor == null ? null : getDocument(ancestor.eResource());
		}

		@Override
		public ITypedElement getLeft() {
			return left == null ? null : getDocument(left.eResource());
		}

		private ITypedElement getDocument(Resource eResource) {
			String text = null;
			for (EObject root : eResource.getContents()) {
				if (!(root instanceof EPackage)) {
					text = "Not an Ecore metamodel - cannot turn into Emfatic source";
				}
			}
			if (text == null) {
				Writer w = new Writer();
				text = w.write(eResource);
			}

			DummyDocument leftDoc = new DummyDocument();
			leftDoc.set(text);
			return leftDoc;
		}

		@Override
		public ITypedElement getRight() {
			return right == null ? null : getDocument(right.eResource());
		}

		@Override
		public void addCompareInputChangeListener(ICompareInputChangeListener listener) {
			// nothing to do
		}

		@Override
		public void removeCompareInputChangeListener(ICompareInputChangeListener listener) {
			// nothing to do
		}

		@Override
		public void copy(boolean leftToRight) {
			// do nothing
		}
	}

	public EcoreToEmfaticViewer(Composite parent, CompareConfiguration configuration) {
		super(parent, configuration);
	}

	@Override
	public void setInput(Object input) {
		if (input instanceof ICompareInput) {
			ICompareInput originalInput = (ICompareInput) input;
			EObject ancestor = getEObject(originalInput.getAncestor());
			EObject left = getEObject(originalInput.getLeft());
			EObject right = getEObject(originalInput.getRight());
			super.setInput(new EmfaticCompareInput(originalInput, left, right, ancestor));
		} else {
			super.setInput(input);
		}
	}

	@Override
	public String getTitle() {
		return "Emfatic Compare";
	}

	protected EObject getEObject(Object inputSide) {
		if (inputSide instanceof AccessorAdapter) {
			AccessorAdapter accAdapter = (AccessorAdapter) inputSide;
			Object target = accAdapter.getTarget();
			if (target instanceof MatchAccessor) {
				MatchAccessor ma = (MatchAccessor) target;
				IMergeViewerItem mergeItem = ma.getInitialItem();
				switch (mergeItem.getSide()) {
				case LEFT:
					return (EObject) mergeItem.getLeft();
				case RIGHT:
					return (EObject) mergeItem.getRight();
				case ANCESTOR:
					return (EObject) mergeItem.getAncestor();
				}
			}
		}

		return null;
	}
}
