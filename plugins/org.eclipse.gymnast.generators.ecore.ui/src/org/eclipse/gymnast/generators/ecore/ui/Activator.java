package org.eclipse.gymnast.generators.ecore.ui;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.gymnast.generators.ecore.ui";

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}

	public String getLexerJavaCC() {
		return getLexerContents("GenericJavaCCLexer.jj");
	}

	public String getLexerAntlrv2() {
		return getLexerContents("GenericAntlrv2Lexer.g");
	}

	public String getLexerContents(String fileName) {
		URL url = getBundle().getEntry("lexers/" + fileName);
		try {
			String fileLocation = FileLocator.toFileURL(url).getPath();
			FileReader fr = new FileReader(fileLocation);
			BufferedReader br = new BufferedReader(fr);
			String contents = "";
			String tmp = "";
			while ((tmp = br.readLine()) != null) {
				contents += tmp + newLine;
			}
			br.close();
			return contents;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static boolean writeStringToFile(String outFilePath, String fileText) {
		try {
			IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
			IPath path = new Path(outFilePath);
			IFile outFile = workspaceRoot.getFile(path);
			java.io.InputStream in = new ByteArrayInputStream(fileText.getBytes());
			if (outFile.exists()) {
				outFile.setContents(in, true, false, null);
			} else {
				outFile.create(in, true, null);
			}
			return true;
		} catch (CoreException e) {
			e.printStackTrace();
			return false; 
		}
	}
	
	public void logError(String className, Exception exception) {
		this.getLog().
				log(
					new Status(IStatus.ERROR,
						this.getBundle().getSymbolicName(),
						IStatus.ERROR, "Error detected in class: " + className,
						exception)
				);
	}
		

	
	private static final String newLine = System.getProperty("line.separator");
}
