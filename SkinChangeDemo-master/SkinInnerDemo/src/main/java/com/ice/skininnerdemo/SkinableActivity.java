package com.ice.skininnerdemo;

import android.app.Activity;
import android.content.SharedPreferences;

/**
 * 换肤Activity抽象类
 * Created by ice on 14-10-8.
 */
public abstract class SkinableActivity extends Activity implements SharedPreferences.OnSharedPreferenceChangeListener{

    @Override
    protected void onStart() {
        super.onStart();
        initSkin();
    }


    /**
     *  初始化皮肤
     */
    private void initSkin() {
        changeSkin();
        // 注册监听，监听换肤的通知
        SkinConfigManager.getInstance(this).getSkinConfigPreferences()
                .registerOnSharedPreferenceChangeListener(this);

    }


    /**
     * sharedPreferences 内容发生改变时触发
     * @param sharedPreferences
     * @param key sharedPreferences中的key值
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (SkinConfigManager.CURSKINTYPEKEY.equals(key)) {
            changeSkin();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        SkinConfigManager.getInstance(this).getSkinConfigPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }


    /**
     * 更改设置皮肤，SkinableActivity子类必须要实现该方法 完成换肤过程
     */
    protected abstract void changeSkin();

}
