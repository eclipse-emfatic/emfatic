/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Miguel Garcia (Tech Univ Hamburg-Harburg) - customization for EMF Generics
 *******************************************************************************/

package org.eclipse.emf.emfatic.core.generics.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class OneToOneMap<K, V> {

	private Map<K, V> fst2snd = new HashMap<K, V>();
	private Map<V, K> snd2fst = new HashMap<V, K>();

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

	public V get(K k) {
		return fst2snd.get(k);
	}

	public K getInv(V v) {
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
