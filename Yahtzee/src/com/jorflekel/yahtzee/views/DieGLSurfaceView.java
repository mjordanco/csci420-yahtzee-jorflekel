package com.example.diceview2;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class DieGLSurfaceView extends GLSurfaceView {

	public DieGLSurfaceView(Context context) {
		super(context);
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
		setRenderer(new DieRenderer(getContext()));
		// Render the view only when there is a change in the drawing data
		//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
}
