package itookay.android.org.contents;

import org.jbox2d.common.Vec2;

/**
 * 			グラウンド上のボディを設置するべき場所を保持する。
 * 			タイルがなくても，ジョイント位置，パネル，インデックスなどを保持する。
 * 			タイルとは一対一対応
 */
class TileBase {

    /** 無効なIDもしくはIndex */
    public static final int		INVALID_ID = -1;

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

    /** TileBaseサイズ */
    private float       mSize = 0;
    /** タイルの片側スペース */
    private float       mSpace = 0;
    /** ジョイントアンカー幅 */
    private float       mJointAnchorWidth = 0;
    /** ジョイントのワールド座標１ */
    private Vec2		mWorldJointPos1 = new Vec2();
    /** ジョイントのワールド座標２ */
    private Vec2		mWorldJointPos2 = new Vec2();
    /** パネルID */
    private int			mPanelId = -1;
    /** パネル内の配列インデックス */
    private int			mIndex = -1;

    /**
     *      コンストラクタ
     * @param panelId パネルID
     * @param index インデックス
     */
    TileBase(int panelId, int index) {
        mPanelId = panelId;
        mIndex = index;
    }
    TileBase() {
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
     *      TileBaseインスタンスのサイズフォーマットをセット<br>
     *      getAnchorWidthでは、ここで指定したフォーマットに対応するジョイント幅を返す。
     * @param sizeFormat サイズフォーマット
     */
    public TileBase setSizeFormat(int sizeFormat) {
        switch (sizeFormat) {
            case Tile.SIZE_NORMAL:
                mSize = NORMAL_SIZE;
                mSpace = NORMAL_SPACE;
                mJointAnchorWidth = NORMAL_ANCHOR_WIDTH;
                break;
            case Tile.SIZE_SMALL:
                mSize = SMALL_SIZE;
                mSpace = SMALL_SPACE;
                mJointAnchorWidth = SMALL_ANCHOR_WIDTH;
                break;
            default:
                break;
        }
        return this;
    }

    /**
     *      タイルサイズ(両側スペースなし)を取得
     */
    float getSize() {
        return mSize;
    }

    /**
     *      アンカー幅を取得
     */
    public float getJointAnchorWidth() {
        return mJointAnchorWidth;
    }

    public void setWorldJointPos1(Vec2 jointPos) {
        mWorldJointPos1.set(jointPos);
    }

    public void setWorldJointPos2(Vec2 jointPos) {
        mWorldJointPos2.set(jointPos);
    }

    public Vec2 getWorldJointPos1() {
        return mWorldJointPos1;
    }

    public Vec2 getWorldJointPos2() {
        return mWorldJointPos2;
    }

    /**
     *          タイルのサイズ(両側スペースを含む)を取得
     */
    public float getSizeWithSpace() {
        return mSize + mSpace * 2;
    }

    /**
     *      ジョイントアンカー1(左)位置取得
     * @return TileBase中心を原点としたジョイントアンカー1位置
     */
    public Vec2 getLocalJointPos1() {
        Vec2		pos = new Vec2();
        pos.x = -mJointAnchorWidth / 2f;
        pos.y = 0f;
        return pos;

    }

    /**
     *      ジョイントアンカー1(左)位置取得
     * @return TileBase中心を原点としたジョイントアンカー2位置
     */
    public Vec2 getLocalJointPos2() {
        Vec2		pos = new Vec2();
        pos.x = mJointAnchorWidth / 2f;
        pos.y = 0f;
        return pos;
    }

    public int getIndex() {
        return mIndex;
    }

    public int getPanelId() {
        return mPanelId;
    }

    public void setPanelId( int id ) {
        mPanelId = id;
    }

    public void setIndex( int index ) {
        mIndex = index;
    }

    /**
     * 			ジョイントアンカーを引数でオフセットする
     */
    public void setOffset( float x, float y ) {
        mWorldJointPos1.x += x;
        mWorldJointPos1.y += y;

        mWorldJointPos2.x += x;
        mWorldJointPos2.y += y;
    }
}
