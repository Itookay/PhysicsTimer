package itookay.android.org.font;

public class Normal extends FontBase {

	private static final long serialVersionUID = 1L;

	public Normal() {
		NAME = "Normal";

		COLUMN_COUNT = 4;
		SEPARATE_COLUMN_COUNT = 1;
		ROW_COUNT = 5;
		ARRAY_SIZE = COLUMN_COUNT * ROW_COUNT;
		COLOGNE_COLUMN_COUNT = 1;

		int[]	zero =
			{	1, 1, 1, 1,
				1, 0, 0, 1,
				1, 0, 0, 1,
				1, 0, 0, 1,
				1, 1, 1, 1	};
		ZERO = zero.clone();

		int[]	one =
			{	0, 1, 1, 0,
				0, 0, 1, 0,
				0, 0, 1, 0,
				0, 0, 1, 0,
				0, 0, 1, 0 };
		ONE = one.clone();

		int[]	two =
			{	1, 1, 1, 1,
				0, 0, 0, 1,
				1, 1, 1, 1,
				1, 0, 0, 0,
				1, 1, 1, 1 };
		TWO = two.clone();

		int[]	three=
			{	1, 1, 1, 1,
				0, 0, 0, 1,
				1, 1, 1, 1,
				0, 0, 0, 1,
				1, 1, 1, 1	};
		THREE = three.clone();

		int[]	four =
			{	1, 0, 0, 1,
				1, 0, 0, 1,
				1, 1, 1, 1,
				0, 0, 0, 1,
				0, 0, 0, 1	};
		FOUR = four.clone();

		int[]	five =
			{	1, 1, 1, 1,
				1, 0, 0, 0,
				1, 1, 1, 1,
				0, 0, 0, 1,
				1, 1, 1, 1	};
		FIVE = five.clone();

		int[]	six =
			{	1, 1, 1, 1,
				1, 0, 0, 0,
				1, 1, 1, 1,
				1, 0, 0, 1,
				1, 1, 1, 1	};
		SIX = six.clone();

		int[]	seven =
			{	1, 1, 1, 1,
				1, 0, 0, 1,
				0, 0, 0, 1,
				0, 0, 0, 1,
				0, 0, 0, 1	};
		SEVEN = seven.clone();

		int[]	eight =
			{	1, 1, 1, 1,
				1, 0, 0, 1,
				1, 1, 1, 1,
				1, 0, 0, 1,
				1, 1, 1, 1	};
		EIGHT = eight.clone();

		int[]	nine =
			{	1, 1, 1, 1,
				1, 0, 0, 1,
				1, 1, 1, 1,
				0, 0, 0, 1,
				1, 1, 1, 1	};
		NINE = nine.clone();

		int[]	none =
			{	0, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0,	};
		NONE = none.clone();

		int[]	cologne =
				{	0,
					1,
					0,
					1,
					0,	};
		COLOGNE = cologne.clone();

		fontArray.add(0, ZERO);
		fontArray.add(1, ONE);
		fontArray.add(2, TWO);
		fontArray.add(3, THREE);
		fontArray.add(4, FOUR);
		fontArray.add(5, FIVE);
		fontArray.add(6, SIX);
		fontArray.add(7, SEVEN);
		fontArray.add(8, EIGHT);
		fontArray.add(9, NINE);
		fontArray.add(10, NONE);
	}
}
