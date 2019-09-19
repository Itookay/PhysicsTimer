package itookay.android.org.contents;

import org.jbox2d.common.Vec2;

public class Scale {

    /** 画面高さ(メートル) */
    public static final int       DISPLAY_HEIGHT_IN_METER = 10;

    /** 画面横幅ピクセル */
    private static float		mDisplayWidthPixel = 0f;
    /** 画面縦幅ピクセル */
    private static float		mDisplayHeightPixel = 0f;
    /** 画面横長さメートル */
    private static float	    mDisplayWidthMeter = 0f;
    /** 画面縦長さメートル */
    private static float	    mDisplayHeightMeter = 0f;
    /** メートルからピクセルへの変換比 */
    private static float        mRatio = 1f;

    /**
     * 			メートルからピクセルへの変換
     * @param meter		メートル
     * @return			引数相応のピクセル
     */
    public static float toPixel( float meter ) {
        return meter * mRatio;
    }

    public static Vec2 toPixel( Vec2 meter ) {
        float	x = toPixel( meter.x );
        float	y = toPixel( meter.y );

        return new Vec2( x, y );
    }

    /**
     * 			ピクセルからメートルへの変換
     */
    public static float toMeter( float pixel ) {
        return pixel / mRatio;
    }

    public static float getDisplayWidthPixel() {
        return mDisplayWidthPixel;
    }

    public static float getDisplayHeightPixel() {
        return mDisplayHeightPixel;
    }

    /**
     *      ディスプレイ高さ(メートル)<br>
     *      端末向きに応じて値が変わる
     */
    public static float getDisplayHeightMeter() {
        return mDisplayHeightMeter;
    }

    /**
     *      ディスプレイ幅(メートル)<br>
     *      端末向きに応じて値が変わる
     */
    public static float getDisplayWidthMeter() {
        return mDisplayWidthMeter;
    }

    /**
     * 			メートルでの画面高さをセット
     * @param displayWidthPixel     画面幅
     * @param displayHeightPixel    画面高さ
     * @param defaultScaleInMeter   画面高さのメートル値
     */
    public static void setDisplay(int displayWidthPixel, int displayHeightPixel, int defaultScaleInMeter) {
        mDisplayWidthPixel = displayWidthPixel;
        mDisplayHeightPixel = displayHeightPixel;

        float		ratio = mDisplayWidthPixel / mDisplayHeightPixel;
        if(mDisplayHeightPixel > mDisplayWidthPixel) {
            mDisplayWidthMeter = defaultScaleInMeter * ratio;
            mDisplayHeightMeter = defaultScaleInMeter;
        }
        else {
//            mDisplayHeightMeter = defaultScaleInMeter / ratio;
//            mDisplayWidthMeter = defaultScaleInMeter;
        }

        mRatio = mDisplayHeightPixel / mDisplayHeightMeter;
    }

    /**
     * 			スクリーンX座標（ピクセル）からワールド座標（メートル）への変換
     */
    public static float getWorldCoordinateX(float xPixel) {
        return toMeter(xPixel);
    }

    /**
     * 			スクリーンY座標（ピクセル）からワールド座標（メートル）への変換
     */
    public static float getWorldCoordinateY(float yPixel) {
        return -toMeter(yPixel);
    }
}
