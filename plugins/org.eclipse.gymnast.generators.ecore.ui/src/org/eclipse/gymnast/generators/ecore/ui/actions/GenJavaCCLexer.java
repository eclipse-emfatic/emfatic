package org.eclipse.gymnast.generators.ecore.ui.actions;

import org.eclipse.gymnast.generators.ecore.cst.RootCS;
import org.eclipse.gymnast.generators.ecore.ui.Activator;
import org.eclipse.jface.action.IAction;

public class GenJavaCCLexer extends GenLexer {

	public GenJavaCCLexer() {
		// TODO Auto-generated constructor stub
	}

	public void run(IAction action) {
		if ((_file != null)) {
			RootCS wfc = RootCS.getWellFormednessChecker(_file);
			String outFilePath = getLexerFilePath(_file, "jj", wfc.languageName);
			Activator d = org.eclipse.gymnast.generators.ecore.ui.Activator.getDefault();
			String fileText = d.getLexerJavaCC();
			d.writeStringToFile(outFilePath, fileText);
		}
	}
	

}
