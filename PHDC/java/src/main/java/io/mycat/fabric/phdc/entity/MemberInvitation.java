package io.mycat.fabric.phdc.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_member_invitation")
public class MemberInvitation {
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 申请的报告
     */
    @Column(name = "report_id")
    private String reportId;

    /**
     * 邀请人ID
     */
    @Column(name = "member_id")
    private Integer memberId;

    /**
     * 被邀请的用户ID
     */
    @Column(name = "invitation_member_id")
    private Integer invitationMemberId;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 姓名
     */
    private String name;

    /**
     * 邀请码
     */
    private String code;

    /**
     * 数据状态 1)已生成 2)已申请 3)拒绝申请 4)同意申请
     */
    private Byte status;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time")
    private Date updateTime;

    /**
     * 获取ID
     *
     * @return id - ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置ID
     *
     * @param id ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取申请的报告
     *
     * @return report_id - 申请的报告
     */
    public String getReportId() {
        return reportId;
    }

    /**
     * 设置申请的报告
     *
     * @param reportId 申请的报告
     */
    public void setReportId(String reportId) {
        this.reportId = reportId == null ? null : reportId.trim();
    }

    /**
     * 获取邀请人ID
     *
     * @return member_id - 邀请人ID
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * 设置邀请人ID
     *
     * @param memberId 邀请人ID
     */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    /**
     * 获取被邀请的用户ID
     *
     * @return invitation_member_id - 被邀请的用户ID
     */
    public Integer getInvitationMemberId() {
        return invitationMemberId;
    }

    /**
     * 设置被邀请的用户ID
     *
     * @param invitationMemberId 被邀请的用户ID
     */
    public void setInvitationMemberId(Integer invitationMemberId) {
        this.invitationMemberId = invitationMemberId;
    }

    /**
     * 获取手机号
     *
     * @return mobile - 手机号
     */
    public String getMobile() {
        return mobile;
    }

    /**
     * 设置手机号
     *
     * @param mobile 手机号
     */
    public void setMobile(String mobile) {
        this.mobile = mobile == null ? null : mobile.trim();
    }

    /**
     * 获取姓名
     *
     * @return name - 姓名
     */
    public String getName() {
        return name;
    }

    /**
     * 设置姓名
     *
     * @param name 姓名
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    /**
     * 获取邀请码
     *
     * @return code - 邀请码
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置邀请码
     *
     * @param code 邀请码
     */
    public void setCode(String code) {
        this.code = code == null ? null : code.trim();
    }

    /**
     * 获取数据状态 1)已生成 2)已申请 3)拒绝申请 4)同意申请
     *
     * @return status - 数据状态 1)已生成 2)已申请 3)拒绝申请 4)同意申请
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置数据状态 1)已生成 2)已申请 3)拒绝申请 4)同意申请
     *
     * @param status 数据状态 1)已生成 2)已申请 3)拒绝申请 4)同意申请
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取更新时间
     *
     * @return update_time - 更新时间
     */
    public Date getUpdateTime() {
        return updateTime;
    }

    /**
     * 设置更新时间
     *
     * @param updateTime 更新时间
     */
    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}