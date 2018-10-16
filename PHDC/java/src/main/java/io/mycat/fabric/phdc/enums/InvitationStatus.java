package io.mycat.fabric.phdc.enums;

public enum InvitationStatus {

	GENERATE((byte)1),
	
	APPLY((byte)2),
	
	APPLY_FALSE((byte)3),
	
	APPLY_AGGRE((byte)4),
	
	ORDERING((byte)5);
	
	private byte value;
	
	InvitationStatus(byte value) {
		this.value = value;
	}
	
	public byte value() {
		return this.value;
	}
	
}
