/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.emf.emfatic.core.generator.ecore;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;
import org.eclipse.emf.emfatic.core.lang.gen.parser.EmfaticParserDriver;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;
import org.eclipse.gymnast.runtime.core.parser.ParseMessage;
import org.eclipse.gymnast.runtime.core.util.MarkerUtil;

/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class EcoreGenerator {
	
	public EcoreGenerator() {
	}

	public void generate(IFile emfFile, IProgressMonitor monitor) {
		generate(emfFile, true, monitor);
	}

	public void generate(IFile emfFile, boolean writeEcore, IProgressMonitor monitor) {
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(emfFile.getContents()));
			EmfaticParserDriver parser = new EmfaticParserDriver(URI.createPlatformResourceURI(emfFile.getFullPath().toPortableString(), true));
			ParseContext parseContext = parser.parse(reader);
			String filePath = getEcoreFilePath(emfFile);
			Resource resource = createResource(filePath, true);
			Builder builder = new Builder();
			builder.build(parseContext, resource, monitor);
			if (!parseContext.hasErrors()) {
				Connector connector = new Connector(builder);
				connector.connect(parseContext, resource, monitor);
			}
			MarkerUtil.updateMarkers(emfFile, parseContext);

			boolean showStopper = false;
			/*
			 * we let ecores to be written as long as they contain only errors
			 * detected by EcoreValidatorDiagnostic, as such errors can be
			 * solved with Sample Ecore Editor
			 */
			for (ParseMessage pm : parseContext.getMessages()) {
				showStopper |= !(pm instanceof EmfaticSemanticWarning.EcoreValidatorDiagnostic);
			}

			if (!showStopper && writeEcore)
				resource.save(null);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void generate(File emfFile, boolean writeEcore) throws Exception {
		NullProgressMonitor monitor = new NullProgressMonitor();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(emfFile)));
		EmfaticParserDriver parser = new EmfaticParserDriver(URI.createFileURI(emfFile.getAbsolutePath()));
		ParseContext parseContext = parser.parse(reader);
		String filePath = emfFile.getAbsolutePath().replaceAll("\\.emf$", ".ecore");
		Resource resource = createResource(filePath, false);
		Builder builder = new Builder();
		builder.build(parseContext, resource, monitor);

		if (!parseContext.hasErrors() && writeEcore) {
			Connector connector = new Connector(builder);
			connector.connect(parseContext, resource, monitor);
			resource.save(null);
		}
		else {
			String message = parseContext.getMessages()[0].getMessage();
			message = message.replaceAll("\\r|\\n", " ");
			message = message.trim();
			throw new Exception("Syntax error: " + message);
		}
	}
	
	private String getEcoreFilePath(IFile emfFile) {
		String emfFileExt = emfFile.getFileExtension();
		int extLen = emfFileExt != null ? emfFileExt.length() + 1 : 0;
		String emfFileName = emfFile.getName();
		String fileName = emfFileName.substring(0, emfFileName.length() - extLen);
		fileName = fileName + ".ecore";
		String filePath = emfFile.getFullPath().removeLastSegments(1).append(fileName).toString();
		return filePath;
	}

	private Resource createResource(String filePath, boolean inWorkspace) {
		ResourceSet resourceSet = new ResourceSetImpl();
		URI uri = null;
		if (!inWorkspace) {
			resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecore", new XMIResourceFactoryImpl());
			uri = URI.createFileURI(filePath);
		}
		else {
			uri = URI.createPlatformResourceURI(filePath, false);
		}
		Resource resource = resourceSet.createResource(uri);
		return resource;
	}
}
