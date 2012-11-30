package com.jorflekel.yahtzee.views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class DieGLSurfaceView extends GLSurfaceView {
	public DieRenderer renderer;
	
	public DieGLSurfaceView(Context context) {
		super(context);
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
		renderer = new DieRenderer(getContext());
		setRenderer(renderer);
		// Render the view only when there is a change in the drawing data
		//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	
	public DieGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
		renderer = new DieRenderer(getContext());
		setRenderer(renderer);
		// Render the view only when there is a change in the drawing data
		//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
}
