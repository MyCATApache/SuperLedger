package io.mycat.fabric.phdc.enums;

public enum ReserveStatus {

	RESERVE((byte)1),
	
	ACCEPT_RESERVE((byte)2),
	
	CANCALE_RESERVE((byte)3);
	
	private byte value;
	
	ReserveStatus(byte value) {
		this.value = value;
	}
	
	public byte value() {
		return this.value;
	}
	
}
