package com.jorflekel.yahtzee.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.jorflekel.yahtzee.ProbHelper;
import com.jorflekel.yahtzee.R;
import com.jorflekel.yahtzee.views.HandPicker.OnHandChangedListener;

public class ProbabilityHelperView extends FrameLayout implements OnHandChangedListener{

	private final int[] ids = new int[] {
			R.id.threeOfAKindProb,
			R.id.fourOfAKindProb,
			R.id.fullHouseProb,
			R.id.smallStraightProb,
			R.id.largeStraightProb,
			R.id.yahtzeeProb
	};
	
	private HandPicker handPicker;
	private TextView[] probBoxes;
	private ProbHelper probHelper;
	
	public ProbabilityHelperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.probability_helper, this);
		
		handPicker = (HandPicker) findViewById(R.id.handPicker);
		handPicker.setOnHandChangedListener(this);
		probBoxes = new TextView[6];
		for(int i = 0; i < ids.length; i++) {
			probBoxes[i] = (TextView) findViewById(ids[i]);
		}
		
		probHelper = ProbHelper.instance();
	}
	
	public void setHand(int[] hand) {
		handPicker.setHand(hand);
	}
	
	public void setHeld(boolean[] held) {
		handPicker.setHeld(held);
		Log.d("ProbabilityHelperView", "Set held");
	}

	@Override
	public void onHandChanged(int[] hand) {
		updateProbs();
	}

	@Override
	public void onHeldChanged(boolean[] held) {
		updateProbs();
		Log.d("ProbabilityHelper", "Held Changed");
	}
	
	public void updateProbs() {
		int[] hand = handPicker.getHand();
		boolean[] held = handPicker.getHeld();
		int sum = 0;
		for(int i = 0; i < held.length; i++) {
			if(held[i]) sum++;
		}
		int[] probHand = new int[sum];
		int index = 0;
		String handstring = "";
		for(int i = 0; i < held.length; i++) {
			if(held[i]) {
				probHand[index] = hand[i];
				handstring += " " + hand[i];
				index++;
			}
		}
		Log.e("PROB_HELPER_VIEW", "calc prob rolling " + (5-sum) + " dice with hand:" + handstring);
		
		double trips = probHelper.probOfComb(3, probHand, 5 - sum, 3);
		probBoxes[0].setText("" + toPercent(trips));
		
		double quads = probHelper.probOfComb(4, probHand, 5 - sum, 3);
		probBoxes[1].setText("" + toPercent(quads));
		
		double full = probHelper.probOfComb(ProbHelper.HOUSE, probHand, 5 - sum, 3);
		probBoxes[2].setText("" + toPercent(full));
		
		double small = probHelper.probOfComb(ProbHelper.SM_STRAIGHT, probHand, 5 - sum, 3);
		probBoxes[3].setText("" + toPercent(small));
		
		double large = probHelper.probOfComb(ProbHelper.LG_STRAIGHT, probHand, 5 - sum, 3);
		probBoxes[4].setText("" + toPercent(large));
		
		double yahtz = probHelper.probOfComb(5, probHand, 5 - sum, 3);
		probBoxes[5].setText("" + toPercent(yahtz));
	}
	
	public String toPercent(double probability) {
		int percent = (int) (probability * 10000);
		double percentValue = percent / 100.0;
		return "" + percentValue + "%";
	}
	
}
