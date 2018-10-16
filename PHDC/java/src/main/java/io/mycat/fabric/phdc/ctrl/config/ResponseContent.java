package io.mycat.fabric.phdc.ctrl.config;

public class ResponseContent  {
	
	/**
	 * 响应码
	 */
	private int code;
	/**
	 * 具体数据
	 */
	private Object data;
	/**
	 * 响应信息
	 */
	private String message;

	
	
	public ResponseContent() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResponseContent(int code, Object data, String message) {
		super();
		this.code = code;
		this.data = data;
		this.message = message;
	}
	
	public ResponseContent(ResponseCode code, Object data) {
		super();
		this.code = code.getValue();
		this.data = data;
		this.message = code.getMsg();
	}
	
	public static ResponseContent ok(Object data) {
		ResponseContent responseContent = new ResponseContent(ResponseCode.OK.getValue(), data, ResponseCode.OK.getMsg());
		return responseContent;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
