package com.jorflekel.yahtzee.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jorflekel.yahtzee.R;

public class HandPicker extends LinearLayout {

	private static final int[] upIds = new int[] {
		R.id.die1Up,
		R.id.die2Up,
		R.id.die3Up,
		R.id.die4Up,
		R.id.die5Up
	};
	
	private static final int[] downIds = new int[] {
		R.id.die1Down,
		R.id.die2Down,
		R.id.die3Down,
		R.id.die4Down,
		R.id.die5Down
	};
	
	private static final String[] numbers = new String[] {
		"1",
		"2",
		"3",
		"4",
		"5",
		"6"
	};
	
	private View[] diceUp, diceDown;
	private DiceHandView handView;
	
	public HandPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		setOrientation(LinearLayout.VERTICAL);
		LayoutInflater.from(context).inflate(R.layout.hand_picker, this);
		
		diceUp = new View[5];
		diceDown = new View[5];
		for(int i = 0; i < 5; i++) {
			diceUp[i] = findViewById(upIds[i]);
			diceUp[i].setOnClickListener(onUpClick);

			diceDown[i] = findViewById(downIds[i]);
			diceDown[i].setOnClickListener(onDownClick);
		}
		
		handView = (DiceHandView) findViewById(R.id.diceHandView);
	}
	
	public void setHand(int[] hand) {
		handView.setHand(hand);
	}
	
	public void setHeld(boolean[] held) {
		handView.setHeld(held);
	}

	private final OnClickListener onUpClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			for(int i = 0; i < upIds.length; i++) {
				int checkId = upIds[i];
				if(id == checkId) {
					handView.incrementDieValue(i);
				}
			}
		}
		
	};
	
	private final OnClickListener onDownClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int id = v.getId();
			for(int i = 0; i < downIds.length; i++) {
				int checkId = downIds[i];
				if(id == checkId) {
					handView.decrementDieValue(i);
				}
			}
		}
		
	};
	
}
