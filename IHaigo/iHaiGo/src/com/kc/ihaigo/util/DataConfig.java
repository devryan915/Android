/**
 * @Title: DataConfig.java
 * @Package: com.kc.ihaigo.util
 * @Description: TODO
 * Copyright: Copyright (c) 2014 
 * Company:上海坤创信息技术有限公司
 * 

 * @author Comsys-ryan.wang

 * @date 2014年7月10日 下午1:53:07

 * @version V1.0

 */

package com.kc.ihaigo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

/**
 * @ClassName: DataConfig
 * @Description: 接口配置文件工具类
 * @author: ryan.wang
 * @date: 2014年7月10日 下午1:53:07
 * 
 */

public class DataConfig {
	private SharedPreferences spf;

	public void setLevelUpdateTime(long levUpdateTime) {
		spf.edit().putLong("levellut", levUpdateTime).commit();
	}

	public long getLevelUpdateTime() {
		return spf.getLong("levellut", 0);
	}

	public void setLevel(String levl) {
		spf.edit().putString("level", levl).commit();
	}

	public String getLevel() {
		return spf.getString("level", null);
	}

	public void setLcompanyUpdateTime(long lcompanylut) {
		spf.edit().putLong("lcompanylut", lcompanylut).commit();
	}

	public long getLcompanyUpdateTime() {
		return spf.getLong("lcompanylut", 0);
	}

	public void setTcompanyUpdateTime(long lcompanylut) {
		spf.edit().putLong("tcompanylut", lcompanylut).commit();
	}

	public long getTcompanyUpdateTime() {
		return spf.getLong("tcompanylut", 0);
	}

	public void setLcompany(String lcompany) {
		spf.edit().putString("lcompany", lcompany).commit();
		String id;
		try {
			JSONObject way = new JSONObject(lcompany);
			JSONArray company = way.getJSONArray("company");
			for (int i = 0; i < company.length(); i++) {
				id = company.getJSONObject(i).getString("id");
				spf.edit()
						.putString("lcompany_" + id,
								company.getJSONObject(i).toString()).commit();

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getLcompanyName(String id) {
		String name = null;

		String com = spf.getString("lcompany_" + id, null);
		JSONObject company;
		try {
			if (!TextUtils.isEmpty(com)) {
				company = new JSONObject(com);
				name = company.getString("name");
			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return name;

	}

	public String getLcompanyIcon(String id) {
		String icon = null;

		String com = spf.getString("lcompany_" + id, null);
		JSONObject company;
		try {
			if (!TextUtils.isEmpty(com)) {
				company = new JSONObject(com);

				icon = company.getString("icon");
			}

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		return icon;

	}

	public String getLcompanyString(String id) {
		return spf.getString("lcompany_" + id, null);
	}

	public String getLcompany() {
		return spf.getString("lcompany", null);
	}

	public void setTcompany(String tcompany) {
		Map<String, Object> map = new HashMap<String, Object>();
		spf.edit().putString("tcompany", tcompany).commit();

		try {
			JSONObject jObject;
			// 获得JSONObject 对象
			jObject = new JSONObject(tcompany);
			JSONArray Tcompany = jObject.getJSONArray("company");
			for (int i = 0; i < Tcompany.length(); i++) {
				JSONObject tcomapny = Tcompany.getJSONObject(i);

				map.put(tcomapny.getString("id"), tcomapny.get("icon"));
				// 根据ID存储数据
				spf.edit()
						.putString("tcompany_" + tcomapny.getString("id"),
								tcomapny.toString()).commit();
			}
			// parseJsonEventList(tcompany);
			spf.edit().putString("tcompany", tcompany).commit();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @Title: getTcompanySty
	 * @user: ryan.wang
	 * @Description: 获取单个转运公司
	 * 
	 * @param id
	 * @return String
	 * @throws
	 */
	public String getTcompanySty(String id) {
		String Tcompany = spf.getString("tcompany_" + id, null);
		return Tcompany;

	}

	private static Map<String, Object> parseJsonEventList(String jsonStr)
			throws Exception {

		Map<String, Object> map = new HashMap<String, Object>();
		List<TransportMOdel> list = new ArrayList<TransportMOdel>();
		TransportMOdel TransportBean = null;
		JSONObject jObject;
		// 获得JSONObject 对象
		jObject = new JSONObject(jsonStr);
		JSONArray Tcompany = jObject.getJSONArray("company");

		Log.e("dataConfig-------", Tcompany.toString() + "");

		for (int i = 0; i < Tcompany.length(); i++) {
			JSONObject tcomapny = Tcompany.getJSONObject(i);

			map.put(tcomapny.getString("id"), tcomapny.get("icon"));

			TransportBean = new TransportMOdel();
			TransportBean.setId(tcomapny.getString("id"));
			TransportBean.setName(tcomapny.getString("name"));
			TransportBean.setIcon(tcomapny.getString("icon"));
			list.add(TransportBean);
		}
		map.put("Transportlist", list);

		return map;
	}

	public String getTcompany() {
		return spf.getString("tcompany", null);
	}

	/**
	 * 
	 * @Title: getTcompanyIconById
	 * @author: Lijie
	 * @Description: 根据转运公司id获取icon
	 * @return String
	 */
	public String getTcompanyIconById(String sourceId) {
		if (TextUtils.isEmpty(sourceId))
			return null;
		String sourceString = getTcompany();
		if (!TextUtils.isEmpty(sourceString)) {
			try {
				JSONObject jObject = new JSONObject(sourceString);
				JSONArray sources = jObject.getJSONArray("company");
				for (int i = 0; i < sources.length(); i++) {
					if (sourceId.equals(sources.getJSONObject(i)
							.getString("id"))) {
						return sources.getJSONObject(i).getString("icon");
					}
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @Title: getTcompanyIconById
	 * @user: Lijie
	 * @Description: 根据转运公司仓库id获取仓库Name
	 * @return String
	 */
	public String getTcompanyStorageById(String sourceId) {
		if (TextUtils.isEmpty(sourceId))
			return null;
		String sourceString = getTcompany();
		if (!TextUtils.isEmpty(sourceString)) {
			try {
				JSONObject jObject = new JSONObject(sourceString);
				JSONArray sources = jObject.getJSONArray("company");

				for (int i = 0; i < sources.length(); i++) {
					JSONArray storage = sources.getJSONObject(i).getJSONArray(
							"addresses");
					for (int j = 0; j < storage.length(); j++) {
						storage.getJSONObject(i).getString("id");
						if (sourceId.equals(storage.getJSONObject(j).getString(
								"id"))) {
							return storage.getJSONObject(i).getString("name");
						}

					}
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @Title: getTcompanyIconById
	 * @user: Lijie
	 * @Description: 根据转运公司id获取仓库信息
	 * @return String
	 */
	public String getTcompanyStorageInfoById(String sourceId) {
		if (TextUtils.isEmpty(sourceId))
			return null;
		String sourceString = getTcompany();
		if (!TextUtils.isEmpty(sourceString)) {
			try {
				JSONObject jObject = new JSONObject(sourceString);
				JSONArray sources = jObject.getJSONArray("company");

				for (int i = 0; i < sources.length(); i++) {
					 sources.getJSONObject(i).getJSONArray(
							"addresses");
					if (sourceId.equals(sources.getJSONObject(i)
							.getString("id"))) {
						return sources.getJSONObject(i).getJSONArray("addresses")
								.toString();
					}

				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @Title: getTcompanyServiceById
	 * @user: Lijie
	 * @Description: 根据id获取Service信息
	 * @return String
	 */
	public String getTcompanyServiceById(String sourceId) {
		if (TextUtils.isEmpty(sourceId))
			return null;
		String sourceString = getTcompany();
		if (!TextUtils.isEmpty(sourceString)) {
			try {
				JSONObject jObject = new JSONObject(sourceString);
				JSONArray sources = jObject.getJSONArray("company");

				for (int i = 0; i < sources.length(); i++) {
					JSONArray services = sources.getJSONObject(i).getJSONArray(
							"services");
					if (sourceId.equals(sources.getJSONObject(i)
							.getString("id"))) {
						return sources.getJSONObject(i)
								.getJSONArray("services").toString();
					}
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @Title: getTcompanyChannelsById
	 * @user: Lijie
	 * @Description: 根据转运公司id获取Channels信息
	 * @return String
	 */
	public String getTcompanyChannelsById(String sourceId) {
		if (TextUtils.isEmpty(sourceId))
			return null;
		String sourceString = getTcompany();
		if (!TextUtils.isEmpty(sourceString)) {
			try {
				JSONObject jObject = new JSONObject(sourceString);
				JSONArray sources = jObject.getJSONArray("company");

				for (int i = 0; i < sources.length(); i++) {
					JSONArray services = sources.getJSONObject(i).getJSONArray(
							"channels");
					if (sourceId.equals(sources.getJSONObject(i)
							.getString("id"))) {
						return sources.getJSONObject(i)
								.getJSONArray("channels").toString();
					}
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @Title: getSourceServiceById
	 * @user: ryan.wang
	 * @Description: 根据id获取电商支持服务类型
	 * 
	 * @param sourceId
	 * @return JSONArray
	 * @throws
	 */
	public JSONArray getSourceServiceById(String sourceId) {
		if (TextUtils.isEmpty(sourceId))
			return null;
		String sourceString = getSource();
		if (!TextUtils.isEmpty(sourceString)) {
			try {
				JSONObject jObject = new JSONObject(sourceString);
				JSONArray sources = jObject.getJSONArray("source");
				for (int i = 0; i < sources.length(); i++) {
					if (sourceId.equals(sources.getJSONObject(i)
							.getString("id"))) {
						return sources.getJSONObject(i).getJSONArray("icon");
					}
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @Title: getSourceById
	 * @user: ryan.wang
	 * @Description: 根据sourceid获取电商icon
	 * 
	 * @return String
	 * @throws
	 */
	public String getSourceById(String sourceId) {
		if (TextUtils.isEmpty(sourceId))
			return null;
		String sourceString = getSource();
		if (!TextUtils.isEmpty(sourceString)) {
			try {
				JSONObject jObject = new JSONObject(sourceString);
				JSONArray sources = jObject.getJSONArray("source");
				for (int i = 0; i < sources.length(); i++) {
					if (sourceId.equals(sources.getJSONObject(i)
							.getString("id"))) {
						return sources.getJSONObject(i).getString("icon");
					}
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @Title: getCurrencySymbolByCode
	 * @user: ryan.wang
	 * @Description: 根据货币编号获取货币符号
	 * 
	 * @param code
	 * @return String
	 * @throws
	 */
	public String getCurrencySymbolByCode(String code) {
		if (TextUtils.isEmpty(code))
			return null;
		String currencyString = getCurrency();
		if (!TextUtils.isEmpty(currencyString)) {
			try {
				JSONObject jObject = new JSONObject(currencyString);
				JSONArray currency = jObject.getJSONArray("currency");
				for (int i = 0; i < currency.length(); i++) {
					if (code.equals(currency.getJSONObject(i).getString("code"))) {
						return currency.getJSONObject(i).getString("symbol");
					}
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @Title: getCurrencyNameByCode
	 * @user: ryan.wang
	 * @Description: 获取币种名称
	 * 
	 * @param code
	 * @return String
	 * @throws
	 */
	public String getCurrencyNameByCode(String code) {
		if (TextUtils.isEmpty(code))
			return null;
		String currencyString = getCurrency();
		if (!TextUtils.isEmpty(currencyString)) {
			try {
				JSONObject jObject = new JSONObject(currencyString);
				JSONArray currency = jObject.getJSONArray("currency");
				for (int i = 0; i < currency.length(); i++) {
					if (code.equals(currency.getJSONObject(i).getString("code"))) {
						return currency.getJSONObject(i).getString("name");
					}
				}
			} catch (JSONException e) {

				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * 
	 * @Title: getCurRateByCode
	 * @user: ryan.wang
	 * @Description: 根据货币编号获取货币比率
	 * 
	 * @param code
	 * @return double 返回1个外币可以兑换多少人民币
	 * @throws
	 */
	public double getCurRateByCode(String code) {
		if (TextUtils.isEmpty(code))
			return 0.0;
		String rateString = getRate();
		if (!TextUtils.isEmpty(rateString)) {
			try {
				JSONObject jObject = new JSONObject(rateString);
				JSONArray rate = jObject.getJSONArray("rates");
				for (int i = 0; i < rate.length(); i++) {
					if (code.equals(rate.getJSONObject(i).getString("code"))) {
						return rate.getJSONObject(i).getDouble("rate") * 0.01;
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return 0.0;
	}

	public String getRate() {
		return spf.getString("rate", null);
	}

	public void setRate(String rate) {
		spf.edit().putString("rate", rate).commit();
	}

	public String getAds() {
		return spf.getString("ads", null);
	}

	public void setAds(String ads) {
		spf.edit().putString("ads", ads).commit();
	}

	public String getCurrency() {
		return spf.getString("currency", null);
	}

	public void setCurrency(String currency) {
		spf.edit().putString("currency", currency).commit();
	}

	public String getSource() {
		return spf.getString("source", null);
	}

	public void setSource(String source) {
		spf.edit().putString("source", source).commit();
	}

	public String getCategory() {
		return spf.getString("category", null);
	}

	public void setCategory(String category) {
		spf.edit().putString("category", category).commit();
	}

	public long getAdsUpdateTime() {
		return spf.getLong("adslut", 0);
	}

	public void setAdsUpdateTime(long adsUpdateTime) {
		spf.edit().putLong("adslut", adsUpdateTime).commit();
	}

	public long getCurrencyUpdateTime() {
		return spf.getLong("currencylut", 0);
	}

	public void setCurrencyUpdateTime(long currencyUpdateTime) {
		spf.edit().putLong("currencylut", currencyUpdateTime).commit();
	}

	public long getSourceUpdateTime() {
		return spf.getLong("sourcelut", 0);
	}

	public void setSourceUpdateTime(long sourceUpdateTime) {
		spf.edit().putLong("sourcelut", sourceUpdateTime).commit();
	}

	public long getCategoryUpdateTime() {
		return spf.getLong("categorylut", 0);
	}

	public void setCategoryUpdateTime(long categoryUpdateTime) {
		spf.edit().putLong("categorylut", categoryUpdateTime).commit();
	}

	public DataConfig(Context ctx) {
		spf = ctx.getSharedPreferences("Config", Context.MODE_WORLD_WRITEABLE);
	}
}
