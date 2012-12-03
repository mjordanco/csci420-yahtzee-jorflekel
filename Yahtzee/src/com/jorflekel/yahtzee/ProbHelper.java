package com.jorflekel.yahtzee;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Random;
import android.util.Log;

public class ProbHelper {
	public static int SM_STRAIGHT = 1234;
	public static int LG_STRAIGHT = 12345;
	public static int HOUSE = 22333;
	
	private static ProbHelper singleton = null;
	private int[][] oneComb, twoComb, threeComb, fourComb, fiveComb;
	
	public static ProbHelper instance() {
		System.out.println("Instancing...");
		if(singleton == null) {
			singleton = new ProbHelper();
		}
		return singleton;
	}
	
	private ProbHelper() {
		oneComb = 	new int[6][1];
		twoComb = 	new int[6*6][2];
		threeComb = new int[6*6*6][3];
		fourComb = 	new int[6*6*6*6][4];
		fiveComb = 	new int[6*6*6*6*6][5];
		
		int oneCount=0, twoCount=0, threeCount=0, fourCount=0, fiveCount = 0;
		for(int a = 1; a < 7; a++) {
			oneComb[oneCount][0]=a;
			oneCount++;
			for(int b = 1; b < 7; b++) {
				twoComb[twoCount][0] = (a);
				twoComb[twoCount][1] = (b);
				twoCount++;
				for(int c = 1; c < 7; c++) {
					threeComb[threeCount][0] = (a);
					threeComb[threeCount][1] = (b);
					threeComb[threeCount][2] = (c);
					threeCount++;
					for(int d = 1; d < 7; d++) {
						fourComb[fourCount][0] = (a);
						fourComb[fourCount][1] = (b);
						fourComb[fourCount][2] = (c);
						fourComb[fourCount][3] = (d);
						fourCount++;
						for(int e = 1; e < 7; e++) {
							fiveComb[fiveCount][0] = (a);
							fiveComb[fiveCount][1] = (b);
							fiveComb[fiveCount][2] = (c);
							fiveComb[fiveCount][3] = (d);
							fiveComb[fiveCount][4] = (e);
							fiveCount++;
						}
					}
				}
			}	
		}
		
	}
	
	public double probOfDoubleJackStyle( int[] hand, int numDiceToRoll, int numTimesToRoll) {
		
		int numUnique = 0;
		if(hand != null) {
			Arrays.sort(hand);
			numUnique = 1;
			int current = hand[0];
			for(int i : hand) {
				if(current != i) {
					numUnique++;
					current = i;
				}
			}
		} else hand = new int[0];
		
		if(numUnique < hand.length || numDiceToRoll > 6) return 1;
		else if((numDiceToRoll + hand.length) < 2) return 0;
		
		double probNoMatchHand = Math.pow((6.0-(double)numUnique)/(6.0), numDiceToRoll);
		double probNoMatchRolled = 1;
		for(double i = 1; i < numDiceToRoll; i++) {
			probNoMatchRolled = probNoMatchRolled * (6.0-i)/6.0;
		}
		
		double result = 1.0 - probNoMatchHand * probNoMatchRolled;

		for(int i = 1; i < numTimesToRoll; i++) {
			
			result = result + (1.0 - result) * result;
		}
		
		return result;
	}
	
	public double probOfComb( int comb, int[] hand, int numDiceToRoll, int numTimesToRoll) {
		//if(comb == SM_STRAIGHT || comb == LG_STRAIGHT || comb == HOUSE || comb == 1234 || comb == 12345 || comb == 22333) Log.e("PROB_HELPER", "calcing prob for straight or house");
		if(hand == null) 
			hand = new int[0];
		if(!(comb == SM_STRAIGHT || comb == LG_STRAIGHT || comb == HOUSE)) {
			if((numDiceToRoll + hand.length) < comb) return 0;
		}
		double numComb = Math.pow(6,numDiceToRoll);
		int totDice = hand.length + numDiceToRoll;
		
		int[][] diceSet;
		switch(numDiceToRoll) {
		case 1: diceSet = oneComb;break;
		case 2: diceSet = twoComb;break;
		case 3: diceSet = threeComb;break;
		case 4: diceSet = fourComb;break;
		case 5: diceSet = fiveComb;break;
		default: diceSet = new int[0][0];break;
		}
		if((hand.length+numDiceToRoll) > 5){
			Log.e("PROB_HELPER", "hand and num dice to roll exceed 5: " + hand.length + ", " + numDiceToRoll);
			return -1;
		}
		int validHands = 0;
		int[][] results = new int[(int)numComb][totDice];
		
		for(int i = 0; i < numComb; i++) {
			for(int j = 0; j < numDiceToRoll; j++) {
				results[i][j] = diceSet[i][j];
			}
			for(int j = 0; j < hand.length; j++) {
				results[i][totDice-j-1] = hand[j];
			}
			if(containsComb(comb, results[i])) {
				validHands++;
			}
		}

		double result = (double) validHands/numComb;
		String handstring = "";
		for(int j = 0; j < hand.length; j++) {
			handstring += " " + hand[j];
		}
		//Log.e("PROB_HELPER", "Found " + result + " prob for hand " + handstring + " and comb: " + comb);
		for(int i = 1; i < numTimesToRoll; i++) {
			
			result = result + (1.0 - result) * result;
		}
		
		return result;
	}
	public boolean containsComb(int comb, int[] hand) {
		//if(comb == SM_STRAIGHT || comb == LG_STRAIGHT || comb == HOUSE || comb == 1234 || comb == 12345 || comb == 22333) Log.e("PROB_HELPER", "DETECTING prob for straight or house");
		int[] set = hand.clone();
		boolean result = false;
		boolean innerResult = true;
		Arrays.sort(set);
		if(comb == SM_STRAIGHT || comb == LG_STRAIGHT) {
			if(set.length < 4) return false;
			int comLength = (comb == SM_STRAIGHT ? 3 : 4);
			//remove double for small straight
			int[] newSet; int numDub = 0;
			if(comb == SM_STRAIGHT && containsComb(2,set)) {
				for(int i = 0; i < (set.length-1); i++) {
					if(set[i]==set[i+1]) {
						numDub++;
					}
				}
				newSet = new int[set.length-numDub];
				int prevAdded = -1;
				int index = 0;
				for(int i = 0; i < (set.length); i++) {
					
					if(set[i] != prevAdded) {
						newSet[index]=set[i];
						index++;
						prevAdded = set[i];
					}
				}
				set = newSet;
				if(set.length < 4) return false;
			}
			
			for(int i = 0; i < (set.length-comLength); i++) {
				for(int j = i; j < (i+comLength); j++) {
					innerResult = innerResult && (set[j]==(set[j+1]-1) );
				}
				result = result || innerResult;
				innerResult = true;
			}
		}else if( comb == HOUSE) {
			if(set.length < 5) return false;
			boolean firstTwo = containsComb(2, new int[] {set[0], set[1]});
			boolean lastTwo = containsComb(2, new int[] {set[3], set[4]});
			boolean firstThree = containsComb(3, new int[] {set[0], set[1], set[2]});
			boolean lastThree = containsComb(3, new int[] {set[2], set[3], set[4]});
			
			return ((firstTwo && lastThree) || (firstThree && lastTwo));
		} else {
			if(set.length < comb) return false;
			for(int i = 0; i < (set.length-comb+1); i++) {
				for(int j = i; j < (i+(comb-1)); j++) {
					innerResult = innerResult && (set[j]==set[j+1] );
				}
				result = result || innerResult;
				innerResult = true;
			}
		}
		return result;
	}

}

