package com.jorflekel.yahtzee;

import java.util.Arrays;

public class Hands {

	public interface Hand {
		public int score(int[] hand);
		public String getName();
	}

	public static final Hand ACES = new Hand() {

		private static final int value = 1;

		@Override
		public int score(int[] hand) {
			int sum = 0;
			for (int i : hand) {
				if (i == value) {
					sum += i;
				}
			}
			return sum;
		}

		@Override
		public String getName() {
			return "Aces";
		}

	};

	public static final Hand TWOS = new Hand() {

		private static final int value = 2;

		@Override
		public int score(int[] hand) {
			int sum = 0;
			for (int i : hand) {
				if (i == value) {
					sum += i;
				}
			}
			return sum;
		}

		@Override
		public String getName() {
			return "Twos";
		}

	};

	public static final Hand THREES = new Hand() {

		private static final int value = 3;

		@Override
		public int score(int[] hand) {
			int sum = 0;
			for (int i : hand) {
				if (i == value) {
					sum += i;
				}
			}
			return sum;
		}

		@Override
		public String getName() {
			return "Threes";
		}

	};

	public static final Hand FOURS = new Hand() {

		private static final int value = 4;

		@Override
		public int score(int[] hand) {
			int sum = 0;
			for (int i : hand) {
				if (i == value) {
					sum += i;
				}
			}
			return sum;
		}

		@Override
		public String getName() {
			return "Fours";
		}

	};

	public static final Hand FIVES = new Hand() {

		private static final int value = 5;

		@Override
		public int score(int[] hand) {
			int sum = 0;
			for (int i : hand) {
				if (i == value) {
					sum += i;
				}
			}
			return sum;
		}

		@Override
		public String getName() {
			return "Fives";
		}

	};

	public static final Hand SIXES = new Hand() {

		private static final int value = 6;

		@Override
		public int score(int[] hand) {
			int sum = 0;
			for (int i : hand) {
				if (i == value) {
					sum += i;
				}
			}
			return sum;
		}

		@Override
		public String getName() {
			return "Sixes";
		}

	};

	public static final Hand THREE_OF_A_KIND = new Hand() {

		@Override
		public int score(int[] hand) {
			int sum = 0;
			boolean isThreeOfAKind = false;
			int[] count = new int[6];
			for (int i = 0; i < 6; i++) {
				count[i] = 0;
			}
			for (int i : hand) {
				count[i - 1]++;
				sum += i;
			}
			for (int i : count) {
				if (i >= 3)
					isThreeOfAKind = true;
			}

			return isThreeOfAKind ? sum : 0;
		}

		@Override
		public String getName() {
			return "Three of a Kind";
		}

	};

	public static final Hand FOUR_OF_A_KIND = new Hand() {

		@Override
		public int score(int[] hand) {
			int sum = 0;
			boolean isFourOfAKind = false;
			int[] count = new int[6];
			for (int i = 0; i < 6; i++) {
				count[i] = 0;
			}
			for (int i : hand) {
				count[i - 1]++;
				sum += i;
			}
			for (int i : count) {
				if (i >= 4)
					isFourOfAKind = true;
			}

			return isFourOfAKind ? sum : 0;
		}

		@Override
		public String getName() {
			return "Four of a Kind";
		}

	};

	public static final Hand FULL_HOUSE = new Hand() {

		@Override
		public int score(int[] hand) {
			Arrays.sort(hand);
			if ((hand[0] == hand[1] && hand[2] == hand[3] && hand[3] == hand[4])
					|| (hand[0] == hand[1] && hand[1] == hand[2] && hand[3] == hand[4]))
				return 25;
			return 0;
		}

		@Override
		public String getName() {
			return "Full House";
		}

	};

	public static final Hand SMALL_STRAIGHT = new Hand() {

		@Override
		public int score(int[] hand) {
			Arrays.sort(hand);
			if ((hand[0] == hand[1] && hand[1] == hand[2] && hand[2] == hand[3])
					|| (hand[1] == hand[2] && hand[2] == hand[3] && hand[3] == hand[4]))
				return 30;
			return 0;
		}

		@Override
		public String getName() {
			return "Small Straight";
		}

	};

	public static final Hand LARGE_STRAIGHT = new Hand() {

		@Override
		public int score(int[] hand) {
			Arrays.sort(hand);
			if (hand[0] == hand[1] && hand[1] == hand[2] && hand[2] == hand[3]
					&& hand[3] == hand[4])
				return 40;
			return 0;
		}

		@Override
		public String getName() {
			return "Large Straight";
		}

	};
	
	public static final Hand YAHTZEE = new Hand() {

		@Override
		public int score(int[] hand) {
			boolean isYahtzee = false;
			int[] count = new int[6];
			for (int i = 0; i < 6; i++) {
				count[i] = 0;
			}
			for (int i : hand) {
				count[i - 1]++;
			}
			for (int i : count) {
				if (i >= 5)
					isYahtzee = true;
			}
			
			if(isYahtzee) return 50;
			return 0;
		}

		@Override
		public String getName() {
			return "Yahtzee";
		}

	};

	public static final Hand CHANCE = new Hand() {

		@Override
		public int score(int[] hand) {
			int sum = 0;
			for (int i : hand) {
					sum += i;
			}
			return sum;
		}

		@Override
		public String getName() {
			return "Chance";
		}

	};
	
	public static final Hand BONUS = new Hand() {

		@Override
		public int score(int[] hand) {
			return 35;
		}

		@Override
		public String getName() {
			return "Bonus";
		}
		
	};
	
}
