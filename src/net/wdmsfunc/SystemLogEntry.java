package net.wdmsfunc;

public class SystemLogEntry {

	private String logid;
	private String docname;
	private String docowner;
	private String lastaccessby;
	private String accessdatetime;
	private String optype;
	private String opdesc;
	private String opresult;
	
	SystemLogEntry (String logid, String doc_name, String doc_owner, String last_access_by, String access_date_time,String op_type,String op_desc,String op_result) {
		this.logid = logid;
		this.docname = doc_name;
		this.docowner = doc_owner;
		this.lastaccessby = last_access_by;
		this.accessdatetime = access_date_time;
		this.optype = op_type;
		this.opdesc = op_desc;
		this.opresult = op_result;
	}
	
	public String getLogid()
	{
		return this.logid;
	}
	
	public void setLogid(String logid)
	{
		this.logid = logid;
	}
	
	public String getDocname()
	{
		return this.docname;
	}
	
	public void setDocname(String doc_name)
	{
		this.docname = doc_name;
	}
	
	public String getDocowner()
	{
		return this.docowner;
	}
	
	public void setDocowner(String doc_owner)
	{
		this.docowner = doc_owner;
	}
	
	public String getLastaccessby()
	{
		return this.lastaccessby;
	}
	
	public void setLastaccessby(String last_access_by)
	{
		this.lastaccessby = last_access_by;
	}
	
	public String getAccessdatetime()
	{
		return this.accessdatetime;
	}
	
	public void setAccessdatetime(String access_date_time)
	{
		this.accessdatetime = access_date_time;
	}

	public void setOptype(String op_type) {
		this.optype = op_type;
	}

	public String getOptype() {
		return this.optype;
	}

	public void setOpdesc(String op_desc) {
		this.opdesc = op_desc;
	}

	public String getOpdesc() {
		return this.opdesc;
	}

	public void setOpresult(String op_result) {
		this.opresult = op_result;
	}

	public String getOpresult() {
		return this.opresult;
	}

}