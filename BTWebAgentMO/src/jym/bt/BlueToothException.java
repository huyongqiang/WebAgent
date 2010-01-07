// CatfoOD 2009-10-4 ионГ06:39:30

package jym.bt;

import java.io.IOException;

public class BlueToothException extends IOException {
	public BlueToothException() {
		super();
	}
	
	public BlueToothException(String text) {
		super(text);
	}
	
	public BlueToothException(Exception e) {
		super(e.getMessage());
	}
}
