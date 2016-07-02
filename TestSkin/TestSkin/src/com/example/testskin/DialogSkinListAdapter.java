package com.example.testskin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class DialogSkinListAdapter extends BaseAdapter {

	private Context ctx;
	private List<Skin> skins = new ArrayList<Skin>();

	public DialogSkinListAdapter(Context ctx, String selSkinID) {
		this.ctx = ctx;
		for (Entry<String, Skin> item : SkinManager.getInstance()
				.getSkinConfig().entrySet()) {
			item.getValue().isSel = selSkinID.equals(item.getValue()
					.getSkinID());
			skins.add(item.getValue());
		}
	}

	public void setSelSkin(String skinID) {
		for (Skin skin : skins) {
			skin.isSel = skin.getSkinID().equals(skinID);
		}
	}

	@Override
	public int getCount() {
		return skins.size();
	}

	@Override
	public Object getItem(int position) {
		return skins.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public View viewSkinImage;
		public TextView textViewSkinName;
		public View viewSel;
		public Skin skin;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_changeskin_item, null);
			holder = new ViewHolder();
			holder.viewSkinImage = convertView.findViewById(R.id.viewSkin);
			holder.textViewSkinName = (TextView) convertView
					.findViewById(R.id.textViewSkinName);
			holder.viewSel = convertView.findViewById(R.id.viewSelSkin);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Skin skin = skins.get(position);
		holder.viewSel
				.setBackgroundResource(skin.isSel ? R.drawable.changeskin_sel
						: R.drawable.changeskin_nor);
		holder.viewSkinImage.setBackground(SkinManager.getInstance()
				.getLocalDrawable(skin.getSkinImageName()));
		holder.textViewSkinName.setText(skin.getSkinName());
		holder.skin = skin;
		return convertView;
	}
}
