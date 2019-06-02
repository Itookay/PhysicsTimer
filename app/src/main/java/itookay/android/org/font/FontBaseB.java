package itookay.android.org.font;

/**
 * 			タイルの大きい数字フォントのスーパークラス
 */
public abstract class FontBaseB extends FontBase {

	public final int	COLUMN_COUNT = 4;
	public final int	SIZE = COLUMN_COUNT * 5;
	public final int	SEPARATE_COLUMN_COUNT = 1;


	public FontBaseB() {

		int[]	none =
			{	0, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0,
				0, 0, 0, 0,	};
		NONE = none.clone();

		int[]	blank =
			{	0,
				0,
				0,
				0,
				0,	};
		BLANK = blank.clone();

		int[]	cologne =
			{	0,
				1,
				0,
				1,
				0,	};
		COLOGNE = cologne.clone();

		int[]	dot =
			{	0,
				0,
				0,
				0,
				1,	};
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
