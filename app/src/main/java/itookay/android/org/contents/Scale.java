package itookay.android.org.contents;

import org.jbox2d.common.Vec2;

import android.util.Log;

public class Scale {

    /** 画面横幅ピクセル */
    private float		mDisplayWidthPixel = 0f;
    /** 画面縦幅ピクセル */
    private float		mDisplayHeightPixel = 0f;
    /** 画面横長さメートル */
    private float	mDisplayWidthMeter = 0f;
    /** 画面縦長さメートル */
    private float	mDisplayHeightMeter = 0f;
    /** メートル画面高さ */
    private int		mHeightMeter = 0;
    /** メートルからピクセルへの変換比 */
    private static float	mRatio = 1f;

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

    public float getDisplayWidthPixel() {
        return mDisplayWidthPixel;
    }

    public float getDisplayHeightPixel() {
        return mDisplayHeightPixel;
    }

    public float getDisplayHeightMeter() {
        return mDisplayHeightMeter;
    }

    public float getDisplayWidthMeter() {
        return mDisplayWidthMeter;
    }

    /**
     * 			メートルでの画面高さをセット
     * @param displayWidthPixel     画面幅
     * @param displayHeightPixel    画面高さ
     * @param heightInMeter         画面高さのメートル値
     */
    public void setDisplayScale( int displayWidthPixel, int displayHeightPixel, int heightInMeter ) {
        mDisplayWidthPixel = displayWidthPixel;
        mDisplayHeightPixel = displayHeightPixel;
        mHeightMeter = heightInMeter;
        initWorldRatio();
    }

    /**
     * 			ワールド座標とスクリーン座標の変換比を算出
     */
    private void initWorldRatio() {
        float		ratio = mDisplayWidthPixel / mDisplayHeightPixel;
        mDisplayWidthMeter = mHeightMeter * ratio;
        mDisplayHeightMeter = mHeightMeter;

        mRatio = mDisplayHeightPixel / mHeightMeter;
    }

    /**
     * 			スクリーンX座標（ピクセル）からワールド座標（メートル）への変換
     */
    public float getWorldCoordinateX(float xPixel) {
        return toMeter(xPixel);
    }

    /**
     * 			スクリーンY座標（ピクセル）からワールド座標（メートル）への変換
     */
    public float getWorldCoordinateY(float yPixel) {
        return -toMeter(yPixel);
    }
}
