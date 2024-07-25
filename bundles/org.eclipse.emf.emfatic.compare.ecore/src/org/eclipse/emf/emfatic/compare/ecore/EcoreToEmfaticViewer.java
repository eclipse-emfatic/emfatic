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
import org.eclipse.jface.text.AbstractDocument;
import org.eclipse.jface.text.DefaultLineTracker;
import org.eclipse.jface.text.ITextStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;

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
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getType() {
			return "dummy";
		}
	}

	protected static class EmfaticCompareInput implements ICompareInput {
		private final ICompareInput originalInput;

		protected EmfaticCompareInput(ICompareInput originalInput) {
			this.originalInput = originalInput;
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
			// TODO no three way support for now
			return null;
		}

		@Override
		public ITypedElement getLeft() {
			// TODO compute Emfatic text from the left element
			DummyDocument leftDoc = new DummyDocument();
			leftDoc.set("common\nleft");
			return leftDoc;
		}

		@Override
		public ITypedElement getRight() {
			// TODO compute Emfatic text from the right element
			DummyDocument rightDoc = new DummyDocument();
			rightDoc.set("common\nright");
			return rightDoc;
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
		ICompareInput originalInput = (ICompareInput) input;
		super.setInput(new EmfaticCompareInput(originalInput));
	}

	@Override
	public String getTitle() {
		return "Emfatic Compare";
	}
	
}
