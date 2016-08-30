package com.broadchance.entity.serverentity;

public class UpLoadData extends Object {
	public UpLoadData() {
	}

	private String datatype;
	private String filename;
	private String starttime;
	private String endtime;
	private String hrs;
	
	public String getHrs() {
		return hrs;
	}
	public void setHrs(String hrs) {
		this.hrs = hrs;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	public String getStarttime() {
		return starttime;
	}
	public void setStarttime(String starttime) {
		this.starttime = starttime;
	}
	public String getEndtime() {
		return endtime;
	}
	public void setEndtime(String endtime) {
		this.endtime = endtime;
	}
	

}
