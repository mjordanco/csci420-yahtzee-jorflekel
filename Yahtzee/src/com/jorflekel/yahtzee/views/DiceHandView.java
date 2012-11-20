package com.jorflekel.yahtzee.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.jorflekel.yahtzee.R;

public class DiceHandView extends FrameLayout {

	int[] ids = new int[] {
		R.id.die1,
		R.id.die2,
		R.id.die3,
		R.id.die4,
		R.id.die5
	};
	
	ToggleButton[] dice;

	private int[] hand;
	
	public DiceHandView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.dice_hand_view, this);
		
		dice = new ToggleButton[5];
		for(int i = 0; i < 5; i++) {
			dice[i] = (ToggleButton) findViewById(ids[i]);
		}
		
		hand = new int[5];
	}
	
	public void toggleOff() {
		for(ToggleButton button : dice) {
			button.setChecked(false);
		}
	}
	
	public void setHand() {
		for(int i = 0; i < 5; i++) {
			dice[i].setText("" + hand[i]);
			dice[i].setTextOn("" + hand[i]);
			dice[i].setTextOff("" + hand[i]);
		}
	}

	public int[] roll() {
		for(int i = 0; i < 5; i++) {
			dice[i].setEnabled(true);
			Log.d("FVNEIZA", " ");
			Log.d("FVNEIZA", "Old die value: " +hand[i]);
			if(!dice[i].isChecked()) {
				hand[i] = (int) (Math.random() * 6 + 1);
			} else {
			}
			Log.d("FVNEIZA", "New die value: " +hand[i]);
    	}
    	setHand();
		return hand;
	}
	
	public void hideNumbers() {
		for(ToggleButton button : dice) {
			button.setText("");
			button.setTextOn("");
			button.setTextOff("");
			button.setEnabled(false);
		}
	}
}
