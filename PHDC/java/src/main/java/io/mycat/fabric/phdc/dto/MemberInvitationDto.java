package io.mycat.fabric.phdc.dto;

import java.util.Date;

public class MemberInvitationDto {
	
	private Integer invitationId;
	
	private Integer memberId;
	
	private Integer invitationMemberId;
	
	private String name;
	
	private byte status;
	
	private Date invitionDate;

	public Integer getInvitationId() {
		return invitationId;
	}

	public void setInvitationId(Integer invitationId) {
		this.invitationId = invitationId;
	}

	public Integer getMemberId() {
		return memberId;
	}

	public void setMemberId(Integer memberId) {
		this.memberId = memberId;
	}

	public Integer getInvitationMemberId() {
		return invitationMemberId;
	}

	public void setInvitationMemberId(Integer invitationMemberId) {
		this.invitationMemberId = invitationMemberId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public byte getStatus() {
		return status;
	}

	public void setStatus(byte status) {
		this.status = status;
	}

	public Date getInvitionDate() {
		return invitionDate;
	}

	public void setInvitionDate(Date invitionDate) {
		this.invitionDate = invitionDate;
	}
	
}
