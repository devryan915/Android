package com.broadchance.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import thoth.holter.ecg_010.R;
import thoth.holter.ecg_010.main.EcgActivity;
import thoth.holter.ecg_010.main.ModeActivity;
import thoth.holter.ecg_010.manager.AppApplication;
import thoth.holter.ecg_010.services.BleDomainService;

public class UIUtil {
	static Activity context;
	static Toast toast;

	public static void showRemoteToast(String content) {
		// if (BleDomainService.Instance != null) {
		// BleDomainService.Instance.showToast(content);
		// }
		showToast(content);
	}

	public static void showRemoteLongToast(String content) {
		// if (BleDomainService.Instance != null) {
		// BleDomainService.Instance.showLongToast(content);
		// }
		showLongToast(content);
	}

	public static void showToast(final String content) {
		_showToast(content, Toast.LENGTH_SHORT);
	}

	public static void showLongToast(String content) {
		_showToast(content, Toast.LENGTH_LONG);
	}

	private static void _showToast(final String content, final int duration) {
		try {
			if ((context = ModeActivity.Instance) == null) {
				context = EcgActivity.Instance;
			}
			if (context != null) {
				context.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Toast.makeText(context, content, duration).show();
					}
				});
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void showToast(Context context, String content) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
		toast.show();
	}

	public static void showLongToast(Context context, String content) {
		Toast.makeText(context, content, Toast.LENGTH_LONG).show();
	}

	public static void setMessage(Handler handler, int what) {
		Message message = Message.obtain();
		message.what = what;
		handler.sendMessage(message);
	}

	public static void setMessage(Handler handler, int what, Object obj) {
		Message message = Message.obtain();
		message.what = what;
		message.obj = obj;
		handler.sendMessage(message);
	}

	public static void sendBroadcast(Intent intent) {
		AppApplication.Instance.sendBroadcast(intent);
	}

	/**
	 * 
	 * @Title: showLoadingDialog
	 * @user: ryan.wang
	 * @Description: 显示阻塞窗体
	 * 
	 * @param context
	 * @param title
	 * @param content
	 * @param confirmBtn
	 * @param call
	 * @return Dialog
	 * @throws
	 */
	public static Dialog showLoadingDialog(final Context context, String content) {
		final Dialog dlg = new Dialog(context, R.style.noDialogTheme);
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_show_loading, null);
		((TextView) layout.findViewById(R.id.content)).setText(content);
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

	public static Dialog buildDialog(Context context, View layout) {
		final Dialog dlg = new Dialog(context, R.style.DialogThemeNoAnimation);
		final int cFullFillWidth = 1000;
		layout.setMinimumWidth(cFullFillWidth);
		Window w = dlg.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();
		// lp.x = 0;
		// final int cMakeBottom = -1000;
		// lp.y = cMakeBottom;
		lp.gravity = Gravity.CENTER;
		dlg.onWindowAttributesChanged(lp);
		dlg.setCanceledOnTouchOutside(true);
		dlg.setContentView(layout);
		return dlg;
	}

	public static Dialog buildTipDialog(Context context, String title,
			String content, OnClickListener onClickOk,
			OnClickListener onClickCancel, String okBtnText,
			String cancelBtnText) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_tips, null);
		TextView textViewDialogTitle = (TextView) layout
				.findViewById(R.id.textViewDialogTitle);
		textViewDialogTitle.setText(title);
		TextView textViewContent = (TextView) layout
				.findViewById(R.id.textViewContent);
		textViewContent.setText(content);
		Button buttonRecCancel = (Button) layout
				.findViewById(R.id.buttonRecCancel);
		buttonRecCancel.setText(cancelBtnText);
		if (onClickCancel != null)
			buttonRecCancel.setOnClickListener(onClickCancel);
		Button buttonRecConfirm = (Button) layout
				.findViewById(R.id.buttonRecConfirm);
		buttonRecConfirm.setText(okBtnText);
		if (onClickOk != null)
			buttonRecConfirm.setOnClickListener(onClickOk);
		return UIUtil.buildDialog(context, layout);
	}
}
