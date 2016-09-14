package com.broadchance.wdecgrec.settings;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;

import com.broadchance.entity.Contact;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.entity.serverentity.StringResponse;
import com.broadchance.entity.serverentity.UIFamily;
import com.broadchance.entity.serverentity.UIFamilyResponse;
import com.broadchance.entity.serverentity.UIFamilyResponseList;
import com.broadchance.manager.DataManager;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.adapter.AddFamilyAdapter;
import com.broadchance.wdecgrec.adapter.AddFamilyAdapter.ViewHolder;

public class AddFamilyActivity extends BaseActivity {

	private GridView gridViewFamily;
	private Button buttonSave;
	private AddFamilyAdapter adapter;
	public final static int REQUEST_ADDFAMILY_CODE = 190;
	/**
	 * 最多可添加家属数量
	 */
	private final static int ADDFAMILY_MAX = 10;
	private ArrayList<String> familyPhoneNumbers;
	/**
	 * 新添加的联系人
	 */
	private Contact newAddContact = null;
	/**
	 * 即将删除的联系人
	 */
	private Contact waitDeleteContact = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_family);
		findViewById(R.id.buttonTitleBack).setOnClickListener(this);
		buttonSave = (Button) findViewById(R.id.buttonSave);
		buttonSave.setOnClickListener(this);
		newAddContact = null;
		waitDeleteContact = null;
		gridViewFamily = (GridView) findViewById(R.id.gridViewFamily);
		ArrayList<Contact> contacts = new ArrayList<Contact>();
		familyPhoneNumbers = new ArrayList<String>();
		Contact contact = null;
		contact = new Contact();
		contact.setAddButton(true);
		contacts.add(contact);
		contact = new Contact();
		contact.setDeleteButton(true);
		contacts.add(contact);
		adapter = new AddFamilyAdapter(AddFamilyActivity.this, contacts);
		gridViewFamily.setAdapter(adapter);
		// gridViewFamily.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// // TODO Auto-generated method stub
		// showToast("setOnItemClickListener");
		// }
		// });
		TextView textViewUseName = (TextView) findViewById(R.id.textViewUseName);
		textViewUseName.setText(DataManager.getUserInfo().getLoginName());
		loadData();
	}

	/**
	 * 从服务端拉取数据
	 */
	private void loadData() {
		serverService.GetMyFamily(DataManager.getUserInfo().getUserID(),
				new HttpReqCallBack<UIFamilyResponseList>() {

					@Override
					public Activity getReqActivity() {
						return AddFamilyActivity.this;
					}

					@Override
					public void doSuccess(UIFamilyResponseList result) {
						if (result.isOk()) {
							Contact contact = null;
							List<UIFamily> family = result.Data;
							for (UIFamily uiFamily : family) {
								contact = new Contact();
								contact.setName(uiFamily.getName());
								contact.setPhoneNo(uiFamily.getLoginName());
								contact.setID(uiFamily.getID());
								familyPhoneNumbers.add(uiFamily.getLoginName());
								adapter.addContact(contact);
							}
							adapter.notifyDataSetChanged();
						} else {
							showToast(result.getMessage());
							// showResponseMsg(result.Code);
						}
					}

					@Override
					public void doError(String result) {
						if (ConstantConfig.Debug) {
							showToast(result);
						} else {
							showToast("操作失败");
						}
					}
				});
	}

	public void showChooseContacts(View view) {
		showChooseContacts();
	}

	public void onGridItemClick(View view) {
		final ViewHolder holder = (ViewHolder) view.getTag();
		if (holder.contact.isAddButton()) {
			if (familyPhoneNumbers.size() < ADDFAMILY_MAX) {
				if (newAddContact == null && waitDeleteContact == null) {
					adapter.resetContact();
					adapter.setAddButtonSel(true);
					adapter.notifyDataSetChanged();
					showChooseContacts();
				} else {
					showToast("请先保存");
				}
			} else {
				showToast("最多可添加" + ADDFAMILY_MAX + "位家属");
			}
		} else if (holder.contact.isDeleteButton()) {
			if (newAddContact == null && waitDeleteContact == null) {
				adapter.setDeleteStatus();
				adapter.setAddButtonSel(false);
				adapter.notifyDataSetChanged();
			} else {
				showToast("请先保存");
			}
		} else if (holder.contact.isDeleteStatus()) {
			if (waitDeleteContact == null) {
				waitDeleteContact = holder.contact;
				adapter.deleteContact(holder.contact);
				adapter.notifyDataSetChanged();
			} else {
				showToast("请先保存");
			}
		}
	}

	private void showChooseContacts() {
		Intent intent = new Intent(AddFamilyActivity.this,
				AddContactActivity.class);
		intent.putExtra("familyPhoneNumbers", familyPhoneNumbers);
		startActivityForResult(intent, REQUEST_ADDFAMILY_CODE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// User chose not to enable Bluetooth.
		if (requestCode == REQUEST_ADDFAMILY_CODE && resultCode == RESULT_OK) {
			String contactName = data.getStringExtra("contactName");
			String phoneNumber = data.getStringExtra("phoneNumber");
			if (!familyPhoneNumbers.contains(phoneNumber)) {
				newAddContact = new Contact();
				newAddContact.setName(contactName);
				newAddContact.setPhoneNo(phoneNumber);
				newAddContact.setNewAdd(true);
				adapter.addContact(newAddContact);
				adapter.notifyDataSetChanged();
			}
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 保存用户数据
	 */
	private void saveData() {
		if (newAddContact == null && waitDeleteContact == null) {
			showToast("请先选择用户");
			return;
		}
		if (waitDeleteContact != null) {
			serverService.DeleteUserFamily(DataManager.getUserInfo()
					.getUserID(), waitDeleteContact.getID(),
					new HttpReqCallBack<StringResponse>() {

						@Override
						public Activity getReqActivity() {
							return AddFamilyActivity.this;
						}

						@Override
						public void doSuccess(StringResponse result) {
							if (result.isOk()) {
								familyPhoneNumbers.remove(waitDeleteContact
										.getPhoneNo());
								adapter.resetContact();
								adapter.notifyDataSetChanged();
								showToast("删除成功");
								waitDeleteContact = null;
							} else {
								showToast(result.getMessage());
								// showResponseMsg(result.Code);
							}
						}

						@Override
						public void doError(String result) {
							if (ConstantConfig.Debug) {
								showToast(result);
							} else {
								showToast("操作失败");
							}
						}
					});
		} else if (newAddContact != null) {
			serverService.AddUserFamily(DataManager.getUserInfo().getUserID(),
					newAddContact.getPhoneNo(), newAddContact.getName(),
					new HttpReqCallBack<UIFamilyResponse>() {

						@Override
						public Activity getReqActivity() {
							return AddFamilyActivity.this;
						}

						@Override
						public void doSuccess(UIFamilyResponse result) {
							if (result.isOk()) {
								adapter.resetContact();
								adapter.notifyDataSetChanged();
								newAddContact.setID(result.Data.getID());
								familyPhoneNumbers.add(newAddContact
										.getPhoneNo());
								newAddContact = null;
								showToast("添加成功");
							}
						}

						@Override
						public void doError(String result) {
							if (ConstantConfig.Debug) {
								showToast(result);
							} else {
								showToast("操作失败");
							}
						}
					});
		}
	}

	private void returnSettingsAcitivity() {
		Intent myIntent = new Intent();
		myIntent = new Intent(AddFamilyActivity.this, SettingsActivity.class);
		startActivity(myIntent);
		this.finish();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.buttonSave:
			saveData();
			break;
		case R.id.buttonTitleBack:
			returnSettingsAcitivity();
			break;
		default:
			break;
		}
	}
}
