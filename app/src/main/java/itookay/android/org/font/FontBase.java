package itookay.android.org.font;

import itookay.android.org.contents.DialPanel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * 			すべてのフォントのスーパークラス
 */
public abstract class FontBase implements Serializable {

	private static final long serialVersionUID = 1L;

	/** フォント名 */
	public String		NAME = null;

	int[]		ONE = null;
	int[]		TWO= null;
	int[]		THREE = null;
	int[]		FOUR = null;
	int[]		FIVE = null;
	int[]		SIX = null;
	int[]		SEVEN = null;
	int[]		EIGHT = null;
	int[]		NINE = null;
	int[]		ZERO = null;
	int[]		NONE = null;

	int		COLUMN_COUNT = -1;
	int		SEPARATE_COLUMN_COUNT = -1;
	int		ROW_COUNT = -1;
	int		ARRAY_SIZE = COLUMN_COUNT * ROW_COUNT;

	/** 時と分の区切り */
	public int[]		COLOGNE = null;

	/** フォント配列を入れるリスト */
	protected ArrayList<int[]>	fontArray = new ArrayList<int[]>();

	public FontBase() {
	}

	/**
	 * 			そのフォントの数字を取得
	 */
	public int[] getNumber(int loc) {
		if(loc < 0) {
			return fontArray.get(10);
		}
		else {
			return fontArray.get(loc);
		}
	}

	/**
	 * 			コロン（セパレート）を取得
	 */
	public int[] getCologne() {
		return COLOGNE;
	}

	/**
	 * 		数字1文字分のカラム数を返す
	 */
	public int getOneNumberColumnCount() {
		return COLUMN_COUNT;
	}

	/**
	 * 		数字2文字分のカラム数を返す
	 */
	public int getTwoNumbersColumnsCount() {
		return COLUMN_COUNT * 2;
	}

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










