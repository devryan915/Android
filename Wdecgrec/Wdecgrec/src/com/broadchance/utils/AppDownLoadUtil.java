/**
 * 
 */
package com.broadchance.utils;

import java.io.File;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.broadchance.entity.DownLoadAPPResponse;
import com.broadchance.manager.AppApplication;
import com.broadchance.manager.PreferencesManager;
import com.broadchance.wdecgrec.BaseActivity;
import com.broadchance.wdecgrec.HttpReqCallBack;
import com.broadchance.wdecgrec.R;

/**
 * @author ryan.wang
 * 
 */
public class AppDownLoadUtil {
	private static final String TAG = AppDownLoadUtil.class.getSimpleName();
	private Dialog dialogAppUpdate;
	private ProgressDialog pBar;
	private Context context;
	private String newVer;

	public void showAppUpdateDialog(BaseActivity context, String verName,
			final String url) {
		this.context = context;
		newVer = verName;
		// newVer = PreferencesManager.getInstance().getString(
		// ConstantConfig.PREFERENCES_NEWAPPVER);
		// if (newVer.trim().isEmpty()) {
		// return false;
		// }
		String curVer = AppApplication.curVer;
		// // 有新的版本
		// if (newVer.compareTo(curVer) > 0) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout layout = (LinearLayout) inflater.inflate(
				R.layout.dialog_appupdate, null);
		TextView textViewCurVer = (TextView) layout
				.findViewById(R.id.textViewCurVer);
		textViewCurVer.setText(curVer);
		TextView textViewNewVer = (TextView) layout
				.findViewById(R.id.textViewNewVer);
		textViewNewVer.setText(newVer);
		Button buttonReject = (Button) layout.findViewById(R.id.buttonReject);
		buttonReject.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (dialogAppUpdate != null) {
					dialogAppUpdate.cancel();
					dialogAppUpdate.dismiss();
				}
			}
		});
		Button buttonAllowed = (Button) layout.findViewById(R.id.buttonAllowed);
		buttonAllowed.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (dialogAppUpdate != null) {
					dialogAppUpdate.cancel();
					dialogAppUpdate.dismiss();
				}
				try {
					downLoadApp(url);
				} catch (Exception e) {
					LogUtil.e(TAG, e);
				}
			}
		});
		dialogAppUpdate = UIUtil.buildDialog(context, layout);
		dialogAppUpdate.show();
		// return true;
		// } else {
		// return false;
		// }
	}

	private void downLoadApp(String url) {
		// String url = PreferencesManager.getInstance().getString(
		// ConstantConfig.PREFERENCES_NEWAPPURL);
		if (url.trim().isEmpty()) {
			return;
		}
		File downFile = new File(FileUtil.APP_DOWNLOAD);
		if (!downFile.exists()) {
			downFile.mkdirs();
		}
		downFile = new File(FileUtil.APP_DOWNLOAD + "wdecgrec" + newVer
				+ ".apk");
		pBar = new ProgressDialog(context);
		pBar.setTitle("正在下载");
		pBar.setMessage("请稍候...");
		pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pBar.setCanceledOnTouchOutside(true);
		pBar.show();
		Handler handler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				pBar.setMessage(msg.obj.toString());
			}

		};
		ClientGameService.getInstance().downLoadApp(url, downFile, handler,
				new HttpReqCallBack<DownLoadAPPResponse>() {

					@Override
					public Activity getReqActivity() {
						return null;
					}

					@Override
					public void doSuccess(DownLoadAPPResponse result) {
						if (result.getDownLoadFile() != null
								&& result.getDownLoadFile().length() > 0) {
							// PreferencesManager.getInstance().putString(
							// ConstantConfig.PREFERENCES_NEWAPPURL, "");
							Intent intent = new Intent(Intent.ACTION_VIEW);
							intent.setDataAndType(
									Uri.fromFile(result.getDownLoadFile()),
									"application/vnd.android.package-archive");
							context.startActivity(intent);
						}
						if (pBar != null) {
							pBar.cancel();
							pBar.dismiss();
						}
					}

					@Override
					public void doError(String result) {
						if (pBar != null) {
							pBar.cancel();
							pBar.dismiss();
						}
						if (ConstantConfig.Debug) {
							LogUtil.d(TAG, result);
						}
					}
				});
	}
}
