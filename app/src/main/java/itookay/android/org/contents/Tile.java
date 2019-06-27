package itookay.android.org.contents;

import android.graphics.*;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import android.content.res.Resources;
import itookay.android.org.R;
import org.jbox2d.dynamics.Fixture;


/**
 * 			タイル１枚の情報を保持
 */
public class Tile {

    /** 無効なIDもしくはIndex */
    public static final int		INVALID_ID = -1;
    /** タイルのサイズ ふつうの */
    public static final int     SIZE_NORMAL = 10;
    /** タイルのサイズ ちいさいの */
    public static final int     SIZE_SMALL = 20;

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
    public static final int		COLOR_BLUE = 100;

    /** タイルのBitmap */
    private Bitmap		mBitmap = null;
    /** 密度 */
    private float		mDensity = 100f;
    /** 摩擦係数 */
    private float		mFriction = 0.4f;
    /** 反発係数 */
    private float		mRestitution = 0.2f;

    /**
     * 			タイルの中心ワールド座標をセット
     */
    public void setPosition(Vec2 pos) {
        mPosition.set(pos);
    }

    /**
     *          タイルのサイズをセット<br>
     *          サイズからジョイントアンカー幅も計算される
     * @param normalTileSize ふつうのタイルサイズ
     * @param smallTileSize ちいさいタイルサイズ
     * @param spaceScale タイルサイズの内、どれだけをスペースにするか。スペースは左右均等になる
     */
    public static void setStaticSize(float normalTileSize, float smallTileSize, float spaceScale) {
        NORMAL_SIZE = normalTileSize;
        NORMAL_SPACE = NORMAL_SIZE * spaceScale / 2f;
        NORMAL_ANCHOR_WIDTH = NORMAL_SIZE / 2f;

        SMALL_SIZE = smallTileSize;
        SMALL_SPACE = SMALL_SIZE * spaceScale / 2f;
        SMALL_ANCHOR_WIDTH = SMALL_SIZE / 2f;
    }

    /**
     *      タイル・インスタンスのサイズフォーマットをセット<br>
     *      getSize系のメソッドでは、ここで指定したフォーマットに対応するサイズを返す。
     */
    public Tile setSizeFormat(int sizeFormat) {
        switch(sizeFormat) {
            case SIZE_NORMAL:
                mSize = NORMAL_SIZE;
                mJointAnchorWidth = NORMAL_ANCHOR_WIDTH;
                break;
            case SIZE_SMALL:
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
    public float getSize() {
        return mSize;
    }

    /**
     *			ジョイントアンカー1(左)位置取得
     * @return タイル中心を原点としたジョイントアンカー1位置
     */
    public Vec2 getJointAnchorPosition1() {
        Vec2		pos = new Vec2();
        pos.x = -mJointAnchorWidth / 2f;
        pos.y = 0f;
        return pos;
    }

    /**
     *			ジョイントアンカー2(右)位置取得
     * @return タイル中心を原点としたジョイントアンカー2位置
     */
    public Vec2 getJointAnchorPosition2() {
        Vec2		pos = new Vec2();
        pos.x = mJointAnchorWidth / 2f;
        pos.y = 0f;
        return pos;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return mBitmap;
    }

    public float getDensity() {
        return mDensity;
    }

    public float getFriction() {
        return mFriction;
    }

    public float getRestitution() {
        return mRestitution;
    }

    /**
     * 			タイルの画像を取得
     * @param res		リソース
     * @param color		Tileのstatic変数を指定する
     */
    public void createTileBitmap( Resources res, int color ) {
        switch(color) {
            default :
                mBitmap = BitmapFactory.decodeResource( res, R.drawable.tile );
        }
    }

    public int getIndex() {
        return mIndex;
    }

    public int getPanelId() {
        return mPanelId;
    }

    /**
     * 			パネルIDとインデックスをセット
     * @param panelId
     * @param index
     */
    public void setUniqueId(int panelId, int index) {
        mPanelId = panelId;
        mIndex = index;
    }

    public Vec2 getPosition() {
        return mPosition;
    }

    /**
     * 			引数の配列で必要なタイルの数を取得
     */
    public static int getTileCount(int[] array) {
        int		count = 0;
        for(int i = 0; i < array.length; i++) {
            count += array[i];
        }

        return count;
    }

    /**
     * 			拘束された回数を一回加算
     */
    public void addRestrainCount() {
        mRestrainCount++;
    }

    /**
     * 			拘束された回数を取得
     */
    public int getRestrainCount() {
        return mRestrainCount;
    }
}