package org.eclipse.gymnast.runtime.core.ast;

public class TokenInfo {
	
	private final String _text;
	private final int _offset;
	private final int _type;
	
	public TokenInfo(String text, int offset, int type) {
		_text = text;
		_offset = offset;
		_type = type;
	}
	
	public String getText() {
		return _text;
	}

	public int getOffset() {
		return _offset;
	}
	
	public int getType() {
		return _type;
	}
}
