package org.eclipse.emf.emfatic.core;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.impl.ResourceImpl;
import org.eclipse.emf.emfatic.core.generator.ecore.Builder;
import org.eclipse.emf.emfatic.core.generator.ecore.Connector;
import org.eclipse.emf.emfatic.core.generator.emfatic.Writer;
import org.eclipse.emf.emfatic.core.lang.gen.parser.EmfaticParserDriver;
import org.eclipse.gymnast.runtime.core.parser.ParseContext;

public class EmfaticResource extends ResourceImpl {
	
	protected ParseContext parseContext = null;
	
	public EmfaticResource(URI uri) {
		super(uri);
	}
	
	@Override
	protected void doLoad(InputStream inputStream, Map<?, ?> options) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
		EmfaticParserDriver parser = new EmfaticParserDriver(uri);
		parseContext = parser.parse(reader);
		Builder builder = new Builder();
		NullProgressMonitor monitor = new NullProgressMonitor();
		builder.build(parseContext, this, monitor);
		if (!parseContext.hasErrors()) {
			Connector connector = new Connector(builder);
			connector.connect(parseContext, this, monitor);
		}
	}
	
	@Override
	protected void doSave(OutputStream outputStream, Map<?, ?> options) throws IOException {
		Writer writer = new Writer();
        String emfaticText = writer.write(this, new NullProgressMonitor(), null);
		outputStream.write(emfaticText.getBytes());
		outputStream.flush();
		outputStream.close();
	}
	
	public ParseContext getParseContext() {
		return parseContext;
	}
	
}
