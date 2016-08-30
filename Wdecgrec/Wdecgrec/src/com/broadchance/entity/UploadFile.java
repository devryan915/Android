/**
 * 
 */
package com.broadchance.entity;

import java.util.Date;

/**
 * @author ryan.wang
 * 
 */
public class UploadFile {
	private String fileName;
	private String path;
	private int uploadtimes;
	private Date dataBeginTime;
	private Date dataEndTime;
	private Date uploadDate;
	private UploadFileStatus status;
	private FileType type;
	private String bpath;
	private String hrs;
	

	public String getBpath() {
		return bpath;
	}

	public void setBpath(String bpath) {
		this.bpath = bpath;
	}

	public String getHrs() {
		return hrs;
	}

	public void setHrs(String hrs) {
		this.hrs = hrs;
	}

	public FileType getType() {
		return type;
	}

	public void setType(FileType type) {
		this.type = type;
	}

	public UploadFileStatus getStatus() {
		return status;
	}

	public void setStatus(UploadFileStatus status) {
		this.status = status;
	}

	public Date getUploadDate() {
		return uploadDate;
	}

	public void setUploadDate(Date uploadDate) {
		this.uploadDate = uploadDate;
	}

	public Date getDataBeginTime() {
		return dataBeginTime;
	}

	public void setDataBeginTime(Date dataBeginTime) {
		this.dataBeginTime = dataBeginTime;
	}

	public Date getDataEndTime() {
		return dataEndTime;
	}

	public void setDataEndTime(Date dataEndTime) {
		this.dataEndTime = dataEndTime;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public int getUploadtimes() {
		return uploadtimes;
	}

	public void setUploadtimes(int uploadtimes) {
		this.uploadtimes = uploadtimes;
	}

}
