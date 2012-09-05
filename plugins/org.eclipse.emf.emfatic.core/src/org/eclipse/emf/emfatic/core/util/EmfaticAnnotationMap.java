/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/

package org.eclipse.emf.emfatic.core.util;

import java.util.Hashtable;

import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.util.ExtendedMetaData;
import org.eclipse.emf.emfatic.core.generator.ecore.EmfaticSemanticWarning;
import org.eclipse.emf.emfatic.core.generator.ecore.GenerationPhase;
import org.eclipse.emf.emfatic.core.lang.gen.ast.KeyEqualsValue;


/**
 * 
 * @author cjdaly@us.ibm.com
 */
public class EmfaticAnnotationMap {
    
    //
    // values for special @namespace faux annotation
    //
    
	public static final String EPACKAGE_NAMESPACE_ANNOTATION = "namespace";
	public static final String EPACKAGE_NSPREFIX_KEY = "prefix";
	public static final String EPACKAGE_NSURI_KEY = "uri";
    
    //
    //
	
	public static final String EMFATIC_ANNOTATION_MAP_LABEL = "EmfaticAnnotationMap";
	public static final String EMFATIC_ANNOTATION_MAP_NS_URI = "http://www.eclipse.org/emf/2004/" + EMFATIC_ANNOTATION_MAP_LABEL;
	
	
	// TODO: copied from EcoreUtil protected member.  Is this in a public place?
	private static final String GEN_MODEL_PACKAGE_LABEL = "GenModel";
	private static final String GEN_MODEL_PACKAGE_NS_URI = "http://www.eclipse.org/emf/2002/GenModel";

	private static final String ECORE_PACKAGE_LABEL = "Ecore";
	
	private static final String EXTENDED_META_DATA_LABEL = "ExtendedMetaData";
	
	//
	//
	//
	
    private Hashtable<String, String> _keyToSourceURI;
    private Hashtable<String, SourceURIDetails> _sourceURIToDetails;
    
    public EmfaticAnnotationMap() {
        _keyToSourceURI = new Hashtable<String, String>();
        _sourceURIToDetails = new Hashtable<String, SourceURIDetails>();
        
        initPredefinedMappings();
    }
    
    private void initPredefinedMappings() {
        addMapping(GEN_MODEL_PACKAGE_LABEL, GEN_MODEL_PACKAGE_NS_URI);
        addMapping(ECORE_PACKAGE_LABEL, EcorePackage.eNS_URI);
        addMapping(EXTENDED_META_DATA_LABEL, ExtendedMetaData.ANNOTATION_URI);
        addMapping(EMFATIC_ANNOTATION_MAP_LABEL, EMFATIC_ANNOTATION_MAP_NS_URI);
    }
    
    public String addMapping(String labelDecl, String sourceURI) {
        return addMapping(labelDecl, sourceURI, null, null);
    }
    
    public String addMapping(String labelDecl, String sourceURI, KeyEqualsValue keyEqualsValue, GenerationPhase reporter) {
        LabelMapping labelMapping = new LabelMapping(labelDecl);
        if (!labelMapping.isWellFormed()) {
            reportAddMappingProblem("label not well formed!", keyEqualsValue, reporter);
            return null;
        }
        String label = labelMapping.getLabel();
        String key = labelMapping.getKey();
        
        if (sourceURI == null) sourceURI = label;
        
        boolean newSourceURI = false;
        
        SourceURIDetails details = (SourceURIDetails)_sourceURIToDetails.get(sourceURI);
        if (details == null) {
            details = new SourceURIDetails(sourceURI);
            newSourceURI = true;
        }

        if (details.hasMapping(labelMapping.getImplicitKeyCount())) {
            String m2 = details.getMapping(labelMapping.getImplicitKeyCount()).toString();
            reportAddMappingProblem("mapping already exists: " + m2, keyEqualsValue, reporter);
            return null;
        }
                
        String sourceURICheck = (String)_keyToSourceURI.get(key);
        if (sourceURICheck == null) {
            _keyToSourceURI.put(key, sourceURI);
        }
        else if (!sourceURICheck.equals(sourceURI)) {
            reportAddMappingProblem("label aready in use for sourceURI: " + sourceURICheck, keyEqualsValue, reporter);
            return null;
        }
        
        if (newSourceURI) {
            _sourceURIToDetails.put(sourceURI, details);
        }
        
        details.addMapping(labelMapping);
        
        return label;
    }
    
    private void reportAddMappingProblem(String message, KeyEqualsValue keyEqualsValue, GenerationPhase reporter) {
        if ((keyEqualsValue != null) && (reporter != null)) {
            reporter.logWarning(new EmfaticSemanticWarning.AnnotationMappingProblem(keyEqualsValue, message));
        }
    }
    
    public String getLabelForSourceURI(String sourceURI, int paramCount) {
        SourceURIDetails details = (SourceURIDetails)_sourceURIToDetails.get(sourceURI);
        if (details == null) return null;

        LabelMapping labelMapping = details.getMapping(paramCount);
        if (labelMapping != null) return labelMapping.getLabel();
        
        // See if there's a "default" label
        labelMapping = details.getMapping(0);
        if (labelMapping != null) return labelMapping.getLabel();
        
        return null;
    }
    
    public String mapSourceURI(String literalSourceUriText) {
        return (String)_keyToSourceURI.get(literalSourceUriText.toLowerCase());
    }
    
    public String getImplicitKeyName(String sourceURI, int paramIndex, int totalParamCount) {
        SourceURIDetails details = (SourceURIDetails)_sourceURIToDetails.get(sourceURI);
        if (details == null) return null;
        LabelMapping labelMapping = details.getMapping(totalParamCount);
        if (labelMapping == null) return null;
        return labelMapping.getImplicitKey(paramIndex);
    }
    
    private static class SourceURIDetails {
        private String _sourceURI;
        private Hashtable<Integer, LabelMapping> _keyCountToLabelMapping;
        
        public SourceURIDetails(String sourceURI) {
            _sourceURI = sourceURI;
            _keyCountToLabelMapping = new Hashtable<Integer, LabelMapping>();
        }
        
        public String getSourceURI() {
            return _sourceURI;
        }
        
        public LabelMapping addMapping(LabelMapping mapping) {
            Integer keyCount = new Integer(mapping.getImplicitKeyCount());
            _keyCountToLabelMapping.put(keyCount, mapping);
            return mapping;
        }
        
        public LabelMapping getMapping(int keyCount) {
            Integer keyCountInt = new Integer(keyCount);
            LabelMapping mapping = (LabelMapping)_keyCountToLabelMapping.get(keyCountInt);
            return mapping;
        }
        
        public boolean hasMapping(String label, int keyCount) {
            LabelMapping mapping = getMapping(keyCount);
            if (mapping == null) return false;
            return mapping.getKey().equals(label.toLowerCase());
        }
        
        public boolean hasMapping(int keyCount) {
            LabelMapping mapping = getMapping(keyCount);
            return mapping != null;
        }
    }
    
    private static class LabelMapping {
        private String _origLabel;
        private String _label;
        private String[] _implicitKeys;
        private boolean _isWellFormed = true;
        
        public LabelMapping(String label) {
            _origLabel = label;
            try {
                init(label);
            }
            catch (Exception ex) {
                _isWellFormed = false;
            }
        }
        private void init(String label) {
            int lParenPos = label.indexOf('(');
            if (lParenPos == -1) {
                _label = label;
                _implicitKeys = new String[0];
            }
            else {
                String implicitKeyText = label.substring(lParenPos+1, label.length());
                _label = label.substring(0, lParenPos);
                implicitKeyText = implicitKeyText.replace(')', ' ');
                _implicitKeys = implicitKeyText.split(",");
                for (int i = 0; i < _implicitKeys.length; i++) {
                    String implicitKey = _implicitKeys[i].trim();
                    if ((implicitKey == null) || ("".equals(implicitKey))) {
                        _implicitKeys[i] = "_undef" + Integer.toString(i);
                        _isWellFormed = false;
                    }
                    else _implicitKeys[i] = implicitKey;
                }
            }
        }
        
        public boolean isWellFormed() {
            return _isWellFormed;
        }
        
        public String getLabel() {
            return _label;
        }
        
        public String getKey() {
            return _label.toLowerCase();
        }
        
        public int getImplicitKeyCount() {
            return _implicitKeys.length;
        }
        
        public String getImplicitKey(int index) {
            if (index >= _implicitKeys.length) return null;
            else return _implicitKeys[index];
        }
        
        public String toString() {
            return _origLabel;
        }
    }

}
