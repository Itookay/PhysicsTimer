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
public class Tile implements DrawableBody {

    /** 無効なIDもしくはIndex */
    public static final int		INVALID_ID = -1;

    /** タイルサイズ */
    public static float         mSize = 0f;
    /** タイル左側の空白 */
    private static float        mLeftSpace = 0f;
    /** タイル右側の空白 */
    private static float        mRightSpace = 0f;

    /** ジョイントアンカーの幅 */
    public static float 	mAnchorWidth = 0f;
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
     *          タイルのサイズをセット
     * @param size タイルサイズ
     * @param spaceScale タイルサイズの内、どれだけをスペースにするか。スペースは左右均等になる
     */
    public static void setSize(float size, float spaceScale) {
        mSize = size;
        mLeftSpace = mRightSpace = size * spaceScale / 2f;
        mAnchorWidth = mSize / 2f;
    }

    /**
     *          タイル左右のスペースをセット
     * @param left タイル左側スペース
     * @param right タイル右側スペース
     */
    public static void setSpace(float left, float right) {
        mLeftSpace = left;
        mRightSpace = right;
    }

    /**
     *          タイルのサイズ(スペースを除く)を取得
     */
    public static float getSize() {
        return mSize;
    }

    /**
     *          タイルのサイズ(スペースを含む)を取得
     */
    public static float getSizeWithSpace() {
        return mLeftSpace + mSize + mRightSpace;
    }

    public static float getLeftSpace() {
        return mLeftSpace;
    }

    public static float getRightSpace() {
        return mRightSpace;
    }

    /**
     * 			ジョイントアンカー1と2の幅を取得
     */
    public static float getJointAnchorWidth() {
        return mAnchorWidth;
    }

    /**
     *			ジョイントアンカー1(左)位置取得
     * @return タイル中心を原点としたジョイントアンカー1位置
     */
    public static Vec2 getJointAnchorPosition1() {
        Vec2		pos = new Vec2();
        pos.x = -mAnchorWidth / 2f;
        pos.y = 0f;
        return pos;
    }

    /**
     *			ジョイントアンカー2(右)位置取得
     * @return タイル中心を原点としたジョイントアンカー2位置
     */
    public static Vec2 getJointAnchorPosition2() {
        Vec2		pos = new Vec2();
        pos.x = mAnchorWidth / 2f;
        pos.y = 0f;
        return pos;
    }

    public void setBitmap(Bitmap bitmap) {
        mBitmap = bitmap;
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
     * 			タイル描画
     */
    @Override
    public void drawBody(Canvas canvas, Body body) {
        Vec2	pos = body.getPosition();

        float	scale = Scale.toPixel(mSize) / mBitmap.getWidth();
        Matrix	matrix = new Matrix();
        matrix.setScale(scale, scale);

        float	x = Scale.toPixel(pos.x - mSize / 2f);
        float	y = Scale.toPixel(pos.y - mSize / 2f);
        matrix.postTranslate(x, y);

        float	deg = (float)Math.toDegrees(body.getAngle());
        matrix.preRotate(deg, mBitmap.getWidth() / 2.0f, mBitmap.getHeight() / 2.0f);

        canvas.drawBitmap(mBitmap, matrix, null);
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