package com.jorflekel.yahtzee.views;

import com.jorflekel.yahtzee.views.DieRenderer.DieState;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.view.MotionEvent;

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
	
	@Override
	public boolean onTouchEvent(MotionEvent e){
		float xSize = renderer.boundary * renderer.ratio;
		float ySize = renderer.boundary;
		float x = -1 * xSize + 2 * xSize * e.getX() / getWidth();
		float y = -1 * ySize + 2 * ySize * e.getY() / getHeight();
		y *= -1; // Flip the y;
		float minDist = Float.MAX_VALUE;
		float currDist = 0;
		float limit = 0.7f * renderer.scale;
		DieState closest = null;
		if(e.getAction() == MotionEvent.ACTION_DOWN){
			for(DieState die : renderer.dice){
				currDist = (float) Math.sqrt(Math.pow(die.coord[0]-x, 2) + Math.pow(die.coord[1]-y, 2));
				if(currDist < limit && currDist < minDist){
					closest = die;
					minDist = currDist;
				}
			}
			if(closest != null){
				closest.lockToggle();
				renderer.diceHandView.toggleHeld(renderer.dice.indexOf(closest));
			}
			return true;
		}
		return false;
	}
}
