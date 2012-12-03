package com.jorflekel.yahtzee;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.jorflekel.yahtzee.Hands.Hand;
import com.jorflekel.yahtzee.views.DiceHandView;
import com.jorflekel.yahtzee.views.DiceHandView.HandChangeListener;
import com.jorflekel.yahtzee.views.DieGLSurfaceView;
import com.jorflekel.yahtzee.views.DieRenderer;
import com.jorflekel.yahtzee.views.DieRenderer.DieState;
import com.jorflekel.yahtzee.views.HelpDialog;
import com.jorflekel.yahtzee.views.ProbabilityHelperView;

public class GameActivity extends Activity implements SensorEventListener, HandChangeListener {

	private List<TextView> scoreBoxes;
	private TextView aces, twos, threes, fours, fives, sixes, bonus, threeOf,
			fourOf, fullHouse, smallSt, largeSt, yahtzee, chance;
	private TextView rollsLabel;
	private DiceHandView diceHandView;
	private int[] hand;
	private int rollsSinceMove = 0;
	private ScoreCard scoreCard;
	private ProbabilityHelperView probabilityHelperView;
	private DieGLSurfaceView dieGLSurfaceView;
	private DieRenderer dieRenderer;
	private ArrayList<DieState> GLDice;
	private boolean moved;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game);
		//

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

		scoreCard = new ScoreCard();

		diceHandView = (DiceHandView) findViewById(R.id.diceHandView);
		diceHandView.hideNumbers();
		diceHandView.setHandChangeListener(this);
		
		dieGLSurfaceView = ((DieGLSurfaceView) findViewById(R.id.dieGLSurfaceView));
		dieRenderer = dieGLSurfaceView.renderer;
		dieRenderer.diceHandView = diceHandView;
		GLDice = dieGLSurfaceView.renderer.getDice();
		
		diceHandView.GLdice = dieRenderer.getDice();

		probabilityHelperView = (ProbabilityHelperView) findViewById(R.id.probabilityHelperView);
		
		rollsLabel = (TextView) findViewById(R.id.rollsLabel);

		bonus.setText("63 more");

		SensorManager sensors = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		sensors.registerListener(this,
				sensors.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), 100);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_game, menu);
		return true;
	}

	private void roll() {
		moved = false;
		hand = diceHandView.roll();
	}

	public void onShakeClick(View v) {
		if (rollsSinceMove < 3) {
			startTurn();
			shakeTime = System.currentTimeMillis();
		}
	}

	public void startTurn() {
    	rollsSinceMove++;
    	roll();
    	rollsLabel.setText("" + (3-rollsSinceMove));
    	MediaPlayer player = MediaPlayer.create(this, R.raw.roll);
    	player.start();
    	for(TextView tv : scoreBoxes) {
    		if(tv.getTag(R.id.scoreId) == null && tv != bonus)
    			tv.setText("" + ((Hand)tv.getTag(R.id.handId)).score(hand));
    	}
    	final DieGLSurfaceView glview = (DieGLSurfaceView) findViewById(R.id.dieGLSurfaceView);
    	glview.renderer.startBounce();
    	final Handler handler = new Handler();
    	handler.postDelayed(new Runnable() {
    	  @Override
    	  public void run() {
    	    glview.renderer.endBounce();
    	    for(int i = 0; i < diceHandView.getHand().length; i++){
    	    	GLDice.get(i).requestFaceUp(diceHandView.getHand()[i]);
    	    }
    	  }
    	}, 1000);
	}

	public void clearEmptyScores() {
		for (TextView tv : scoreBoxes) {
			if (tv.getTag(R.id.scoreId) == null
					&& !(tv.getId() == R.id.upperBonusScore))
				tv.setText("");
		}
	}

	public void onScoreClick(View v) {
		if (v.getId() == R.id.upperBonusScore)
			return;
		if (v.getTag(R.id.scoreId) == null && !moved && rollsSinceMove > 0) {
			v.setTag(R.id.scoreId, ((Hand) v.getTag(R.id.handId)).score(hand));
			((TextView) v).setTextColor(Color.parseColor("#254117"));
			rollsSinceMove = 0;
			moved = true;
			diceHandView.hideNumbers();
			diceHandView.toggleOff();
			rollsLabel.setText("3");
			scoreCard.setScore(((Hand) v.getTag(R.id.handId)).getName(),
					((Hand) v.getTag(R.id.handId)).score(hand));
			int upperScore = scoreCard.getUpperScore();
			Log.d("GameActivity", "" + upperScore);
			if (scoreCard.getUpperScore() >= 63) {
				bonus.setText("35");
				bonus.setTextColor(Color.parseColor("#254117"));
				bonus.setTag(35);
				scoreCard.setScore("bonus", 35);
			} else {
				((TextView) findViewById(R.id.upperBonusScore)).setText(""
						+ (63 - scoreCard.getUpperScore()) + " more");
			}
			clearEmptyScores();
			if (scoreCard.isUpperSectionDone() && bonus.getTag() == null) {
				scoreCard.setScore("bonus", 0);
				bonus.setTag(0);
			}
			if (scoreCard.isDone()) {
				new AlertDialog.Builder(this)
						.setTitle("Game Over!")
						.setMessage(
								"Congratulations! You scored "
										+ scoreCard.getTotalScore()).create()
						.show();
			}
		}
	}

	public void onLabelClick(View v) {
		switch (v.getId()) {
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

	public void onNewGameClick(View v) {
		Intent intent = new Intent(this, GameActivity.class);
		startActivity(intent);
		finish();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}

	private float yA = 0;
	private long shakeTime = 0;
	private boolean paused;

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (!paused) {
			float nYA = event.values[1];
			if (Math.abs(nYA) > .3 && Math.abs(yA) > .3
					&& Math.signum(nYA) == -Math.signum(yA)
					&& rollsSinceMove < 3
					& System.currentTimeMillis() - shakeTime > 2000) {
				startTurn();
				shakeTime = System.currentTimeMillis();
			}
			yA = nYA;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		paused = true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		paused = false;
	}

	@Override
	public void onHeldChanged(boolean[] held) {
		probabilityHelperView.setHeld(held);
	}

	@Override
	public void onHandChanged(int[] hand) {
		probabilityHelperView.setHand(hand);
	}

}
