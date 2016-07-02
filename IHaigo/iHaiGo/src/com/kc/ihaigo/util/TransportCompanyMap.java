package com.kc.ihaigo.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TransportCompanyMap {
	public static JSONArray datas;
	List<TransportMOdel> list = new ArrayList<TransportMOdel>();

	public static Map<String, Object> parseJsonTcompany(String jsonStr) {

		// Log.e("parseJsonEventList====", jsonStr);

		Map<String, Object> map = new HashMap<String, Object>();
		List<TransportMOdel> list = new ArrayList<TransportMOdel>();
		TransportMOdel Tcompany = null;
		// 获得JSONObject 对象
		DataConfig dConfig = new DataConfig(null);
		JSONObject resData;
		try {
			resData = new JSONObject(dConfig.getTcompany());
			datas = resData.getJSONArray("company");
			// map.put(datas.getJSONObject(i).getString("id"),
			// resData.getJSONArray("company"));
			for (int i = 0; i < datas.length(); i++) {
				Tcompany = new TransportMOdel();
				Tcompany.setId(datas.getJSONObject(i).getString("id"));
				Tcompany.setIcon(datas.getJSONObject(i).getString("icon"));
				Tcompany.setName(datas.getJSONObject(i).getString("name"));
				list.add(Tcompany);
				map.put(datas.getJSONObject(i).getString("id"), datas
						.getJSONObject(i).getString("icon"));
			}
			// map.put("companyList", list);

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return map;
	}

}
