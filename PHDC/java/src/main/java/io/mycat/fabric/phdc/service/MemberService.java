package io.mycat.fabric.phdc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mycat.fabric.phdc.dao.mapper.MemberMapper;
import io.mycat.fabric.phdc.entity.Member;

@Service
public class MemberService extends BaseService<Member>{

	@Autowired
	MemberMapper memberMapper;
	
	public Integer getLastId() {
		return memberMapper.getLastId();
	}
	
}
