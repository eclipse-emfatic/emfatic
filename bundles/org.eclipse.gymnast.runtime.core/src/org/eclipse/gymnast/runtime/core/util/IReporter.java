/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.runtime.core.util;

/**
 * @author cjdaly@us.ibm.com
 *
 */
public interface IReporter {
	
	void report(String message);
	void report(String message, int level);
	
	void reportWarning(String message);

	void reportError(String message);
	void reportError(Exception ex);
	
}
