package itookay.android.org.contents;

import java.util.ArrayList;
import java.util.Iterator;

import android.graphics.PointF;
import itookay.android.org.font.FontBase;
import org.jbox2d.common.Vec2;
import android.util.Log;

public class DialPanel {

    /* static定数 */
    /** 時 */
    public static final int		HOUR = 100;
    /** 分 */
    public static final int		MINUTE = 110;
    /** 秒 */
    public static final int		SECOND = 120;
    /** コロン */
    public static final int		COLOGNE = 130;
    /** ドット */
    public static final int		DOT = 140;
    /** 空白 */
    public static final int		BLANK = 150;

    /** フォント */
    private static FontBase		mFont = null;

    /** 数字1桁目左側の空白 */
    private static float        mNumberLeftSpace = 0f;
    /** 数字1桁目と2桁目の間の空白 */
    private static float        mNumberCenterSpace = 0f;
    /** 数字2桁目右側の空白 */
    private static float        mNumberRightSpace = 0f;

    /** コロン左側の空白 */
    private static float        mCologneLeftSpace = 0f;;
    /** コロン右側の空白 */
    private static float        mCologneRightSpace = 0f;

    /** TileBaseリスト */
    private ArrayList<TileBase>		mTileBaseList = new ArrayList<TileBase>();
    /** このパネルが保持する時間の属性。時・分・秒・コロン・ドッドのどれか */
    private int			mFormat = -1;
    /** タイルの配列 */
    private int[]		mArray = null;
    /** タイル配列のカラム数 */
    private int			mArrayColumnCount = 0;
    /** タイル配列長さ */
    private int			mArrayLength = 0;
    /** 行 */
    private int			mRow = -1;
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
     * @param format	パネルのフォーマット
     * @param row		このパネルの行．ほかのパネルと同じ行なら横一列に並ぶ
     */
    public DialPanel(int format, int row, int id) {
        mFormat = format;
        mRow = row;
        mId = id;
    }

    /**
     *          DialPanel数字のスペースをセット
     * @param left 1桁目の左側スペース
     * @param center 1桁目と2桁目の中間スペース
     * @param right 2桁目の右側スペース
     */
    public static void setNumberSpaceSize(float left, float center, float right) {
        mNumberLeftSpace = left;
        mNumberCenterSpace = center;
        mNumberRightSpace = right;
    }

    /**
     *          DialPanelコロンのスペースをセット
     * @param left 左側スペース
     * @param right 右側スペース
     */
    public static void setCologneSpaceSize(float left, float right) {
        mCologneLeftSpace = left;
        mCologneRightSpace = right;
    }

    public static float getNumberLeftSpace() {
        return mNumberLeftSpace;
    }

    public static float getNumberCenterSpace() {
        return mNumberCenterSpace;
    }

    public static float getNumberRightSpace() {
        return mNumberRightSpace;
    }

    public static float getCologneLeftSpace() {
        return mCologneLeftSpace;
    }

    public static float getCologneRightSpace() {
        return mCologneRightSpace;
    }

    /**
     *          DialPanelの幅(左右スペース有り)を取得
     */
    public float getWidthWithSpace() {
        if(mFormat == MINUTE || mFormat == SECOND) {
            return mNumberLeftSpace + mArrayColumnCount * Tile.getSizeWithSpace() + mNumberRightSpace;
        }
        else if(mFormat == COLOGNE) {
            return mCologneLeftSpace + Tile.getSizeWithSpace() + mCologneRightSpace;
        }

        return 0f;
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
        getTimeArrayColumnCount();
        createTileBase();
    }

    /**
     *          TileBaseを作成<br>
     *          DialPanelは画面の左下原点<br>
     *          TileBaseとTileはTileの中心が原点。Tileから取得できるアンカー位置もTile中心が原点
     */
    private void createTileBase() {
        TileBase tileBase = null;
        int currentColumn = 0;
        /* 現在のTileBase中心座標(DialPanel左上原点) */
        Vec2 currentTileBaseCenterPos = new Vec2(mPosition);
        currentTileBaseCenterPos.x = mPosition.x + Tile.getSize() / 2f;
        currentTileBaseCenterPos.y = mPosition.y - Tile.getSize() / 2f;

        for (int index = 0; index < mArrayLength; index++) {
            /* コロンの場合：左すきまを追加 */
            if(mFormat == COLOGNE) {
                currentTileBaseCenterPos.addLocal(mCologneLeftSpace, 0f);
            }

            tileBase = new TileBase(mId, index);
            //TileBaseのアンカー位置(DialPanel左上原点)
            Vec2 tileBaseAnchorPos = new Vec2();
            //Tileのアンカー位置(Tile左上原点)
            Vec2 tileAnchorPos = null;

            //左側アンカーポイント
            tileAnchorPos = Tile.getJointAnchorPosition1();
            tileBaseAnchorPos.x = currentTileBaseCenterPos.x + tileAnchorPos.x;
            tileBaseAnchorPos.y = currentTileBaseCenterPos.y + tileAnchorPos.y;
            tileBase.setWorldJointPos1(tileBaseAnchorPos);

            //右側アンカーポイント
            tileAnchorPos = Tile.getJointAnchorPosition2();
            tileBaseAnchorPos.x = currentTileBaseCenterPos.x + tileAnchorPos.x;
            tileBaseAnchorPos.y = currentTileBaseCenterPos.y + tileAnchorPos.y;
            tileBase.setWorldJointPos2(tileBaseAnchorPos);

            mTileBaseList.add(tileBase);

            /* 分・秒の場合：1桁目と2桁目の数字の間にすきま追加 */
            if(mFormat == MINUTE || mFormat == SECOND) {
                if(currentColumn + 1 == mFont.getColumnCount()) {
                    currentTileBaseCenterPos.addLocal(DialPanel.getNumberCenterSpace(), 0);
                }
            }

            currentColumn++;
            //次のタイルに座標を移動
            currentTileBaseCenterPos.addLocal(Tile.getSizeWithSpace(), 0);

            //タイルを改行
            if (currentColumn == mArrayColumnCount) {
                currentTileBaseCenterPos.x = mPosition.x + Tile.getSize() / 2f;
                currentTileBaseCenterPos.y -= Tile.getSizeWithSpace();
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

    public static void setFont(FontBase font) {
        mFont = font;
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

    /**
     * 			setTimeFormatで指定した引数で取得できる配列のカラム数を取得
     */
    private void getTimeArrayColumnCount() {
        if( mFormat == -1 ) return;

        if(mFormat == MINUTE  || mFormat == SECOND) {
            mArrayColumnCount = mFont.get2NumbersColumnsCount();
            mArrayLength = mFont.getArrayLength() * 2;
        }
        else if(mFormat == COLOGNE) {
            mArrayColumnCount = mFont.getSeparateColumnCount();
            mArrayLength = mFont.getArrayLength();
        }
    }

    public int getFormat() {
        return mFormat;
    }

    public int getId() {
        return mId;
    }
}
