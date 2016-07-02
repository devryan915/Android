package com.ice.skininnerdemo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SkinInnerDemoActivity extends SkinableActivity {

    private static final String TAG = "SkinInnerDemoActivity";
    private GridView gv_traffic;
    private Button btn_skin_setting;
    private TextView tv_title;

    private List<TrafficType> trafficTypeList;
    private TrafficTypeAdapter ttAdapter;

    // 定义默认图标数组
    private int[] imageRes = { R.drawable.main_metro, R.drawable.main_passager, R.drawable.main_railway,
            R.drawable.main_taxi, R.drawable.main_waterbus
    };

    //定义标题数组
    private int[] itemName = {R.string.traffic_metro, R.string.traffic_passager, R.string.traffic_railway,
            R.string.traffic_taxi, R.string.traffic_waterbus
    };

    private String[] trafficSkins = new String[]{
            "main_metro.png", "main_passager.png", "main_railway.png",
            "main_taxi.png", "main_waterbus.png"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_skin_main);

        initView();
        bindEvent();
    }


    private void initView() {
        gv_traffic = (GridView)findViewById(R.id.gv_traffic);
        btn_skin_setting = (Button) findViewById(R.id.btn_skin_setting);
        tv_title = (TextView)findViewById(R.id.tv_title);

        setGridViewAdapter(gv_traffic);
    }


    private void bindEvent() {
        btn_skin_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SkinInnerDemoActivity.this, SkinSettingActivity.class);
                startActivity(intent);
            }
        });
    }


    /**
     * 设置GridView适配器
     * @param mGridView
     */
    private void setGridViewAdapter(GridView mGridView) {
        trafficTypeList = new ArrayList<TrafficType>();
        int length = itemName.length;
        for(int i = 0; i < length; i++){
            TrafficType trafficType = new TrafficType();

            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageRes[i]);
            trafficType.setTrafficBitmap(bitmap);
            trafficType.setTrafficName(getString(itemName[i]));

            trafficTypeList.add(trafficType);
        }
        ttAdapter = new TrafficTypeAdapter(this, trafficTypeList);
        mGridView.setAdapter(ttAdapter);
    }


    @Override
    protected void changeSkin() {
        String skinFileName = SkinConfigManager.getInstance(SkinInnerDemoActivity.this).getSkinFileName();
        // Log.d(TAG, "changeSkin() 被执行 / skinFileName: " + skinFileName);

        if(skinFileName != null){
            /* Mark：关于获取assets下面的.9格式图片问题,这里是很有趣的， <br>
             * Tip：该demo中assets下的.9格式图片 都是先使用 AAPT 命令编译处理后的  <br>
             *      后期 小吕将会整理 关于获取assets下的.9格式图片问题
             */
            Drawable drawable = SkinManager.getInstance(this).getSkinDrawable(skinFileName, "bg_title.9.png");
            tv_title.setBackground(drawable);

            int length = trafficSkins.length;
            for (int j = 0; j < length; j++) {
                Bitmap bitmap = SkinManager.getInstance(this).getSkinBitmap(skinFileName, trafficSkins[j]);
                trafficTypeList.get(j).setTrafficBitmap(bitmap);
            }
        } else {
            // 加载默认皮肤
            tv_title.setBackgroundResource(R.drawable.bg_title);

            int length = imageRes.length;
            for (int i = 0; i < length; i++) {
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), imageRes[i]);
                trafficTypeList.get(i).setTrafficBitmap(bitmap);
            }
        }
        ttAdapter.notifyDataSetChanged();
    }

}
