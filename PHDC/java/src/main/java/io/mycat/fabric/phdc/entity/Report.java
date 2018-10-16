package io.mycat.fabric.phdc.entity;

import java.util.Date;
import javax.persistence.*;

@Table(name = "tb_report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 报告ID
     */
    @Column(name = "report_id")
    private String reportId;

    /**
     * 科室名称
     */
    @Column(name = "depart_name")
    private String departName;

    /**
     * 体检时间
     */
    @Column(name = "check_date")
    private Date checkDate;

    /**
     * 体检结果
     */
    private String result;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取报告ID
     *
     * @return report_id - 报告ID
     */
    public String getReportId() {
        return reportId;
    }

    /**
     * 设置报告ID
     *
     * @param reportId 报告ID
     */
    public void setReportId(String reportId) {
        this.reportId = reportId == null ? null : reportId.trim();
    }

    /**
     * 获取科室名称
     *
     * @return depart_name - 科室名称
     */
    public String getDepartName() {
        return departName;
    }

    /**
     * 设置科室名称
     *
     * @param departName 科室名称
     */
    public void setDepartName(String departName) {
        this.departName = departName == null ? null : departName.trim();
    }

    /**
     * 获取体检时间
     *
     * @return check_date - 体检时间
     */
    public Date getCheckDate() {
        return checkDate;
    }

    /**
     * 设置体检时间
     *
     * @param checkDate 体检时间
     */
    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    /**
     * 获取体检结果
     *
     * @return result - 体检结果
     */
    public String getResult() {
        return result;
    }

    /**
     * 设置体检结果
     *
     * @param result 体检结果
     */
    public void setResult(String result) {
        this.result = result == null ? null : result.trim();
    }
}