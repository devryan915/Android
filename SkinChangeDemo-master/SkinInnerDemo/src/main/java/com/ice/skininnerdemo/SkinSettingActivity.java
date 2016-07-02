package com.ice.skininnerdemo;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 皮肤设置Activity
 * Created by ice on 14-10-10.
 */
public class SkinSettingActivity extends SkinableActivity{

    private static final String TAG = "SkinSettingActivity";
    private GridView gv_skin_type;
    private TextView tv_skin_cur;
    private TextView tv_title_skin_setting;

    private int[] skinTypeImage = new int[]{
            R.drawable.overview_skin_default, R.drawable.overview_skin_blue,
            R.drawable.overview_skin_orange, R.drawable.overview_skin_red
    };

    private int[] skinTypeName = new int[]{
            R.string.skin_default, R.string.skin_blue,
            R.string.skin_orange, R.string.skin_red
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_setting);
        initView();
        bindEvent();
    }


    private void initView() {
        tv_title_skin_setting = (TextView)findViewById(R.id.tv_title_skin_setting);
        tv_skin_cur = (TextView)findViewById(R.id.tv_skin_cur);
        gv_skin_type = (GridView)findViewById(R.id.gv_skin_type);

        setGridViewAdapter(gv_skin_type);
    }


    private void setGridViewAdapter(GridView mGridView) {
        List<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
        int length = skinTypeImage.length;
        for(int i=0; i<length; i++){
            HashMap<String,Object> map = new HashMap<String, Object>();
            map.put("skinTypeImage", skinTypeImage[i]);
            map.put("skinTypeName", getString(skinTypeName[i]));
            data.add(map);
        }

        SimpleAdapter simpleAdapter = new SimpleAdapter(
                this,
                data,
                R.layout.traffic_item,
                new String[]{"skinTypeImage", "skinTypeName"},
                new int[]{R.id.iv_traffic, R.id.tv_trafficName});

        mGridView.setAdapter(simpleAdapter);
    }


    private void bindEvent() {
        gv_skin_type.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                tv_skin_cur.setText(skinTypeName[i]);
                // 将当前选择的皮肤对应的序号 储存到 SharedPreferences 中
                SkinConfigManager.getInstance(SkinSettingActivity.this).setCurSkinType(i);
            }
        });
    }


    @Override
    protected void changeSkin() {
        SkinConfigManager mSkinConfigManager = SkinConfigManager.getInstance(SkinSettingActivity.this);
        String skinFileName = mSkinConfigManager.getSkinFileName();
        Log.d(TAG, "changeSkin() 被执行 / skinFileName: " + skinFileName);

        // 获取当前正在使用的皮肤序号
        int skinType = mSkinConfigManager.getCurSkinType();
        tv_skin_cur.setText(skinTypeName[skinType]);

        if(skinFileName != null){
            Drawable drawable = SkinManager.getInstance(this).getSkinDrawable(skinFileName, "bg_title.9.png");
            tv_title_skin_setting.setBackground(drawable);
        } else {
            tv_title_skin_setting.setBackgroundResource(R.drawable.bg_title);
        }
    }

}
