package com.langlang.data;

public class Library {
private String name;
private String url;
private String time;
private int state;
public String getTime() {
	return time;
}
public int getState() {
	return state;
}
public void setState(int state) {
	this.state = state;
}
public void setTime(String time) {
	this.time = time;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public String getUrl() {
	return url;
}
public void setUrl(String url) {
	this.url = url;
}

}
