/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lucas Bigeardel - fix EPL header and innapropriate tag
 *******************************************************************************/

package org.eclipse.emf.emfatic.ui;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.emf.emfatic.ui.redsquiggles.EmfaticRedSquiggler;
import org.eclipse.emf.emfatic.ui.templates.EmfaticContextType;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;


/**
 * 
 */
public class EmfaticUIPlugin extends AbstractUIPlugin {

	public EmfaticUIPlugin() {
		plugin = this;
		try {
			resourceBundle = ResourceBundle.getBundle("org.eclipse.emf.emfatic.ui.EmfaticUIPluginResources");
		} catch (MissingResourceException _ex) {
			resourceBundle = null;
		}

		redSquiggler = new EmfaticRedSquiggler();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(redSquiggler, IResourceChangeEvent.POST_CHANGE);
		// TODO use IWorkspace.removeResourceChangeListener on plugin shutdown
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	public void stop(BundleContext context) throws Exception {
		super.stop(context);
	}

	public static EmfaticUIPlugin getDefault() {
		return plugin;
	}

	public static String getResourceString(String key) {
		ResourceBundle bundle = getDefault().getResourceBundle();
		try {
			return bundle == null ? key : bundle.getString(key);
		} catch (MissingResourceException _ex) {
			return key;
		}
	}

	public ResourceBundle getResourceBundle() {
		return resourceBundle;
	}

	/**
	 * Returns this plugin's template store.
	 * 
	 * @return the template store of this plug-in instance
	 */
	public TemplateStore getEmfaticTemplateStore() {
		if (emfaticTemplateStore == null) {
			emfaticTemplateStore = new ContributionTemplateStore(getEmfaticContextTypeRegistry(), getPreferenceStore(),
					CUSTOM_TEMPLATES_EMFATIC_KEY);
			try {
				emfaticTemplateStore.load();
			} catch (IOException e) {
				// e.printStackTrace();
				EmfaticUIPlugin.log("Loading Emfatic template store", e);
				throw new RuntimeException(e);
			}
		}
		return emfaticTemplateStore;
	}

	/**
	 * Returns this plugin's LaTeX context type registry.
	 * 
	 * @return the context type registry for this plug-in instance
	 */
	public ContextTypeRegistry getEmfaticContextTypeRegistry() {
		if (emfaticTypeRegistry == null) {
			// create an configure the contexts available in the template editor
			emfaticTypeRegistry = new ContributionContextTypeRegistry();
			emfaticTypeRegistry.addContextType(new EmfaticContextType());
		}
		return emfaticTypeRegistry;
	}

	/**
	 * Display a message in the Eclipse's Error Log. This is equivalent to
	 * calling <code>log(msg, t, IStatus.ERROR)</code>.
	 * 
	 * @param msg
	 *            error message to display in error log
	 * @param t
	 *            exception
	 */
	public static void log(String msg, Throwable t) {
		log(msg, t, IStatus.ERROR);
	}

	/**
	 * Display a message in the Eclipse's Error Log. Used by e.g. the project
	 * creation wizard.
	 * 
	 * @param msg
	 *            error message
	 * @param t
	 *            exception
	 * @param level
	 *            one of the error levels defined in the <code>IStatus</code>
	 *            -interface
	 */
	public static void log(String msg, Throwable t, int level) {
		IStatus stat = new Status(level, getPluginId(), level, msg, t);
		getDefault().getLog().log(stat);
	}

	/**
	 * Returns the name of the plugin.
	 * 
	 * Used by project creation wizard.
	 * 
	 * @return unique id of this plugin
	 */
	public static String getPluginId() {
		return getDefault().getBundle().getSymbolicName();
	}

	/**
	 * Return an image from the plugin's icons-directory.
	 * 
	 * @param name
	 *            name of the icon
	 * @return the icon as an image object
	 */
	public static Image getImage(String name) {
		return getDefault().getCachedImage(name);
	}

	/**
	 * Cache the image if it is found.
	 * 
	 * @param key
	 *            name of the image
	 * @return image from the cache or from disk, null if image is not found in
	 *         either
	 */
	protected Image getCachedImage(String key) {
		if (key == null) {
			return null;
		}

		Image g = (Image) imageRegistry.get(key);
		if (g != null) {
			return g;
		}

		ImageDescriptor d = ImageDescriptor.createFromURL(getBundle().getEntry("icons/" + key + ".gif"));
		if (d == null) {
			return null;
		}

		// we want null instead of default missing image
		if (d.equals(ImageDescriptor.getMissingImageDescriptor())) {
			return null;
		}

		g = d.createImage();
		imageRegistry.put(key, g);
		return g;
	}

	/**
	 * Returns the image descriptor for the given image (from eclipse help)
	 * 
	 * @param name
	 *            Name of the iamge
	 * @return The corresponding image descriptor or
	 *         <code>MissingImageDescriptor</code> if none is found
	 */
	public static ImageDescriptor getImageDescriptor(String name) {
		String iconPath = "icons/";
		try {
			URL installURL = getDefault().getBundle().getEntry("/");
			URL url = new URL(installURL, iconPath + name + ".gif");
			return ImageDescriptor.createFromURL(url);
		} catch (MalformedURLException e) {
			// should not happen
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static EmfaticUIPlugin plugin;
	private ResourceBundle resourceBundle;
	private IResourceChangeListener redSquiggler;
	private TemplateStore emfaticTemplateStore;
	private ContributionContextTypeRegistry emfaticTypeRegistry = null;

	// Key to store custom templates.
	private static final String CUSTOM_TEMPLATES_EMFATIC_KEY = "EmfaticTemplates";

	// cache for icons
	private HashMap<String, Image> imageRegistry = new HashMap<String, Image>();
}
