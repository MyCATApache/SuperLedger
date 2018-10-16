package io.mycat.fabric.phdc.business;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.mycat.fabric.phdc.dto.MemberInstResultDto;
import io.mycat.fabric.phdc.dto.MemberReserveDto;
import io.mycat.fabric.phdc.dto.ReserveExamResultDetail;
import io.mycat.fabric.phdc.entity.Institution;
import io.mycat.fabric.phdc.entity.InstitutionDepart;
import io.mycat.fabric.phdc.entity.InstitutionDictionary;
import io.mycat.fabric.phdc.entity.Member;
import io.mycat.fabric.phdc.entity.Reserve;
import io.mycat.fabric.phdc.entity.Result;
import io.mycat.fabric.phdc.enums.ReserveStatus;
import io.mycat.fabric.phdc.enums.ResultStatus;
import io.mycat.fabric.phdc.exception.BuzException;
import io.mycat.fabric.phdc.service.InstitutionDepartService;
import io.mycat.fabric.phdc.service.InstitutionDictionaryService;
import io.mycat.fabric.phdc.service.InstitutionService;
import io.mycat.fabric.phdc.service.MemberService;
import io.mycat.fabric.phdc.service.ReserveService;
import io.mycat.fabric.phdc.service.ResultService;
import io.mycat.fabric.phdc.util.UserElementUtil;
import tk.mybatis.mapper.entity.Example;

@Service
public class InvitationBusiness {

	@Autowired
	ReserveService reserveService;
	
	@Autowired
	ResultService resultService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	InstitutionService institutionService;
	
	@Autowired
	InstitutionDictionaryService institutionDictionaryService;
	
	@Autowired
	InstitutionDepartService institutionDepartService;
	
	public void reserveStatus(int reserveId, byte status) {
		Reserve reserve = reserveService.selectByKey(reserveId);
		reserve.setStatus(status);
		reserveService.updateNotNull(reserve);
	}
	
	public List<MemberReserveDto> getInstitutionMemberReserveList(int institutionId) throws BuzException {
		Institution institution = institutionService.selectByKey(institutionId);
		if (institution == null) {
			throw new BuzException("机构不存在");
		}
		Example reservExample = new Example(Reserve.class);
		reservExample
			.createCriteria()
			.andEqualTo("institutionId", institutionId)
			.andEqualTo("status", ReserveStatus.ACCEPT_RESERVE.value());
		reservExample.orderBy("createTime").desc();
		List<Reserve> reserves = reserveService.selectByExample(reservExample);
		List<MemberReserveDto> result = reserves.stream().collect(Collectors.groupingBy(Reserve::getName))
			.entrySet().stream().map(entry->{
				MemberReserveDto res =new MemberReserveDto();
				Reserve reserve = entry.getValue().stream().sorted((r1,r2)->(r2.getCheckDate().compareTo(r1.getCheckDate())))
					.findFirst().get();
				Member member = memberService.selectByKey(reserve.getMemberId());
				res.setAge(UserElementUtil.getAgeByBirthday(member.getBirthday()));
				res.setCheckDate(reserve.getCheckDate());
				res.setGender(reserve.getGender());
				res.setMemberId(reserve.getMemberId());
				res.setName(reserve.getName());
				res.setStatus(reserve.getStatus());
				return res;
			}).collect(Collectors.toList());
		return result;
	}
	
	public List<MemberInstResultDto> getMemberInstResult(int institutionId,int memberId){
		//查询所有reserveId
		Example reservExample = new Example(Reserve.class);
		reservExample
			.createCriteria()
			.andEqualTo("institutionId", institutionId)
			.andEqualTo("memberId", memberId)
			.andEqualTo("status", ReserveStatus.ACCEPT_RESERVE.value());
		reservExample.orderBy("checkDate").desc();
		reservExample.selectProperties("checkDate","id");
		List<Reserve> reserves = reserveService.selectByExample(reservExample);
		List<Integer> reserveIds = reserves.stream().map(Reserve::getId).collect(Collectors.toList());
		Example resultExample = new Example(Result.class);
		resultExample
			.createCriteria()
			.andIn("reserveId", reserveIds);
		List<Result> results = resultService.selectByExample(resultExample);
		return results.stream().map(res->{
			int dictionaryId = res.getExamDictId();
			InstitutionDictionary dictionary = institutionDictionaryService.selectByKey(dictionaryId);
			int departId = dictionary.getDepartId();
			InstitutionDepart depart = institutionDepartService.selectByKey(departId);
			MemberInstResultDto dto = new MemberInstResultDto();
			dto.setCheckDoctor(res.getCheckDoctor());
			dto.setDepartDoctor(depart.getName());
			dto.setResult(res.getResult());
			dto.setResultId(res.getId());
			dto.setSummary(res.getSummary());
			dto.setDepartName(depart.getName());
			dto.setCheckDate(res.getCreateTime());
			return dto;
		}).collect(Collectors.toList());
	}
	
	public List<ReserveExamResultDetail> reserveExamResultDetails(int institutionId){
		//查询所有reserveId
		Example reservExample = new Example(Reserve.class);
		reservExample
			.createCriteria()
			.andEqualTo("institutionId", institutionId)
			.andEqualTo("status", ReserveStatus.ACCEPT_RESERVE.value());
		reservExample.orderBy("createTime").desc();
		List<Reserve> reserves = reserveService.selectByExample(reservExample);
		List<ReserveExamResultDetail> resultS = reserves.stream().map(reserve->{
			ReserveExamResultDetail res =new ReserveExamResultDetail();
			Example resultExample = new Example(Result.class);
			resultExample
				.createCriteria()
				.andEqualTo("reserveId", reserve.getId());
			List<Result> reses = resultService.selectByExample(resultExample);
			//FIXME: 暂时1:1
			Result checkResult = reses.get(0);//暂时1:1
			int dictionaryId = reserve.getDictionaryId();
			InstitutionDictionary institutionDictionary = institutionDictionaryService.selectByKey(dictionaryId);
			Member member = memberService.selectByKey(reserve.getMemberId());
			res.setAge(UserElementUtil.getAgeByBirthday(member.getBirthday()));
			res.setCheckDate(reserve.getCheckDate());
			res.setGender(reserve.getGender());
			res.setStatus(checkResult.getStatus());
			res.setDictionaryName(institutionDictionary.getName());
			res.setCheckDoctor(checkResult.getCheckDoctor());
			res.setDepartDoctor(checkResult.getDepartDoctor());
			res.setResultId(checkResult.getId());
			res.setSummary(checkResult.getSummary());
			res.setResult(checkResult.getResult());
			
			return res;
		}).collect(Collectors.toList());
		return resultS;
	}
	
}
