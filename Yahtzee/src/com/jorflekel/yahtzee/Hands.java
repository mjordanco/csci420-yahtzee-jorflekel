package com.jorflekel.yahtzee;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Hands {

	public interface Hand {
		public int score(int[] hand);
		public String getName();
		public int getDescriptionId();
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

		@Override
		public int getDescriptionId() {
			return R.string.aces_desc;
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

		@Override
		public int getDescriptionId() {
			return R.string.twos_desc;
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

		@Override
		public int getDescriptionId() {
			return R.string.threes_desc;
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

		@Override
		public int getDescriptionId() {
			return R.string.fours_desc;
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

		@Override
		public int getDescriptionId() {
			return R.string.fives_desc;
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

		@Override
		public int getDescriptionId() {
			return R.string.sixes_desc;
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

		@Override
		public int getDescriptionId() {
			return R.string.triple_desc;
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

		@Override
		public int getDescriptionId() {
			return R.string.quad_desc;
		}

	};

	public static final Hand FULL_HOUSE = new Hand() {

		@Override
		public int score(int[] hand) {
			int[] copy = hand.clone();
			Arrays.sort(copy);
			if ((copy[0] == copy[1] && copy[2] == copy[3] && copy[3] == copy[4])
					|| (copy[0] == copy[1] && copy[1] == copy[2] && copy[3] == copy[4]))
				return 25;
			return 0;
		}

		@Override
		public String getName() {
			return "Full House";
		}

		@Override
		public int getDescriptionId() {
			return R.string.house_desc;
		}

	};

	public static final Hand SMALL_STRAIGHT = new Hand() {

		@Override
		public int score(int[] hand) {
			Set<Integer> set = new HashSet<Integer>();
			for(int i : hand) {
				set.add(i);
			}
			Integer[] copy = set.toArray(new Integer[] {});
			Arrays.sort(copy);
			if ((copy.length >= 4 && copy[0] == copy[1] - 1 && copy[1] == copy[2] - 1  && copy[2] == copy[3] - 1)
					|| (copy.length >= 5 && copy[1] == copy[2] - 1 && copy[2] == copy[3] - 1 && copy[3] == copy[4] - 1))
				return 30;
			return 0;
		}

		@Override
		public String getName() {
			return "Small Straight";
		}

		@Override
		public int getDescriptionId() {
			return R.string.sm_st_desc;
		}

	};

	public static final Hand LARGE_STRAIGHT = new Hand() {

		@Override
		public int score(int[] hand) {
			int[] copy = hand.clone();
			Arrays.sort(copy);
			if (copy[0] == copy[1] - 1 && copy[1] == copy[2] - 1 && copy[2] == copy[3] - 1
					&& copy[3] == copy[4] - 1)
				return 40;
			return 0;
		}

		@Override
		public String getName() {
			return "Large Straight";
		}

		@Override
		public int getDescriptionId() {
			return R.string.lg_st_desc;
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

		@Override
		public int getDescriptionId() {
			return R.string.yahtzee_desc;
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

		@Override
		public int getDescriptionId() {
			return R.string.chance_desc;
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

		@Override
		public int getDescriptionId() {
			return R.string.bonus_desc;
		}
		
	};
	
}
