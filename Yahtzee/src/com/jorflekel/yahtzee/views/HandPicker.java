package com.jorflekel.yahtzee.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jorflekel.yahtzee.R;
import com.jorflekel.yahtzee.views.DiceHandView.HandChangeListener;

public class HandPicker extends LinearLayout implements HandChangeListener{

	public interface OnHandChangedListener {
		public void onHandChanged(int[] hand);
		public void onHeldChanged(boolean[] held);
	}
	
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
	private OnHandChangedListener onHandChangedListener;
	
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
		handView.setHideNotHeld(true);
		handView.setHandChangeListener(this);
	}
	
	public void setHand(int[] hand) {
		handView.setHand(hand);
		if(onHandChangedListener != null) onHandChangedListener.onHandChanged(hand);
	}
	
	public void setHeld(boolean[] held) {
		handView.setHeld(held);
		if(onHandChangedListener != null) onHandChangedListener.onHeldChanged(held);
	}
	
	public boolean[] getHeld(){
		return handView.getHeld();
	}
	
	public void setOnHandChangedListener(OnHandChangedListener onHandChangedListener) {
		this.onHandChangedListener = onHandChangedListener;
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

	public int[] getHand() {
		return handView.getHand();
	}

	@Override
	public void onHeldChanged(boolean[] held) {
		if(onHandChangedListener != null) onHandChangedListener.onHeldChanged(held);
	}

	@Override
	public void onHandChanged(int[] hand) {
		if(onHandChangedListener != null) onHandChangedListener.onHandChanged(hand);
	}
	
}
