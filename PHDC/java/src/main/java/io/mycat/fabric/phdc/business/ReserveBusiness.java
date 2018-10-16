package io.mycat.fabric.phdc.business;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import io.mycat.fabric.phdc.bass.HDCChainCodeManager;
import io.mycat.fabric.phdc.bass.dto.BaseResp;
import io.mycat.fabric.phdc.dto.MemberInstResultDto;
import io.mycat.fabric.phdc.dto.ReserveDetailDto;
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
import io.mycat.fabric.phdc.service.MemberService;
import io.mycat.fabric.phdc.service.ReserveService;
import io.mycat.fabric.phdc.service.ResultService;
import io.mycat.fabric.phdc.util.UserElementUtil;
import tk.mybatis.mapper.entity.Example;

@Service
public class ReserveBusiness {

	@Autowired
	ReserveService reserveService;
	
	@Autowired
	ResultService resultService;
	
	@Autowired
	MemberService memberService;
	
	@Autowired
	InstitutionDictionaryService institutionDictionaryService;
	
	@Autowired
	InstitutionDepartService institutionDepartService;
	
	@Autowired
	HDCChainCodeManager hdcChainCodeManager;
	
	/**
	 * 预约
	 * @param memberId
	 * @param dictionaryId
	 * @param name
	 * @param mobile
	 * @param gender
	 * @param reserveTime
	 * return 返回最后ID
	 */
	public Integer reserve(int memberId,int institutionId,int dictionaryId,String name,String mobile,byte gender,Date reserveTime) {
		Reserve reserve = new Reserve();
		reserve.setCreateTime(new Date());
		reserve.setInstitutionId(institutionId);
		reserve.setDictionaryId(dictionaryId);
		reserve.setGender(gender);
		reserve.setMemberId(memberId);
		reserve.setMobile(mobile);
		reserve.setName(name);
		reserve.setReserveTime(reserveTime);
		reserve.setStatus(ReserveStatus.RESERVE.value());
		reserveService.saveNotNull(reserve);
		return memberService.getLastId();
	}
	
	/**
	 * 到检
	 * @param reserveId
	 * @throws BuzException 
	 */
	@Transactional(rollbackFor=BuzException.class)
	public void signIn(int reserveId) throws BuzException {
		Reserve reserve = reserveService.selectByKey(reserveId);
		if (reserve == null) {
			throw new BuzException("预约不存在");
		}
		reserve.setStatus(ReserveStatus.ACCEPT_RESERVE.value());
		reserveService.updateNotNull(reserve);
		Result result = new Result();
		result.setExamDictId(reserve.getDictionaryId());
		result.setReserveId(reserveId);
		result.setMemberId(reserve.getMemberId());
		result.setStatus(ResultStatus.NOT_RESULT.value());
		resultService.saveNotNull(result);
	}
	
	/**
	 * 根据用户ID获取结果
	 * @param memberId
	 * @return
	 */
	public List<ReserveDetailDto> getReserveDetailList(int institutionId){
		Example reservExample = new Example(Reserve.class);
		reservExample
			.createCriteria()
			.andEqualTo("institutionId", institutionId);
		List<Reserve> reserves = reserveService.selectByExample(reservExample);
		return reserves.stream().map(re->{
			ReserveDetailDto reserveDetailDto = new ReserveDetailDto();
			Member member = memberService.selectByKey(re.getMemberId());
			Date birthDay = member.getBirthday();
			int age = UserElementUtil.getAgeByBirthday(birthDay);
	        reserveDetailDto.setAge(age);
	        reserveDetailDto.setGender(re.getGender());
	        reserveDetailDto.setName(re.getName());
	        reserveDetailDto.setReserveId(re.getId());
	        reserveDetailDto.setReserveTime(re.getReserveTime());
	        reserveDetailDto.setStatus(re.getStatus());
			return reserveDetailDto;
		}).collect(Collectors.toList());
	}
	
	@Transactional(rollbackFor=BuzException.class)
	public void memberExamStatus(int resultId,String resule,String summary,String departDoctor, String checkDoctor) throws BuzException{
		Result result = resultService.selectByKey(resultId);
		result.setStatus(ResultStatus.HAS_RESULT.value());
		result.setCheckDoctor(checkDoctor);
		result.setDepartDoctor(departDoctor);
		result.setResult(resule);
		result.setSummary(summary);
		resultService.updateNotNull(result);
		// 将数据写入chaincode里
		InstitutionDictionary institutionDictionary = institutionDictionaryService.selectByKey(result.getExamDictId());
		InstitutionDepart institutionDepart = institutionDepartService.selectByKey(institutionDictionary.getDepartId());
		
		String arg1 = "{\"UserID\":\""+result.getMemberId()+"\", \"DepartID\":\""+institutionDepart.getId()+"\", \"ItemID\":\""+institutionDictionary.getId()+"\", \"Result\":\""+resule+"\", \"Doctor\":\""+checkDoctor+"\"}";
		String arg2 = "{\"UserID\":\""+result.getMemberId()+"\", \"DepartID\":\""+institutionDepart.getId()+1+"\", \"ItemID\":\""+institutionDictionary.getId()+1+"\", \"Result\":\""+summary+"\", \"Doctor\":\""+departDoctor+"\"}";
		String[] args = new String[] {arg1,arg2}; 
		BaseResp baseResp = hdcChainCodeManager.putData(args);
		if (!baseResp.getRet().equals("ok")) {
			throw new BuzException("操作chaincode错误->存储体检项结论失败");
		}
	}
	@Transactional(rollbackFor=BuzException.class)
	public MemberInstResultDto getMmeberResultById(int reserveId) {
		Example resultExample = new Example(Result.class);
		resultExample
			.createCriteria()
			.andEqualTo("reserveId", reserveId);
		List<Result> results = resultService.selectByExample(resultExample);
		if (CollectionUtils.isEmpty(results)) {
			return null;
		}
		Result res = results.get(0);
		int dictionaryId = res.getExamDictId();
		InstitutionDictionary dictionary = institutionDictionaryService.selectByKey(dictionaryId);
		int departId = dictionary.getDepartId();
		InstitutionDepart depart = institutionDepartService.selectByKey(departId);
		MemberInstResultDto dto = new MemberInstResultDto();
		dto.setCheckDoctor(res.getCheckDoctor());
		dto.setDepartDoctor(depart.getName());
		dto.setResult(res.getResult());
		dto.setResultId(res.getId());
		dto.setDictionaryId(dictionaryId);
		dto.setDepartId(departId);
		dto.setSummary(res.getSummary());
		dto.setDepartName(depart.getName());
		dto.setCheckDate(res.getCreateTime());
		return dto;
	}
	@Transactional(rollbackFor=BuzException.class)
	public void memberExamResult(int resultId, String result, String checkDoctor) throws BuzException {
		Result sresult = resultService.selectByKey(resultId);
		sresult.setStatus(ResultStatus.HAS_RESULT.value());
		sresult.setCheckDoctor(checkDoctor);
		sresult.setResult(result);
		resultService.updateNotNull(sresult);
		// 将数据写入chaincode里
		InstitutionDictionary institutionDictionary = institutionDictionaryService.selectByKey(sresult.getExamDictId());
		InstitutionDepart institutionDepart = institutionDepartService.selectByKey(institutionDictionary.getDepartId());
		
		String arg1 = "{\"UserID\":\""+sresult.getMemberId()+"\", \"DepartID\":\""+institutionDepart.getId()+"\", \"ItemID\":\""+institutionDictionary.getId()+"\", \"Result\":\""+result+"\", \"Doctor\":\""+checkDoctor+"\"}";
		String[] args = new String[] {arg1}; 
		BaseResp baseResp = hdcChainCodeManager.putData(args);
		if (!baseResp.getRet().equals("ok")) {
			throw new BuzException("操作chaincode错误->存储体检项结论失败");
		}
	}
	@Transactional(rollbackFor=BuzException.class)
	public void memberExamSummary(int resultId, String summary, String departDoctor) throws BuzException {
		Result sresult = resultService.selectByKey(resultId);
		if (!sresult.getStatus().equals(ResultStatus.HAS_RESULT.value())) {
			throw new BuzException("未出结果,不能填结论");
		}
		sresult.setStatus(ResultStatus.HAS_RESULT.value());
		sresult.setDepartDoctor(departDoctor);
		sresult.setSummary(summary);
		resultService.updateNotNull(sresult);
		// 将数据写入chaincode里
		InstitutionDictionary institutionDictionary = institutionDictionaryService.selectByKey((sresult.getExamDictId()));
		InstitutionDepart institutionDepart = institutionDepartService.selectByKey((institutionDictionary.getDepartId()));
		String arg1 = "{\"UserID\":\""+sresult.getMemberId()+"\", \"DepartID\":\""+(institutionDepart.getId()+1)+"\", \"ItemID\":\""+(institutionDictionary.getId()+1)+"\", \"Result\":\""+summary+"\", \"Doctor\":\""+departDoctor+"\"}";
		String[] args = new String[] {arg1}; 
		BaseResp baseResp = hdcChainCodeManager.putData(args);
		if (!baseResp.getRet().equals("ok")) {
			throw new BuzException("操作chaincode错误->存储体检项结论失败");
		}
	}
	

}
