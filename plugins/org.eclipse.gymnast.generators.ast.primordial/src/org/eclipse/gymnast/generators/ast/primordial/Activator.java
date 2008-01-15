package org.eclipse.gymnast.generators.ast.primordial;

import java.io.IOException;

import org.eclipse.gymnast.generators.ast.primordial.templates.GymnastContextType;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class Activator extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.eclipse.gymnast.generators.ast.primordial";

	// The shared instance
	private static Activator plugin;
	
	/**
	 * The constructor
	 */
	public Activator() {
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugins#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.runtime.Plugin#stop(org.osgi.framework.BundleContext)
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
	
	private static final String CUSTOM_TEMPLATES_KEY = "org.eclipse.gymnast.generators.ast.primordial.preferences.templates";
	
	public TemplateStore getTemplateStore() {
		
	    TemplateStore templateStore = new ContributionTemplateStore(getContextTypeRegistry(), getDefault()
				.getPreferenceStore(), CUSTOM_TEMPLATES_KEY);
	    
		try {
			templateStore.load();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return templateStore;
	}

	private ContextTypeRegistry _contextTypeRegistry;
	public ContextTypeRegistry getContextTypeRegistry() {
		if (_contextTypeRegistry == null) {
			_contextTypeRegistry = new ContextTypeRegistry();
			_contextTypeRegistry.addContextType(GymnastContextType.JavaMethod);
			_contextTypeRegistry.addContextType(GymnastContextType.JavaCompUnit);
			_contextTypeRegistry.addContextType(GymnastContextType.JavaRuleCompUnit);
		}
		return _contextTypeRegistry;
	}
	
}
