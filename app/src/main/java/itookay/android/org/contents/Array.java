package itookay.android.org.contents;

/**
 * 			タイル配列用の配列処理クラス
 */
public class Array {

	/**
	 * 			引数の配列を[array1 array2]の並びで1つの配列に結合<br>
	 * 			配列の行数は同じであること
	 */
	public static int[] conbine( int[] array1, int[] array2, int columnCount1, int columnCount2 ) {

		int[]	ret = new int[ array1.length + array2.length ];
		int		len = ret.length;
		int		array1Count = 0;
		int		array2Count = 0;

		for( int i = 0; i < len; ) {
			for( int n = 0; n < columnCount1; n++ ) {
				ret[i++] = array1[array1Count++];
			}
			for( int n = 0; n < columnCount2; n++ ) {
				ret[i++] = array2[array2Count++];
			}
		}

		array2Count = 0;

		return ret;
	}

	/**
	 * 			引数のint（二桁）を一桁ずつバラして配列に入れて返す<br>
	 */
	public static int[] toArray( int num ) {

		String	str = Integer.toString( num );
		int[]	ret = new int[ 2 ];

		//10より小さい正の数
		if( num < 10 && num >= 0 ) {
			ret[0] = 0;
			ret[1] = str.charAt( 0 ) - '0';
		}
		//負の数
		else if( num < 0 ) {
			ret[0] = -1;
			ret[1] = -1;
		}
		else {
			ret[0] = str.charAt( 0 ) - '0';
			ret[1] = str.charAt( 1 ) - '0';
		}

		return ret;
	}
}
