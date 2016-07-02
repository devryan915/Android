package com.example.testopengl;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {

	public MainActivity context;

	public MyGLSurfaceView(Context context) {
		super(context);
		this.context = (MainActivity) context;
		setMyRender();
		// TODO Auto-generated constructor stub
	}

	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = (MainActivity) context;
		setMyRender();
		// TODO Auto-generated constructor stub
	}

	void setMyRender() {
		// setRenderer(new MyRenderer(context));
		setRenderer(new ECGRenderer(context));
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

}
