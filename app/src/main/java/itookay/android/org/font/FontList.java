package itookay.android.org.font;

import itookay.android.org.font.NormalA;
import itookay.android.org.font.NormalRoundA;

/**
 *
 * 			フォントリスト管理クラス
 * 				FontListActivityから呼ばれる。
 *
 */
public class FontList {

	private static FontBase[]		mFontListEntry = {
		new NormalA(),
		new NormalRoundA(),
	};

	/** かなフォント名 */
	private static String[]		mFontListJp = null;
	/** フォント名 */
	private static String[]		mFontList = null;
	/** 直前に選択されたフォントの配列インデックス */
	private static int			mId = 0;
	/** デフォルトフォント */
	private static FontBase			mDefaultFont = null;
	/** デフォルトフォントID */
	private static int			mDefaultFontId = -1;

	static {
		//フォント名を配列にセット
		int		length = mFontListEntry.length;
		mFontList = new String[length];
		mFontListJp = new String[length];

		for( int i = 0; i < length; i++ ) {
			mFontListJp[i] = mFontListEntry[i].getFontNameJp();
			mFontList[i] = mFontListEntry[i].getFontName();
		}

		mDefaultFontId = 0;
		mDefaultFont = mFontListEntry[mDefaultFontId];
	}

	public static FontBase getDefaultFont() {

		return mDefaultFont;
	}

	public static int getDefaultFontId() {

		return mDefaultFontId;
	}

	/**
	 * 			フォント名（カナ）のリストを取得
	 */
	public static String[] getFontJpList() {

		return mFontListJp;
	}

	/**
	 * 			フォント名のリストを取得
	 */
	public static String[] getFontList() {

		return mFontList;
	}

	/**
	 * 			フォント名から対応するリストのIDを取得
	 * @param name		リストに存在するフォント名
	 * @return			対応するID。存在しないフォント名には負の数
	 */
	public static int getFontIdByName( String name ) {

		int		length = mFontListEntry.length;
		int		ret = -1;

		for( int id = 0; id < length; id++ ) {
			if( mFontListEntry[id].getFontName().equals( name ) == true ) {
				ret = id;
				break;
			}
		}

		return ret;
	}

	/**
	 * 			フォント名を取得
	 */
	public static String getFontName( int id ) {

		return mFontListEntry[id].getFontName();
	}

	/**
	 * 			フォント名（カナ）を取得
	 */
	public static String getFontNameJp( int id ) {

		return mFontListEntry[id].getFontNameJp();
	}

	/**
	 * 			フォントを取得
	 */
	public static FontBase getFont( int id ) {

		mId = id;
		return mFontListEntry[id];
	}

	/**
	 * 			次のフォントを取得
	 */
	public static FontBase getNext() {

		FontBase		font = null;
		if( mId++ < mFontListEntry.length ) {
			font = mFontListEntry[mId];
		}

		return font;
	}

	/**
	 * 			前のフォントを取得
	 */
	public static FontBase getBack() {

		FontBase		font = null;
		if( mId-- >= 0 ) {
			font = mFontListEntry[mId];
		}

		return font;
	}

	/**
	 * 			次のフォントが存在する
	 */
	public static boolean isNext() {

		boolean		ret = false;

		if( mId + 1 < mFontListEntry.length ) {
			ret = true;
		}

		return ret;
	}

	/**
	 * 			前のフォントが存在する
	 */
	public static boolean isBack() {

		boolean		ret = false;

		if( mId - 1 >= 0 ) {
			ret = true;
		}

		return ret;
	}

	/**
	 * 			現在選択中のフォントを取得
	 */
	public static FontBase getSelectedFont() {

		FontBase	font = null;

		if( mId >= 0 && mId < mFontListEntry.length ) {
			font = mFontListEntry[mId];
		}

		return  font;
	}

	/**
	 * 			現在選択中のフォントを取得
	 */
	public static int getSelectedFontId() {

		int		ret = -1;

		if( mId >= 0 && mId < mFontListEntry.length ) {
			ret = mId;
		}

		return  ret;
	}
}
