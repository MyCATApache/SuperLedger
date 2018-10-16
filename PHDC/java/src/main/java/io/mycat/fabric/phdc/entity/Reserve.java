package io.mycat.fabric.phdc.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_reserve")
public class Reserve {
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 预约机构ID
     */
    @Column(name = "dictionary_id")
    private Integer dictionaryId;

    /**
     * 机构ID
     */
    @Column(name = "institution_id")
    private Integer institutionId;

    /**
     * 性别 0保密 1男 2女
     */
    private Byte gender;

    /**
     * 手机号
     */
    private String mobile;

    /**
     * 姓名
     */
    private String name;

    /**
     * 预约人ID
     */
    @Column(name = "member_id")
    private Integer memberId;

    /**
     * 检查日期
     */
    @Column(name = "check_date")
    private Date checkDate;

    /**
     * 预约状态 1)已预约 2）已到检 3）未到检 4) 已过期
     */
    private Byte status;

    /**
     * 预约时间
     */
    @Column(name = "reserve_time")
    private Date reserveTime;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

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
     * 获取预约机构ID
     *
     * @return dictionary_id - 预约机构ID
     */
    public Integer getDictionaryId() {
        return dictionaryId;
    }

    /**
     * 设置预约机构ID
     *
     * @param dictionaryId 预约机构ID
     */
    public void setDictionaryId(Integer dictionaryId) {
        this.dictionaryId = dictionaryId;
    }

    /**
     * 获取机构ID
     *
     * @return institution_id - 机构ID
     */
    public Integer getInstitutionId() {
        return institutionId;
    }

    /**
     * 设置机构ID
     *
     * @param institutionId 机构ID
     */
    public void setInstitutionId(Integer institutionId) {
        this.institutionId = institutionId;
    }

    /**
     * 获取性别 0保密 1男 2女
     *
     * @return gender - 性别 0保密 1男 2女
     */
    public Byte getGender() {
        return gender;
    }

    /**
     * 设置性别 0保密 1男 2女
     *
     * @param gender 性别 0保密 1男 2女
     */
    public void setGender(Byte gender) {
        this.gender = gender;
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
     * 获取预约人ID
     *
     * @return member_id - 预约人ID
     */
    public Integer getMemberId() {
        return memberId;
    }

    /**
     * 设置预约人ID
     *
     * @param memberId 预约人ID
     */
    public void setMemberId(Integer memberId) {
        this.memberId = memberId;
    }

    /**
     * 获取检查日期
     *
     * @return check_date - 检查日期
     */
    public Date getCheckDate() {
        return checkDate;
    }

    /**
     * 设置检查日期
     *
     * @param checkDate 检查日期
     */
    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    /**
     * 获取预约状态 1)已预约 2）已到检 3）未到检 4) 已过期
     *
     * @return status - 预约状态 1)已预约 2）已到检 3）未到检 4) 已过期
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置预约状态 1)已预约 2）已到检 3）未到检 4) 已过期
     *
     * @param status 预约状态 1)已预约 2）已到检 3）未到检 4) 已过期
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取预约时间
     *
     * @return reserve_time - 预约时间
     */
    public Date getReserveTime() {
        return reserveTime;
    }

    /**
     * 设置预约时间
     *
     * @param reserveTime 预约时间
     */
    public void setReserveTime(Date reserveTime) {
        this.reserveTime = reserveTime;
    }

    /**
     * 获取创建时间
     *
     * @return create_time - 创建时间
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 创建时间
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}