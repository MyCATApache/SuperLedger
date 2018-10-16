package io.mycat.fabric.phdc.dao.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import io.mycat.fabric.phdc.entity.Report;
import io.mycat.fabric.phdc.entity.Reserve;
import tk.mybatis.mapper.common.Mapper;

public interface ReserveMapper extends Mapper<Reserve> {

	void saveInvitationResult(@Param("reports")List<Report> reports);
}