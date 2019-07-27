package itookay.android.org.font;

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

	public int[]		NONE = null;
	public int[]		ONE = null;
	public int[]		TWO= null;
	public int[]		THREE = null;
	public int[]		FOUR = null;
	public int[]		FIVE = null;
	public int[]		SIX = null;
	public int[]		SEVEN = null;
	public int[]		EIGHT = null;
	public int[]		NINE = null;
	public int[]		ZERO = null;
	/** 区切りなし */
	public int[]		BLANK = null;
	/** 時と分の区切り */
	public int[]		COLOGNE = null;
	/** 分と秒の区切り */
	public int[]		DOT = null;

	/** フォント配列を入れるリスト */
	protected ArrayList<int[]>	fontArray = new ArrayList<int[]>();
	/** 数字1文字分の配列のカラム数を取得 */
	public abstract int getOneNumberColumnCount();
	/** 数字2文字分の配列のカラムを取得 */
	public abstract	int	getTwoNumbersColumnsCount();
	/** DialPanel 1枚に必要なタイルのカラム数を取得<br>数字・コロンで適切な数を取得。DialPanel.MINUTE, SECOND, COLOGNE */
	public abstract int getDialPanelColumnCount(int format);
	/** DialPanel 1枚に必要なタイルの数を取得<br>数字・コロンで適切な数を取得。DialPanel.MINUTE, SECOND, COLOGNE */
	public abstract int getDialPanelArrayCount(int format);

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
	 * 			ドット（セパレート）を取得
	 */
	public int[] getDot() {
		return DOT;
	}

	/**
	 * 			空白のセパレート（セパレートなし）を取得
	 */
	public int[] getBlankSeparate() {
		return BLANK;
	}

	/**
	 * 			数字1文字あたりの平均必要タイル数を取得
	 */
	public int getAverageTileCount() {
		Iterator<int[]>  it = fontArray.iterator();
		int[]	font = null;
		int		count = 0;
		int		length = 0;
		while( it.hasNext() == true ) {
			font = it.next();
			length = font.length;

			for( int i = 0; i < length; i++ ) {
				if( font[i] == 1 ) {
					count++;
				}
			}
		}

		return count / fontArray.size();
	}
}










