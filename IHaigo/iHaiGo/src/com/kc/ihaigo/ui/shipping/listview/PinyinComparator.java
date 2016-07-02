package com.kc.ihaigo.ui.shipping.listview;

import java.util.Comparator;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author xiaanming
 * 
 */
public class PinyinComparator implements Comparator<JSONObject> {

	public int compare(JSONObject o1, JSONObject o2) {
		try {
			if (o1.getString("letter").equals("@")
					|| o2.getString("letter").equals("#")) {
				return -1;
			} else if (o1.getString("letter").equals("#")
					|| o2.getString("letter").equals("@")) {
				return 1;
			} else {
				return o1.getString("letter").compareTo(o2.getString("letter"));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return -2;
	}
}
