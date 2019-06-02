package itookay.android.org.font;

public class NormalB extends FontBaseB {

	private static final long serialVersionUID = 1L;

	public NormalB() {

		FONT_NAME = "ノーマルＢ";

		int[]	zero =
			{	1, 1, 1, 1, 0,
				1, 0, 0, 1, 0,
				1, 0, 0, 1, 0,
				1, 0, 0, 1, 0,
				1, 1, 1, 1, 0	};
		ZERO = zero.clone();

		int[]	one =
			{	0, 1, 1, 0, 0,
				0, 0, 1, 0, 0,
				0, 0, 1, 0, 0,
				0, 0, 1, 0, 0,
				0, 0, 1, 0, 0	};
		ONE = one.clone();

		int[]	two =
			{	1, 1, 1, 1, 0,
				0, 0, 0, 1, 0,
				1, 1, 1, 1, 0,
				1, 0, 0, 0, 0,
				1, 1, 1, 1, 0	};
		TWO = two.clone();

		int[]	three=
			{	1, 1, 1, 1, 0,
				0, 0, 0, 1, 0,
				1, 1, 1, 1, 0,
				0, 0, 0, 1, 0,
				1, 1, 1, 1, 0	};
		THREE = three.clone();

		int[]	four =
			{	1, 0, 0, 1, 0,
				1, 0, 0, 1, 0,
				1, 1, 1, 1, 0,
				0, 0, 0, 1, 0,
				0, 0, 0, 1, 0	};
		FOUR = four.clone();

		int[]	five =
			{	1, 1, 1, 1, 0,
				1, 0, 0, 0, 0,
				1, 1, 1, 1, 0,
				0, 0, 0, 1, 0,
				1, 1, 1, 1, 0	};
		FIVE = five.clone();

		int[]	six =
			{	1, 1, 1, 1, 0,
				1, 0, 0, 0, 0,
				1, 1, 1, 1, 0,
				1, 0, 0, 1, 0,
				1, 1, 1, 1, 0	};
		SIX = six.clone();

		int[]	seven =
			{	1, 1, 1, 1, 0,
				1, 0, 0, 1, 0,
				0, 0, 0, 1, 0,
				0, 0, 0, 1, 0,
				0, 0, 0, 1, 0	};
		SEVEN = seven.clone();

		int[]	eight =
			{	1, 1, 1, 1, 0,
				1, 0, 0, 1, 0,
				1, 1, 1, 1, 0,
				1, 0, 0, 1, 0,
				1, 1, 1, 1, 0	};
		EIGHT = eight.clone();

		int[]	nine =
			{	1, 1, 1, 1, 0,
				1, 0, 0, 1, 0,
				1, 1, 1, 1, 0,
				0, 0, 0, 1, 0,
				1, 1, 1, 1, 0	};
		NINE = nine.clone();

		fontArray.add( 0, ZERO );
		fontArray.add( 1, ONE );
		fontArray.add( 2, TWO );
		fontArray.add( 3, THREE );
		fontArray.add( 4, FOUR );
		fontArray.add( 5, FIVE );
		fontArray.add( 6, SIX );
		fontArray.add( 7, SEVEN );
		fontArray.add( 8, EIGHT );
		fontArray.add( 9, NINE );
		fontArray.add( 10, NONE );
	}

}
