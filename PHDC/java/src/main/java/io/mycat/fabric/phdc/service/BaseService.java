package io.mycat.fabric.phdc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;

import tk.mybatis.mapper.common.Mapper;

public abstract class BaseService<T>{

    @Autowired
    protected Mapper<T> mapper;

    public Mapper<T> getMapper() {
        return mapper;
    }

    public T selectByKey(Object key) {
        return mapper.selectByPrimaryKey(key);
    }
    public int save(T entity) {
        return mapper.insert(entity);
    }
    
    public int saveNotNull(T entity) {
        return mapper.insertSelective(entity);
    }
    
    public int delete(Object key) {
        return mapper.deleteByPrimaryKey(key);
    }

    public int deleteByExample(Object example) {

        return mapper.deleteByExample(example);

    }

    public int updateAll(T entity) {
        return mapper.updateByPrimaryKey(entity);
    }
    public int updateNotNull(T entity) {

        return mapper.updateByPrimaryKeySelective(entity);
    }

    public int updateByExample(T entity, Object example) {
        return mapper.updateByExample(entity,example);
    }
    public int updateByExampleNotNull(T entity, Object example) {

        return mapper.updateByExampleSelective(entity,example);
    }
    public int selectCountByEntity(T entity) {
        return mapper.selectCount(entity);
    }
    public int selectCountByExample(Object example) {
        return mapper.selectCountByExample(example);
    }
    public List<T> selectAll() {
        return mapper.selectAll();
    }
    public List<T> selectByEntity(T entity) {
        return mapper.select(entity);
    }
    public List<T> selectByExample(Object example) {
        return mapper.selectByExample(example);
    }
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public PageInfo selectByExample(Object example, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<T> list =
         mapper.selectByExample(example);
        return new PageInfo(list);
    }
}
