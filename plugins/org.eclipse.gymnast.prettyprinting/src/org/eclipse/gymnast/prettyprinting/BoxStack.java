package org.eclipse.gymnast.prettyprinting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ListIterator;

/**
 * It's intentional for this object to keep an alias to the stack List, so as to
 * allow a prettyPrint method to push into it for processing with commands
 * invoked on this object.
 * 
 */
public class BoxStack {

	public List<Object> stack = new ArrayList<Object>();

	public Box h(int hs, int howMany) {
		List<Object> args = removeNTop(howMany);
		Box res = BoxLanguage.h(hs, args);
		stack.add(res);
		return res;
	}

	private List<Object> removeNTop(int howMany) {
		List<Object> removed = new ArrayList<Object>();
		int fromIndex = stack.size() - howMany;
		int toIndex = stack.size();
		removed.addAll(stack.subList(fromIndex, toIndex));
		for (int i = 0; i < howMany; i++) {
			stack.remove(stack.size() - 1);
		}
		return removed;
	}

	public Box interleaveH(int beforeSep, int afterSep) {

		List<Object> names = removeNTop(1);
		List<Object> separators = removeNTop(1);
		Box res = BoxLanguage.interleaveH(names, separators, beforeSep, afterSep);
		stack.add(res);
		return res;
	}

	public Box interleaveHSepConstant(int beforeSep, int afterSep) {
		List<Object> names = removeNTop(1);
		List<Object> separator = removeNTop(1);
		Box res = BoxLanguage.interleaveHSepConstant(separator, names, beforeSep, afterSep);
		stack.add(res);
		return res;
	}

	public Box interleaveV(int beforeSep, int afterSep) {

		List<Object> names = removeNTop(1);
		List<Object> separators = removeNTop(1);
		Box res = BoxLanguage.interleaveV(names, separators, beforeSep, afterSep);
		stack.add(res);
		return res;
	}

	public Box interleaveVSepConstant(int beforeSep, int afterSep) {
		List<Object> names = removeNTop(1);
		List<Object> separator = removeNTop(1);
		Box res = BoxLanguage.interleaveVSepConstant(separator, names, beforeSep, afterSep);
		stack.add(res);
		return res;
	}

	public Box peekTop() {
		Object t = stack.get(stack.size() - 1);
		if (t instanceof Box) {
			return (Box) t;
		} else if (t instanceof PrettyPrintable) {
			PrettyPrintable pp = (PrettyPrintable) t;
			return pp.prettyPrint();
		} else if (t != null) {
			String s = t.toString();
			return BoxLanguage.b(s);
		}
		return BoxLanguage.emptyBox();
	}

	public Box v(int vs, int is, int howMany) {
		List<Object> args = removeNTop(howMany);
		Box res = BoxLanguage.v(vs, is, args);
		stack.add(res);
		return res;
	}

	public void add(Object newTop) {
		stack.add(newTop);
	}

	public Box packHorizUpToWidth(int minWidth, int hs, int howMany) {
		assert minWidth > 0;
		List<Object> args = removeNTop(howMany);
		Box[] bs = BoxLanguage.toBoxArray(args);
		List<Box> boxes = Arrays.asList(bs);
		Box runningRes = BoxLanguage.emptyBox();
		int i = 0;
		while (i < boxes.size()) {
			List<Box> horizBatch = new ArrayList<Box>();
			int widthSoFar = 0;
			Box current = boxes.get(i);
			horizBatch.add(current);
			widthSoFar += current.getWidth();
			i++;
			while ((widthSoFar < minWidth) && (current.getHeight() <= 1) && (i < boxes.size())) {
				current = boxes.get(i);
				horizBatch.add(current);
				widthSoFar += current.getWidth();
				i++;
			}
			Box boxForBatch = BoxLanguage.h(hs, horizBatch); 
			runningRes.affixBelow(boxForBatch);
		}
		stack.add(runningRes);
		return runningRes;
	}
}
