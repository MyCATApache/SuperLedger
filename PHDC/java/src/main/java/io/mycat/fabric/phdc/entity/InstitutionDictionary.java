package io.mycat.fabric.phdc.entity;

import javax.persistence.*;

@Table(name = "tb_institution_dictionary")
public class InstitutionDictionary {
    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 科室ID
     */
    @Column(name = "depart_id")
    private Integer departId;

    /**
     * 所属机构ID
     */
    @Column(name = "inst_id")
    private Integer instId;

    /**
     * 项目名称
     */
    private String name;

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
     * 获取科室ID
     *
     * @return depart_id - 科室ID
     */
    public Integer getDepartId() {
        return departId;
    }

    /**
     * 设置科室ID
     *
     * @param departId 科室ID
     */
    public void setDepartId(Integer departId) {
        this.departId = departId;
    }

    /**
     * 获取所属机构ID
     *
     * @return inst_id - 所属机构ID
     */
    public Integer getInstId() {
        return instId;
    }

    /**
     * 设置所属机构ID
     *
     * @param instId 所属机构ID
     */
    public void setInstId(Integer instId) {
        this.instId = instId;
    }

    /**
     * 获取项目名称
     *
     * @return name - 项目名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置项目名称
     *
     * @param name 项目名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}