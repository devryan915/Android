package com.broadchance.xiaojian.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.broadchance.xiaojian.CallBack;
import com.broadchance.xiaojian.R;

public class CommonUtil {

	public static String getCurTime() {
		return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
				.format(Calendar.getInstance(Locale.getDefault()).getTime());
	}

	public static String getCurTime(String format) {
		return new SimpleDateFormat(format, Locale.getDefault())
				.format(Calendar.getInstance(Locale.getDefault()).getTime());
	}

	public static Dialog showSetGoalDialog(final Context context,
			final CallBack call) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_show_setgoal, null);
		final EditText goal = (EditText) layout.findViewById(R.id.dialog_goal);
		layout.findViewById(R.id.dialog_setgoal).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View view) {
						if (call != null) {
							call.doBack(goal.getText());
						}
					}
				});
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		return dlg;
	}

	public static Dialog showPeriodDialog(final Context context,
			final CallBack call) {
		final Dialog dlg = new Dialog(context, R.style.DialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_show_playperiod, null);
		ListView lv = (ListView) layout.findViewById(R.id.dialog_ll_period);
		lv.setAdapter(new ArrayAdapter<String>(context,
				R.layout.listview_item_playperiod, context.getResources()
						.getStringArray(R.array.dialog_period)));
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (call != null) {
					call.doBack(((TextView) view).getText());
				}
			}
		});
		final int cFullFillWidth = 10000;
		layout.setMinimumWidth(cFullFillWidth);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		return dlg;
	}

	public static String IntArray2String(int[] array) {
		StringBuffer stringBuffer = new StringBuffer();
		if (array != null && array.length > 0) {
			for (int i = 0; i < array.length; i++) {
				stringBuffer.append(array[i] + ":");
			}
		}
		return stringBuffer.toString();
	}

	public static int[] String2IntArray(String str) {
		int[] intArray = null;
		if (str != null && str.length() > 0) {
			String[] strArray = str.split(":");
			intArray = new int[strArray.length];
			for (int i = 0; i < strArray.length; i++) {
				intArray[i] = Integer.parseInt(strArray[i]);
			}
		}
		return intArray;
	}
}
