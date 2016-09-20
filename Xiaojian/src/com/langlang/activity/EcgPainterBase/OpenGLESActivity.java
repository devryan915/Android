package com.langlang.activity.EcgPainterBase;

import javax.microedition.khronos.opengles.GL10;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

public class OpenGLESActivity extends Activity implements IOpenGLPainter{
	
	/** Called when the activity is first created. */
    @SuppressLint("NewApi")
	@Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	this.requestWindowFeature(Window.FEATURE_NO_TITLE); 
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN); 

        mGLSurfaceView = new GLSurfaceView(this);
        
        mGLSurfaceView.setRenderer(new OpenGLRenderer(this));
   		setContentView(mGLSurfaceView);

		mGLSurfaceView.setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
   		mGLSurfaceView.requestRender();
    }

	@Override
	public void DrawScene(GL10 gl) {
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);	
		// Clears the screen and depth buffer.
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
	}
	
	@Override
    protected void onResume() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onResume();
        mGLSurfaceView.onResume();
    }

    @Override
    protected void onPause() {
        // Ideally a game should implement onResume() and onPause()
        // to take appropriate action when the activity looses focus
        super.onPause();
        mGLSurfaceView.onPause();
    }
    
    protected void updateCanvas() {
    	mGLSurfaceView.requestRender();
    }

    protected GLSurfaceView mGLSurfaceView;

}
