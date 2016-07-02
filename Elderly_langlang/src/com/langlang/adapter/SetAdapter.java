package com.langlang.adapter;

import com.langlang.elderly_langlang.R;
import com.langlang.global.UserInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SetAdapter extends BaseAdapter{
	private Context context;
	private String [] data={"初始化姿态","我的信息","密码设置","选项设置与设备信息","换肤","系统更新","关于软件"};
	private String [] datas={"我的信息","密码设置","换肤","系统更新","关于软件"};
	private String [] data3={"初始化姿态","换肤","关于软件"};
	
	private int[] image = { R.drawable.seting_preset, R.drawable.seting_myinfo,
			R.drawable.seting_password, R.drawable.setting_device,R.drawable.seting_skin,R.drawable.seting_update,R.drawable.seting_aboutus};
	
	private int[] images = {R.drawable.seting_myinfo,
			R.drawable.seting_password, R.drawable.seting_skin,
			R.drawable.seting_update,R.drawable.seting_aboutus};
	
	
	private int[] image3 = {R.drawable.seting_preset,R.drawable.seting_skin,R.drawable.seting_aboutus};
	
	
	private LayoutInflater inflater;
	public SetAdapter(Context context){
		this.context=context;
		inflater=LayoutInflater.from(context);
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		
		if("guardian".equals(UserInfo.getIntance().getUserData().getRole())){
			System.out.println("setadapter1");
			return datas.length;
		}
		else{
			System.out.println("setadapter2");
			if(UserInfo.getIntance().getUserData().getLogin_mode()==1){
				return data3.length;
			}else{
			return data.length;
			}
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return data[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder;
		if(convertView==null){
			viewHolder=new ViewHolder();
			convertView=inflater.inflate(R.layout.item_set, null);
			viewHolder.imageView=(ImageView)convertView.findViewById(R.id.set_item_image);
			viewHolder.textView=(TextView)convertView.findViewById(R.id.set_item_tw);
			convertView.setTag(viewHolder);
		}else{
			viewHolder=(ViewHolder)convertView.getTag();
		}
		if("guardian".equals(UserInfo.getIntance().getUserData().getRole())){
			viewHolder.imageView.setBackgroundResource(images[position]);
			viewHolder.textView.setText(datas[position]);
			System.out.println("setadapter11");
		}else{
			System.out.println("setadapter22");
			if(UserInfo.getIntance().getUserData().getLogin_mode()==1){
				viewHolder.imageView.setBackgroundResource(image3[position]);
				viewHolder.textView.setText(data3[position]);
			}else{
			
			viewHolder.imageView.setBackgroundResource(image[position]);
			viewHolder.textView.setText(data[position]);
		}}
		return convertView;
	}
	class ViewHolder{
		ImageView imageView;
		TextView textView;
	}

}
