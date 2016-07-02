package com.langlang.activity;



import com.langlang.elderly_langlang.R;
import com.langlang.utils.DeviceMessage;
import com.langlang.utils.HttpUtils;
import com.langlang.utils.UIUtil;

import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.Toast;

public class LoadingActivity extends BaseActivity {
	private ImageView imageView;
	private Animation animation;
	private SharedPreferences app_skin;
	   //获取手机屏幕分辨率的类  
    private DisplayMetrics dm;  
    int widthPixels;
    int heightPixels;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_loading);
		
		 dm = new DisplayMetrics();  
         getWindowManager().getDefaultDisplay().getMetrics(dm);  
         widthPixels=dm.widthPixels;
         heightPixels=dm.heightPixels;  
         DeviceMessage.getInstance().getDeviceData().setHeightPixels(heightPixels);
         DeviceMessage.getInstance().getDeviceData().setWidthPixels(widthPixels);
                 //获得手机的宽度和高度像素单位为px  
         System.out.println("屏幕宽高："+widthPixels+"："+heightPixels);
 		if (DeviceMessage.getInstance().getDeviceData().getHeightPixels() >=800
				&& DeviceMessage.getInstance().getDeviceData().getWidthPixels() >=480) {
		} else {
			LoadingActivity.this.finish();
			UIUtil.setToast(LoadingActivity.this,"手机分辨率过低");
		}
		app_skin=getSharedPreferences("app_skin",MODE_PRIVATE);
		showNetWork();
		imageView = (ImageView) this.findViewById(R.id.qqs);

		animation = AnimationUtils.loadAnimation(
				imageView.getContext(), R.anim.myanim);
		imageView.startAnimation(animation);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				// startActivity(new Intent(MainActivity.this,Asa.class));
				// MainActivity.this.finish();

				startActivity(new Intent(LoadingActivity.this,
						LoginActivity.class));
				LoadingActivity.this.finish();

			}
		});
	}

	private void showNetWork() {
		if (HttpUtils.isNetworkAvailable(LoadingActivity.this) == false) {
			Toast.makeText(LoadingActivity.this, "网络连接失败", Toast.LENGTH_SHORT)
					.show();
		}
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		imageView.clearAnimation();
		animation = null;
		System.gc();
	}

}
