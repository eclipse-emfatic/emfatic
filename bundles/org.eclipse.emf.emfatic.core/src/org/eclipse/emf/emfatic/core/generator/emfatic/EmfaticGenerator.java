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

package org.eclipse.emf.emfatic.core.generator.emfatic;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;

/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class EmfaticGenerator {
	
    public EmfaticGenerator()
    {
        _resourceSet = new ResourceSetImpl();
    }

    public void generate(IFile ecoreFile, IProgressMonitor monitor)
    {
        try
        {
            String ecoreFilePath = ecoreFile.getFullPath().toString();
            Resource ecoreResource = getResource(_resourceSet, ecoreFilePath);
            String emfaticFilePath = getEmfaticFilePath(ecoreFile);
            Writer writer = new Writer();
            String emfaticText = writer.write(ecoreResource, monitor, ecoreFile);
            writeEmfaticFile(emfaticFilePath, emfaticText.toString());
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    private Resource getResource(ResourceSet resourceSet, String filePath)
    {
        URI uri = URI.createPlatformResourceURI(filePath, false);
        Resource resource = resourceSet.getResource(uri, true);
        return resource;
    }

    private void writeEmfaticFile(String emfaticFilePath, String fileText)
        throws CoreException
    {
        IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
        IPath path = new Path(emfaticFilePath);
        IFile emfaticFile = workspaceRoot.getFile(path);
        java.io.InputStream in = new ByteArrayInputStream(fileText.getBytes());
        if(emfaticFile.exists())
            emfaticFile.setContents(in, true, false, null);
        else
            emfaticFile.create(in, true, null);
    }

    private String getEmfaticFilePath(IFile ecoreFile)
    {
        String ecoreFileExt = ecoreFile.getFileExtension();
        int extLen = ecoreFileExt != null ? ecoreFileExt.length() + 1 : 0;
        String ecoreFileName = ecoreFile.getName();
        String fileName = ecoreFileName.substring(0, ecoreFileName.length() - extLen);
        fileName = fileName + ".emf";
        String filePath = ecoreFile.getFullPath().removeLastSegments(1).append(fileName).toString();
        return filePath;
    }

    private ResourceSet _resourceSet;
}
