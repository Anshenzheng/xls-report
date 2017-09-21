package com.an.report.pojo;

import com.an.report.util.DBColumn;
import com.an.report.util.ExcelAttribute;

public class FastpassReportDTO {

	@ExcelAttribute(name="Park Ticket VID")
	private String partTicketVID;

	@DBColumn(name="Attraction ID")
	@ExcelAttribute(name="Attraction ID",index=1, width = 25)
	private String attractionID;

	@DBColumn(name="DFP Status Code")
	@ExcelAttribute(name="DFP Status Code",index=2, width = 15)
	private String dfpStatusCode;

	@DBColumn(name="DFP PLU")
	@ExcelAttribute(name="DFP PLU",index=3, width = 10)
	private String dfpPLU;

	@DBColumn(name="Fastpass Type")
	@ExcelAttribute(name="Fastpass Type",index=4, width = 10)
	private String fastpassType;

	@DBColumn(name="Reason Code")
	@ExcelAttribute(name="Reason Code",index=5, width = 10)
	private String reasonCode;

	@DBColumn(name="Bundle")
	@ExcelAttribute(name="Bundle",index=6, width = 10)
	private String bundle;

	@DBColumn(name="DFP Issue Date")
	@ExcelAttribute(name="DFP Issue Date",index=7, width=17)
	private String dfpIssueDate;

	@DBColumn(name="Fasspass ID")
	@ExcelAttribute(name="Fasspass ID",index=8,width = 10)
	private String fastpassID;

	@DBColumn(name="Issued By")
	@ExcelAttribute(name="Issued By",index=9,width = 10)
	private String issuedBy;

	@DBColumn(name="Use Time")
	@ExcelAttribute(name="Use Time",index=10, width = 17)
	private String useTime;

	@DBColumn(name="Guest ID")
	@ExcelAttribute(name="Guest ID",isExport=false)
	private String guestID;

    @ExcelAttribute(name="Cast Notes",index=11,wrapContent = true,width=90)
    private String castNotes;

	public String getPartTicketVID() {
		return partTicketVID;
	}

	public void setPartTicketVID(String partTicketVID) {
		this.partTicketVID = partTicketVID;
	}

	public String getAttractionID() {
		return attractionID;
	}

	public void setAttractionID(String attractionID) {
		this.attractionID = attractionID;
	}

	public String getDfpStatusCode() {
		return dfpStatusCode;
	}

	public void setDfpStatusCode(String dfpStatusCode) {
		this.dfpStatusCode = dfpStatusCode;
	}

	public String getDfpPLU() {
		return dfpPLU;
	}

	public void setDfpPLU(String dfpPLU) {
		this.dfpPLU = dfpPLU;
	}

	public String getFastpassType() {
		return fastpassType;
	}

	public void setFastpassType(String fastpassType) {
		this.fastpassType = fastpassType;
	}

	public String getBundle() {
		return bundle;
	}

	public void setBundle(String bundle) {
		this.bundle = bundle;
	}

	public String getDfpIssueDate() {
		return dfpIssueDate;
	}

	public void setDfpIssueDate(String dfpIssueDate) {
		this.dfpIssueDate = dfpIssueDate;
	}

	public String getGuestID() {
		return guestID;
	}

	public void setGuestID(String guestID) {
		this.guestID = guestID;
	}

	public String getFastpassID() {
		return fastpassID;
	}

	public void setFastpassID(String fastpassID) {
		this.fastpassID = fastpassID;
	}

	public String getIssuedBy() {
		return issuedBy;
	}

	public void setIssuedBy(String issuedBy) {
		this.issuedBy = issuedBy;
	}

	public String getUseTime() {
		return useTime;
	}

	public void setUseTime(String useTime) {
		this.useTime = useTime;
	}

    public String getCastNotes() {
        return castNotes;
    }

    public void setCastNotes(String castNotes) {
        this.castNotes = castNotes;
    }

	public String getReasonCode() {
		return reasonCode;
	}

	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
}
