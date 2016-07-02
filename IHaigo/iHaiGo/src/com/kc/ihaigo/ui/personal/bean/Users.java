/**
 * @Title: Users.java
 * @Package: com.kc.ihaigo.ui.personal.bean
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-helen.yang

 * @date 2014年7月27日 下午6:38:32

 * @version V1.0

 */


package com.kc.ihaigo.ui.personal.bean;

import org.json.JSONException;
import org.json.JSONObject;

import com.kc.ihaigo.model.User;

/**
 * @ClassName: Users
 * @Description: TODO
 * @author: helen.yang
 * @date: 2014年7月27日 下午6:38:32
 *
 */

public class Users {
	private String nextLvSore;
//	private int nextLvSore;
	private String title;
	private String rank;
	private String sex;
	private String headPortnextrait;
	private String nickName;
	private String nextRank;
	private String score;
	private String introduce;
	private String code;
	
	public static Users parse(String jsonString) {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			return Users.parse(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return null;
	}

	public static Users parse(JSONObject jsonObject) {
		if (null == jsonObject) {
			return null;
		}

		Users user = new Users();
		user.nextLvSore = jsonObject.optString("nextLvSore", "");
		user.title = jsonObject.optString("title", "");
		user.rank = jsonObject.optString("rank", "");
		user.sex = jsonObject.optString("sex", "");
		user.headPortnextrait = jsonObject.optString("headPortnextrait", "");
		user.nickName = jsonObject.optString("nickName", "");
		user.nextRank = jsonObject.optString("nextRank", "");
		user.score = jsonObject.optString("score", "");
		user.introduce = jsonObject.optString("introduce", "");
		user.code = jsonObject.optString("code", "");

		return user;
	}
}
