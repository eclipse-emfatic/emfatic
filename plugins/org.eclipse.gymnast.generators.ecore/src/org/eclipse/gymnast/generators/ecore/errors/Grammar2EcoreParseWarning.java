package org.eclipse.gymnast.generators.ecore.errors;

import org.eclipse.gymnast.runtime.core.parser.ParseWarning;

public abstract class Grammar2EcoreParseWarning extends ParseWarning {

	public static class GeneralWarning extends Grammar2EcoreParseWarning {

		public GeneralWarning(String msg, int rangeStart, int rangeLength) {
			init(msg, rangeStart, rangeLength);
		}
	}

}
