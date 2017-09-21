package com.an.report.pojo;

import com.an.report.util.ExcelAttribute;

public class BoFastpassCountsFormattedDTO {
	
	@ExcelAttribute(name="Type")
	private String type;
	
	@ExcelAttribute(name="Blocked Count",index=1)
	private String bkdCount = "0";
	
	@ExcelAttribute(name="Cancelled Count",index=2)
	private String canCount = "0";
	
	@ExcelAttribute(name="Expired Count",index=3)
	private String expCount = "0";
	
	@ExcelAttribute(name="Inquired Count",index=4)
	private String inqCount = "0";
	
	@ExcelAttribute(name="Redeemed Count",index=5)
	private String redCount = "0";
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getBkdCount() {
		return bkdCount;
	}
	public void setBkdCount(String bkdCount) {
		this.bkdCount = bkdCount;
	}
	public String getCanCount() {
		return canCount;
	}
	public void setCanCount(String canCount) {
		this.canCount = canCount;
	}
	public String getExpCount() {
		return expCount;
	}
	public void setExpCount(String expCount) {
		this.expCount = expCount;
	}
	public String getInqCount() {
		return inqCount;
	}
	public void setInqCount(String inqCount) {
		this.inqCount = inqCount;
	}
	public String getRedCount() {
		return redCount;
	}
	public void setRedCount(String redCount) {
		this.redCount = redCount;
	}
	
	
}
