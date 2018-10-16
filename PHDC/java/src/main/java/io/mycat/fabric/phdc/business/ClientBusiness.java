package io.mycat.fabric.phdc.business;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.alibaba.fastjson.JSON;

import io.mycat.fabric.phdc.bass.DRCChainCodeManager;
import io.mycat.fabric.phdc.bass.dto.InviteRespDto;
import io.mycat.fabric.phdc.dto.MemberInvitationDetail;
import io.mycat.fabric.phdc.dto.MemberInvitationDetailItem;
import io.mycat.fabric.phdc.dto.MemberInvitationDto;
import io.mycat.fabric.phdc.entity.Member;
import io.mycat.fabric.phdc.entity.MemberInvitation;
import io.mycat.fabric.phdc.entity.Report;
import io.mycat.fabric.phdc.enums.InvitationStatus;
import io.mycat.fabric.phdc.exception.BuzException;
import io.mycat.fabric.phdc.service.InstitutionDictionaryService;
import io.mycat.fabric.phdc.service.MemberInvitationService;
import io.mycat.fabric.phdc.service.MemberService;
import io.mycat.fabric.phdc.service.ReportService;
import io.mycat.fabric.phdc.service.ResultService;
import io.mycat.fabric.phdc.util.DateUtil;
import io.mycat.fabric.phdc.vo.InvitationDataVo;
import tk.mybatis.mapper.entity.Example;

@Service
public class ClientBusiness {

	private Logger loggger = LoggerFactory.getLogger(ClientBusiness.class);
	
	@Autowired
	MemberInvitationService memberInvitationService;
	
	@Autowired
	ReportService reportService;
	
	@Autowired
	ResultService resultService;
	
	@Autowired
	InstitutionDictionaryService institutionDictionaryService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	DRCChainCodeManager drcChainCodeManager;
	
	public List<MemberInvitationDto> getMemberInvitationList(int memberId) {
		Example memberInvitationExample = new Example(MemberInvitation.class);
		memberInvitationExample
			.createCriteria()
			.andEqualTo("memberId", memberId);
		List<MemberInvitation> memberInvitations = memberInvitationService.selectByExample(memberInvitationExample);
		return memberInvitations.stream().map(m->{
			MemberInvitationDto dto = new MemberInvitationDto();
			dto.setInvitationId(m.getId());
			dto.setInvitationMemberId(m.getInvitationMemberId());
			dto.setInvitionDate(m.getCreateTime());
			dto.setMemberId(m.getMemberId());
			dto.setStatus(m.getStatus());
			dto.setName(m.getName());
			return dto;
		}).collect(Collectors.toList());
	}
	
	public MemberInvitationDetail getMemberInvitationDetail(int invitationId) throws BuzException {
		MemberInvitationDetail detail = new MemberInvitationDetail();
		MemberInvitation memberInvitation = memberInvitationService.selectByKey(invitationId);
		if (memberInvitation == null) {
			throw new BuzException("匹配不到邀请数据");
		}
		String reportId = memberInvitation.getReportId();
		Example reportExample = new Example(Report.class);
		reportExample
			.createCriteria()
			.andEqualTo("reportId", reportId);
		List<Report> reports = reportService.selectByExample(reportExample);
		/*List<Integer> resultIds = reports.stream().map(Report::getResultId).collect(Collectors.toList());
		Example resultExample = new Example(Result.class);
		resultExample
			.createCriteria()
			.andIn("resultId", resultIds);
		resultExample.orderBy("createTime").desc();
		List<Result> results = resultService.selectByExample(resultExample);
		List<MemberInvitationDetailItem> items = results.stream().map(re->{
			MemberInvitationDetailItem item = new MemberInvitationDetailItem();
			int dictionaryId = re.getExamDictId();
			InstitutionDictionary institutionDictionary = institutionDictionaryService.selectByKey(dictionaryId);
			item.setDictionaryName(institutionDictionary.getName());
			item.setResult(re.getResult());
			item.setResultId(re.getReserveId());
			item.setCheckDate(re.getCreateTime());
			return item;
		}).collect(Collectors.toList());*/
		List<MemberInvitationDetailItem> items = reports.stream().map(re->{
			MemberInvitationDetailItem item = new MemberInvitationDetailItem();
			item.setCheckDate(re.getCheckDate());
			item.setDictionaryName(re.getDepartName());
			item.setResult(re.getResult());
			return item;
		}).collect(Collectors.toList());
		detail.setMobile(memberInvitation.getMobile());
		detail.setName(memberInvitation.getName());
		detail.setInvitationTime(memberInvitation.getCreateTime());
		detail.setItems(items);
		return detail;
	}
	
	@Transactional
	public void saveInvitationResult(String invitationCode, List<InvitationDataVo> invitationDataVos) throws BuzException {
		Example invitationExample = new Example(MemberInvitation.class);
		invitationExample
			.createCriteria()
			.andEqualTo("code", invitationCode);
		List<MemberInvitation> invitations = memberInvitationService.selectByExample(invitationExample);
		if (CollectionUtils.isEmpty(invitations)) {
			throw new BuzException("匹配不到邀请数据");
		}
		MemberInvitation invitation = invitations.get(0);
		invitation.setStatus(InvitationStatus.APPLY_AGGRE.value());
		invitation.setUpdateTime(new Date());
		String reportId = invitation.getReportId();
		memberInvitationService.updateNotNull(invitation);
		invitationDataVos.stream().forEach(i->{
			Report report = new Report();
			report.setCheckDate(DateUtil.StringToDate(i.getDate()));
			report.setReportId(reportId);
			report.setDepartName(i.getDepartName());
			report.setResult(i.getResult());
			reportService.saveNotNull(report);
		});
		//reportService.saveInvitationResult(reports);
	}
	
	public void invitationAccess(int memberId,String name,String mobile,String invitationCode,int patternId) throws BuzException {
		MemberInvitation memberInvitation = new MemberInvitation();
		//查询用户是否对
		Example memberExample = new Example(Member.class);
		memberExample
			.createCriteria()
			.andEqualTo("name", name)
			.andEqualTo("mobile", mobile);
		List<Member> members = memberService.selectByExample(memberExample);
		if (CollectionUtils.isEmpty(members)) {
			throw new BuzException("手机号姓名匹配用户失败");
		}
		Member buzMember = memberService.selectByKey(memberId);
		if (buzMember == null) {
			throw new BuzException("检查MemberId是否正确");
		}
		Member member = members.get(0);
		memberInvitation.setCode(invitationCode);/////http://39.104.99.78:8666/client/member/invitation/result/list
		memberInvitation.setCreateTime(new Date());
		memberInvitation.setInvitationMemberId(member.getId());
		memberInvitation.setMemberId(memberId);
		memberInvitation.setMobile(mobile);
		memberInvitation.setName(name);
		memberInvitation.setStatus(InvitationStatus.ORDERING.value());
		memberInvitation.setUpdateTime(new Date());
		//drcChainCodeManager.invite(userId, callBack, dUName, patternId, "CERT", "SECRET");
		String callback = "http://39.104.99.78:8666/client/member/invitation/result/list?invitationCode="+invitationCode;
		drcChainCodeManager.invite(member.getId(), callback, buzMember.getName(), patternId, "CERT","SECRET",(resp)->{
			Example applyInvitationExample = new Example(MemberInvitation.class);
			applyInvitationExample
				.createCriteria()
				.andEqualTo("reportId", resp)
				.andEqualTo("status", InvitationStatus.ORDERING.value());
			List<MemberInvitation> invis = memberInvitationService.selectByExample(applyInvitationExample);
			if (!CollectionUtils.isEmpty(invis)) {
				MemberInvitation invi = invis.get(0);
				invi.setStatus(InvitationStatus.APPLY.value());
				memberInvitationService.updateNotNull(invi);
			}
		},(resp)->{
			InviteRespDto dto = JSON.parseObject(resp, InviteRespDto.class);
			try {
				if (dto.getRet() == null || !dto.getRet().equals("ok")) {
					throw new BuzException("调用chaincode出错");
				}
				if (StringUtils.isEmpty(dto.getInviteId())) {
					throw new BuzException("chaincode返回错误响应");
				}
				String reportId = dto.getInviteId();
				memberInvitation.setReportId(reportId);
				memberInvitationService.saveNotNull(memberInvitation);
			} catch (BuzException e) {
				loggger.error("调用链表失败",e);
				e.printStackTrace();
			}
		});
	}
	
}
