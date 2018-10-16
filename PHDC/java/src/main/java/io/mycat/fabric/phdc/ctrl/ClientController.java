package io.mycat.fabric.phdc.ctrl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.mycat.fabric.phdc.business.ClientBusiness;
import io.mycat.fabric.phdc.ctrl.config.ResponseContent;
import io.mycat.fabric.phdc.dto.MemberInvitationDetail;
import io.mycat.fabric.phdc.dto.MemberInvitationDto;
import io.mycat.fabric.phdc.exception.BuzException;
import io.mycat.fabric.phdc.vo.InvitationDataVo;


@RestController
@RequestMapping("client")
public class ClientController {

	@Autowired
	ClientBusiness clientBusiness;
	
	@GetMapping("member/invitation/list")
	public ResponseContent memberInvitationList(@RequestParam(required=true) int memberId){
		List<MemberInvitationDto> result = clientBusiness.getMemberInvitationList(memberId);
		return ResponseContent.ok(result);
	}
	
	@GetMapping("member/invitation/detail")
	public ResponseContent getMemberInvitationDetail(@RequestParam(required=true) int invitationId) throws BuzException {
		MemberInvitationDetail detail = clientBusiness.getMemberInvitationDetail(invitationId);
		return ResponseContent.ok(detail);
	}
	
	@PostMapping("member/invitation/result/list")
	public ResponseContent saveResultList(@RequestParam(required=true)String invitationCode,@RequestBody(required=true)List<InvitationDataVo> invitationDataVo) throws BuzException {
		clientBusiness.saveInvitationResult(invitationCode, invitationDataVo);
		return ResponseContent.ok(null);
	}
	
	
	@PostMapping("member/invitation/access")
	public ResponseContent validateInvitation(@RequestParam(required=true) int memberId,
			@RequestParam(required=true) String name,@RequestParam(required=true) String mobile,
			@RequestParam(required=true) String invitationCode,
			@RequestParam(required=true) int patternId
				) throws BuzException {
		clientBusiness.invitationAccess(memberId, name, mobile, invitationCode, patternId);
		return ResponseContent.ok(null);
	}
	
}
