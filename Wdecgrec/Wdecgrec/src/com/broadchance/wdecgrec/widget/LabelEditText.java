package com.broadchance.wdecgrec.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.widget.EditText;

import com.broadchance.wdecgrec.R;

public class LabelEditText extends EditText {
	private String label = "";
	private int color = Color.WHITE;
	private float paddingleft = 62f;
	private float textsize = 25f;

	public LabelEditText(Context context) {
		super(context);
	}

	public LabelEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context, attrs);
	}

	public LabelEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context, attrs);
	}

	private void init(Context context, AttributeSet attrs) {
		TypedArray typedArray = context.obtainStyledAttributes(attrs,
				R.styleable.labeledittext);
		for (int i = 0; i < typedArray.getIndexCount(); i++) {
			switch (typedArray.getIndex(i)) {
			case R.styleable.labeledittext_labelcolor:
				color = typedArray.getColor(
						R.styleable.labeledittext_labelcolor, color);
				break;
			case R.styleable.labeledittext_labeltext:
				label = typedArray
						.getString(R.styleable.labeledittext_labeltext);
				break;
			case R.styleable.labeledittext_labelpaddingleft:
				paddingleft = typedArray
						.getDimension(
								R.styleable.labeledittext_labelpaddingleft,
								paddingleft);
				break;
			case R.styleable.labeledittext_labeltextsize:
				textsize = typedArray.getDimension(
						R.styleable.labeledittext_labeltextsize, textsize);
				break;
			default:
				break;
			}
		}
		typedArray.recycle();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setTextSize(textsize);
		paint.setColor(color);
		canvas.drawText(label, paddingleft, (getHeight() + textsize * 0.75f) / 2,
				paint);
		super.onDraw(canvas);
	}
}
