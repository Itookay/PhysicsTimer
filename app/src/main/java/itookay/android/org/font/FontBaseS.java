package itookay.android.org.font;


/**
 * 			タイルの小さい数字フォントのスーパークラス
 */
public abstract class FontBaseS extends FontBase {

	public final int	COLUMN_COUNT = 9;
	public final int	SIZE = COLUMN_COUNT * 10;
	public final int	SEPARATE_COLUMN_COUNT = 3;

	public FontBaseS() {

		int[]	none =
			{ 	0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0,
				0, 0, 0, 0, 0, 0, 0, 0, 0	};
		NONE = none.clone();

		int[]	blank =
			{	0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,	};
		BLANK = blank.clone();

		int[]	cologne =
			{	0, 0, 0,
				0, 0, 0,
				1, 1, 0,
				1, 1, 0,
				0, 0, 0,
				0, 0, 0,
				1, 1, 0,
				1, 1, 0,
				0, 0, 0,
				0, 0, 0,	};
		COLOGNE = cologne.clone();

		int[]	dot =
			{	0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				0, 0, 0,
				1, 1, 0,
				1, 1, 0,	};
		DOT = dot.clone();
	}

	@Override
	public int getColumnCount() {

		return COLUMN_COUNT;
	}

	@Override
	public int getSeparateColumnCount() {

		return SEPARATE_COLUMN_COUNT;
	}

	@Override
	public int get2NumbersColumnsCount() {

		return COLUMN_COUNT * 2;
	}

	@Override
	public int getArrayLength() {

		return SIZE;
	}
}
