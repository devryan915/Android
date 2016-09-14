package com.broadchance.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.content.res.Resources.Theme;
import android.content.res.XmlResourceParser;
import android.graphics.drawable.Drawable;

import com.broadchance.entity.Skin;
import com.broadchance.entity.UIUserInfoLogin;
import com.broadchance.utils.ConstantConfig;
import com.broadchance.utils.LogUtil;
import com.broadchance.wdecgrec.R;
import com.broadchance.wdecgrec.Skinable;

/**
 * 
 * @author ryan.wang 皮肤管理器
 */
public class SkinManager {
	private final static String TAG = SkinManager.class.getSimpleName();
	private static SkinManager Instance = null;
	// private AssetManager assetManager;// 资源管理器
	private Resources resources;// 资源
	private Resources localResources;
	private Theme theme;// 主题

	private final static String SKIN_DIR = "Skin";
	public final static String DEFAULT_SKINID = "0";
	private Context ctx;
	private Skin curSkin;
	/**
	 * 可更换皮肤的Activity列表，更换皮肤后用来通知ui
	 */
	private List<Skinable> skinNotifyList = new ArrayList<Skinable>();
	/**
	 * 皮肤列表
	 */
	private Map<String, Skin> skinMap = new HashMap<String, Skin>();

	private SkinManager(Context context) {
		this.ctx = context;
		localResources = this.ctx.getResources();
		loadSkinConfig();
		initSkin();
	}

	public synchronized static SkinManager getInstance() {
		if (Instance == null)
			Instance = new SkinManager(AppApplication.Instance);
		return Instance;
	}

	/**
	 * 加载Skin XML配置文件
	 */
	private void loadSkinConfig() {
		skinMap.clear();
		/***** 加载默认的皮肤 *****/
		Skin skin;
		String skinID = "";
		String assetSkinFileName = "";
		String skinName = "";
		String skinPkgName = "";
		String skinImageName = "";
		// 统一改到配置文件中，方便管理
		// skin = new Skin();
		// skin.setSkinID(skinID);
		// skin.setAssetSkinFileName(assetSkinFileName);
		// skin.setSkinName(skinName);
		// skin.setSkinPkgName(skinPkgName);
		// skinMap.put(skin.getSkinID(), skin);
		/****** 加载内置皮肤 *********/
		Resources r = this.ctx.getResources();
		XmlResourceParser xrp = r.getXml(R.xml.skin_config);
		try {
			// 当没有达到xml的逻辑结束终点
			// getEventType方法返回读取xml当前的事件
			while (xrp.getEventType() != XmlResourceParser.END_DOCUMENT) {
				if (xrp.getEventType() == XmlResourceParser.START_TAG) {
					String name = xrp.getName();
					if (name.equals("skin")) // 查找符合条件的
					{
						skin = new Skin();
						skinID = xrp.getAttributeValue(null, "skin_id");
						assetSkinFileName = xrp.getAttributeValue(null,
								"asset_skin_filename");
						skinName = xrp.getAttributeValue(null, "skin_name");
						skinPkgName = xrp.getAttributeValue(null,
								"skin_pkg_name");
						skinImageName = xrp.getAttributeValue(null,
								"skin_image_name");
						skin.setSkinID(skinID);
						skin.setAssetSkinFileName(assetSkinFileName);
						skin.setSkinName(skinName);
						skin.setSkinImageName(skinImageName);
						skin.setSkinPkgName(skinPkgName);
						skinMap.put(skin.getSkinID(), skin);
					}
				}// 当读取到xml节点是一个元素的尾标记时
				else if (xrp.getEventType() == XmlPullParser.END_TAG) {
					// 控制台输出xml节点结束
					System.out.println(xrp.getName() + "---End！");
				} // 当读取到xml节点是文本时
				else if (xrp.getEventType() == XmlPullParser.TEXT) {
					// 输出文本
					System.out.println(xrp.getText() + "\n");
				}
				xrp.next();
			}
		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Skinable子类在onResume调用
	 * 
	 * @param skin
	 */
	public void registerSkin(Skinable skin) {
		skinNotifyList.add(skin);
	}

	/**
	 * Skinable子类在onPause调用
	 * 
	 * @param skin
	 */
	public void unRegisterSkin(Skinable skin) {
		skinNotifyList.remove(skin);
	}

	/**
	 * 初始化皮肤
	 */
	public void initSkin() {
		String setSkinID = "";
		UIUserInfoLogin user = DataManager.getUserInfo();
		if (user != null) {
			setSkinID = PreferencesManager.getInstance().getString(
					user.getUserID() + ConstantConfig.PREFERENCES_SKINID);
		}
		if (setSkinID.isEmpty() || setSkinID.equals(DEFAULT_SKINID)) {
			setDefaultSkin();
		} else if (skinMap.containsKey(setSkinID)) {
			curSkin = skinMap.get(setSkinID);
			// 检查皮肤文件
			if (checkSkinFiles(curSkin.getAssetSkinFileName())) {
				// 加载皮肤
				loadResources(getSkinPath(curSkin.getAssetSkinFileName()));
			}
		} else {
			LogUtil.d(TAG, "非法的皮肤名，系统将使用默认皮肤");
			setDefaultSkin();
		}
		notifySkinChanged();
	}

	private void setDefaultSkin() {
		// 使用默认皮肤
		resources = this.ctx.getResources();
		theme = this.ctx.getTheme();
		curSkin = skinMap.get(DEFAULT_SKINID);
	}

	/**
	 * 通知更新所有的UI
	 */
	private void notifySkinChanged() {
		for (Skinable skinable : skinNotifyList) {
			skinable.loadSkin();
		}
	}

	/**
	 * 获取皮肤配置信息
	 * 
	 * @return
	 */
	public Map<String, Skin> getSkinConfig() {
		return skinMap;
	}

	public Resources getLocalResources() {
		return localResources;
	}

	public Drawable getLocalDrawable(String resName) {
		return localResources.getDrawable(localResources.getIdentifier(resName,
				"drawable", skinMap.get(DEFAULT_SKINID).getSkinPkgName()));
	}

	public XmlResourceParser getLocalLayout(String resName) {
		return localResources.getLayout(localResources.getIdentifier(resName,
				"layout", skinMap.get(DEFAULT_SKINID).getSkinPkgName()));
	}

	/**
	 * 通过资源名称(不使用ID为了防止皮肤包的R对应的资源ID有可能和主app的R对应的资源ID不同)获取String值
	 * 
	 * @param resName
	 * @return
	 */
	public String getString(int resNameId) {
		return resources.getString(resources.getIdentifier(
				localResources.getString(resNameId), "string",
				curSkin.getSkinPkgName()));
	}

	/**
	 * 通过资源名称获取尺寸值
	 * 
	 * @param resName
	 * @return
	 */
	public float getDimen(int resNameId) {
		return resources.getDimension(resources.getIdentifier(
				localResources.getString(resNameId), "dimen",
				curSkin.getSkinPkgName()));
	}

	/**
	 * 通过资源名称获取图像资源
	 * 
	 * @param resName
	 * @return
	 */
	public Drawable getDrawable(int resNameId) {
		return resources.getDrawable(resources.getIdentifier(
				localResources.getString(resNameId), "drawable",
				curSkin.getSkinPkgName()));
	}

	/**
	 * 通过资源名称获取颜色值
	 * 
	 * @param resName
	 * @return
	 */
	public int getColor(int resNameId) {
		return resources.getColor(resources.getIdentifier(
				localResources.getString(resNameId), "color",
				curSkin.getSkinPkgName()));
	}

	/**
	 * 根据配置加载皮肤文件
	 * 
	 * @param dexPath
	 */
	private void loadResources(String dexPath) {
		AssetManager assetManager = null;
		try {
			assetManager = AssetManager.class.newInstance();
			Method addAssetPath = assetManager.getClass().getMethod(
					"addAssetPath", String.class);
			addAssetPath.invoke(assetManager, dexPath);
			// this.assetManager = assetManager;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Resources superRes = this.ctx.getResources();
		superRes.getDisplayMetrics();
		superRes.getConfiguration();
		resources = new Resources(assetManager, superRes.getDisplayMetrics(),
				superRes.getConfiguration());
		theme = resources.newTheme();
		theme.setTo(this.ctx.getTheme());
	}

	/**
	 * 获取皮肤本地存储路径
	 * 
	 * @param assetSkinFile
	 * @return
	 */
	private String getSkinPath(String assetSkinFile) {
		return this.ctx.getFilesDir().getAbsoluteFile() + "/" + SKIN_DIR + "/"
				+ assetSkinFile;
	}

	/**
	 * 检查皮肤文件 copy asset下的皮肤文件到file目录，如果存在则覆盖
	 * 
	 * @param assetSkinFile
	 *            asset目录下的皮肤文件名称
	 */
	private boolean checkSkinFiles(String assetSkinFile) {
		try {
			InputStream inputStream = this.ctx.getAssets().open(assetSkinFile);
			String skinDir = this.ctx.getFilesDir().getAbsoluteFile() + "/"
					+ SKIN_DIR;
			File apkFileDir = new File(skinDir);
			if (!apkFileDir.exists()) {
				apkFileDir.mkdirs();
			}
			File apkFile = new File(skinDir, assetSkinFile);
			if (apkFile.exists()) {
				if (ConstantConfig.Debug) {
					apkFile.delete();
				} else {
					return true;
				}
			}
			FileOutputStream outputStream = new FileOutputStream(new File(
					apkFileDir, assetSkinFile));
			byte[] arrayOfByte = new byte[1024];
			int readLength = -1;
			while ((readLength = inputStream.read(arrayOfByte)) != -1) {
				outputStream.write(arrayOfByte, 0, readLength);
			}
			outputStream.close();
			inputStream.close();
			return true;
		} catch (Exception e) {
			LogUtil.e(TAG, e);
			return false;
		}
	}
}
