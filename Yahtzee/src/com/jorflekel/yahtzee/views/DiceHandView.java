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
	
	public boolean[] getHeldDice() {
		boolean[] held = new boolean[5];
		for(int i = 0; i < 5; i++) {
			held[i] = dice[i].isChecked();
		}
		return held;
	}
	
	public int[] getHand() {
		return hand != null ? hand : new int[5];
	}

	public int[] roll() {
		for(int i = 0; i < 5; i++) {
			dice[i].setEnabled(true);
			if(!dice[i].isChecked()) {
				hand[i] = (int) (Math.random() * 6 + 1);
			} else {
				Log.d("FJVRCE", "" + i + " is checked");
			}
			Log.d("FVNEIZA", "Die value: " +hand[i]);
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
