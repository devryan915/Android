package com.example.testirregularpicture;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by zhy on 15/5/14.
 */
public class ColourImageBaseLayerView extends View {

	private LayerDrawable mDrawables;
	private Context context;
	private Drawable lastDrawable;

	public ColourImageBaseLayerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDrawables = (LayerDrawable) getBackground();
		this.context = context;
	}

	Toast toast;

	private void showToast(String text) {
		if (toast != null) {
			toast.cancel();
		}
		toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		setMeasuredDimension(mDrawables.getIntrinsicWidth(),
				mDrawables.getIntrinsicHeight());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		final float x = event.getX();
		final float y = event.getY();
		if (event.getAction() == MotionEvent.ACTION_DOWN) {
			Drawable drawable = findDrawable(x, y);
			if (drawable != null) {
				if (lastDrawable != null) {
					lastDrawable.clearColorFilter();
				}
				// drawable.setColorFilter(randomColor(),
				// PorterDuff.Mode.SRC_IN);
				drawable.setColorFilter(Color.BLACK, PorterDuff.Mode.MULTIPLY);
				// drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.CLEAR);
				lastDrawable = drawable;
			}
		}

		return super.onTouchEvent(event);
	}

	private int randomColor() {
		Random random = new Random();
		int color = Color.argb(255, random.nextInt(256), random.nextInt(256),
				random.nextInt(256));
		return color;
	}

	private Drawable findDrawable(float x, float y) {
		final int numberOfLayers = mDrawables.getNumberOfLayers();
		Drawable drawable = null;
		Bitmap bitmap = null;
		for (int i = numberOfLayers - 1; i >= 0; i--) {
			drawable = mDrawables.getDrawable(i);
			bitmap = ((BitmapDrawable) drawable).getBitmap();
			try {
				int pixel = bitmap.getPixel((int) x, (int) y);
				if (pixel == Color.TRANSPARENT) {
					continue;
				}
				int layerID = mDrawables.getId(i);
				switch (layerID) {
				case R.id.layer1:
					showToast("东北会");
					break;
				case R.id.layer2:
					showToast("河北会");
					break;
				case R.id.layer3:
					showToast("河南会");
					break;
				case R.id.layer4:
					showToast("华南会");
					break;
				case R.id.layer5:
					showToast("江苏会");
					break;
				case R.id.layer6:
					showToast("安徽会");
					break;
				case R.id.layer7:
					showToast("宁夏会");
					break;
				case R.id.layer8:
					showToast("山东会");
					break;
				case R.id.layer9:
					showToast("浙江会");
					break;
				case R.id.layer10:
					showToast("四川会");
					break;
				default:
					break;
				}
			} catch (Exception e) {
				continue;
			}
			return drawable;
		}
		return null;
	}

}