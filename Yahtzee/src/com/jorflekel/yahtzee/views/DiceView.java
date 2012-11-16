package com.jorflekel.yahtzee.views;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.SurfaceHolder;

public class DiceView extends GLSurfaceView {

	public DiceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DiceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) { }
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) { }
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) { }
	
}
