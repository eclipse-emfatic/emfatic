package org.eclipse.emf.emfatic.core;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceFactoryImpl;

public class EmfaticResourceFactory extends ResourceFactoryImpl {
	
	public org.eclipse.emf.ecore.resource.Resource createResource(URI uri) {
		return new EmfaticResource(uri);
	};
	
}
