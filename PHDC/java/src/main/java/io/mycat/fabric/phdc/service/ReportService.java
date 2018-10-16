package io.mycat.fabric.phdc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mycat.fabric.phdc.dao.mapper.ReserveMapper;
import io.mycat.fabric.phdc.entity.Report;

@Service
public class ReportService extends BaseService<Report>{
	
	@Autowired
	ReserveMapper reserveMapper;
	
	public void saveInvitationResult(List<Report> reports) {
		reserveMapper.saveInvitationResult(reports);
	}
	
}
