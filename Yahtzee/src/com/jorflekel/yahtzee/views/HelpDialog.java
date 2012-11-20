package com.jorflekel.yahtzee.views;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.widget.TextView;

import com.jorflekel.yahtzee.Hands.Hand;
import com.jorflekel.yahtzee.R;

public class HelpDialog extends Dialog {

	private TextView title;

	public HelpDialog(Context context, Hand hand) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.help_pop_up);
		
		title = (TextView) findViewById(R.id.helpTitle);
		
		title.setText(hand.getName());
	}

}
