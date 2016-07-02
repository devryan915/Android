package com.broadchance.utils.comparator;

import java.util.Comparator;

import org.json.JSONObject;

import com.broadchance.entity.Contact;

/**
 * 
 * @author
 * 
 */
public class PinyinComparator implements Comparator<Contact> {

	public int compare(Contact o1, Contact o2) {
		if (o1.getLetter().equals("@") || o2.getLetter().equals("#")) {
			return -1;
		} else if (o1.getLetter().equals("#") || o2.getLetter().equals("@")) {
			return 1;
		} else {
			return o1.getLetter().compareTo(o2.getLetter());
		}
		// return -2;
	}
}
