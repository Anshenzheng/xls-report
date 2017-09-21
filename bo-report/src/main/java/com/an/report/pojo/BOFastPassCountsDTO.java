package com.an.report.pojo;

import com.an.report.util.DBColumn;
import com.an.report.util.ExcelAttribute;

/**
 * @author aan
 *
 */
public class BOFastPassCountsDTO {
	
	@ExcelAttribute(name="Type")
	@DBColumn(name="Type")
	private String type;
	
	@ExcelAttribute(name="Status",index=1)
	@DBColumn(name="Status")
	private String status;
	
	@ExcelAttribute(name="Count",index=2)
	@DBColumn(name="Count")
	private String count = "0";
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCount() {
		return count;
	}
	public void setCount(String count) {
		this.count = count;
	}
	
	

}
