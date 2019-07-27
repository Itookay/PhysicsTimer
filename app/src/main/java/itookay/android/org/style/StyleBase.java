package itookay.android.org.style;

import itookay.android.org.contents.*;
import itookay.android.org.font.FontBase;
import org.jbox2d.common.Vec2;

import java.util.ArrayList;

public abstract class StyleBase {

    /** スタイル名 */
    public String		NAME = null;

    /** 画面幅に対してタイマーの幅比 */
    protected float             DIAL_WIDTH_RATIO = 0.7f;
    /** DialPanelに対してのスペース幅比 */
    protected float             DIALPANEL_SPACE_RATIO = 0.2f;
    /** タイルに対してのスペース幅比 */
    protected float             TILE_SPACE_RATIO = 0.1f;

    /** DialPanelのスタイルをここで定義する。コンンストラクタから呼ばれる。 */
    protected abstract void     defineStyle();
    /** 小さいタイルの数を取得 */
    public abstract int         getSmallTileCount(FontBase font);
    /** ふつうタイルの数を取得 */
    public abstract int         getNormalTileCount(FontBase font);
    /** 「分」のタイルサイズを取得 */
    public abstract int         getMinuteTileSizeFormat();
    /** 「秒」のタイルサイズを取得 */
    public abstract int         getSecondTileSizeFormat();
    /** 「コロン」のタイルサイズを取得 */
    public abstract int         getCologneTileSizeFormat();
    /** タイルサイズの基準となるセクションを取得 */
    public abstract int         getSection();
    /** スタイルにコロンがあるか */
    public abstract boolean     existCologne();
    /** このメソッドで各スタイルごとにDialPanelを配置する */
    protected abstract void     customArrangement(ArrayList<DialPanel> dialPanels);
    /** Dialを画面の中央に配置 */
    public abstract void        alignCenter(ArrayList<DialPanel> dialPanels);

    /** 端末向き */
    protected int       mOrientation = PhysicsTimer.PORTRAIT;
    /** スケール */
    //protected Scale     mScale = null;

    public StyleBase() {
        defineStyle();
    }

    public void setOrientation(int orientation) {
        mOrientation = orientation;
    }

    /**
     *      DialPanelをスタイルの指定デザインに整列
     */
    public final void arrangeDialPanels(ArrayList<DialPanel> dialPanels) {
        baseArrangement(dialPanels);
        customArrangement(dialPanels);
    }

    /**
     *      DialPanelをディスプレイ原点(左上)に配置
     */
    protected void baseArrangement(ArrayList<DialPanel> dialPanels) {
    }

    /**
     *      Dialを回転
     * @param deg 回転角度
     */
    public final void rotateDial(ArrayList<DialPanel> dialPanels, float deg) {
        //Dialの原点
        Vec2 center = dialPanels.get(0).getPosition();

        for(DialPanel panel : dialPanels) {
            for (TileBase tileBase : panel.getTileBaseList()) {
                Vec2 pos1 = tileBase.getWorldJointPos1();
                Vec2 pos2 = tileBase.getWorldJointPos2();

                pos1 = rotate(center, pos1, deg);
                pos2 = rotate(center, pos2, deg);
                tileBase.setWorldJointPos1(pos1);
                tileBase.setWorldJointPos2(pos2);
            }
        }

        alignCenter(dialPanels);
    }

    /**
     *      座標を回転
     * @param c 回転の中心
     * @param p 回転する座標
     * @param w 回転角度
     * @return 回転後の座標
     */
    private Vec2 rotate(Vec2 c, Vec2 p, float w) {
        float   dx = p.x - c.x;
        float   dy = p.y - c.y;
        Vec2    r = new Vec2();
        float   pi = 3.14f;
        r.x = (float)(dx*Math.cos(w/180*pi) - dy*Math.sin(w/180*pi) + c.x);
        r.y = (float)(dx*Math.sin(w/180*pi) + dy*Math.cos(w/180*pi) + c.y);
        return r;
    }



    /**
     *      ディスプレイに対するDial幅との比を取得
     */
    public float getDialWidthRatio() {
        return DIAL_WIDTH_RATIO;
    }

    /**
     *      DialPanelに対するDialPanel内スペースとの比を取得
     */
    public float getDialPanelSpaceRatio() {
        return DIALPANEL_SPACE_RATIO;
    }

    /**
     *      Tileに対するTile内スペースとの比を取得
     */
    public float getTileSpaceRatio() {
        return TILE_SPACE_RATIO;
    }
}
