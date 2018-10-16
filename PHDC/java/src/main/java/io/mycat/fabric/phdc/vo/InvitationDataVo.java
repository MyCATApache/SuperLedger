package io.mycat.fabric.phdc.vo;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InvitationDataVo {

	@JsonProperty(value="Date")
	private String date;
	
	@JsonProperty(value="DepartName")
	private String departName;
	
	@JsonProperty(value="Result")
	private String result;

	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}
	
}
