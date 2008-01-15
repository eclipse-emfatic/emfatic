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

import org.eclipse.gymnast.runtime.ui.editor.LDTCodeScanner;
import org.eclipse.gymnast.runtime.ui.util.LDTColorProvider;
import org.eclipse.swt.SWT;

public class GameCodeScanner extends LDTCodeScanner {
    
    private static final String[] COMMANDS = {"fire", "move"};
    private static final String[] DIRECTIONS = {"left", "right"};
    
    public void initKeywords() {
        addKeywords(COMMANDS, LDTColorProvider.BLUE);
        addKeywords(DIRECTIONS, LDTColorProvider.DARK_GREEN, null, SWT.BOLD);
    }

}
