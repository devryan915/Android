package com.langlang.data;

public class ECGDate {
	private String getecg;
	private String getsport;
	private String getmls;
	private String getbct;
	private String getskint;
	private String getrct;
	private String getkll;
	private String image;
	private int pose;

	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public String getGetecg() {
		return getecg;
	}

	public void setGetecg(String getecg) {
		this.getecg = getecg;
	}

	public String getGetsport() {
		return getsport;
	}

	public void setGetsport(String getsport) {
		this.getsport = getsport;
	}

	public String getGetmls() {
		return getmls;
	}

	public void setGetmls(String getmls) {
		this.getmls = getmls;
	}

	public String getGetbct() {
		return getbct;
	}

	public void setGetbct(String getbct) {
		this.getbct = getbct;
	}

	public String getGetskint() {
		return getskint;
	}

	public void setGetskint(String getskint) {
		this.getskint = getskint;
	}

	public String getGetrct() {
		return getrct;
	}

	public void setGetrct(String getrct) {
		this.getrct = getrct;
	}

	public String getGetkll() {
		return getkll;
	}

	public void setGetkll(String getkll) {
		this.getkll = getkll;
	}

	@Override
	public String toString() {
		return "ECGDate [getecg=" + getecg + ", getsport=" + getsport
				+ ", getmls=" + getmls + ", getbct=" + getbct + ", getskint="
				+ getskint + ", getrct=" + getrct + ", getkll=" + getkll + "]";
	}
}
