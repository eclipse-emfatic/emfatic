/*******************************************************************************
 * Copyright (c) 2004, 2008 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Lucas Bigeardel - fix Generics warns
 *******************************************************************************/

package org.eclipse.emf.emfatic.core.util;

import java.util.Hashtable;

public class EmfaticKeywords
{

    public EmfaticKeywords()
    {
    }

    private static void initKeywords()
    {
        _allKeywordsTable = new Hashtable<String, String>();
        _normalKeywordsTable = new Hashtable<String, String>();
        _specialKeywordsTable = new Hashtable<String, String>();
        _hoverTextTable = new Hashtable<String, String>();
        add("abstract", "EClass.isAbstract() == <T>");
        add("attr", true);
        add("class", true);
        add("datatype", true);
        add("derived", "EStructuralFeature.isDerived() == <T>");
        add("enum", true);
        add("extends");
        add("super");
        add("false");
        add("id", "EAttribute.isID() == <T>");
        add("import", true);
        add("interface", true, "EClass.isInterface() == <T>");
        add("mapentry", true);
        add("op", true);
        add("ordered", "ETypedElement.isOrdered() == <T>");
        add("package", true);
        add("readonly", "EStructuralFeature.isChangeable() == <F>");
        add("ref", true, "EReference.isContainment() == <F>");
        add("resolve", "EReference.isResolveProxies() == <T>");
        add("throws");
        add("transient", "EStructuralFeature.isTransient() == <T>");
        add("true");
        add("unique", "ETypedElement.isUnique() == <T>");
        add("unsettable", "EStructuralFeature.isUnsettable() == <T>");
        add("val", true, "EReference.isContainment() == <T>");
        add("void");
        add("volatile", "EStructuralFeature.isVolatile() == <T>");
        _allKeywords = (String[])_allKeywordsTable.values().toArray(new String[_allKeywordsTable.size()]);
        _normalKeywords = (String[])_normalKeywordsTable.values().toArray(new String[_normalKeywordsTable.size()]);
        _specialKeywords = (String[])_specialKeywordsTable.values().toArray(new String[_specialKeywordsTable.size()]);
        _isInitialized = true;
    }

    private static void add(String keyword)
    {
        add(keyword, false, null);
    }

    private static void add(String keyword, boolean isSpecial)
    {
        add(keyword, isSpecial, null);
    }

    private static void add(String keyword, String hoverText)
    {
        add(keyword, false, hoverText);
    }

    private static void add(String keyword, boolean isSpecial, String hoverText)
    {
        _allKeywordsTable.put(keyword, keyword);
        if(isSpecial)
            _specialKeywordsTable.put(keyword, keyword);
        else
            _normalKeywordsTable.put(keyword, keyword);
        if(hoverText != null)
            _hoverTextTable.put(keyword, hoverText);
    }

    public static String[] GetKeywords()
    {
        if(!_isInitialized)
            initKeywords();
        return (String[])_allKeywords.clone();
    }

    public static String[] GetNormalKeywords()
    {
        if(!_isInitialized)
            initKeywords();
        return (String[])_normalKeywords.clone();
    }

    public static String[] GetSpecialKeywords()
    {
        if(!_isInitialized)
            initKeywords();
        return (String[])_specialKeywords.clone();
    }

    public static boolean IsKeyword(String id)
    {
        if(!_isInitialized)
            initKeywords();
        return _allKeywordsTable.containsKey(id);
    }

    public static String GetHoverText(String id)
    {
        if(!_isInitialized)
            initKeywords();
        return (String)_hoverTextTable.get(id);
    }

    public static String Escape(String id)
    {
        if(!_isInitialized)
            initKeywords();
        String ids[] = id.split("\\.");
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < ids.length; i++)
        {
            if(IsKeyword(ids[i]))
                sb.append(KEYWORD_ESCAPE_STRING + ids[i]);
            else
                sb.append(ids[i]);
            if(i + 1 < ids.length)
                sb.append(".");
        }

        return sb.toString();
    }

    public static final char KEYWORD_ESCAPE_CHAR = 126;
    public static final String KEYWORD_ESCAPE_STRING = String.valueOf('~');
    private static boolean _isInitialized = false;
    
    private static Hashtable<String, String> _allKeywordsTable;
    private static Hashtable<String, String> _normalKeywordsTable;
    private static Hashtable<String, String> _specialKeywordsTable;
    private static Hashtable<String, String> _hoverTextTable;
    
    private static String _allKeywords[];
    private static String _normalKeywords[];
    private static String _specialKeywords[];

}
