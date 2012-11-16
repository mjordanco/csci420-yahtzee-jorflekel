package com.jorflekel.yahtzee;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.jorflekel.yahtzee.Hands.Hand;
import com.jorflekel.yahtzee.views.HelpDialog;

public class GameActivity extends Activity implements SensorEventListener {
	
	private List<TextView> scoreBoxes;
	private TextView aces, twos, threes, fours, fives, sixes, bonus, threeOf, fourOf, fullHouse, smallSt, largeSt, yahtzee, chance;
	private TextView handLabel;
	private int[] hand;
	private boolean rolledWithoutMove = false;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        aces = (TextView) findViewById(R.id.acesScore);
        aces.setTag(R.id.handId, Hands.ACES);
        twos = (TextView) findViewById(R.id.twosScore);
        twos.setTag(R.id.handId, Hands.TWOS);
        threes = (TextView) findViewById(R.id.threesScore);
        threes.setTag(R.id.handId, Hands.THREES);
        fours = (TextView) findViewById(R.id.foursScore);
        fours.setTag(R.id.handId, Hands.FOURS);
        fives = (TextView) findViewById(R.id.fivesScore);
        fives.setTag(R.id.handId, Hands.FIVES);
        sixes = (TextView) findViewById(R.id.sixesScore);
        sixes.setTag(R.id.handId, Hands.SIXES);
        bonus = (TextView) findViewById(R.id.upperBonusScore);
        bonus.setTag(R.id.handId, Hands.BONUS);

        threeOf = (TextView) findViewById(R.id.threeOfAKindScore);
        threeOf.setTag(R.id.handId, Hands.THREE_OF_A_KIND);
        fourOf = (TextView) findViewById(R.id.fourOfAKindScore);
        fourOf.setTag(R.id.handId, Hands.FOUR_OF_A_KIND);
        fullHouse = (TextView) findViewById(R.id.fullHouseScore);
        fullHouse.setTag(R.id.handId, Hands.FULL_HOUSE);
        smallSt = (TextView) findViewById(R.id.smallStraightScore);
        smallSt.setTag(R.id.handId, Hands.SMALL_STRAIGHT);
        largeSt = (TextView) findViewById(R.id.largeStraightScore);
        largeSt.setTag(R.id.handId, Hands.LARGE_STRAIGHT);
        yahtzee = (TextView) findViewById(R.id.yahtzeeScore);
        yahtzee.setTag(R.id.handId, Hands.YAHTZEE);
        chance = (TextView) findViewById(R.id.chanceScore);
        chance.setTag(R.id.handId, Hands.CHANCE);
        
        scoreBoxes = new ArrayList<TextView>();
        
        scoreBoxes.add(aces);
        scoreBoxes.add(twos);
        scoreBoxes.add(threes);
        scoreBoxes.add(fours);
        scoreBoxes.add(fives);
        scoreBoxes.add(sixes);
        scoreBoxes.add(bonus);
        
        scoreBoxes.add(threeOf);
        scoreBoxes.add(fourOf);
        scoreBoxes.add(fullHouse);
        scoreBoxes.add(smallSt);
        scoreBoxes.add(largeSt);
        scoreBoxes.add(yahtzee);
        scoreBoxes.add(chance);
        
        handLabel = (TextView) findViewById(R.id.handLabel);
        
        startTurn();
        
        SensorManager sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensors.registerListener(this, sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 100);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_game, menu);
        return true;
    }
    
    private void roll() {
    	hand = new int[5];
    	String string = "";
    	for(int i = 0; i < 5; i++) {
    		hand[i] = (int) (Math.random() * 6 + 1);
    		string += hand[i] + " ";
    	}
    	handLabel.setText(string);
    }
    
    public void onShakeClick(View v) {
    	if(!rolledWithoutMove) startTurn();
    }
    
    public void startTurn() {
    	rolledWithoutMove = true;
    	roll();
    	for(TextView tv : scoreBoxes) {
    		if(tv.getTag(R.id.scoreId) == null)
    			tv.setText("" + ((Hand)tv.getTag(R.id.handId)).score(hand));
    	}
    }
    
    public void onScoreClick(View v) {
    	if(v.getTag(R.id.scoreId) == null) {
    		v.setTag(R.id.scoreId, ((Hand)v.getTag(R.id.handId)).score(hand));
    		((TextView) v).setTextColor(Color.BLACK);
    		rolledWithoutMove = false;
    	}
    }
    
    public void onLabelClick(View v) {
    	switch(v.getId()) {
    	case R.id.acesLabel:
    		new HelpDialog(this, Hands.ACES).show();
    		break;
    	case R.id.twosLabel:
    		new HelpDialog(this, Hands.TWOS).show();
    		break;
    	case R.id.threesLabel:
    		new HelpDialog(this, Hands.THREES).show();
    		break;
    	case R.id.foursLabel:
    		new HelpDialog(this, Hands.FOURS).show();
    		break;
    	case R.id.fivesLabel:
    		new HelpDialog(this, Hands.FIVES).show();
    		break;
    	case R.id.sixesLabel:
    		new HelpDialog(this, Hands.SIXES).show();
    		break;
    	case R.id.upperBonusLabel:
    		new HelpDialog(this, Hands.BONUS).show();
    		break;
    	case R.id.threeOfAKindLabel:
    		new HelpDialog(this, Hands.THREE_OF_A_KIND).show();
    		break;
    	case R.id.fourOfAKindLabel:
    		new HelpDialog(this, Hands.FOUR_OF_A_KIND).show();
    		break;
    	case R.id.fullHouseLabel:
    		new HelpDialog(this, Hands.FULL_HOUSE).show();
    		break;
    	case R.id.smallStraightLabel:
    		new HelpDialog(this, Hands.SMALL_STRAIGHT).show();
    		break;
    	case R.id.largeStraightLabel:
    		new HelpDialog(this, Hands.LARGE_STRAIGHT).show();
    		break;
    	case R.id.yahtzeeLabel:
    		new HelpDialog(this, Hands.YAHTZEE).show();
    		break;
    	case R.id.chanceLabel:
    		new HelpDialog(this, Hands.CHANCE).show();
    		break;
    	}
    }

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
		
	}

	private float yA = 0;
	
	@Override
	public void onSensorChanged(SensorEvent event) {
		float nYA = event.values[1];
		if(Math.signum(nYA) == -Math.signum(yA) && !rolledWithoutMove) {
			startTurn();
		}
		yA = nYA;
	}
    
}
