package com.kc.ihaigo;

import android.app.ActivityGroup;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.magus.MagusTools;

/**
 * 一般activity都继承至该类，如activity的布局文件符合命名规则，
 * 则调用addViewToRoot（）来设置布局文件（可以在自己项目中有个RootActiviy来统一处理）。
 * 如特殊情况不符合命名规则，则在调用super.onCreate(savedInstanceState) 之前 必须先调用setResourceID(int
 * id)来设置资源文件。 在其子类中可以直接通用变量sp取得默认的
 * SharedPreferences的引用，并会自动给activity中的Button设置监听， 处理监听事件时只需要覆盖Onclick方法 通过case：
 * R.id.ButtonID 来处理就行了， 不需要先findViewbyID， 再设置。
 * @author Administrator
 * 
 */
public class BaseActivityGroup extends ActivityGroup {
	/**
	 * 资源文件名
	 */
	protected String resourceName;
	/**
	 * 如想刷新此对象或重新获取实例，则可调用PreferenceManager.getDefaultSharedPreferences(Context)
	 * ;
	 */
	protected SharedPreferences sp;

	private int resourceID = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = PreferenceManager.getDefaultSharedPreferences(this);
		resourceName = MagusTools.getMatcherResourceName(this);
	}

	protected void setResourceID(int id) {
		resourceID = id;
	}

	/**
	 * 隐藏系统自带的title
	 */
	protected void hideSystemTitle() {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}

	/**
	 * 设置全屏
	 */
	protected void fullScreen() {
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN); // 设置全屏
	}

	/**
	 * 添加符合Activity命名规范的布局文件到给定视图容器中，如符合命名规范的布局文件就是Activity的最终布局，不需要加以修饰
	 * （如统一加标题栏, 底部的导航栏等） 则 参数传 null
	 * 
	 * @param rootView
	 *            添加符合规范布局文件的视图容器，如没有则传null
	 */
	protected void addViewToRoot(ViewGroup rootView) {
		try {
			int resourceId;
			if (resourceID != -1) {
				resourceId = resourceID;
			} else {
				resourceId = getFieldValue("layout", resourceName);
			}
			if (rootView == null) {
				setContentView(resourceId);
			} else {
				getLayoutInflater().inflate(resourceId, rootView);
			}
		} catch (Exception e) {
//			Toast.makeText(this, "没有找到匹配的资源文件。", Toast.LENGTH_LONG).show();
			MagusTools.writeLog(e);
		}
	}
	/**
	 * 根据给定的类型名和字段名，返回R文件中的字段的值
	 * 
	 * @param typeName
	 *            属于哪个类别的属性 （id,layout,drawable,string,color,attr......）
	 * @param fieldName
	 *            字段名
	 * @return 字段的值
	 * @throws Exception
	 */
	protected int getFieldValue(String typeName, String fieldName) {
		int i = MagusTools.getFieldValue(typeName, fieldName);
		return i;
	}
	/**
	 * 显示Toast提示
	 * 
	 * @param content
	 *            要提示的内容
	 */
	public void showToast(String content) {
		Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
	}
}
