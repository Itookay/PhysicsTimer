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
	protected String		FONT_NAME = null;

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

	/** ノーマルフォント */
	public final int		FONT_NOMAL = 10;

	/** フォント配列を入れるリスト */
	protected ArrayList<int[]>	fontArray = new ArrayList<int[]>();
	/** 数字1文字分の配列のカラム数を取得 */
	public abstract int getColumnCount();
	/** 数字2文字分の配列のカラムを取得 */
	public abstract	int	get2NumbersColumnsCount();
	/** セパレートのカラム数を取得 */
	public abstract int getSeparateColumnCount();
	/** 数字一個分の配列サイズを取得 */
	public abstract int getArrayLength();

	public FontBase() {

	}

	/**
	 * 			フォント名を取得
	 */
	public String getFontName() {

		return getClass().getSimpleName();
	}

	/**
	 * 			フォント名（かな）を取得
	 */
	public String getFontNameJp() {

		return FONT_NAME;
	}

	/**
	 * 			そのフォントの数字を取得
	 */
	public int[] getNumber( int loc ) {

		if( loc < 0 ) {
			return fontArray.get( 10 );
		}
		else {
			return fontArray.get( loc );
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










