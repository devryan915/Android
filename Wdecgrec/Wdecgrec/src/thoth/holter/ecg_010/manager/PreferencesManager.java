package thoth.holter.ecg_010.manager;

import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.utils.ConstantConfig;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class PreferencesManager {
	private final static String DATA_NAME = "PreferencesManager";
	private static PreferencesManager Instance = null;
	private Context ctx;

	private PreferencesManager(Context context) {
		this.ctx = context;
	}

	/**
	 * 
	 * @param key
	 * @param value
	 */
	public void putBoolean(String key, boolean value) {
		SharedPreferences sp = getSharedPreferences();
		Editor editor = sp.edit();
		editor.putBoolean(key, value);
		editor.commit();
	}

	/**
	 * 存储数据
	 * 
	 * @param key
	 * @param value
	 */
	public void putString(String key, String value) {
		SharedPreferences sp = getSharedPreferences();
		Editor editor = sp.edit();
		editor.putString(key, value);
		editor.commit();
	}

	public void putLong(String key, long value) {
		SharedPreferences sp = getSharedPreferences();
		Editor editor = sp.edit();
		editor.putLong(key, value);
		editor.commit();
	}

	public long getLong(String key) {
		SharedPreferences sp = getSharedPreferences();
		return sp.getLong(key, 0);
	}

	public void putFloat(String key, float value) {
		SharedPreferences sp = getSharedPreferences();
		Editor editor = sp.edit();
		editor.putFloat(key, value);
		editor.commit();
	}

	public float getFloat(String key) {
		SharedPreferences sp = getSharedPreferences();
		return sp.getFloat(key, 0f);
	}

	public void putInt(String key, int value) {
		SharedPreferences sp = getSharedPreferences();
		Editor editor = sp.edit();
		editor.putInt(key, value);
		editor.commit();
	}

	public int getInt(String key) {
		SharedPreferences sp = getSharedPreferences();
		return sp.getInt(key, 0);
	}

	public int getInt(String key, int defValue) {
		SharedPreferences sp = getSharedPreferences();
		return sp.getInt(key, defValue);
	}

	/**
	 * 获取String值
	 * 
	 * @param key
	 * @return 未取得值返回“”
	 */
	public String getString(String key) {
		SharedPreferences sp = getSharedPreferences();
		return sp.getString(key, "");
	}

	public String getString(String key, String def) {
		SharedPreferences sp = getSharedPreferences();
		return sp.getString(key, def);
	}

	/**
	 * 
	 * @param key
	 * @return
	 */
	public boolean getBoolean(String key) {
		SharedPreferences sp = getSharedPreferences();
		return sp.getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defValue) {
		SharedPreferences sp = getSharedPreferences();
		return sp.getBoolean(key, defValue);
	}

	public synchronized static PreferencesManager getInstance() {
		if (Instance == null)
			Instance = new PreferencesManager(AppApplication.Instance);
		return Instance;
	}

	public SharedPreferences getSharedPreferences() {
		return this.ctx.getSharedPreferences(DATA_NAME,
				Context.MODE_MULTI_PROCESS);
	}
}
