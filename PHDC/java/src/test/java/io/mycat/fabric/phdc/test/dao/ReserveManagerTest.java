package io.mycat.fabric.phdc.test.dao;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import io.mycat.fabric.phdc.PHDCApplication;
import io.mycat.fabric.phdc.entity.Reserve;
import io.mycat.fabric.phdc.service.ReserveService;

@SpringBootTest(classes=PHDCApplication.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class ReserveManagerTest {
	
	@Autowired
	ReserveService reserveService;
	
	@Test
	public void testADD() {
		Reserve reserve = new Reserve();
		reserve.setCheckDate(new Date());
		reserve.setDictionaryId(1);
		reserve.setCreateTime(new Date());
		reserve.setGender((byte)1);
		reserve.setInstitutionId(1);
		reserve.setMemberId(1);
		reserve.setMobile("111111111");
		reserve.setName("raindropsOxO");
		reserve.setReserveTime(new Date());
		reserve.setStatus((byte)1);
		reserveService.saveNotNull(reserve);
	}
	
}
