package com.jorflekel.yahtzee.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ToggleButton;

import com.jorflekel.yahtzee.R;

public class DiceHandView extends FrameLayout implements OnCheckedChangeListener{

	public interface HandChangeListener {
		public void onHeldChanged(boolean[] held);
		public void onHandChanged(int[] hand);
	}
	
	int[] ids = new int[] {
		R.id.die1,
		R.id.die2,
		R.id.die3,
		R.id.die4,
		R.id.die5
	};
	
	ToggleButton[] dice;

	private int[] hand;
	private HandChangeListener handChangeListener;
	
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
	
	public void setHand(int[] hand) {
		this.hand = hand;
	}

	public int[] roll() {
		for(int i = 0; i < 5; i++) {
			dice[i].setEnabled(true);
			if(!dice[i].isChecked()) {
				hand[i] = (int) (Math.random() * 6 + 1);
			} else {
			}
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

	public void incrementDieValue(int i) {
		hand[i] = hand[i] < 6 ? hand[i] + 1 : 1; 
		setHand();
	}
	
	public void decrementDieValue(int i) {
		hand[i] = hand[i] > 1 ? hand[i] - 1 : 6; 
		setHand();
	}

	public void setHeld(boolean[] held) {
		for(int i = 0; i < held.length; i++) {
			dice[i].setChecked(held[i]);
		}
	}

	public void setHeldChangeListener(HandChangeListener heldChangeListener) {
		this.handChangeListener = heldChangeListener;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		boolean[] held = new boolean[5];
		for(int i = 0; i < 5; i++) {
			ToggleButton button = dice[i];
			held[i] = button.isChecked();
		}
		if(handChangeListener != null) {
			handChangeListener.onHeldChanged(held);
		}
	}
}
