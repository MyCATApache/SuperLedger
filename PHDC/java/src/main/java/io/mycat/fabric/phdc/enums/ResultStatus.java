package io.mycat.fabric.phdc.enums;

public enum ResultStatus {

	NOT_RESULT((byte)1),
	
	HAS_RESULT((byte)2);
	
	private byte value;
	
	ResultStatus(byte value) {
		this.value = value;
	}
	
	public byte value() {
		return this.value;
	}
	
}
