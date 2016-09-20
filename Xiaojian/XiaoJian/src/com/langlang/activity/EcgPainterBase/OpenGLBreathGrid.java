package com.langlang.activity.EcgPainterBase;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import javax.microedition.khronos.opengles.GL10;

public class OpenGLBreathGrid {
	private FloatBuffer vertexBuffer_H;
	private FloatBuffer vertexBuffer_V;
	
	private final static float SCALE = 3.0f;
//	private final static int GRID_NUM_H = 75;
	private final static int GRID_NUM_H = 60;
	
//	public final static float CANVAS_WIDTH = 1.6f;
	public final static float CANVAS_WIDTH = 1.26f;
	
//	public final static float UNIT_SIZE = (1.6f / 75 * 3);
	public final static float UNIT_SIZE = (CANVAS_WIDTH / GRID_NUM_H * SCALE);
	
//	public final static float START_X_VLINE = -0.9f * 3;
//	public final static float END_X_VLINE = 0.9f * 3;
	public final static float START_X_VLINE = -CANVAS_WIDTH / 2 * SCALE;
	public final static float END_X_VLINE = CANVAS_WIDTH  / 2 * SCALE;
	
	public final static float START_Y_HLINE = -1.80f + UNIT_SIZE * 2 + 0.05f; 
	public final static float END_Y_HLINE = START_Y_HLINE + 50 * UNIT_SIZE;// 1.50f + UNIT_SIZE ;// + 0.05f;
	
//	private final static float END_Y_HLINE = 1.0f;
	
	private float unitCellSize;
	private int vNum;
	private int hNum;

    private float[] verticeHLines;
    private float[] verticeVLines;
    
    public OpenGLBreathGrid() {
    	initParams();
    	initBuffer();
    }
    
	private void initParams() {
		unitCellSize = UNIT_SIZE; //1.8f / 75 * 3;//1.6f / 750.0f;		
		vNum = GRID_NUM_H;
		//<hNum = 50;
		hNum = 50;
		
	    verticeVLines = new float[(vNum + 1) * 6];
	    verticeHLines = new float[(hNum + 1) * 6];

	    float xPosition = START_X_VLINE;
	    for (int i = 0; i < vNum + 1; i++) {
	    	verticeVLines[i * 6] = xPosition;
//	    	verticeVLines[i * 6 + 1] = 1f;
//	    	verticeVLines[i * 6 + 1] = 1.3f;
//	    	verticeVLines[i * 6 + 1] = 1.25f;
	    	verticeVLines[i * 6 + 1] = END_Y_HLINE;//< 1.50f + (1.8f / 75 * 3);
	    	verticeVLines[i * 6 + 2] = 0;
	    	verticeVLines[i * 6 + 3] = xPosition;
//	    	verticeVLines[i * 6 + 4] = -0.2f;
//	    	verticeVLines[i * 6 + 4] = 0.1f;
//	    	verticeVLines[i * 6 + 4] = 0.05f; 
	    	verticeVLines[i * 6 + 4] = START_Y_HLINE; 
	    	verticeVLines[i * 6 + 5] = 0;
	    	
	    	xPosition += unitCellSize;
	    }
	    
	    float yPosition = START_Y_HLINE;
	    for (int i = 0; i < hNum + 1; i++) {
	    	verticeHLines[i * 6] = START_X_VLINE; //<-0.9f * 3;
	    	verticeHLines[i * 6 + 1] = yPosition;
	    	verticeHLines[i * 6 + 2] = 0;
	    	verticeHLines[i * 6 + 3] = END_X_VLINE; //<0.9f * 3;
	    	verticeHLines[i * 6 + 4] = yPosition;
	    	verticeHLines[i * 6 + 5] = 0;
	    	
	    	yPosition += unitCellSize;
	    }
	}

	// -------------------------------------------------------------------------------------	
    private void initBuffer(){  
        ByteBuffer vertexByteBuffer_H = ByteBuffer.allocateDirect(verticeHLines.length * 4);  
        vertexByteBuffer_H.order(ByteOrder.nativeOrder());  
        vertexBuffer_H = vertexByteBuffer_H.asFloatBuffer();  
        vertexBuffer_H.put(verticeHLines);  
        vertexBuffer_H.position(0);
        
        ByteBuffer vertexByteBuffer_V = ByteBuffer.allocateDirect(verticeVLines.length * 4);  
        vertexByteBuffer_V.order(ByteOrder.nativeOrder());  
        vertexBuffer_V = vertexByteBuffer_V.asFloatBuffer();  
        vertexBuffer_V.put(verticeVLines);  
        vertexBuffer_V.position(0);
    }    
    
    public void drawGrid(GL10 gl){    	
    	gl.glColor4f(220.0f/256, 70.0f/256, 67.0f/256, 1.0f);
    	
    	drawHLines(gl);
        drawVLines(gl);
    }
    
    private void drawBackgound(GL10 gl) {
    	gl.glColor4f(144.0f/256, 137.0f/256, 131.0f/256, 1.0f);
	}

	private void drawHLines(GL10 gl) {
    	// 指向数组数据   
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer_H); 
    	
        //设置直线的宽度   
        //gl.glLineWidth(1.0f);
//    	gl.glLineWidth(0.001f);
    	gl.glLineWidth(1.0f);

        byte[] line = new byte[2];
        for (int i = 0; i < hNum + 1; i++) {
        	line[0] = (byte) (i * 2);
        	line[1] = (byte) (i * 2 + 1);
        	
        	if (i % 5 == 0) {
        		gl.glLineWidth(2.0f);
        	} else {
        		gl.glLineWidth(1.0f);
        	}
        	
        	gl.glDrawElements(GL10.GL_LINE_STRIP, 
        				2, GL10.GL_UNSIGNED_BYTE, ByteBuffer.wrap(line));
        }
    }
    
    private void drawVLines(GL10 gl) {
    	gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer_V); 
        gl.glLineWidth(1.0f); 
        
        byte[] line = new byte[2];
        for (int i = 0; i < vNum + 1; i++) {
        	line[0] = (byte) (i * 2);
        	line[1] = (byte) (i * 2 + 1);
        	
        	if (i % 5 == 0) {
        		gl.glLineWidth(2.0f);
        	} else {
        		gl.glLineWidth(1.0f);
        	}
        	
        	gl.glDrawElements(GL10.GL_LINE_STRIP, 
        				2, GL10.GL_UNSIGNED_BYTE, ByteBuffer.wrap(line));
        }
    }
}
