package org.eclipse.gymnast.prettyprinting;

import java.util.List;

public interface Box extends PrettyPrintable {

	String[] getRows();

	int getWidth();

	int getHeight();

	/**
	 * Updates and returns this object by appending as many rows as needed to
	 * reach the newHeight (leaves the object untouched in case the requested
	 * height would require shrinking this object).
	 */
	void growToHeight(int newHeight);

	/**
	 * Updates this object by appending to each row as many blanks as needed to
	 * reach the newWidth (leaves the object untouched in case the requested
	 * width would require narrowing this object).
	 */
	void growToWidth(int newWidth);

	String getRow(int i);

	/**
	 * Updates this object by appending (without any intervening blank space) as
	 * new rows those from the argument.
	 */
	public <PP extends PrettyPrintable> Box affixBelow(List<PP> newRightMost);

	/**
	 * Updates this object by appending (without any intervening blank space) as
	 * new rows those from the argument.
	 */
	public <PP extends PrettyPrintable> Box affixBelow(PP... newBottomMost);

	/**
	 * Updates and returns this object by appending to each row (without any
	 * intervening blank space) its counterpart from the argument.
	 */
	public <PP extends PrettyPrintable> Box affixRight(List<PP> newRightMost);

	/**
	 * Updates and returns this object by appending to each row (without any
	 * intervening blank space) its counterpart from the argument.
	 */
	public <PP extends PrettyPrintable> Box affixRight(PP... newRightMost);

	/**
	 * Updates and returns this object by appending to each row (without any
	 * intervening blank space) its counterpart from the argument.
	 */
	Box affixRight(String... newRightMost);

	/**
	 * Updates this object by appending (without any intervening blank space) as
	 * new rows those from the argument.
	 */
	Box affixBelow(String... newBottomMost);

	public int firstNonEmptyLine();

	public int lastNonEmptyLine();
}
