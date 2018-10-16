package io.mycat.fabric.phdc.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DateUtil {
	/** 
     * 判断字符串是否为日期字符串 
     * @param date 日期字符串 
     * @return true or false 
     */  
    public static boolean isDate(String date) {
        boolean isDate = false;  
        if (date != null) {  
            if (StringToDate(date) != null) {  
                isDate = true;  
            }  
        }  
        return isDate;  
    }  
  
    /** 
     * 获取日期字符串的日期风格。失敗返回null。 
     * @param date 日期字符串 
     * @return 日期风格 
     */  
    public static DateStyle getDateStyle(String date) {
        DateStyle dateStyle = null;  
        Map<Long, DateStyle> map = new HashMap<Long, DateStyle>();
        List<Long> timestamps = new ArrayList<Long>();
        for (DateStyle style : DateStyle.values()) {  
            Date dateTmp = StringToDate(date, style.getValue());
            if (dateTmp != null) {  
                timestamps.add(dateTmp.getTime());  
                map.put(dateTmp.getTime(), style); 
                break;
            }  
        }  
        dateStyle = map.get(getAccurateDate(timestamps).getTime());  
        return dateStyle;  
    }  
  
    /** 
     * 将日期字符串转化为日期。失败返回null。 
     * @param date 日期字符串 
     * @return 日期 
     */  
    public static Date StringToDate(String date) {
        DateStyle dateStyle = null;  
        return StringToDate(date, dateStyle);  
    }  
  
    /** 
     * 将日期字符串转化为日期。失败返回null。 
     * @param date 日期字符串 
     * @param parttern 日期格式 
     * @return 日期 
     */  
    public static Date StringToDate(String date, String parttern) {
        Date myDate = null;
        if (date != null) {  
            try {  
                myDate = getDateFormat(parttern).parse(date);  
            } catch (Exception e) {
            }  
        }  
        return myDate;  
    }  
  
    /** 
     * 将日期字符串转化为日期。失败返回null。 
     * @param date 日期字符串 
     * @param dateStyle 日期风格 
     * @return 日期 
     */  
    public static Date StringToDate(String date, DateStyle dateStyle) {
        Date myDate = null;
        if (dateStyle == null) {  
            List<Long> timestamps = new ArrayList<Long>();
            for (DateStyle style : DateStyle.values()) {  
                Date dateTmp = StringToDate(date, style.getValue());
                if (dateTmp != null) {  
                    timestamps.add(dateTmp.getTime());  
                    break;
                }  
            }  
            myDate = getAccurateDate(timestamps);  
        } else {  
            myDate = StringToDate(date, dateStyle.getValue());  
        }  
        return myDate;  
    }  
  
    /** 
     * 将日期转化为日期字符串。失败返回null。 
     * @param date 日期 
     * @param parttern 日期格式 
     * @return 日期字符串 
     */  
    public static String DateToString(Date date, String parttern) {
        String dateString = null;
        if (date != null) {  
            try {  
                dateString = getDateFormat(parttern).format(date);  
            } catch (Exception e) {
            }  
        }  
        return dateString;  
    }  
	
	
    /** 
     * 将日期转化为日期字符串。失败返回null。 
     * @param date 日期 
     * @param dateStyle 日期风格 
     * @return 日期字符串 
     */  
    public static String DateToString(Date date, DateStyle dateStyle) {
        String dateString = null;
        if (dateStyle != null) {  
            dateString = DateToString(date, dateStyle.getValue());  
        }  
        return dateString;  
    }  
  
    /** 
     * 将日期字符串转化为另一日期字符串。失败返回null。 
     * @param date 旧日期字符串 
     * @param parttern 新日期格式 
     * @return 新日期字符串 
     */  
    public static String StringToString(String date, String parttern) {
        return StringToString(date, null, parttern);  
    }  
  
    /** 
     * 将日期字符串转化为另一日期字符串。失败返回null。 
     * @param date 旧日期字符串 
     * @param dateStyle 新日期风格 
     * @return 新日期字符串 
     */  
    public static String StringToString(String date, DateStyle dateStyle) {
        return StringToString(date, null, dateStyle);  
    }  
  
    /** 
     * 将日期字符串转化为另一日期字符串。失败返回null。 
     * @param date 旧日期字符串 
     * @param olddParttern 旧日期格式 
     * @param newParttern 新日期格式 
     * @return 新日期字符串 
     */  
    public static String StringToString(String date, String olddParttern, String newParttern) {
        String dateString = null;
        if (olddParttern == null) {  
            DateStyle style = getDateStyle(date);  
            if (style != null) {  
                Date myDate = StringToDate(date, style.getValue());
                dateString = DateToString(myDate, newParttern);  
            }  
        } else {  
            Date myDate = StringToDate(date, olddParttern);
            dateString = DateToString(myDate, newParttern);  
        }  
        return dateString;  
    }  
  
    /** 
     * 将日期字符串转化为另一日期字符串。失败返回null。 
     * @param date 旧日期字符串 
     * @param olddDteStyle 旧日期风格 
     * @param newDateStyle 新日期风格 
     * @return 新日期字符串 
     */  
    public static String StringToString(String date, DateStyle olddDteStyle, DateStyle newDateStyle) {
        String dateString = null;
        if (olddDteStyle == null) {  
            DateStyle style = getDateStyle(date);  
            dateString = StringToString(date, style.getValue(), newDateStyle.getValue());  
        } else {  
            dateString = StringToString(date, olddDteStyle.getValue(), newDateStyle.getValue());  
        }  
        return dateString;  
    }  
  
    /** 
     * 增加日期的年份。失败返回null。 
     * @param date 日期 
     * @param yearAmount 增加数量。可为负数 
     * @return 增加年份后的日期字符串 
     */  
    public static String addYear(String date, int yearAmount) {
        return addInteger(date, Calendar.YEAR, yearAmount);
    }  
      
    /** 
     * 增加日期的年份。失败返回null。 
     * @param date 日期 
     * @param yearAmount 增加数量。可为负数 
     * @return 增加年份后的日期 
     */  
    public static Date addYear(Date date, int yearAmount) {
        return addInteger(date, Calendar.YEAR, yearAmount);
    }  
      
    /** 
     * 增加日期的月份。失败返回null。 
     * @param date 日期 
     * @param yearAmount 增加数量。可为负数 
     * @return 增加月份后的日期字符串 
     */  
    public static String addMonth(String date, int yearAmount) {
        return addInteger(date, Calendar.MONTH, yearAmount);
    }  
      
    /** 
     * 增加日期的月份。失败返回null。 
     * @param date 日期 
     * @param yearAmount 增加数量。可为负数 
     * @return 增加月份后的日期 
     */  
    public static Date addMonth(Date date, int yearAmount) {
        return addInteger(date, Calendar.MONTH, yearAmount);
    }  
      
    /** 
     * 增加日期的天数。失败返回null。 
     * @param date 日期字符串 
     * @param dayAmount 增加数量。可为负数 
     * @return 增加天数后的日期字符串 
     */  
    public static String addDay(String date, int dayAmount) {
        return addInteger(date, Calendar.DATE, dayAmount);
    }  
  
    /** 
     * 增加日期的天数。失败返回null。 
     * @param date 日期 
     * @param dayAmount 增加数量。可为负数 
     * @return 增加天数后的日期 
     */  
    public static Date addDay(Date date, int dayAmount) {
        return addInteger(date, Calendar.DATE, dayAmount);
    }  
      
    /** 
     * 增加日期的小时。失败返回null。 
     * @param date 日期字符串
     * @return 增加小时后的日期字符串 
     */  
    public static String addHour(String date, int hourAmount) {
        return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
    }  
  
    /** 
     * 增加日期的小时。失败返回null。 
     * @param date 日期
     * @return 增加小时后的日期 
     */  
    public static Date addHour(Date date, int hourAmount) {
        return addInteger(date, Calendar.HOUR_OF_DAY, hourAmount);
    }  
      
    /** 
     * 增加日期的分钟。失败返回null。 
     * @param date 日期字符串
     * @return 增加分钟后的日期字符串 
     */  
    public static String addMinute(String date, int hourAmount) {
        return addInteger(date, Calendar.MINUTE, hourAmount);
    }  
  
    /** 
     * 增加日期的分钟。失败返回null。 
     * @param date 日期
     * @return 增加分钟后的日期 
     */  
    public static Date addMinute(Date date, int hourAmount) {
        return addInteger(date, Calendar.MINUTE, hourAmount);
    }  
      
    /** 
     * 增加日期的秒钟。失败返回null。 
     * @param date 日期字符串
     * @return 增加秒钟后的日期字符串 
     */  
    public static String addSecond(String date, int hourAmount) {
        return addInteger(date, Calendar.SECOND, hourAmount);
    }  
  
    /** 
     * 增加日期的秒钟。失败返回null。 
     * @param date 日期
     * @return 增加秒钟后的日期 
     */  
    public static Date addSecond(Date date, int hourAmount) {
        return addInteger(date, Calendar.SECOND, hourAmount);
    }  
  
    /** 
     * 获取日期的年份。失败返回0。 
     * @param date 日期字符串 
     * @return 年份 
     */  
    public static int getYear(String date) {
        return getYear(StringToDate(date));  
    }  
  
    /** 
     * 获取日期的年份。失败返回0。 
     * @param date 日期 
     * @return 年份 
     */  
    public static int getYear(Date date) {
        return getInteger(date, Calendar.YEAR);
    }  
  
    /** 
     * 获取日期的月份。失败返回0。 
     * @param date 日期字符串 
     * @return 月份 
     */  
    public static int getMonth(String date) {
        return getMonth(StringToDate(date));  
    }  
  
    /** 
     * 获取日期的月份。失败返回0。 
     * @param date 日期 
     * @return 月份 
     */  
    public static int getMonth(Date date) {
        return getInteger(date, Calendar.MONTH);
    }  
  
    /** 
     * 获取日期的天数。失败返回0。 
     * @param date 日期字符串 
     * @return 天 
     */  
    public static int getDay(String date) {
        return getDay(StringToDate(date));  
    }  
  
    /** 
     * 获取日期的天数。失败返回0。 
     * @param date 日期 
     * @return 天 
     */  
    public static int getDay(Date date) {
        return getInteger(date, Calendar.DATE);
    }  
      
    /** 
     * 获取日期的小时。失败返回0。 
     * @param date 日期字符串 
     * @return 小时 
     */  
    public static int getHour(String date) {
        return getHour(StringToDate(date));  
    }  
  
    /** 
     * 获取日期的小时。失败返回0。 
     * @param date 日期 
     * @return 小时 
     */  
    public static int getHour(Date date) {
        return getInteger(date, Calendar.HOUR_OF_DAY);
    }  
      
    /** 
     * 获取日期的分钟。失败返回0。 
     * @param date 日期字符串 
     * @return 分钟 
     */  
    public static int getMinute(String date) {
        return getMinute(StringToDate(date));  
    }  
  
    /** 
     * 获取日期的分钟。失败返回0。 
     * @param date 日期 
     * @return 分钟 
     */  
    public static int getMinute(Date date) {
        return getInteger(date, Calendar.MINUTE);
    }  
      
    /** 
     * 获取日期的秒钟。失败返回0。 
     * @param date 日期字符串 
     * @return 秒钟 
     */  
    public static int getSecond(String date) {
        return getSecond(StringToDate(date));  
    }  
  
    /** 
     * 获取日期的秒钟。失败返回0。 
     * @param date 日期 
     * @return 秒钟 
     */  
    public static int getSecond(Date date) {
        return getInteger(date, Calendar.SECOND);
    }  
  
    /** 
     * 获取日期 。默认yyyy-MM-dd格式。失败返回null。 
     * @param date 日期字符串 
     * @return 日期 
     */  
    public static String getDate(String date) {
        return StringToString(date, DateStyle.YYYY_MM_DD);  
    }  
  
    /** 
     * 获取日期。默认yyyy-MM-dd格式。失败返回null。 
     * @param date 日期 
     * @return 日期 
     */  
    public static String getDate(Date date) {
        return DateToString(date, DateStyle.YYYY_MM_DD);  
    }  
  
    /** 
     * 获取日期的时间。默认HH:mm:ss格式。失败返回null。 
     * @param date 日期字符串 
     * @return 时间 
     */  
    public static String getTime(String date) {
        return StringToString(date, DateStyle.HH_MM_SS);  
    }  
  
    /** 
     * 获取日期的时间。默认HH:mm:ss格式。失败返回null。 
     * @param date 日期 
     * @return 时间 
     */  
    public static String getTime(Date date) {
        return DateToString(date, DateStyle.HH_MM_SS);  
    }  
  


    /** 
     * 获取两个日期相差的天数 
     * @param date 日期字符串 
     * @param otherDate 另一个日期字符串 
     * @return 相差天数 
     */  
    public static int getIntervalDays(String date, String otherDate) {
        return getIntervalDays(StringToDate(date), StringToDate(otherDate));  
    }  
      
    /** 
     * @param date 日期 
     * @param otherDate 另一个日期 
     * @return 相差天数 
     */  
    public static int getIntervalDays(Date date, Date otherDate) {
        date = DateUtil.StringToDate(DateUtil.getDate(date));
        long time = Math.abs(date.getTime() - otherDate.getTime());
        return (int)(time/(24 * 60 * 60 * 1000));  
    }  
    
    public static String showTimeOnPage(Date date, Date otherDate){
    	String r = "";
    //	int intervalDays = DateUtils.getIntervalDays(date, otherDate);//间隔时间
    //	if(intervalDays <= 1){//一天之内
    		long msecs = Math.abs(date.getTime()-otherDate.getTime());
    		
    		if(msecs < 60000){// 一分钟内  
	            long seconds = msecs / 1000;  
	            if(seconds == 0){  
	                r = "刚刚";  
	            }else{  
	                r = seconds + "秒前";  
	            }  
	        }else if (msecs >= 60000 && msecs < 3600000){// 一小时内  
	            long mins = msecs / 60000;  
	            r = mins + "分钟前";  
	        }else if(msecs >= 3600000 && msecs < 86400000){//一天内
	        	long hours = msecs / 3600000;  
	            r = hours + "小时前";  
	        }else if(msecs >= 86400000){//大于一天的，如果是当年的，就显示MM_DD_HH_MM_SS，不是当年的，就显示YYYY_MM_DD_HH_MM_SS
	        	int year1 = DateUtil.getYear(date);
 	        	int year2 = DateUtil.getYear(otherDate);
	        	if(year1 == year2){//同一年
	        		r = DateUtil.DateToString(otherDate, DateStyle.MM_DD_HH_MM);
	        	}else{
	        		r = DateUtil.DateToString(otherDate, DateStyle.YYYY_MM_DD);
	        	}
	        }
    		return r;
    }
	
	
	
	 /** 
     * 获取SimpleDateFormat 
     * @param parttern 日期格式 
     * @return SimpleDateFormat对象 
     * @throws RuntimeException 异常：非法日期格式
     */  
    private static SimpleDateFormat getDateFormat(String parttern) throws RuntimeException {
        return new SimpleDateFormat(parttern);
    }  
	
    /** 
     * 获取日期中的某数值。如获取月份 
     * @param date 日期 
     * @param dateType 日期格式 
     * @return 数值 
     */  
    private static int getInteger(Date date, int dateType) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);  
        return calendar.get(dateType);  
    }  
    
    /** 
     * 增加日期中某类型的某数值。如增加日期 
     * @param date 日期字符串 
     * @param dateType 类型 
     * @param amount 数值 
     * @return 计算后日期字符串 
     */  
    private static String addInteger(String date, int dateType, int amount) {
        String dateString = null;
        DateStyle dateStyle = getDateStyle(date);  
        if (dateStyle != null) {  
            Date myDate = StringToDate(date, dateStyle);
            myDate = addInteger(myDate, dateType, amount);  
            dateString = DateToString(myDate, dateStyle);  
        }  
        return dateString;  
    }  
    
    /** 
     * 增加日期中某类型的某数值。如增加日期 
     * @param date 日期 
     * @param dateType 类型 
     * @param amount 数值 
     * @return 计算后日期 
     */  
    private static Date addInteger(Date date, int dateType, int amount) {
        Date myDate = null;
        if (date != null) {  
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);  
            calendar.add(dateType, amount);  
            myDate = calendar.getTime();  
        }  
        return myDate;  
    }  
    
    /** 
     * 获取精确的日期 
     * @param timestamps 时间long集合 
     * @return 日期 
     */  
    private static Date getAccurateDate(List<Long> timestamps) {
        Date date = null;
        long timestamp = 0;  
        Map<Long, long[]> map = new HashMap<Long, long[]>();
        List<Long> absoluteValues = new ArrayList<Long>();
  
        if (timestamps != null && timestamps.size() > 0) {  
            if (timestamps.size() > 1) {  
                for (int i = 0; i < timestamps.size(); i++) {  
                    for (int j = i + 1; j < timestamps.size(); j++) {  
                        long absoluteValue = Math.abs(timestamps.get(i) - timestamps.get(j));
                        absoluteValues.add(absoluteValue);  
                        long[] timestampTmp = { timestamps.get(i), timestamps.get(j) };  
                        map.put(absoluteValue, timestampTmp);  
                    }  
                }  
  
                // 有可能有相等的情况。如2012-11和2012-11-01。时间戳是相等的  
                long minAbsoluteValue = -1;  
                if (!absoluteValues.isEmpty()) {  
                    // 如果timestamps的size为2，这是差值只有一个，因此要给默认值  
                    minAbsoluteValue = absoluteValues.get(0);  
                }  
                for (int i = 0; i < absoluteValues.size(); i++) {  
                    for (int j = i + 1; j < absoluteValues.size(); j++) {  
                        if (absoluteValues.get(i) > absoluteValues.get(j)) {  
                            minAbsoluteValue = absoluteValues.get(j);  
                        } else {  
                            minAbsoluteValue = absoluteValues.get(i);  
                        }  
                    }  
                }  
  
                if (minAbsoluteValue != -1) {  
                    long[] timestampsLastTmp = map.get(minAbsoluteValue);  
                    if (absoluteValues.size() > 1) {  
                        timestamp = Math.max(timestampsLastTmp[0], timestampsLastTmp[1]);
                    } else if (absoluteValues.size() == 1) {  
                        // 当timestamps的size为2，需要与当前时间作为参照  
                        long dateOne = timestampsLastTmp[0];  
                        long dateTwo = timestampsLastTmp[1];  
                        if ((Math.abs(dateOne - dateTwo)) < 100000000000L) {
                            timestamp = Math.max(timestampsLastTmp[0], timestampsLastTmp[1]);
                        } else {  
                            long now = new Date().getTime();
                            if (Math.abs(dateOne - now) <= Math.abs(dateTwo - now)) {
                                timestamp = dateOne;  
                            } else {  
                                timestamp = dateTwo;  
                            }  
                        }  
                    }  
                }  
            } else {  
                timestamp = timestamps.get(0);  
            }  
        }  
  
        if (timestamp != 0) {  
            date = new Date(timestamp);
        }  
        return date;  
    }  
	
	/**
	 * 
	 * @description: 获取指定时间的下一天日期
	 * @param date
	 * @return
	 * @time:2017年6月19日上午10:39:12
	 */
    public static Date getNextDay(Date date) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.DAY_OF_MONTH, +1);  
        date = calendar.getTime();  
        return date;  
    } 
    /**
     * 
     * @description: 获取指定时间的上一天日期
     * @param date
     * @return
     * @time:2017年6月19日上午10:39:49
     */
    public static Date getLastDay(Date date) {  
        Calendar calendar = Calendar.getInstance();  
        calendar.setTime(date);  
        calendar.add(Calendar.DAY_OF_MONTH, -1);  
        date = calendar.getTime();  
        return date;  
    }
    
    public static Date getStartTimeOfDay(Date date) {
    	Calendar day = Calendar.getInstance();
    	day.setTime(date);
    	day.set(Calendar.HOUR_OF_DAY, 0);
    	day.set(Calendar.MINUTE, 0);
    	day.set(Calendar.SECOND, 0);
    	return day.getTime();
    }
    
    public static Long getEndTime() {
    	Calendar todayEnd = Calendar.getInstance();
    	todayEnd.set(Calendar.HOUR_OF_DAY, 23); // Calendar.HOUR 12小时制
    	// HOUR_OF_DAY 24小时制
    	todayEnd.set(Calendar.MINUTE, 59);
    	todayEnd.set(Calendar.SECOND, 59);
    	todayEnd.set(Calendar.MILLISECOND, 999);
    	return todayEnd.getTimeInMillis();
    }
    
    public static Date getEndTimeOfDay(Date date) {
    	Calendar day = Calendar.getInstance();
    	day.setTime(date);
    	day.set(Calendar.HOUR_OF_DAY, 23);
    	day.set(Calendar.MINUTE, 59);
    	day.set(Calendar.SECOND, 59);
    	day.set(Calendar.MILLISECOND, 999);
    	return day.getTime();
    }
    
    /** 
     * 得到本月第一天的日期 
     * @Methods Name getFirstDayOfMonth 
     * @return Date 
     */  
    public static Date getFirstDayOfMonth(Date date)   {     
        Calendar cDay = Calendar.getInstance();     
        cDay.setTime(date);  
        cDay.set(Calendar.DAY_OF_MONTH, 1);  
        return cDay.getTime();     
    }     
    /** 
     * 得到本月最后一天的日期 
     * @Methods Name getLastDayOfMonth 
     * @return Date 
     */  
    public static Date getLastDayOfMonth(Date date)   {     
        Calendar cDay = Calendar.getInstance();     
        cDay.setTime(date);  
        cDay.set(Calendar.DAY_OF_MONTH, cDay.getActualMaximum(Calendar.DAY_OF_MONTH));  
        return cDay.getTime();     
    }  
    /**
     * 
     * @description: 获取上月第一天
     * @return
     * @time:2017年6月20日上午11:03:19
     */
    public static String getFirstDayOfLastMonth()   {     
    	SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
    	Calendar calendar=Calendar.getInstance();
    	calendar.add(Calendar.MONTH, -1);
    	calendar.set(Calendar.DAY_OF_MONTH, 1);
    	return format.format(calendar.getTime());
    } 
    /**
     * 
     * @description: 获取上月最后一天
     * @return
     * @time:2017年6月20日上午11:03:19
     */
    public static String getLastDayOfLastMonth()   {     
    	SimpleDateFormat sf=new SimpleDateFormat("yyyy-MM-dd");
    	Calendar calendar=Calendar.getInstance();
    	int month=calendar.get(Calendar.MONTH);
    	calendar.set(Calendar.MONTH, month-1);
    	calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        return sf.format(calendar.getTime());     
    }

    public static String getTotalTime(String YYYY_MM_DD,String HH_MM_SS) {
		try {
			String total = YYYY_MM_DD + " " + HH_MM_SS;
			DateFormat d = new SimpleDateFormat(DateStyle.YYYY_MM_DD_HH_MM_SS.getValue());
			Date totalDate;
				totalDate = d.parse(total);
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(totalDate);
//			calendar.add(Calendar.MINUTE, minute);
			Date f = calendar.getTime();
			return d.format(f);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
    
  //获取凌晨时间
    public static Date getTodayStartTime(Date date){
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(date);
        todayEnd.set(Calendar.HOUR_OF_DAY, 0);
        todayEnd.set(Calendar.MINUTE, 5);
        todayEnd.set(Calendar.SECOND, 0);
        todayEnd.set(Calendar.MILLISECOND, 0);
        return todayEnd.getTime();
    }
    //获取半夜23:59:59
    public static Date getTodayEnd(Date date){
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.setTime(date);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 0);
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        return todayEnd.getTime();
    }
    
}
