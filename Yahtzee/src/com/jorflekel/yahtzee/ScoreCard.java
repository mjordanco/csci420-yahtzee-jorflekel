package com.jorflekel.yahtzee;

import java.util.HashMap;

import android.util.Log;

public class ScoreCard {

	/**
	 * @param args
	 */
	private HashMap<String, Integer> scores;
	private HashMap<String, Integer> optimal;
	private static String[] sections = {"aces", "twos", "threes", "fours", "fives", "sixes", "bonus",
		"triple", "quad", "house", "small", "large", "yahtzee", "chance"};
	
	public ScoreCard() {
		scores = new HashMap<String, Integer>();
		optimal =  new HashMap<String, Integer>();
		optimal.put("aces", 	5);
		optimal.put("twos", 	10);
		optimal.put("threes", 	15);
		optimal.put("fours", 	20);
		optimal.put("fives", 	25);
		optimal.put("sixes", 	30);
		optimal.put("bonus", 	35);
		optimal.put("triple", 	30);
		optimal.put("quad", 	30);
		optimal.put("house", 	25);
		optimal.put("small", 	30);
		optimal.put("large", 	40);
		optimal.put("yahtzee", 	50);
		optimal.put("chance", 	30);
		
		scores.put("aces", 		-1);
		scores.put("twos", 		-1);
		scores.put("threes", 	-1);
		scores.put("fours", 	-1);
		scores.put("fives", 	-1);
		scores.put("sixes", 	-1);
		scores.put("bonus", 	-1);
		scores.put("triple", 	-1);
		scores.put("quad", 		-1);
		scores.put("house", 	-1);
		scores.put("small", 	-1);
		scores.put("large", 	-1);
		scores.put("yahtzee", 	-1);
		scores.put("chance", 	-1);
	}
	
	public void setScore(String section, int val) {
		boolean valid = false;
		for(String s : sections) {
			valid = valid || section.equals(s);
		}
		if(valid && val >= 0) {
			scores.remove(section);
			scores.put(section, val);
		} else {
			Log.e("SCORECARD", "Invalid section or score: " + section + ", " + val);
		}
	}
	public int getScore(String section) {
		boolean valid = false;
		for(String s : sections) {
			valid = valid || section.equals(s);
		}
		if(valid) {
			return scores.get(section);
		} else {
			Log.e("SCORECARD", "Invalid section!");
			return -1;
		}
	}
	
	public int calcDeficit() {
		int deficit = 0;
		for(String s : sections) {
			if(scores.get(s) != -1) {
				deficit += (optimal.get(s) - scores.get(s));
			}
		}
		
		return deficit;
	}
	
	public int calcBestPossible() {
		int deficit = calcDeficit();
		int best = 0;
		for(String s : sections) {
			best += optimal.get(s);
		}
		
		return (best - deficit);
	}

}
