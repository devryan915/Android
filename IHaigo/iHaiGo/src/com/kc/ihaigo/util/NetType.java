package com.kc.ihaigo.util;


public enum NetType {
	WIFI(0, "WIFI"),

	NETWORK_TYPE_1(1, "unicom2G"),

	NETWORK_TYPE_2(2, "mobile2G"),

	NETWORK_TYPE_4(4, "telecom2G"),

	NETWORK_TYPE_5(5, "telecom3G"),

	NETWORK_TYPE_6(6, "telecom3G"),

	NETWORK_TYPE_12(12, "telecom3G"),

	NETWORK_TYPE_8(8, "unicom3G"),

	NETWORK_TYPE_3(3, "unicom3G"),

	NETWORK_TYPE_13(13, "LTE"),

	NETWORK_TYPE_11(11, "IDEN"),

	NETWORK_TYPE_9(9, "HSUPA"),

	NETWORK_TYPE_10(10, "HSPA"),

	NETWORK_TYPE_15(15, "HSPAP"),

	NONE(-1, "none");

	private int code;
	private String name;

	private NetType(int code, String name) {
		this.code = code;
		this.name = name;
	}

	public final int getCode() {
		return this.code;
	}

	public final String getName() {
		return this.name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public static NetType getTypeByCode(int code) {
		NetType[] types = values();
		for (NetType type : types) {
			if (type.getCode() == code) {
				return type;
			}
		}
		return NONE;
	}
}