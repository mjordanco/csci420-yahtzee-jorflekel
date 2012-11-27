package com.jorflekel.yahtzee;

import java.util.HashMap;

import android.util.Log;

public class ScoreCard {

	/**
	 * @param args
	 */
	private HashMap<String, Integer> scores;
	private HashMap<String, Integer> optimal;
	private static final String[] SECTIONS = { "aces", "twos", "threes",
			"fours", "fives", "sixes", "bonus", "three of a kind",
			"four of a kind", "full house", "small straight", "large straight",
			"yahtzee", "chance" };

	private static final String[] UPPER_SECTION = { "aces", "twos", "threes",
			"fours", "fives", "sixes" };

	private static final int[] OPTIMAL_SCORES = { 5, 10, 15, 20, 25, 30, 35,
			30, 30, 25, 30, 40, 50, 30 };

	public ScoreCard() {
		scores = new HashMap<String, Integer>();
		optimal = new HashMap<String, Integer>();

		for (int i = 0; i < SECTIONS.length; i++) {
			optimal.put(SECTIONS[i], OPTIMAL_SCORES[i]);
//			 scores.put(SECTIONS[i], -1);
		}
	}

	public void setScore(String section, int val) {
		section =  section.toLowerCase();
		boolean valid = false;
		for (String s : SECTIONS) {
			valid = valid || section.equalsIgnoreCase(s);
		}
		if (valid && val >= 0) {
			scores.remove(section);
			scores.put(section, val);
		} else {
			Log.e("SCORECARD", "Invalid section or score: " + section + ", "
					+ val);
		}
	}

	public int getScore(String section) {
		section = section.toLowerCase();
		boolean valid = false;
		for (String s : SECTIONS) {
			valid = valid || section.equals(s);
		}
		if (valid) {
			return scores.get(section);
		} else {
			Log.e("SCORECARD", "Invalid section!");
			return -1;
		}
	}

	public int calcDeficit() {
		int deficit = 0;
		for (String s : SECTIONS) {
			if (scores.get(s) != -1) {
				deficit += (optimal.get(s) - scores.get(s));
			}
		}

		return deficit;
	}

	public int calcBestPossible() {
		int deficit = calcDeficit();
		int best = 0;
		for (String s : SECTIONS) {
			best += optimal.get(s);
		}

		return (best - deficit);
	}
	
	public boolean isUpperSectionDone() {
		boolean done = true;
		for(int i = 0; i < UPPER_SECTION.length - 1; i++) {
			String section = UPPER_SECTION[i];
			done = done && scores.containsKey(section);
		}
		return done;
	}

	public int getUpperScore() {
		int sum = 0;
		for (String s : UPPER_SECTION) {
			if (scores.containsKey(s))
				sum += getScore(s);
			else
				Log.d("ScoreCard", "Does not contain value for key " + s);
		}
		return sum;
	}
	
	public boolean isDone() {
		boolean done = true;
		for(String section : SECTIONS) {
			done = done && scores.containsKey(section) || section.equals("bonus");
		}
		return done;
	}

	public int getTotalScore() {
		int sum = 0;
		for (String s : SECTIONS) {
			if (scores.containsKey(s))
				sum += getScore(s);
			else
				Log.d("ScoreCard", "Does not contain value for key " + s);
		}
		return sum;
	}

}
