package io.mycat.fabric.phdc.dao.mapper;

import io.mycat.fabric.phdc.entity.Member;
import tk.mybatis.mapper.common.Mapper;

public interface MemberMapper extends Mapper<Member> {

	Integer getLastId();
}