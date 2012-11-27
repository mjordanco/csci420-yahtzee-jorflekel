package com.example.diceview2;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.ViewGroup;

public class DiceTestingActivity extends Activity {

	private DieGLSurfaceView mDieView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		mDieView = new DieGLSurfaceView(this);
        setContentView(R.layout.activity_dice_testing);
        ViewGroup layout = (ViewGroup) findViewById(R.id.root);
        layout.addView(mDieView);
        setContentView(layout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_dice_testing, menu);
		return true;
	}

}
