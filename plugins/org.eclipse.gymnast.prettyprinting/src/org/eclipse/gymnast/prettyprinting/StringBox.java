package org.eclipse.gymnast.prettyprinting;

import java.util.Arrays;
import java.util.List;

public class StringBox implements Box {

	private String[] rows;
	private int width;

	StringBox(String s) {
		if (s == null || s.equals("")) {
			rows = new String[] {};
			width = 0;
			return;
		}
		rows = new String[] { s };
		width = s.length();
	}

	public String[] getRows() {
		return rows;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return rows.length;
	}

	/**
	 * Updates this object by affixing at the bottom the argument, this object's
	 * width becomes the largest between the previous width and that of the
	 * argument.
	 */
	public void addRow(String s) {
		if (s == null ) {
			return;
		}
		if (s.length() > width) {
			growToWidth(s.length());
		}
		String[] c2 = new String[rows.length + 1];
		System.arraycopy(rows, 0, c2, 0, rows.length);
		c2[c2.length - 1] = s;
		rows = c2;
		width = s.length();
	}

	public void growToHeight(int newHeight) {
		if (newHeight < this.getHeight()) {
			return;
		}
		for (int i = this.getHeight(); i < newHeight; i++) {
			addRow(blankString(width));
		}
	}

	public void growToWidth(int newWidth) {
		if (newWidth <= this.getWidth()) {
			return;
		}
		if (rows.length == 0) {
			return;
			// rows = new String[] { "" };
		}
		for (int i = 0; i < rows.length; i++) {
			rows[i] += blankString(newWidth - width);
		}
		width = newWidth;
	}

	public static String blankString(int length) {
		char[] blanks = new char[length];
		Arrays.fill(blanks, ' ');
		return String.valueOf(blanks);
	}

	public String getRow(int i) {
		if (rows.length == 0) {
			return "";
		} else {
			return rows[i];
		}
	}

	@Override
	public String toString() {
		StringBuffer res = new StringBuffer();
		for (String r : rows) {
			res.append(r);
			res.append(newLine);
		}
		return res.toString();
	}

	private static final String newLine = System.getProperty("line.separator");

	public Box prettyPrint() {
		return this;
	}

	public <PP extends PrettyPrintable> Box affixBelow(PP... newBottomMost) {
		if (newBottomMost == null) {
			return this;
		}
		for (PP pp : newBottomMost) {
			affixBelowOne(pp);
		}
		return this;
	}

	private <PP extends PrettyPrintable> Box affixBelowOne(PP newBottomMost) {
		if (newBottomMost == null) {
			return this;
		}
		Box nbmBox = null;
		if (newBottomMost instanceof Box) {
			nbmBox = (Box) newBottomMost;
		} else {
			nbmBox = newBottomMost.prettyPrint();
		}
		if (nbmBox.getHeight() == 0) {
			return this; 
		}
		int newW = Math.max(this.getWidth(), nbmBox.getWidth());
		this.growToWidth(newW);
		nbmBox.growToWidth(newW);
		for (int i = 0; i < nbmBox.getHeight(); i++) {
			this.addRow(nbmBox.getRow(i));
		}
		return this;
	}

	public <PP extends PrettyPrintable> Box affixRight(List<PP> newRightMost) {
		if (newRightMost == null) {
			return this;
		}
		for (PP pp : newRightMost) {
			if (pp != null) {
				this.affixRightOnePP(pp);
			}
		}
		return this;
	}

	public <PP extends PrettyPrintable> Box affixRight(PP... newRightMost) {
		if (newRightMost == null) {
			return this;
		}
		for (PP pp : newRightMost) {
			if (pp != null) {
				this.affixRightOnePP(pp);
			}
		}
		return this;
	}

	private <PP extends PrettyPrintable> Box affixRightOnePP(PP newRightMost) {
		if (newRightMost == null) {
			return this;
		}
		Box nrmBox = null;
		if (newRightMost instanceof Box) {
			nrmBox = (Box) newRightMost;
		} else {
			nrmBox = newRightMost.prettyPrint();
		}
		if (nrmBox.getWidth() == 0) {
			return this; 
		}
		if (this.getHeight() > 1) {
			if (this.lastNonEmptyLine() > nrmBox.firstNonEmptyLine()) {
				affixBelowOne(nrmBox);
				return this;
			}
		}
		int newH = Math.max(this.getHeight(), nrmBox.getHeight());
		this.growToHeight(newH);
		nrmBox.growToHeight(newH);
		for (int i = 0; i < this.getHeight(); i++) {
			rows[i] += nrmBox.getRow(i);
		}
		width += nrmBox.getWidth();
		return this;
	}

	public <PP extends PrettyPrintable> Box affixBelow(List<PP> newRightMost) {
		if (newRightMost == null) {
			return null;
		}
		for (PP pp : newRightMost) {
			this.affixBelowOne(pp);
		}
		return this;
	}

	public Box affixBelow(String... newBottomMost) {
		Box res = this;
		for (String s : newBottomMost) {
			if (s != null) {
				res.affixBelow(BoxLanguage.b(s));
			}
		}
		return res;
	}

	public Box affixRight(String... newRightMost) {
		Box res = this;
		for (String s : newRightMost) {
			if (s != null) {
				res.affixRight(BoxLanguage.b(s));
			}
		}
		return res;
	}

	public int firstNonEmptyLine() {
		int res = 0;
		for (int i = 0; i < rows.length; i++) {
			if (!rows[i].trim().equals("")) {
				return i;
			}
		}
		return -1;
	}

	public int lastNonEmptyLine() {
		int res = 0;
		for (int i = rows.length - 1; i >= 0; i--) {
			if (!rows[i].trim().equals("")) {
				return i;
			}
		}
		return 0;
	}
}
