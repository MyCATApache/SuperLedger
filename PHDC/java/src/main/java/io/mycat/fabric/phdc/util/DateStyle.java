package io.mycat.fabric.phdc.util;

/**
 * 
 * 日期格式
 * @author xusihan
 *
 */
public enum DateStyle {
	
	
	YYYY_MM_DD_HH_MM_SS("yyyy-MM-dd HH:mm:ss"), 
	YYYY_MM_DD_HH_MM("yyyy-MM-dd HH:mm"),  
	YYYY_MM_DD("yyyy-MM-dd"),  
    YYYY_MM("yyyy-MM"), 
    MM_DD_HH_MM_SS("MM-dd HH:mm:ss"),  
    MM_DD_HH_MM("MM-dd HH:mm"),  
    MM_DD("MM-dd"),  
    HH_MM_SS("HH:mm:ss"),
    HH_MM("HH:mm"), 
   
    YYYYMMDDHHMMSS("yyyyMMddHHmmss"),
    YYYYMMDDHH("yyyyMMddHH"),
    YYYYMMDD("yyyyMMdd"),
     
    DD_HH_MM("dd日 HH:mm"),
    
    YYYYMMdd_Sprit("yyyy/MM/dd"),
    
    YYYYMMdd_Cn("yyyy年MM月dd日"),
    
    YYYYMMdd_Comma("yyyy.MM.dd")
    
   
    ;
    
    private String value;
    
    DateStyle(String value) {
        this.value = value;  
    }  
      
    public String getValue() {
        return value;  
    }  
}
