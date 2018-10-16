package io.mycat.fabric.phdc.ctrl.config;

public enum ResponseCode {
	OK(1000,"ok"),
	TEST(1,"test"),
	TOKEN_INVALID(1001,"访问令牌失效,请登录"),
	FAILED(500,"请求失败")
	;
	
	private int value;
	
	private String msg;

	private ResponseCode(int value, String msg) {
		this.value = value;
		this.msg = msg;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}
	
	
	
}
