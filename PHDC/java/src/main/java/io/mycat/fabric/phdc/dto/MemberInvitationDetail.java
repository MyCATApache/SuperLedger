package io.mycat.fabric.phdc.dto;

import java.util.Date;
import java.util.List;

public class MemberInvitationDetail {
	
	private String name;
	
	private String mobile;

	private Date invitationTime;
	
	private List<MemberInvitationDetailItem> items;
	
	public Date getInvitationTime() {
		return invitationTime;
	}

	public void setInvitationTime(Date invitationTime) {
		this.invitationTime = invitationTime;
	}

	public List<MemberInvitationDetailItem> getItems() {
		return items;
	}

	public void setItems(List<MemberInvitationDetailItem> items) {
		this.items = items;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	
}
