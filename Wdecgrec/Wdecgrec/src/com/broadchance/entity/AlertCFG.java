package com.broadchance.entity;

import com.broadchance.wdecgrec.alert.AlertType;

public class AlertCFG {
	private AlertType id;
	private String limit_raise;
	private Integer intValueRaise;
	private Integer intValueClear;
	private Float floatValueRaise;
	private Float floatValueClear;
	private int delay_raise;
	private String limit_clear;
	private int delay_clear;

	public Integer getIntValueRaise() {
		return intValueRaise;
	}

	public void setIntValueRaise(Integer intValueRaise) {
		this.intValueRaise = intValueRaise;
	}

	public Integer getIntValueClear() {
		return intValueClear;
	}

	public void setIntValueClear(Integer intValueClear) {
		this.intValueClear = intValueClear;
	}

	public Float getFloatValueRaise() {
		return floatValueRaise;
	}

	public void setFloatValueRaise(Float floatValueRaise) {
		this.floatValueRaise = floatValueRaise;
	}

	public Float getFloatValueClear() {
		return floatValueClear;
	}

	public void setFloatValueClear(Float floatValueClear) {
		this.floatValueClear = floatValueClear;
	}

	public AlertType getId() {
		return id;
	}

	public void setId(AlertType id) {
		this.id = id;
	}

	public String getLimit_raise() {
		return limit_raise;
	}

	public void setLimit_raise(String limit_raise) {
		this.limit_raise = limit_raise;
	}

	public int getDelay_raise() {
		return delay_raise;
	}

	public void setDelay_raise(int delay_raise) {
		this.delay_raise = delay_raise;
	}

	public String getLimit_clear() {
		return limit_clear;
	}

	public void setLimit_clear(String limit_clear) {
		this.limit_clear = limit_clear;
	}

	public int getDelay_clear() {
		return delay_clear;
	}

	public void setDelay_clear(int delay_clear) {
		this.delay_clear = delay_clear;
	}

}
