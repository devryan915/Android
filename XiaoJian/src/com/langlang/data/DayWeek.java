package com.langlang.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DayWeek {

	public String getDay() {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日 ");
		Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
		String str = formatter.format(curDate);
		return str;

	}

	public String getWeek(String pTime) {

		String Week = "";

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Calendar c = Calendar.getInstance();
		try {

			c.setTime(format.parse(pTime));

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 星期一 星期二 星期三 星期四 星期五 星期六 星期天
		if (c.get(Calendar.DAY_OF_WEEK) == 1) {
			Week += "SUN";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 2) {
			Week += "MON";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 3) {
			Week += "TUE";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 4) {
			Week += "WED";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 5) {
			Week += "THU";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 6) {
			Week += "FRI";
		}
		if (c.get(Calendar.DAY_OF_WEEK) == 7) {
			Week += "SAT";
		}

		return Week;
	}
}
