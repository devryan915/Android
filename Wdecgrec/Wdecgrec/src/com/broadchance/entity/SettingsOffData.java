/**
 * 
 */
package com.broadchance.entity;

/**
 * @author ryan.wang
 * 
 */
public class SettingsOffData {
	private int capacity;
	private String dataTime;
	private boolean isSelect;

	public String getDataTime() {
		return dataTime;
	}

	public void setDataTime(String dataTime) {
		this.dataTime = dataTime;
	}

	public int getCapacity() {
		return capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

}
