package com.broadchance.wdecgrec.settings;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.Contacts.Photo;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.broadchance.entity.Contact;
import com.broadchance.utils.ACache;
import com.broadchance.utils.UIUtil;
import com.broadchance.utils.comparator.CharacterParser;
import com.broadchance.utils.comparator.PinyinComparator;
import com.broadchance.utils.comparator.SideBar;
import com.broadchance.utils.comparator.SideBar.OnTouchingLetterChangedListener;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.adapter.AddContactAdapter;
import com.broadchance.wdecgrec.adapter.AddContactAdapter.ViewHolder;
import com.broadchance.wdecgrec.adapter.DialogBleAdapter;
import com.broadchance.wdecgrec.adapter.DialogChooseContactAdapter;
import com.broadchance.wdecgrec.main.ModeActivity;

public class AddContactActivity extends BaseActivity {

	/** 获取库Phon表字段 **/
	private static final String[] PHONES_PROJECTION = new String[] {
			Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID, Phone.CONTACT_ID };

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 头像ID **/
	private static final int PHONES_PHOTO_ID_INDEX = 2;

	/** 联系人的ID **/
	private static final int PHONES_CONTACT_ID_INDEX = 3;
	/**
	 * 所有联系人(包括手机和sim卡)
	 */
	private Map<String, Contact> contactsMap = new HashMap<String, Contact>();

	/**
	 * 汉字转换成拼音的类
	 */
	private CharacterParser characterParser;
	/**
	 * 根据拼音来排列ListView里面的数据类
	 */
	private PinyinComparator pinyinComparator;
	private ListView listViewContacts;
	private SideBar sideBar;
	private TextView dialog;
	private AddContactAdapter adapter;
	DialogChooseContactAdapter.ViewHolder holderSel;
	private List<Contact> contactsList;
	Dialog dialogChoosePhoneNumber;
	private List<String> familyPhoneNumbers;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		findViewById(R.id.buttonTitleBack).setOnClickListener(this);
		familyPhoneNumbers = getIntent().getStringArrayListExtra(
				"familyPhoneNumbers");
		getContacts();
		listViewContacts = (ListView) findViewById(R.id.listViewContacts);
		// 实例化汉字转拼音类
		characterParser = CharacterParser.getInstance();
		pinyinComparator = new PinyinComparator();
		sideBar = (SideBar) findViewById(R.id.sidrbar);
		dialog = (TextView) findViewById(R.id.dialog);
		sideBar.setTextView(dialog);
		// 设置右侧触摸监听
		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {
			@Override
			public void onTouchingLetterChanged(String s) {
				// 该字母首次出现的位置
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					listViewContacts.setSelection(position);
				}
			}
		});
		contactsList = filledData();
		adapter = new AddContactAdapter(AddContactActivity.this, contactsList);
		listViewContacts.setAdapter(adapter);
		// listViewContacts
		// .setOnItemSelectedListener(new OnItemSelectedListener() {
		//
		// @Override
		// public void onItemSelected(AdapterView<?> parent,
		// View view, int position, long id) {
		// showToast("setOnItemSelectedListener onItemSelected");
		// }
		//
		// @Override
		// public void onNothingSelected(AdapterView<?> parent) {
		// showToast("setOnItemSelectedListener onNothingSelected");
		// }
		// });
		// listViewContacts.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> parent, View view,
		// int position, long id) {
		// showToast("onItemClick");
		// }
		// });
	}

	private void getContacts() {
		getPhoneContacts();
		getSIMContacts();
	}

	private List<Contact> filledData() {
		List<Contact> fillDatas = null;
		fillDatas = new ArrayList<Contact>();
		for (Entry<String, Contact> item : contactsMap.entrySet()) {
			Contact contact = item.getValue();
			String name = contact.getName();
			String letter = (String) characterParser.getSelling(name)
					.subSequence(0, 1);
			contact.setLetter(letter);
			fillDatas.add(contact);
		}
		Collections.sort(fillDatas, pinyinComparator);
		return fillDatas;
	}

	public void addFamily(View view) {
		ViewHolder holder = (ViewHolder) view.getTag();
		if (holder.contact.getPhoneNumbers().size() > 1) {
			List<Contact> contacts = new ArrayList<Contact>();
			for (String phoneNumber : holder.contact.getPhoneNumbers()) {
				Contact contact = new Contact();
				contact.setName(holder.contact.getName());
				contact.setPhoneNo(phoneNumber);
				contacts.add(contact);
			}
			LayoutInflater inflater = (LayoutInflater) AddContactActivity.this
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			LinearLayout layout = (LinearLayout) inflater.inflate(
					R.layout.dialog_choosephoneno, null);
			ListView listViewChangeSkin = (ListView) layout
					.findViewById(R.id.listViewBle);
			DialogChooseContactAdapter adapter = new DialogChooseContactAdapter(
					AddContactActivity.this, contacts);
			listViewChangeSkin.setAdapter(adapter);
			listViewChangeSkin
					.setOnItemClickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent,
								View view, int position, long id) {
							DialogChooseContactAdapter.ViewHolder holder = (DialogChooseContactAdapter.ViewHolder) view
									.getTag();
							if (holderSel != null)
								holderSel.viewSel
										.setBackgroundResource(R.drawable.changeskin_nor);
							holderSel = holder;
							holderSel.viewSel
									.setBackgroundResource(R.drawable.changeskin_sel);
						}
					});
			Button btnConectBLe = (Button) layout.findViewById(R.id.buttonSave);
			btnConectBLe.setOnClickListener(this);
			dialogChoosePhoneNumber = UIUtil.buildDialog(
					AddContactActivity.this, layout);
			dialogChoosePhoneNumber.show();
		} else {
			sendResult(holder.contact.getName(), holder.contact.getPhoneNo());
		}
	}

	private void choosePhoneNumber() {
		if (dialogChoosePhoneNumber != null) {
			dialogChoosePhoneNumber.cancel();
			dialogChoosePhoneNumber.dismiss();
			sendResult(holderSel.contact.getName(),
					holderSel.contact.getPhoneNo());
		}
	}

	private void sendResult(String contactName, String phoneNumber) {
		Intent data = new Intent();
		data.putExtra("contactName", contactName);
		data.putExtra("phoneNumber", phoneNumber);
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.buttonSave:
			choosePhoneNumber();
			break;
		case R.id.buttonTitleBack:
			finish();
			break;
		default:
			break;
		}
	}

	void addPhoneContact(String phoneNumber, String contactName) {
		if (familyPhoneNumbers.contains(phoneNumber)) {
			// showToast("此号码已是家属，请勿重复添加");
			return;
		}
		Contact contact = null;
		if (!contactsMap.containsKey(contactName)) {
			contact = new Contact();
			contact.setPhoneNo(phoneNumber);
			contact.setName(contactName);
			List<String> phoneNumbers = new ArrayList<String>();
			phoneNumbers.add(phoneNumber);
			contact.setPhoneNumbers(phoneNumbers);
			contactsMap.put(contactName, contact);
		} else {
			contact = contactsMap.get(contactName);
			List<String> phoneNumbers = contact.getPhoneNumbers();
			if (!phoneNumbers.contains(phoneNumber)) {
				phoneNumbers.add(phoneNumber);
			}
			// if (!contact.equals(contactName)) {
			// // 合并联系人
			// contact.setName(contact.getName() + "，" + contactName);
			// }
		}
	}

	/** 得到手机通讯录联系人信息 **/
	private void getPhoneContacts() {
		ContentResolver resolver = getContentResolver();
		// 获取手机联系人
		Cursor phoneCursor = resolver.query(Phone.CONTENT_URI,
				PHONES_PROJECTION, null, null, null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {

				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				// 得到联系人ID
				Long contactid = phoneCursor.getLong(PHONES_CONTACT_ID_INDEX);
				// 得到联系人头像ID
				Long photoid = phoneCursor.getLong(PHONES_PHOTO_ID_INDEX);
				// 得到联系人头像Bitamp
				Bitmap contactPhoto = null;
				// photoid 大于0 表示联系人有头像 如果没有给此人设置头像则给他一个默认的
				if (photoid > 0) {
					Uri uri = ContentUris.withAppendedId(
							ContactsContract.Contacts.CONTENT_URI, contactid);
					InputStream input = ContactsContract.Contacts
							.openContactPhotoInputStream(resolver, uri);
					contactPhoto = BitmapFactory.decodeStream(input);
				} else {
					contactPhoto = BitmapFactory.decodeResource(getResources(),
							R.drawable.addfamily_head_nor);
				}
				addPhoneContact(phoneNumber, contactName);

			}
			phoneCursor.close();
		}
	}

	/** 得到手机SIM卡联系人人信息 **/
	private void getSIMContacts() {
		ContentResolver resolver = getContentResolver();
		// 获取Sims卡联系人
		Uri uri = Uri.parse("content://icc/adn");
		Cursor phoneCursor = resolver.query(uri, PHONES_PROJECTION, null, null,
				null);
		if (phoneCursor != null) {
			while (phoneCursor.moveToNext()) {
				// 得到手机号码
				String phoneNumber = phoneCursor.getString(PHONES_NUMBER_INDEX);
				// 当手机号码为空的或者为空字段 跳过当前循环
				if (TextUtils.isEmpty(phoneNumber))
					continue;
				// 得到联系人名称
				String contactName = phoneCursor
						.getString(PHONES_DISPLAY_NAME_INDEX);
				// Sim卡中没有联系人头像
				addPhoneContact(phoneNumber, contactName);
			}
			phoneCursor.close();
		}
	}
}
