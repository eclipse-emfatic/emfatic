package org.eclipse.gymnast.prettyprinting;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BoxLanguage {

	public static Box b(String s) {
		if (s == null) {
			return new StringBox("");
		}
		return new StringBox(s);
	}

	public static Box emptyBox() {
		return new StringBox("");
	}

	public static Box blank(int width, int height) {
		Box res = new StringBox("");
		res.growToWidth(width);
		res.growToHeight(height);
		return res;
	}

	private static Box h(Box... bs) {
		return h(1, bs);
	}

	private static Box v(Box... bs) {
		return v(0, 0, bs);
	}

	private static List<Box> flatten(List... pps) {
		List<Box> args = new ArrayList<Box>();
		for (List list : pps) {
			if (list != null) {
				for (Object o : list) {
					if (o instanceof Box) {
						args.add((Box) o);
					} else if (o instanceof PrettyPrintable) {
						PrettyPrintable pp = (PrettyPrintable) o;
						args.add(pp.prettyPrint());
					} else if (o instanceof List) {
						List lst = (List) o;
						args.addAll(flatten(lst));
					} else if (o != null) {
						args.add(BoxLanguage.b(o.toString()));
					}
				}
			}
		}
		return args;
	}

	/*
	 * public static <PP extends PrettyPrintable> Box h(PP pa, List<PP>... pps) {
	 * List<PrettyPrintable> args = new ArrayList<PrettyPrintable>(); if (pa !=
	 * null) { args.add(pa); } args.addAll(flatten(pps)); return h(args); }
	 */

	static Box[] toBoxArray(List... pps) {
		List<Box> f = flatten(pps);
		Box[] args = new Box[f.size()];
		for (int i = 0; i < args.length; i++) {
			args[i] = f.get(i);
		}
		return args;
	}

	private static Box v(int vs, Box... bs) {
		return v(vs, 0, bs);
	}

	public static <OO extends Object> Box v(int vs, int is, OO... pps) {
		return v(vs, is, toBoxArray(pps));
	}

	private static <PP extends PrettyPrintable> Box[] toBoxArray(Object[] pps) {
		List<Box> res = new ArrayList<Box>();
		for (int i = 0; i < pps.length; i++) {
			if (pps[i] != null) {
				if (pps[i] instanceof Box) {
					res.add((Box) pps[i]);
				} else if (pps[i] instanceof PrettyPrintable) {
					PrettyPrintable pp = (PrettyPrintable) pps[i];
					res.add(pp.prettyPrint());
				} else if (pps[i] instanceof List) {
					List lst = (List) pps[i];
					res.addAll(flatten(lst));
				} else if (pps[i] != null) {
					String s = pps[i].toString();
					res.add(BoxLanguage.b(s));
				}
			}
		}
		Box[] boxes = new Box[res.size()];
		boxes = res.toArray(boxes);
		return boxes;
	}

	public static <PP extends PrettyPrintable> Box h(int hs, Object... pps) {
		return h(hs, toBoxArray(pps));
	}

	public static Box h(int hs, List... pps) {
		return h(hs, toBoxArray(pps));
	}

	private static Box h(int hs, Box... bs) {
		if (bs == null) {
			return null;
		}
		if (bs.length == 0) {
			return null;
		}
		Box res = bs[0];
		for (int i = 1; i < bs.length; i++) {
			res = h2(hs, res, bs[i]);
		}
		return res;
	}

	private static <B extends Box> Box[] skipNulls(B... bs) {
		List<Box> res = new ArrayList<Box>();
		for (int i = 0; i < bs.length; i++) {
			if (bs[i] != null) {
				res.add(bs[i]);
			}
		}
		Box[] boxes = new Box[res.size()];
		boxes = res.toArray(boxes);
		return boxes;
	}

	private static <B extends Box> Box v(int vs, int is, B... bs) {
		if (bs == null) {
			return null;
		}
		Box[] bsNonNull = skipNulls(bs);
		if (bsNonNull.length == 0) {
			return new StringBox("");
		}
		if (bsNonNull.length == 1) {
			return bs[0];
		}
		int newW = Math.max(bsNonNull[0].getWidth(), bsNonNull[bsNonNull.length - 1].getWidth());
		for (int i = 1; i < bsNonNull.length - 1; i++) {
			newW = Math.max(newW, is + bsNonNull[i].getWidth());
		}
		for (int i = 0; i < bsNonNull.length; i++) {
			bsNonNull[i].growToWidth(newW);
		}
		Box res = bsNonNull[0];
		for (int i = 1; i < bsNonNull.length - 1; i++) {
			Box indentation = blank(is, bsNonNull[i].getHeight());
			Box indented = h2(0, indentation, bsNonNull[i]);
			res = v2(vs, res, indented);
		}
		res = v2(vs, res, bsNonNull[bsNonNull.length - 1]);
		return res;
	}

	private static <B extends Box> Box v2(int vs, B a, B b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		int maxW = Math.max(a.getWidth(), b.getWidth());
		a.growToWidth(maxW);
		b.growToWidth(maxW);
		StringBox res = new StringBox(a.getRow(0));
		for (int i = 1; i < a.getHeight(); i++) {
			res.addRow(a.getRow(i));
		}
		String filler = StringBox.blankString(maxW);
		for (int i = 0; i < vs; i++) {
			res.addRow(filler);
		}
		for (int i = 0; i < b.getHeight(); i++) {
			res.addRow(b.getRow(i));
		}
		return res;
	}

	private static Box h2(int hs, Box a, Box b) {
		if (a == null) {
			return b;
		}
		if (b == null) {
			return a;
		}
		if (a.getHeight() > 1) {
			if (a.lastNonEmptyLine() > b.firstNonEmptyLine()) {
				Box res = v2(0, a, b);
				return res; 
			}
		}
		int maxH = Math.max(a.getHeight(), b.getHeight());
		a.growToHeight(maxH);
		b.growToHeight(maxH);
		String filler = StringBox.blankString(hs);
		StringBox res = new StringBox(a.getRow(0) + filler + b.getRow(0));
		for (int i = 1; i < maxH; i++) {
			res.addRow(a.getRow(i) + filler + b.getRow(i));
		}
		return res;
	}

	private static List<Box> b(List<String> names) {
		List<Box> res = new ArrayList<Box>();
		for (String s : names) {
			if (s != null) {
				res.add(b(s));
			}
		}
		return res;
	}

	/**
	 * Qualified names are parsed by Grammar2Ecore into two separate (usually
	 * string) lists: one for 'names' and another (whose length is just one
	 * element shorter) for (non-constant) 'separators'). At pretty-print time,
	 * it's necessary to pick one element of each list in sequence to build a
	 * Box. That's what this method does.
	 */
	private static <PP extends PrettyPrintable> Box interleaveHPrettyPrintables(List<PP> names, List<PP> separators,
			int beforeSep, int afterSep) {
		if (names == null) {
			return null;
		}
		if (names.isEmpty()) {
			return null;
		}
		List<Box> namesBoxes = flatten(names);
		List<Box> separatorBoxes = flatten(separators);
		Box boxBeforeSep = new StringBox("");
		boxBeforeSep.growToWidth(beforeSep);
		Box boxAfterSep = new StringBox("");
		boxAfterSep.growToWidth(afterSep);
		assert (!namesBoxes.isEmpty() ? (separatorBoxes.size() == namesBoxes.size() - 1) : true);
		Box res = new StringBox("");
		java.util.Iterator<Box> iternamesBoxes = namesBoxes.iterator();
		java.util.Iterator<Box> iternameSeparators = separatorBoxes.iterator();
		while (iternamesBoxes.hasNext()) {
			res.affixRight(iternamesBoxes.next());
			if (iternameSeparators.hasNext()) {
				res.affixRight(boxBeforeSep);
				res.affixRight(iternameSeparators.next());
				if (iternamesBoxes.hasNext()) {
					res.affixRight(boxAfterSep);
				}
			}
		}
		return res;
	}

	private static <PP extends PrettyPrintable> Box interleaveVPrettyPrintables(List<PP> names, List<PP> separators,
			int beforeSep, int afterSep) {
		if (names == null) {
			return null;
		}
		if (names.isEmpty()) {
			return null;
		}
		List<Box> namesBoxes = flatten(names);
		List<Box> separatorBoxes = flatten(separators);
		Box boxBeforeSep = new StringBox("");
		boxBeforeSep.growToHeight(beforeSep);
		Box boxAfterSep = new StringBox("");
		boxAfterSep.growToHeight(afterSep);
		assert (!namesBoxes.isEmpty() ? (separatorBoxes.size() == namesBoxes.size() - 1) : true);
		Box res = new StringBox("");
		java.util.Iterator<Box> iternamesBoxes = namesBoxes.iterator();
		java.util.Iterator<Box> iternameSeparators = separatorBoxes.iterator();
		while (iternamesBoxes.hasNext()) {
			res.affixBelow(iternamesBoxes.next());
			if (iternameSeparators.hasNext()) {
				res.affixBelow(boxBeforeSep);
				res.affixBelow(iternameSeparators.next());
				if (iternamesBoxes.hasNext()) {
					res.affixBelow(boxAfterSep);
				}
			}
		}
		return res;
	}

	private static List<PrettyPrintable> objectToPP(List<? extends Object> lst) {
		List<PrettyPrintable> res = new ArrayList<PrettyPrintable>();
		for (Object o : lst) {
			if (o instanceof PrettyPrintable) {
				res.add((PrettyPrintable) o);
			} else if (o != null) {
				res.add(BoxLanguage.b(o.toString()));
			}
		}
		return res;
	}

	public static Box interleaveH(List<? extends Object> names, List<? extends Object> separators, int beforeSep,
			int afterSep) {
		List<PrettyPrintable> ppNames = objectToPP(names);
		List<PrettyPrintable> ppSeps = objectToPP(separators);
		return interleaveHPrettyPrintables(ppNames, ppSeps, beforeSep, afterSep);
	}

	public static Box interleaveV(List<? extends Object> names, List<? extends Object> separators, int beforeSep,
			int afterSep) {
		List<PrettyPrintable> ppNames = objectToPP(names);
		List<PrettyPrintable> ppSeps = objectToPP(separators);
		return interleaveVPrettyPrintables(ppNames, ppSeps, beforeSep, afterSep);
	}

	/**
	 * Qualified names are parsed by Grammar2Ecore into two separate (usually
	 * string) lists: one for 'names' and another (whose length is just one
	 * element shorter) for (non-constant) 'separators'). At pretty-print time,
	 * it's necessary to pick one element of each list in sequence to build a
	 * Box. That's what this method does.
	 */
	public static <PP extends PrettyPrintable> Box interleaveHSepConstant(Object separator, List names, int beforeSep,
			int afterSep) {
		if (names == null) {
			return null;
		}
		if (names.isEmpty()) {
			return null;
		}
		List<Box> namesBoxes = flatten(names);
		List<Box> separatorsBoxes = new ArrayList<Box>();
		Box sB = null;
		if (separator instanceof Box) {
			sB = (Box) separator;
		} else if (separator instanceof PrettyPrintable) {
			PrettyPrintable pp = (PrettyPrintable) separator;
			sB = pp.prettyPrint();
		} else if (separator != null) {
			sB = BoxLanguage.b(separator.toString());
		}
		separatorsBoxes.addAll(Collections.nCopies(namesBoxes.size() - 1, sB));
		return interleaveHPrettyPrintables(namesBoxes, separatorsBoxes, beforeSep, afterSep);
	}

	public static <PP extends PrettyPrintable> Box interleaveVSepConstant(Object separator, List names, int beforeSep,
			int afterSep) {
		if (names == null) {
			return null;
		}
		if (names.isEmpty()) {
			return null;
		}
		List<Box> namesBoxes = flatten(names);
		List<Box> separatorsBoxes = new ArrayList<Box>();
		Box sB = null;
		if (separator instanceof Box) {
			sB = (Box) separator;
		} else if (separator instanceof PrettyPrintable) {
			PrettyPrintable pp = (PrettyPrintable) separator;
			sB = pp.prettyPrint();
		} else if (separator != null) {
			sB = BoxLanguage.b(separator.toString());
		}
		separatorsBoxes.addAll(Collections.nCopies(namesBoxes.size() - 1, sB));
		return interleaveVPrettyPrintables(namesBoxes, separatorsBoxes, beforeSep, afterSep);
	}
}
