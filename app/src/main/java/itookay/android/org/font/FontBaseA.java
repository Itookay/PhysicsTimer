package itookay.android.org.font;

import itookay.android.org.contents.DialPanel;

/**
 * 			タイルの大きい数字フォントのスーパークラス
 */
public abstract class FontBaseA extends FontBase {

	private final int	COLUMN_COUNT = 4;
	private final int	SEPARATE_COLUMN_COUNT = 1;
	private final int	ROW_COUNT = 5;
	private final int	ARRAY_SIZE = COLUMN_COUNT * ROW_COUNT;


	FontBaseA() {
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

	/**
	 * 		数字1文字分のカラム数を返す
	 */
	@Override
	public int getOneNumberColumnCount() {
		return COLUMN_COUNT;
	}

	/**
	 * 		数字2文字分のカラム数を返す
	 */
	@Override
	public int getTwoNumbersColumnsCount() {
		return COLUMN_COUNT * 2;
	}

	@Override
	public int getDialPanelColumnCount(int format) {
		if(format == DialPanel.MINUTE || format == DialPanel.SECOND) {
			return COLUMN_COUNT * 2;
		}
		else if(format == DialPanel.COLOGNE) {
			return SEPARATE_COLUMN_COUNT;
		}
		else {
			return 0;
		}
	}

	@Override
	public int getDialPanelArrayCount(int format) {
		if(format == DialPanel.MINUTE || format == DialPanel.SECOND) {
			return ARRAY_SIZE * 2;
		}
		else if(format == DialPanel.COLOGNE) {
			return SEPARATE_COLUMN_COUNT * ROW_COUNT;
		}
		else {
			return 0;
		}
	}
}
