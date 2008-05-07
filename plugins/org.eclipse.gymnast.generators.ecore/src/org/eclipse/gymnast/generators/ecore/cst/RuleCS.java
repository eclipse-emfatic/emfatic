package org.eclipse.gymnast.generators.ecore.cst;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;

public abstract class RuleCS {

	protected RootCS c;

	public RuleCS(RootCS c2) {
		this.c = c2;
	}

	public String name;
	public List<String> attrs = new ArrayList<String>();

	protected String attrsToString() {
		if (attrs.isEmpty()) {
			return "";
		}
		String res = "[";
		for (Iterator<String> ai = attrs.iterator(); ai.hasNext();) {
			String a = ai.next();
			res += a + (ai.hasNext() ? ", " : "");
		}
		res += "]";
		return res;
	}

	public String getJavaFQNInGymnast() {
		String res = c.getOption_astPackageName() + "." + c.camelCase(name);
		return res;

	}

	public abstract Rule getRule();

	public abstract void addParseMessages(ParseContext parseContext);

}
