package io.mycat.fabric.phdc.dto;

import java.util.Date;

public class ReserveExamResultDetail {

	private Integer resultId;
	
	private Date checkDate;
	
	private byte gender;
	
	private int age;
	
	private String dictionaryName;
	
	private String result;
	
	private String checkDoctor;
	
	private String summary;
	
	private String departDoctor;
	
	private byte status;

	public Integer getAge() {
		return age;
	}

	public void setAge(Integer age) {
		this.age = age;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public int getResultId() {
		return resultId;
	}

	public void setResultId(int resultId) {
		this.resultId = resultId;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public byte getGender() {
		return gender;
	}

	public void setGender(byte gender) {
		this.gender = gender;
	}

	public String getDictionaryName() {
		return dictionaryName;
	}

	public void setDictionaryName(String dictionaryName) {
		this.dictionaryName = dictionaryName;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public String getCheckDoctor() {
		return checkDoctor;
	}

	public void setCheckDoctor(String checkDoctor) {
		this.checkDoctor = checkDoctor;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getDepartDoctor() {
		return departDoctor;
	}

	public void setDepartDoctor(String departDoctor) {
		this.departDoctor = departDoctor;
	}
	
}
