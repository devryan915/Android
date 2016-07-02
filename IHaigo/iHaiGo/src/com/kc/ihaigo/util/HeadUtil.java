package com.kc.ihaigo.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

public class HeadUtil {
	private Context context;
	public static final int NONE = 0;
	public static final int PHOTOHRAPH = 1;// 拍照
	public static final int PHOTOZOOM = 2; // 缩放
	public static final int PHOTORESOULT = 3;// 结果
	public static final String IMAGE_UNSPECIFIED = "image/*";
	public Uri uri;
	public HeadUtil(Context context) {
		this.context = context;
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			createFile(Constants.HEAD_URL);
		} else {
			Toast.makeText(context, "亲,没有内存卡,无法上传头像了", 1).show();
		}
	}
	public void takePhoto() {// 照相
		Intent intentPaizhao = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		/*
		 * intentPaizhao.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new
		 * File(Environment.getExternalStorageDirectory(), "/imageName.jpg")));
		 */
		intentPaizhao.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(Constants.HEAD_URL, "imagename.jpg")));
		((Activity) context).startActivityForResult(intentPaizhao, PHOTOHRAPH);
	}
	public void takePhoto1() {// 照相
		Intent intentPaizhao = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		/*
		 * intentPaizhao.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new
		 * File(Environment.getExternalStorageDirectory(), "/imageName.jpg")));
		 */
		intentPaizhao.putExtra(MediaStore.EXTRA_OUTPUT,
				Uri.fromFile(new File(Constants.HEAD_URL, "personimage.jpg")));
		((Activity) context).startActivityForResult(intentPaizhao, PHOTOHRAPH);
	}
	public void pickfromCameraSet() {// 从图库或相册
		Intent intentPhotoset = new Intent(Intent.ACTION_PICK, null);
		intentPhotoset
				.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
						IMAGE_UNSPECIFIED);
		// 调用剪切功能
		((Activity) context).startActivityForResult(intentPhotoset, PHOTOZOOM);

	}
	public void clip(Uri uri) {// 裁剪
		Intent intent = new Intent("com.android.camera.action.CROP");
		// 大图
		intent.setDataAndType(uri, IMAGE_UNSPECIFIED);
		// 缩略图
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 340);
		intent.putExtra("outputY", 340);
		intent.putExtra("return-data", true);
		// //intent.setType("image/*");
		// intent.putExtra("crop", "true");
		// intent.putExtra("aspectX", 1);
		// intent.putExtra("aspectY", 1);
		// intent.putExtra("outputX",500);
		// intent.putExtra("outputY",500);
		// intent.putExtra("scale", true);
		// intent.putExtra("return-data", true);
		// intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		// intent.putExtra("outputFormat",
		// Bitmap.CompressFormat.JPEG.toString());
		// //intent.putExtra("noFaceDetection", true);
		((Activity) context).startActivityForResult(intent, PHOTORESOULT);
	}
	@SuppressLint("SimpleDateFormat")
	public String getStringToday() {
		Date currentTime = new Date();
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy.MM.dd");
		String dateString = formatter.format(currentTime);
		return dateString;
	}
	public File createFile(String path) {
		File file = new File(path);
		if (!file.exists()) {
			// file.getParentFile().mkdirs();//创建一级父级目录
			file.mkdirs();
		}
		return file;
	}
}
