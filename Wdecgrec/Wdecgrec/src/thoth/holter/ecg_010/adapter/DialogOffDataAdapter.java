package thoth.holter.ecg_010.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.broadchance.entity.SettingsOffData;
import thoth.holter.ecg_010.R;

public class DialogOffDataAdapter extends BaseAdapter {

	private Context ctx;
	private List<SettingsOffData> offDataTimes = null;

	public DialogOffDataAdapter(Context ctx, List<SettingsOffData> offDataTimes) {
		this.ctx = ctx;
		this.offDataTimes = offDataTimes;
	}

	public void setSelectData(long capacity) {
		for (SettingsOffData data : offDataTimes) {
			data.setSelect(data.getCapacity() == capacity);
		}
	}

	@Override
	public int getCount() {
		return this.offDataTimes.size();
	}

	@Override
	public Object getItem(int position) {
		return this.offDataTimes.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView textViewBleName;
		public View viewSel;
		public SettingsOffData data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_chooseble_item, null);
			holder = new ViewHolder();
			holder.textViewBleName = (TextView) convertView
					.findViewById(R.id.textViewBleName);
			holder.viewSel = convertView.findViewById(R.id.viewSelSkin);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		SettingsOffData data = offDataTimes.get(position);
		holder.textViewBleName.setText(data.getDataTime());
		holder.viewSel
				.setBackgroundResource(data.isSelect() ? R.drawable.changeskin_sel
						: R.drawable.changeskin_nor);
		holder.data = data;
		return convertView;
	}
}
