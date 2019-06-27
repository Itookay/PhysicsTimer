package itookay.android.org.contents;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.PointF;
import itookay.android.org.Style.StyleBase;
import itookay.android.org.font.FontBase;
import org.jbox2d.common.Vec2;
import android.util.Log;

public class DialPanel {

    /* static定数 */
    /** 分 */
    public static final int		MINUTE = 110;
    /** 秒 */
    public static final int		SECOND = 120;
    /** コロン */
    public static final int		COLOGNE = 130;
    /** 空白 */
    public static final int		BLANK = 140;

    /** ふつうの数字1桁目左側の空白 */
    private static float        NORMAL_LEFT_SPACE = 0f;
    /** ふつうの数字1桁目と2桁目の間の空白 */
    private static float        NORMAL_CENTER_SPACE = 0f;
    /** ふつうの数字2桁目右側の空白 */
    private static float        NORMAL_RIGHT_SPACE = 0f;
    /** ちいさい数字1桁目左側の空白 */
    private static float        SMALL_LEFT_SPACE = 0f;
    /** ちいさい数字1桁目と2桁目の間の空白 */
    private static float        SMALL_CENTER_SPACE = 0f;
    /** ちいさい数字2桁目右側の空白 */
    private static float        SMALL_RIGHT_SPACE = 0f;
    /** コロン左側スペース */
    private static float        COLOGNE_LEFT_SPACE = 0;
    /** コロン右側スペース */
    private static float        COLOGNE_RIGHT_SPACE = 0;

    /** タイルサイズフォーマット */
    private int         mSizeFormat = 0;
    /** パネル左側の空白 */
    private float       mLeftSpace = 0;
    /** パネルの数字1桁目と2桁目の間の空白 */
    private float       mCenterSpace = 0;
    /** パネル右側の空白 */
    private float       mRightSpace = 0;

    /** ToleBaseのカラム数 */
    private int         mTileBaseColumnCount = 0;
    /** TileBaseの配列数 */
    private int         mTileBaseArrayCount = 0;

    /** TileBaseリスト */
    private ArrayList<TileBase>		mTileBaseList = new ArrayList<TileBase>();
    /** このパネルが保持する時間の属性。時・分・秒・コロン・ドッドのどれか */
    private int			mFormat = -1;
    /** パネルの描画開始位置．デフォルトで原点．必要に応じてオフセット */
    private Vec2		mPosition = new Vec2( 0, 0 );

    /** パネルの識別ID */
    private int			mId = -1;
    /** 次の時刻で落下予定のタイル */
    private ArrayList<Integer>			mDestroyTileIndexList = new ArrayList<Integer>();
    /** 次の時刻でジョイント予定のタイル */
    private ArrayList<Integer>			mJointTileIndexList = new ArrayList<Integer>();

    /**
     * 			コンストラクタ
     * @param format	パネルのフォーマット(時or分orコロン)
     * @param id		パネルのID
     */
    DialPanel(int format, int id) {
        mFormat = format;
        mId = id;
    }

    /**
     *      DialPanel数字のスペースをセット
     */
    public static void setStaticSpace(float normalLeft, float normalCenter, float normalRight, float smallLeft, float smallCenter, float smallRight) {
        NORMAL_LEFT_SPACE = normalLeft;
        NORMAL_CENTER_SPACE = normalCenter;
        NORMAL_RIGHT_SPACE = normalRight;
        SMALL_LEFT_SPACE = smallLeft;
        SMALL_CENTER_SPACE = smallCenter;
        SMALL_RIGHT_SPACE = smallRight;
    }

    /**
     *      DialPanelコロンのスペースをセット<br>
     *      サイズはふつうのみ
     */
    public static void setStaticCologneSpace(float left, float right) {
        COLOGNE_LEFT_SPACE = left;
        COLOGNE_RIGHT_SPACE = right;
    }

    public void setTileBaseArray(int columnCount, int arrayCount) {
        mTileBaseColumnCount = columnCount;
        mTileBaseArrayCount = arrayCount;
    }

    /**
     *      このパネルのサイズフォーマットをセット<br>
     *      このフォーマットに従ったサイズでパネルと隙間のサイズを決定
     */
    public void setSizeFormat(int format) {
        mSizeFormat = format;

        switch(mSizeFormat) {
            case Tile.SIZE_NORMAL:
                if(mFormat == COLOGNE) {
                    mLeftSpace = COLOGNE_LEFT_SPACE;
                    mRightSpace = COLOGNE_RIGHT_SPACE;
                    //コロンの場合は中心のスペースがない
                    mCenterSpace = 0;
                }
                else {
                    mLeftSpace = NORMAL_LEFT_SPACE;
                    mRightSpace = NORMAL_RIGHT_SPACE;
                    mCenterSpace = NORMAL_CENTER_SPACE;
                }
                break;

            case Tile.SIZE_SMALL:
                mLeftSpace = SMALL_LEFT_SPACE;
                mRightSpace = SMALL_RIGHT_SPACE;
                mCenterSpace = SMALL_CENTER_SPACE;
                break;
        }

    }

    public float getLeftSpace() {
        return mLeftSpace;
    }

    public float getCenterSpace() {
        return mCenterSpace;
    }

    public float getRightSpace() {
        return mRightSpace;
    }

    /**
     *          DialPanelの幅(左右スペース有り)を取得
     */
    public float getWidthWithSpace() {
        float   tileSize = new TileBase().setSizeFormat(mSizeFormat).getSizeWithSpace();
        return  mRightSpace + mTileBaseColumnCount * tileSize + mCenterSpace + mRightSpace;
    }

    /**
     * 			ダイアルパネルの位置をオフセット
     * @param x X方向オフセット量
     * @param y Y方向オフセット量
     */
    public void OffsetPosition(float x, float y) {
        mPosition.x += x;
        mPosition.y += y;

        //TileBaseジョイント座標もオフセット
        Iterator<TileBase>	it = mTileBaseList.iterator();
        while(it.hasNext()) {
            it.next().setOffset(x, y);
        }
    }

    /**
     *          DialPanel生成
     */
    public void createDialPanel() {
        createTileBase();
    }

    /**
     *          TileBaseを作成<br>
     *          DialPanelは画面の左下原点<br>
     *          TileBaseとTileはTileの中心が原点。Tileから取得できるアンカー位置もTile中心が原点
     */
    private void createTileBase() {
        int     currentColumn = 0;
        float   tileSize = new TileBase().setSizeFormat(mSizeFormat).getSize();
        float   tileSizeWithSpace = new TileBase().setSizeFormat(mSizeFormat).getSizeWithSpace();

        /* 現在のTileBase中心座標(DialPanel左上原点) */
        Vec2 currentTileBaseCenterPos = new Vec2(0, 0);
        currentTileBaseCenterPos.x = mPosition.x + tileSize / 2f;
        currentTileBaseCenterPos.y = mPosition.y - tileSize / 2f;

        for (int index = 0; index <mTileBaseArrayCount ; index++) {
            /* コロンの場合：左すきまを追加 */
            if(mFormat == COLOGNE) {
                currentTileBaseCenterPos.addLocal(mLeftSpace, 0f);
            }

            TileBase    tileBase = new TileBase(mId, index);
            tileBase.setSizeFormat(mSizeFormat);
            //TileBaseのアンカー位置(DialPanel左上原点)
            Vec2 tileBaseAnchorPos = new Vec2();
            //Tileのアンカー位置(Tile左上原点)
            Vec2 tileAnchorPos = null;

            //左側アンカーポイント
            tileAnchorPos = tileBase.getLocalJointPos1();
            tileBaseAnchorPos.x = currentTileBaseCenterPos.x + tileAnchorPos.x;
            tileBaseAnchorPos.y = currentTileBaseCenterPos.y + tileAnchorPos.y;
            tileBase.setWorldJointPos1(tileBaseAnchorPos);

            //右側アンカーポイント
            tileAnchorPos = tileBase.getLocalJointPos2();
            tileBaseAnchorPos.x = currentTileBaseCenterPos.x + tileAnchorPos.x;
            tileBaseAnchorPos.y = currentTileBaseCenterPos.y + tileAnchorPos.y;
            tileBase.setWorldJointPos2(tileBaseAnchorPos);

            mTileBaseList.add(tileBase);

            /* 分・秒の場合：1桁目と2桁目の数字の間にすきま追加 */
            if(mFormat == MINUTE || mFormat == SECOND) {
                if(currentColumn + 1 == mTileBaseColumnCount) { //mFont.getColumnCount();
                    currentTileBaseCenterPos.addLocal(mCenterSpace, 0);
                }
            }

            currentColumn++;
            //次のタイルに座標を移動
            currentTileBaseCenterPos.addLocal(tileSizeWithSpace, 0);

            //タイルを改行
            if (currentColumn == mTileBaseColumnCount) {  //mFont.getDialPanelColumnCount(mFormat)
                currentTileBaseCenterPos.x = mPosition.x + tileSize / 2f;
                currentTileBaseCenterPos.y -= tileSizeWithSpace;
                currentColumn = 0;
            }
        }
    }

    public ArrayList<Integer> getDestroyTileList() {
        return mDestroyTileIndexList;
    }

    public ArrayList<Integer> getJointTileList() {
        return mJointTileIndexList;
    }

    /**
     * 			DialPanel上の指定したindexのTileBaseを取得
     */
    public TileBase getTileBase(int index) {
        Iterator<TileBase>	it = mTileBaseList.iterator();
        TileBase	tileBase = null;
        while(it.hasNext()) {
            tileBase = it.next();
            if(tileBase.getIndex() == index) {
                return tileBase;
            }
        }

        return null;
    }

    public int getFormat() {
        return mFormat;
    }

    public int getId() {
        return mId;
    }

    /**
     *      TileBaseリストを取得
     * @return
     */
    public ArrayList<TileBase> getTileBaseList() {
        return mTileBaseList;
    }
}
