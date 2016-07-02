package com.broadchance.entity;

import java.io.File;

import android.R.integer;

import com.broadchance.entity.serverentity.BaseResponse;

public class DownLoadAPPResponse extends BaseResponse<String> {
	public DownLoadAPPResponse() {
	}
	private File downLoadFile;

	public File getDownLoadFile() {
		return downLoadFile;
	}

	public void setDownLoadFile(File downLoadFile) {
		this.downLoadFile = downLoadFile;
	}

}
