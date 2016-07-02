package com.kc.ihaigo.ui.selfwidget;

import java.util.Arrays;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.TextView;
/**
 * 
 * @ClassName: AutoLineText
 * @Description: 自动换行
 * @author: ryan.wang
 * @date: 2014年8月5日 上午11:55:53
 * 
 */
public class AutoLineText extends TextView {
	private String mString = "";
	public AutoLineText(Context con) {
		super(con);
	}

	public AutoLineText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	public AutoLineText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	@Override
	public boolean isFocused() {
		return true;
	}
	@Override
	protected void onFocusChanged(boolean focused, int direction,
			Rect previouslyFocusedRect) {
	}
	/**
	 * 自动分割文本
	 * 
	 * @param content
	 *            需要分割的文本
	 * @param p
	 *            画笔，用来根据字体测量文本的宽度
	 * @param width
	 *            指定的宽度
	 * @return 一个字符串数组，保存每行的文本
	 */
	private String[] autoSplit(String content, Paint p, float width) {
		int length = content.length();
		float textWidth = p.measureText(content);
		if (textWidth <= width) {
			return new String[]{content};
		}
		int start = 0, end = 1, i = 0;
		int lines = (int) Math.ceil(textWidth / width); // 计算行数
		String[] lineTexts = new String[lines];
		while (start < length) {
			if (p.measureText(content, start, end) > width) { // 文本宽度超出控件宽度时
				lineTexts[i++] = (String) content.subSequence(start, end);
				start = end;
			}
			if (end == length) { // 不足一行的文本
				lineTexts[i] = (String) content.subSequence(start, end);
				break;
			}
			end += 1;
		}
		return lineTexts;
	}

	/**
	 * 获取指定单位对应的原始大小（根据设备信息） px,dip,sp -> px
	 * 
	 * Paint.setTextSize()单位为px
	 * 
	 * 代码摘自：TextView.setTextSize()
	 * 
	 * @param unit
	 *            TypedValue.COMPLEX_UNIT_*
	 * @param size
	 * @return
	 */
	public float getRawSize(int unit, float size) {
		Context c = getContext();
		Resources r;
		if (c == null)
			r = Resources.getSystem();
		else
			r = c.getResources();
		return TypedValue.applyDimension(unit, size, r.getDisplayMetrics());
	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (false) {
			return;
		}
		// view.draw()绘制了控件的背景
		// 控件的绘制操作及顺序：
		/*
		 * Draw traversal performs several drawing steps which must be executed
		 * in the appropriate order:
		 * 
		 * 1. Draw the background （绘制控件设置的背景） 2. If necessary, save the canvas'
		 * layers to prepare for fading 3. Draw view's content (可以重写，
		 * onDraw(canvas);) 4. Draw children
		 * (可重写，用来分发画布到子控件，具体看ViewGroup。对应方法dispatchDraw
		 * (canvas);此方法依次调用了子控件的draw()方法) 5. If necessary, draw the fading edges
		 * and restore layers （绘制控件四周的阴影渐变效果） 6. Draw decorations (scrollbars
		 * for instance) （用来绘制滚动条，对应方法onDrawScrollBars(canvas);。
		 * 可以重写onDrawHorizontalScrollBar()和onDrawVerticalScrollBar()来自定义滚动条）
		 */
		// 可以绘制内容和滚动条。
		// draw backgroud
		// canvas.drawColor(Color.WHITE);
		// draw text
		TextPaint mPaint = getPaint();
		FontMetrics fm = getPaint().getFontMetrics();
		float baseline = fm.descent - fm.ascent;
		float x = 0;
		float y = baseline; // 由于系统基于字体的底部来绘制文本，所有需要加上字体的高度。
		// 文本自动换行
		String[] texts = autoSplit(mString, mPaint, getWidth() - 50);
		if (texts.length > 2) {
			texts[1] = texts[1].substring(0, texts[1].length() - 1) + "...";
		}
		System.out.printf("line indexs: %s\n", Arrays.toString(texts));

		for (String text : texts) {
			canvas.drawText(text, x, y, mPaint); // 坐标以控件左上角为原点
			y += baseline + fm.leading; // 添加字体行间距
		}
	}
	@Override
	public void setText(CharSequence text, BufferType type) {
		mString = text == null ? "" : text.toString();
		// try {
		// str = stringFilter(text.toString());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		super.setText("", type);
	}

	@Override
	public CharSequence getText() {
		return mString;
	}
	/**
	 * 去除特殊字符或将所有中文标号替换为英文标号
	 * 
	 * @param str
	 * @return
	 */
	// public String stringFilter(String str) {
	// if (str == null)
	// return "";
	// str = str.replaceAll("【", "[").replaceAll("】", "]")
	// .replaceAll("！", "!").replaceAll("：", ":");// 替换中文标号
	// String regEx = "[『』]"; // 清除掉特殊字符
	// Pattern p = Pattern.compile(regEx);
	// Matcher m = p.matcher(str);
	// return m.replaceAll("").trim();
	// }

	// public String ToSBC(String input) {
	// char c[] = input.toCharArray();
	// for (int i = 0; i < c.length; i++) {
	// if (c[i] == ' ') {
	// c[i] = '\u3000';
	// } else if (c[i] < '\177') {
	// c[i] = (char) (c[i] + 65248);
	// }
	// }
	// return new String(c);
	// }
	/**
	 * 半角转换为全角
	 * 
	 * @param input
	 * @return
	 */
	// public static String ToDBC(String input) {
	// char[] c = input.toCharArray();
	// for (int i = 0; i < c.length; i++) {
	// if (c[i] == 12288) {
	// c[i] = (char) 32;
	// continue;
	// }
	// if (c[i] > 65280 && c[i] < 65375)
	// c[i] = (char) (c[i] - 65248);
	// }
	// return new String(c);
	// }
}