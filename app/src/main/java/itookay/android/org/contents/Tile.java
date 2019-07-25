package itookay.android.org.contents;

import android.graphics.*;
import org.jbox2d.common.Vec2;

import android.content.res.Resources;
import itookay.android.org.R;


/**
 * 			タイル１枚の情報を保持
 */
public class Tile {

    /** 無効なIDもしくはIndex */
    public static final int		INVALID_ID = -1;
    /** タイルのサイズ ふつうの */
    public static final int     NORMAL = 10;
    /** タイルのサイズ ちいさいの */
    public static final int     SMALL = 20;
    /** ふつうサイズのタイルに対してちいさいのの比 */
    public static final float  SMALL_RATIO = 0.7f;

    /** ふつうタイルサイズ */
    private static float        NORMAL_SIZE = 0;
    /** ちいさいタイルサイズ */
    private static float        SMALL_SIZE = 0;
    /** ふつうタイルの空白 */
    private static float        NORMAL_SPACE = 0f;
    /** ちいさいタイルの空白 */
    private static float        SMALL_SPACE = 0f;
    /** ふつうジョイントアンカーの幅 */
    private static float 	    NORMAL_ANCHOR_WIDTH = 0f;
    /** ちいさいジョイントアンカーの幅 */
    private static float 	    SMALL_ANCHOR_WIDTH = 0f;

    /** Tileサイズ */
    private float       mSize = 0;
    /** サイズフォーマット */
    private int         mSizeFormat = 0;
    /** ジョイントアンカー幅 */
    private float       mJointAnchorWidth = 0;
    /** パネルID */
    private int			mPanelId = -1;
    /** パネル内の配列インデックス */
    private int			mIndex = -1;
    /** パネルに拘束された回数 */
    private int			mRestrainCount = 0;

    /** ワールド上の中心位置 */
    private Vec2		mPosition = new Vec2();
    /** 青色 */
    static final int		COLOR_BLUE = 100;

    /** タイルのBitmap */
    private Bitmap		mBitmap = null;
    /** 密度 */
    private float		mDensity = 100f;
    /** 摩擦係数 */
    private float		mFriction = 0.4f;
    /** 反発係数 */
    private float		mRestitution = 0.15f;

    /**
     * 			タイルの中心ワールド座標をセット
     */
    void setPosition(Vec2 pos) {
        mPosition.set(pos);
    }

    /**
     *          タイルのふつうサイズをセット<br>
     *          サイズからちいさいのとジョイントアンカー幅も計算される
     * @param normalTileSize ふつうのタイルサイズ
     * @param spaceScale タイルサイズの内、どれだけをスペースにするか。スペースは左右均等になる
     */
    static void setStaticSize(float normalTileSize, float spaceScale) {
        NORMAL_SIZE = normalTileSize;
        NORMAL_SPACE = NORMAL_SIZE * spaceScale / 2f;
        NORMAL_ANCHOR_WIDTH = NORMAL_SIZE / 2f;

        SMALL_SIZE = normalTileSize * SMALL_RATIO;
        SMALL_SPACE = SMALL_SIZE * spaceScale / 2f;
        SMALL_ANCHOR_WIDTH = SMALL_SIZE / 2f;
    }

    /**
     *      タイル・インスタンスのサイズフォーマットをセット<br>
     *      getSize系のメソッドでは、ここで指定したフォーマットに対応するサイズを返す。
     */
    Tile setSizeFormat(int sizeFormat) {
        mSizeFormat = sizeFormat;
        switch(mSizeFormat) {
            case NORMAL:
                mSize = NORMAL_SIZE;
                mJointAnchorWidth = NORMAL_ANCHOR_WIDTH;
                break;
            case SMALL:
                mSize = SMALL_SIZE;
                mJointAnchorWidth = SMALL_ANCHOR_WIDTH;
                break;
            default:
                break;
        }
        return this;
    }

    /**
     *      タイルのサイズ(スペースを除く)を取得
     */
    float getSize() {
        return mSize;
    }

    int getSizeFormat() {
        return mSizeFormat;
    }

    /**
     *			ジョイントアンカー1(左)位置取得
     * @return タイル中心を原点としたジョイントアンカー1位置
     */
    Vec2 getJointAnchorPosition1() {
        Vec2		pos = new Vec2();
        pos.x = -mJointAnchorWidth / 2f;
        pos.y = 0f;
        return pos;
    }

    /**
     *			ジョイントアンカー2(右)位置取得
     * @return タイル中心を原点としたジョイントアンカー2位置
     */
    Vec2 getJointAnchorPosition2() {
        Vec2		pos = new Vec2();
        pos.x = mJointAnchorWidth / 2f;
        pos.y = 0f;
        return pos;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    Bitmap getBitmap() {
        return mBitmap;
    }

    float getDensity() {
        return mDensity;
    }

    float getFriction() {
        return mFriction;
    }

    float getRestitution() {
        return mRestitution;
    }

    /**
     * 			タイルの画像を取得
     * @param res		リソース
     * @param color		Tileのstatic変数を指定する
     */
    void createTileBitmap( Resources res, int color ) {
        switch(color) {
            default :
                mBitmap = BitmapFactory.decodeResource( res, R.drawable.tile );
        }
    }

    int getIndex() {
        return mIndex;
    }

    int getPanelId() {
        return mPanelId;
    }

    /**
     * 			パネルIDとインデックスをセット
     * @param panelId
     * @param index
     */
    void setUniqueId(int panelId, int index) {
        mPanelId = panelId;
        mIndex = index;
    }

    Vec2 getPosition() {
        return mPosition;
    }

    /**
     * 			引数の配列で必要なタイルの数を取得
     */
    static int getTileCount(int[] array) {
        int		count = 0;
        for(int i = 0; i < array.length; i++) {
            count += array[i];
        }

        return count;
    }

    /**
     * 			拘束された回数を一回加算
     */
    void addRestrainCount() {
        mRestrainCount++;
    }

    /**
     * 			拘束された回数を取得
     */
    int getRestrainCount() {
        return mRestrainCount;
    }
}