package io.mycat.fabric.phdc.util;

import java.util.Calendar;
import java.util.Date;

public class UserElementUtil {

	public static int getAgeByBirthday(Date birthday) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH);
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthday);
 
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH);
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
 
        int age = yearNow - yearBirth;
 
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) age--;
            }else{
                age--;
            }
        }
        return age;
	}
	
	public static void main(String[] args) {
		Date birthday = DateUtil.StringToDate("1994-10-15");
		System.out.println(birthday);
		System.out.println(getAgeByBirthday(birthday));
	}
	
}
