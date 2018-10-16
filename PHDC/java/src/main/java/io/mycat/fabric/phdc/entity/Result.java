package io.mycat.fabric.phdc.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_result")
public class Result {
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 预约ID
     */
    @Column(name = "reserve_id")
    private Integer reserveId;

    /**
     * 预约人ID
     */
    @Column(name = "member_id")
    private Integer memberId;

    /**
     * 检查项ID
     */
    @Column(name = "exam_dict_id")
    private Integer examDictId;

    /**
     * 科室医生
     */
    @Column(name = "depart_doctor")
    private String departDoctor;

    /**
     * 医生
     */
    @Column(name = "check_doctor")
    private String checkDoctor;

    /**
     * 创建时间
     */
    @Column(name = "create_time")
    private Date createTime;

    /**
     * 结果状态 1)未出结果 2)已出结果
     */
    private Byte status;

    /**
     * 检查结果
     */
    private String result;

    /**
     * 结论
     */
    private String summary;

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
     * 获取预约ID
     *
     * @return reserve_id - 预约ID
     */
    public Integer getReserveId() {
        return reserveId;
    }

    /**
     * 设置预约ID
     *
     * @param reserveId 预约ID
     */
    public void setReserveId(Integer reserveId) {
        this.reserveId = reserveId;
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
     * 获取检查项ID
     *
     * @return exam_dict_id - 检查项ID
     */
    public Integer getExamDictId() {
        return examDictId;
    }

    /**
     * 设置检查项ID
     *
     * @param examDictId 检查项ID
     */
    public void setExamDictId(Integer examDictId) {
        this.examDictId = examDictId;
    }

    /**
     * 获取科室医生
     *
     * @return depart_doctor - 科室医生
     */
    public String getDepartDoctor() {
        return departDoctor;
    }

    /**
     * 设置科室医生
     *
     * @param departDoctor 科室医生
     */
    public void setDepartDoctor(String departDoctor) {
        this.departDoctor = departDoctor == null ? null : departDoctor.trim();
    }

    /**
     * 获取医生
     *
     * @return check_doctor - 医生
     */
    public String getCheckDoctor() {
        return checkDoctor;
    }

    /**
     * 设置医生
     *
     * @param checkDoctor 医生
     */
    public void setCheckDoctor(String checkDoctor) {
        this.checkDoctor = checkDoctor == null ? null : checkDoctor.trim();
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

    /**
     * 获取结果状态 1)未出结果 2)已出结果
     *
     * @return status - 结果状态 1)未出结果 2)已出结果
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置结果状态 1)未出结果 2)已出结果
     *
     * @param status 结果状态 1)未出结果 2)已出结果
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * 获取检查结果
     *
     * @return result - 检查结果
     */
    public String getResult() {
        return result;
    }

    /**
     * 设置检查结果
     *
     * @param result 检查结果
     */
    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }

    /**
     * 获取结论
     *
     * @return summary - 结论
     */
    public String getSummary() {
        return summary;
    }

    /**
     * 设置结论
     *
     * @param summary 结论
     */
    public void setSummary(String summary) {
        this.summary = summary == null ? null : summary.trim();
    }
}