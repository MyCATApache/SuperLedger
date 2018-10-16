package io.mycat.fabric.phdc.dto;

import java.util.Date;

public class MemberInstResultDto {

	private Integer resultId;
	
	private String dictionaryName;
	
	private Date checkDate;
	
	private Integer dictionaryId;
	
	private Integer departId;
	
	private String result;
	
	private String summary;
	
	private String departName;
	
	private String departDoctor;
	
	private String checkDoctor;

	
	public Integer getDictionaryId() {
		return dictionaryId;
	}

	public void setDictionaryId(Integer dictionaryId) {
		this.dictionaryId = dictionaryId;
	}

	public Integer getDepartId() {
		return departId;
	}

	public void setDepartId(Integer departId) {
		this.departId = departId;
	}

	public Date getCheckDate() {
		return checkDate;
	}

	public void setCheckDate(Date checkDate) {
		this.checkDate = checkDate;
	}

	public String getDepartName() {
		return departName;
	}

	public void setDepartName(String departName) {
		this.departName = departName;
	}

	public Integer getResultId() {
		return resultId;
	}

	public void setResultId(Integer resultId) {
		this.resultId = resultId;
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

	public String getCheckDoctor() {
		return checkDoctor;
	}

	public void setCheckDoctor(String checkDoctor) {
		this.checkDoctor = checkDoctor;
	}
	
}
