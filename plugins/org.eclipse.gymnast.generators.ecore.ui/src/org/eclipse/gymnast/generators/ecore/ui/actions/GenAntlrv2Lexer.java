package org.eclipse.gymnast.generators.ecore.ui.actions;

import org.eclipse.gymnast.generators.ecore.cst.RootCS;
import org.eclipse.gymnast.generators.ecore.ui.Activator;
import org.eclipse.jface.action.IAction;

public class GenAntlrv2Lexer extends GenLexer {

	public GenAntlrv2Lexer() {
		// TODO Auto-generated constructor stub
	}

	public void run(IAction action) {
		if ((_file != null)) {
			RootCS wfc = RootCS.getWellFormednessChecker(_file);
			if (wfc != null) {
				String outFilePath = getLexerFilePath(_file, "g", wfc.languageName);
				Activator d = org.eclipse.gymnast.generators.ecore.ui.Activator.getDefault();
				String fileText = "";
				fileText += newLine + "class " + getLanguageName(_file) + "Lexer extends Lexer;" + newLine ;
				fileText += d.getLexerAntlrv2();
				d.writeStringToFile(outFilePath, fileText);
			}
		}
	}
}
