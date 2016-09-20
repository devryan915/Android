package com.langlang.data;

public class City {
	private String name;
	private String weather;
	private String week;
	private String calendar;
	private String temp;
	private String index_d;
	public String getIndex_d() {
		return index_d;
	}

	public void setIndex_d(String index_d) {
		this.index_d = index_d;
	}

	public String getTemp() {
		return temp;
	}

	public void setTemp(String temp) {
		this.temp = temp;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getWeather() {
		return weather;
	}

	public void setWeather(String weather) {
		this.weather = weather;
	}

	public String getWeek() {
		return week;
	}

	public void setWeek(String week) {
		this.week = week;
	}

	public String getCalendar() {
		return calendar;
	}

	public void setCalendar(String calendar) {
		this.calendar = calendar;
	}

	@Override
	public String toString() {
		return "City [name=" + name + ", weather=" + weather + ", week=" + week
				+ ", calendar=" + calendar + ", temp=" + temp + ", index_d="
				+ index_d + "]";
	}

}

