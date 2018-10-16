package io.mycat.fabric.phdc.entity;

import javax.persistence.*;

@Table(name = "tb_institution_depart")
public class InstitutionDepart {
    /**
     * 科室ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * 科室名称
     */
    private String name;

    /**
     * 获取科室ID
     *
     * @return id - 科室ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置科室ID
     *
     * @param id 科室ID
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取科室名称
     *
     * @return name - 科室名称
     */
    public String getName() {
        return name;
    }

    /**
     * 设置科室名称
     *
     * @param name 科室名称
     */
    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }
}