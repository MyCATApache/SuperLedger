package io.mycat.fabric.phdc.ctrl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mycat.fabric.phdc.business.ReserveBusiness;
import io.mycat.fabric.phdc.ctrl.config.ResponseContent;
import io.mycat.fabric.phdc.dto.MemberInstResultDto;
import io.mycat.fabric.phdc.exception.BuzException;

@RestController
@RequestMapping("member")
public class MemberController {
	
	@Autowired
	ReserveBusiness reserveBusiness;
	
	/**
	 * 用户预约
	 * @param memberId
	 * @param dictionaryId
	 * @param institutionId
	 * @param name
	 * @param mobile
	 * @param gender
	 * @param reserveTime
	 * @return
	 */
	@PostMapping("reserve")
	public ResponseContent saveMemberReserve(
			@RequestParam(required=true) int memberId,
			@RequestParam(required = true) int dictionaryId,
			@RequestParam(required=true) int institutionId,
			@RequestParam(required=true) String name,
			@RequestParam(required=true) String mobile,
			@RequestParam(required=true) byte gender,
			@RequestParam(required=true) @DateTimeFormat(pattern="yyyy/MM/dd") Date reserveTime) {
		//
		Integer reserveId = reserveBusiness.reserve(memberId, institutionId,dictionaryId, name, mobile, gender, reserveTime);
		return ResponseContent.ok(reserveId);
	}
	
	/**
	 * 到检
	 * @param reserveId
	 * @return
	 * @throws BuzException
	 */
	@PostMapping("reserve/sign")
	public ResponseContent reserveSign(@RequestParam(required=true) int reserveId) throws BuzException {
		reserveBusiness.signIn(reserveId);
		return ResponseContent.ok(null);
	}
	
	/**
	 * 体检结果
	 * @param id
	 * @return
	 */
	@GetMapping("reserve/exam/result")
	public ResponseContent getReserveExamResultListByReserveId(
			@RequestParam(required=true) int id) {
		MemberInstResultDto dto = reserveBusiness.getMmeberResultById(id);
		return ResponseContent.ok(dto);
	}
	
}
