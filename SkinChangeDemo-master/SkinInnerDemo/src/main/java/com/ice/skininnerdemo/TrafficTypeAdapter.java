package com.ice.skininnerdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * 首页 显示交通方式Adapter类
 * Created by ice on 14-10-11.
 */
public class TrafficTypeAdapter extends BaseAdapter{

    private List<TrafficType> mList;
    private Context mContext;

    public TrafficTypeAdapter(Context context, List<TrafficType> trafficList) {
        this.mContext = context;
        this.mList = trafficList;

    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {
        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();

            convertView = LayoutInflater.from(mContext).inflate(R.layout.traffic_item, null);
            viewHolder.iv_trafficImage = (ImageView)convertView.findViewById(R.id.iv_traffic);
            viewHolder.tv_trafficName = (TextView)convertView.findViewById(R.id.tv_trafficName);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        TrafficType trafficType = (TrafficType)getItem(position);
        viewHolder.iv_trafficImage.setImageBitmap(trafficType.getTrafficBitmap());
        viewHolder.tv_trafficName.setText(trafficType.getTrafficName());

        return convertView;
    }

    class ViewHolder {
        public TextView tv_trafficName;
        public ImageView iv_trafficImage;
    }

}
