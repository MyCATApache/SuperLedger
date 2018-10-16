package io.mycat.fabric.phdc.ctrl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mycat.fabric.phdc.business.InvitationBusiness;
import io.mycat.fabric.phdc.business.ReserveBusiness;
import io.mycat.fabric.phdc.ctrl.config.ResponseContent;
import io.mycat.fabric.phdc.dto.MemberInstResultDto;
import io.mycat.fabric.phdc.dto.MemberReserveDto;
import io.mycat.fabric.phdc.dto.ReserveDetailDto;
import io.mycat.fabric.phdc.dto.ReserveExamResultDetail;
import io.mycat.fabric.phdc.exception.BuzException;

/**
 * 
 * @author xu
 *
 */
@RestController
@RequestMapping("institution")
public class InstitutionController {
	
	@Autowired
	ReserveBusiness reserveBusiness;
	
	@Autowired
	InvitationBusiness invitationBusiness;
	
	/**
	 * 根据用户ID获取所有预约信息
	 * @param memberId
	 * @author xu
	 * @return
	 */
	@GetMapping("member/reserve/list")
	public ResponseContent getMemberReserveList(@RequestParam(required=true) int institutionId) {
		List<ReserveDetailDto> results = reserveBusiness.getReserveDetailList(institutionId);
		return ResponseContent.ok(results);
	}
	
	/**
	 * 修改用户状态 是否到检  2）已到检 3）未到检',
	 * @param reserveId
	 * @param status
	 * @author xu
	 * @return
	 */
	@PostMapping("/member/reserve/status")
	public ResponseContent updateReserveStatus(@RequestParam(required=true) int reserveId,@RequestParam(required=true)byte status) {
		invitationBusiness.reserveStatus(reserveId, status);
		return ResponseContent.ok(null);
	}
	/**
	 * 根据机构ID来获取所有该机构下的预约信息
	 * @param institutionId
	 * @author xu
	 * @return
	 * @throws BuzException
	 */
	@GetMapping("member/list")
	public ResponseContent memberList(@RequestParam(required=true)int institutionId) throws BuzException {
		List<MemberReserveDto> reserves = invitationBusiness.getInstitutionMemberReserveList(institutionId);
		return ResponseContent.ok(reserves);
	}
	
	/**
	 * 获取该机构下 该用户的所有体检结果数据
	 * @author xu
	 * @param institutionId
	 * @param memberId
	 * @return
	 */
	@GetMapping("member/exam/result/list")
	public ResponseContent memberResuleList(@RequestParam(required=true)int institutionId,
			@RequestParam(required=true) int memberId) {
		List<MemberInstResultDto> result = invitationBusiness.getMemberInstResult(institutionId, memberId);
		return ResponseContent.ok(result);
	}
	
	/**
	 * 获取该机构下的所有体检数据
	 * @param institutionId
	 * @return
	 * @throws BuzException 
	 */
	@GetMapping("member/exam/list")
	public ResponseContent memberExamList(@RequestParam(required=true)int institutionId) throws BuzException {
		List<ReserveExamResultDetail> resultDetails = invitationBusiness.reserveExamResultDetails(institutionId);
		return ResponseContent.ok(resultDetails);
	}
	
	/**
	 * 填写用户数据结论
	 * @param resultId
	 * @param resuleSummary
	 * @param departCheckDoctor
	 * @return
	 * @throws BuzException
	 */
	@PostMapping("member/exam/result")
	public ResponseContent memberExamStatus(@RequestParam(required=true) int resultId,
			@RequestParam(required=true) String result,
			@RequestParam(required=true) String checkDoctor) throws BuzException {
		reserveBusiness.memberExamResult(resultId, result,checkDoctor);
		return ResponseContent.ok(null);
	}
	
	@PostMapping("member/exam/summary")
	public ResponseContent memberExamSummary(@RequestParam(required=true) int resultId,
			@RequestParam(required=true) String summary,
			@RequestParam(required=true) String departDoctor) throws BuzException {
		reserveBusiness.memberExamSummary(resultId, summary,departDoctor);
		return ResponseContent.ok(null);
	}
	
}
