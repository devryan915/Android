package com.broadchance.entity;

/**
 * 皮肤配置的实体类
 * 
 * @author ryan.wang
 * 
 */
public class Skin {
	/**
	 * 皮肤ID
	 */
	private String skinID;
	/**
	 * asset下皮肤文件名
	 */
	private String assetSkinFileName;
	/**
	 * 皮肤显示名
	 */
	private String skinName;
	/**
	 * 图片资源名字res/drawable/皮肤图片名称
	 */
	private String skinImageName;
	/**
	 * 皮肤包名
	 */
	private String skinPkgName;
	public boolean isSel;

	public String getSkinID() {
		return skinID;
	}

	public void setSkinID(String skinID) {
		this.skinID = skinID;
	}

	public String getSkinImageName() {
		return skinImageName;
	}

	public void setSkinImageName(String skinImageName) {
		this.skinImageName = skinImageName;
	}

	public String getAssetSkinFileName() {
		return assetSkinFileName;
	}

	public void setAssetSkinFileName(String assetSkinFileName) {
		this.assetSkinFileName = assetSkinFileName;
	}

	public String getSkinName() {
		return skinName;
	}

	public void setSkinName(String skinName) {
		this.skinName = skinName;
	}

	public String getSkinPkgName() {
		return skinPkgName;
	}

	public void setSkinPkgName(String skinPkgName) {
		this.skinPkgName = skinPkgName;
	}
}
