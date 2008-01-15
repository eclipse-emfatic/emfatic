/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.core.generics.util;

import java.util.Collection;
import java.util.Set;

public class BiMultiMap<K, V> {
	private OneToManyMap<K, V> fst2snd = new OneToManyMap<K, V>();
	private OneToManyMap<V, K> snd2fst = new OneToManyMap<V, K>();

	/**
	 * only non-null args are stored
	 */
	public void put(K fst, V snd) {
		if (fst == null || snd == null) {
			return;
		}
		fst2snd.put(fst, snd);
		snd2fst.put(snd, fst);
	}

	/**
	 * never returns null but empty set
	 */
	public Set<V> get(K k) {
		return fst2snd.get(k);
	}

	/**
	 * never returns null but empty set
	 */
	public Set<K> getInv(V v) {
		return snd2fst.get(v);
	}

	public void clear() {
		fst2snd.clear();
		snd2fst.clear();
	}

	public Set<K> keySet() {
		return fst2snd.keySet();
	}

	public Collection<V> values() {
		return fst2snd.values();
	}

}
