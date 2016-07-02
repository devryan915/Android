package com.ice.skininnerdemo;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ice on 14-10-8.
 * 皮肤资源管理器<单例>
 */
public class SkinManager {

    private static SkinManager mSkinManager;
    private AssetManager mAssetManager;

    private SkinManager (Context context) {
        this.mAssetManager = context.getAssets();
    }

    public synchronized static SkinManager getInstance(Context context){

        if (mSkinManager == null) {
            mSkinManager = new SkinManager(context);
        }
        return mSkinManager;
    }


    /**
     * 根据皮肤文件名 和 资源文件名 获取Assets 里面的皮肤资源Drawable对象
     * @param skinFileName  皮肤文件名
     * @param fileName   资源文件名
     * @return
     */
    public Drawable getSkinDrawable(String skinFileName, String fileName) {
        Drawable drawable = null;
        try {
            InputStream inputStream = mAssetManager.open(skinFileName + "/" + fileName);
            drawable = Drawable.createFromStream(inputStream, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return drawable;
    }


    /**
     * 根据皮肤文件名 和 资源文件名 获取Assets 里面的皮肤资源Bitmap对象
     * @param skinFileName
     * @param fileName
     * @return
     */
    public Bitmap getSkinBitmap(String skinFileName, String fileName){
        Bitmap image = null;
        try {
            InputStream inputStream = mAssetManager.open(skinFileName + "/" + fileName);
            image = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

}
