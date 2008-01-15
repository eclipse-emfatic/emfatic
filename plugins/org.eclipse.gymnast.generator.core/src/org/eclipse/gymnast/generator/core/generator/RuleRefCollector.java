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

package org.eclipse.gymnast.generator.core.generator;

import java.util.ArrayList;
import java.util.Hashtable;

import org.eclipse.gymnast.generator.core.ast.AltSeq;
import org.eclipse.gymnast.generator.core.ast.Alts;
import org.eclipse.gymnast.generator.core.ast.GymnastASTNodeVisitor;
import org.eclipse.gymnast.generator.core.ast.Rule;
import org.eclipse.gymnast.generator.core.ast.SimpleExpr;



/**
 * @author cjdaly@us.ibm.com
 * 
 */
public class RuleRefCollector extends GymnastASTNodeVisitor {

	private GeneratorContext _context;
	private Rule _rule;
	
	private ArrayList _labels;
	private Hashtable _labelToLabelData;
	private int _inheritedLabelCount = 0;
	
	private boolean _doingAltSeq = false;
	
	public RuleRefCollector(Rule rule, GeneratorContext context) {
		_context = context;
		_rule = rule;
		
		_labels = new ArrayList();
		_labelToLabelData = new Hashtable();
		
		visit(rule);
	}

	public int getCount() {
		return _labels.size();
	}
	public int getInheritedLabelCount() {
		return _inheritedLabelCount;
	}
	
	public String[] getLabels() {
		return (String[]) _labels.toArray(new String[_labels.size()]);
	}
	
	public boolean hasLabel(String label) {
		return _labelToLabelData.containsKey(label);
	}
	
	private LabelData getLabelData(String label) {
		return (LabelData)_labelToLabelData.get(label);
	}
	public String getType(String label) {
		return getLabelData(label).getType();
	}
	public boolean isAltSeq(String label) {
		return getLabelData(label).isAltSeq();
	}
	public boolean isInherited(String label) {
		return getLabelData(label).isInherited();
	}
	

	public boolean beginVisit(Alts alts) {
		// don't worry about labels for elements of Alts!
		return false;
	}
	
	public boolean beginVisit(AltSeq altSeq) {
		_doingAltSeq = true;
		return true;
	}
	public void endVisit(AltSeq altSeq) {
		_doingAltSeq = false;
	}

	public boolean beginVisit(SimpleExpr simpleExpr) {
		
		if (!ASTUtil.hasAttr(simpleExpr, "ignore")) {
			String type = simpleExpr.getValue().getText();
			String name = _context.getUtil().getLabel(simpleExpr);
			add(name, type);
		}
		
		return false;
	}
	
	private void add(String label, String type) {
		if (_labelToLabelData.containsKey(label)) {
			if (_context != null) {
				_context.getUtil().reportWarning("Label: " + label + " used more than once in rule: " + ASTUtil.getName(_rule));
			}
			return;
		}
		
		_labels.add(label);
		_labelToLabelData.put(label, new LabelData(type, _doingAltSeq, false));
	}
	
	void addInherited(String label, String type) {
		if (_labelToLabelData.containsKey(label)) {
			if (!getType(label).equals(type)) {
				String msg = "Label: " + label + " needed for more than one type in params for: " + ASTUtil.getName(_rule);
				throw new RuntimeException(msg);
			}
		}
		else {
			_labels.add(_inheritedLabelCount, label);
			_labelToLabelData.put(label, new LabelData(type, false, true));
			_inheritedLabelCount++;
		}
	}

	private class LabelData {
		String _type;
		boolean _isAltSeq;
		boolean _isInherited;
		
		LabelData(String type, boolean isAltSeq, boolean isInherited) {
			_type = type;
			_isAltSeq = isAltSeq;
			_isInherited = isInherited;
		}
		
		String getType() {
			return _type;
		}
		
		boolean isAltSeq() {
			return _isAltSeq;
		}
		
		boolean isInherited() {
			return _isInherited;
		}
	}
	
}
