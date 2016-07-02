package com.broadchance.wdecgrec.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.broadchance.entity.Contact;
import com.broadchance.manager.SkinManager;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.settings.AddFamilyActivity;

public class AddFamilyAdapter extends BaseAdapter {

	private Context ctx;
	private ArrayList<Contact> contacts = null;

	public AddFamilyAdapter(Context ctx, ArrayList<Contact> contacts) {
		this.ctx = ctx;
		this.contacts = contacts;
	}

	public void resetContact() {
		for (Contact contact : contacts) {
			contact.setNewAdd(false);
			contact.setDeleteStatus(false);
			if (contact.isAddButton()) {
				contact.setAddButtonSel(false);
			}
			if (contact.isDeleteButton()) {
				contact.setDeleteButtonSel(false);
			}
		}
	}

	public void setAddButtonSel(boolean sel) {
		for (Contact contact : contacts) {
			if (contact.isAddButton()) {
				contact.setAddButtonSel(sel);
			}
			if (contact.isDeleteButton()) {
				contact.setDeleteButtonSel(!sel);
			}
		}
	}

	public void setDeleteStatus() {
		for (Contact contact : contacts) {
			if (!contact.isAddButton() && !contact.isDeleteButton()) {
				contact.setNewAdd(false);
				contact.setDeleteStatus(true);
			}
		}
	}

	public void deleteContact(Contact contact) {
		contacts.remove(contact);
	}

	public void addContact(Contact contact) {
		this.contacts.add(0, contact);
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
		public View viewContact;
		public View buttonContactAdd;
		public View buttonContactDelete;
		public View viewHead;
		public Button contactDelete;
		public TextView contactName;
		public TextView contactPhoneNo;
		public Contact contact;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(this.ctx).inflate(
					R.layout.listview_addfamily_item, null);
			holder = new ViewHolder();
			holder.viewContact = convertView.findViewById(R.id.viewContact);
			holder.buttonContactAdd = convertView
					.findViewById(R.id.buttonContactAdd);
			holder.buttonContactDelete = convertView
					.findViewById(R.id.buttonContactDelete);
			holder.viewHead = convertView.findViewById(R.id.viewHead);
			holder.contactDelete = (Button) convertView
					.findViewById(R.id.contactDelete);
			holder.contactName = (TextView) convertView
					.findViewById(R.id.contactName);
			holder.contactPhoneNo = (TextView) convertView
					.findViewById(R.id.contactPhoneNo);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (ctx instanceof AddFamilyActivity) {
					((AddFamilyActivity) ctx).onGridItemClick(v);
				}
			}
		});
		Contact contact = contacts.get(position);
		if (contact.isAddButton()) {
			holder.viewContact.setVisibility(View.GONE);
			holder.buttonContactAdd.setVisibility(View.VISIBLE);
			holder.buttonContactAdd.setBackgroundResource(contact
					.isAddButtonSel() ? R.drawable.addfamily_addmode_sel
					: R.drawable.addfamily_addmode_nor);
			holder.buttonContactDelete.setVisibility(View.GONE);
		} else if (contact.isDeleteButton()) {
			holder.viewContact.setVisibility(View.GONE);
			holder.buttonContactAdd.setVisibility(View.GONE);
			holder.buttonContactDelete.setVisibility(View.VISIBLE);
			holder.buttonContactDelete.setBackgroundResource(contact
					.isDeleteButtonSel() ? R.drawable.addfamily_deletemode_sel
					: R.drawable.addfamily_deletemode_nor);
		} else {
			holder.viewContact.setVisibility(View.VISIBLE);
			holder.buttonContactAdd.setVisibility(View.GONE);
			holder.buttonContactDelete.setVisibility(View.GONE);
			holder.viewHead.setBackground(SkinManager
					.getInstance()
					.getLocalResources()
					.getDrawable(
							contact.isNewAdd() ? R.drawable.addfamily_head_sel
									: R.drawable.addfamily_head_nor));
			holder.contactDelete
					.setVisibility(contact.isDeleteStatus() ? View.VISIBLE
							: View.GONE);
			holder.contactName.setText(contact.getName());
			holder.contactPhoneNo.setText(contact.getPhoneNo());
		}
		holder.contact = contact;
		return convertView;
	}
}
