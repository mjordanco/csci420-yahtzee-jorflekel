package com.jorflekel.yahtzee.views;

import com.jorflekel.yahtzee.ProbHelper;
import com.jorflekel.yahtzee.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.TextView;

public class ProbabilityHelperView extends FrameLayout {

	
	
	public ProbabilityHelperView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(context).inflate(R.layout.probability_helper, this);
		
	}

}
