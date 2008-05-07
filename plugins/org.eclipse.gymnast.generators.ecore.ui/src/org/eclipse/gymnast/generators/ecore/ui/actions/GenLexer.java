package org.eclipse.gymnast.generators.ecore.ui.actions;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.gymnast.generators.ecore.cst.GymnastCollector;
import org.eclipse.gymnast.generators.ecore.cst.RootCS;
import org.eclipse.gymnast.generators.ecore.walker.GymnastWalker;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;

public abstract class GenLexer implements IObjectActionDelegate {
	protected IFile _file;

	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	public void selectionChanged(IAction action, ISelection selection) {
		_file = null;

		if (selection instanceof IStructuredSelection) {
			IStructuredSelection sel = (IStructuredSelection) selection;
			Object selElem = sel.getFirstElement();
			if (selElem instanceof IFile) {
				_file = (IFile) selElem;
			}
		}
	}

	protected String getLexerFilePath(IFile astFile, String lexerFileExtension, String fileName) {
		String astFileExt = astFile.getFileExtension();
		int extLen = astFileExt != null ? astFileExt.length() + 1 : 0;
		String astFileName = astFile.getName();
		fileName = fileName + "Lexer." + lexerFileExtension;
		String filePath = astFile.getFullPath().removeLastSegments(1).append(fileName).toString();
		return filePath;
	}

	protected String getLanguageName(IFile astFile) {
		
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(astFile.getContents()));
			GymnastWalker<Object> gw = new GymnastWalker<Object>();
			RootCS wellFormednessChecker = new RootCS();
			GymnastCollector vCollect = new GymnastCollector(wellFormednessChecker);
			gw.walk(reader, vCollect);
			return vCollect.c.languageName;
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	protected static final String newLine = System.getProperty("line.separator");

}
