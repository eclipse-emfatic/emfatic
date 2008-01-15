/*******************************************************************************
 * Copyright (c) 2005, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.gymnast.examples.game.ui;

import org.eclipse.gymnast.runtime.core.outline.IOutlineBuilder;
import org.eclipse.gymnast.runtime.ui.outline.LDTOutlineConfiguration;

public class GameOutlineConfiguration extends LDTOutlineConfiguration {
    
    public GameOutlineConfiguration(GameEditor editor) {
        super(editor);
    }
    
    public IOutlineBuilder getOutlineBuilder() {
        return new GameOutlineBuilder();
    }

}
