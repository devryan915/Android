package com.broadchance.wdecgrec.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.broadchance.entity.Contact;
import com.broadchance.utils.UIUtil;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.settings.AddContactActivity;

public class AddContactAdapter extends BaseAdapter implements SectionIndexer {

	private Context ctx;
	private List<Contact> contacts = null;

	public AddContactAdapter(Context ctx, List<Contact> contacts) {
		this.ctx = ctx;
		this.contacts = contacts;
	}

	@Override
	public int getCount() {
		return this.contacts.size();
	}

	@Override
	public Object getItem(int position) {
		return this.contacts.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public class ViewHolder {
		public TextView catalog;
		public View contactHead;
		public TextView contactName;
		public TextView contactPhoneNo;
		public Contact contact;
		public View viewAddContact;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_choosecontact_item, null);
			holder = new ViewHolder();
			holder.catalog = (TextView) convertView.findViewById(R.id.catalog);
			holder.contactHead = convertView.findViewById(R.id.contactHead);
			holder.contactName = (TextView) convertView
					.findViewById(R.id.contactName);
			holder.contactPhoneNo = (TextView) convertView
					.findViewById(R.id.contactPhoneNo);
			holder.viewAddContact = convertView
					.findViewById(R.id.viewAddContact);
			convertView.setTag(holder);
			holder.viewAddContact.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Contact contact = contacts.get(position);
		holder.contact = contact;
		// holder.viewAddContact.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// UIUtil.showToast(ctx, "viewAddContact");
		// if (ctx instanceof AddContactActivity) {
		// ((AddContactActivity) ctx).addFamily(v);
		// }
		// }
		// });
		// 根据position获取分类的首字母的Char ascii值
		int section = getSectionForPosition(position);
		// 如果当前位置等于该分类首字母的Char的位置 ，则认为是第一次出现
		if (position == getPositionForSection(section)) {
			holder.catalog.setVisibility(View.VISIBLE);
			holder.catalog.setText(contact.getLetter());
		} else {
			holder.catalog.setVisibility(View.GONE);
		}
		holder.contactName.setText(contact.getName());
		// List<String> numbers = contact.getPhoneNumbers();
		holder.contactPhoneNo.setText(contact.getPhoneNo());
		return convertView;
	}

	/**
	 * 根据ListView的当前位置获取分类的首字母的Char ascii值
	 */
	public int getSectionForPosition(int position) {
		return contacts.get(position).getLetter().charAt(0);
	}

	/**
	 * 根据分类的首字母的Char ascii值获取其第一次出现该首字母的位置
	 */
	public int getPositionForSection(int section) {
		for (int i = 0; i < getCount(); i++) {
			char firstChar = 0;
			firstChar = contacts.get(i).getLetter().charAt(0);
			if (firstChar == section) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * 提取英文的首字母，非英文字母用#代替。
	 * 
	 * @param str
	 * @return
	 */
	private String getAlpha(String str) {
		String sortStr = str.trim().substring(0, 1).toUpperCase();
		// 正则表达式，判断首字母是否是英文字母
		if (sortStr.matches("[A-Z]")) {
			return sortStr;
		} else {
			return "#";
		}
	}

	@Override
	public Object[] getSections() {
		// TODO Auto-generated method stub
		return null;
	}
}
