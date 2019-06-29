package itookay.android.org.Style;

import itookay.android.org.contents.Dial;
import itookay.android.org.contents.DialPanel;
import itookay.android.org.contents.Scale;
import itookay.android.org.contents.Tile;
import itookay.android.org.font.FontBase;

import java.util.ArrayList;

public abstract class StyleBase {

    /** DialPanelのスタイルをここで定義する。コンンストラクタから呼ばれる。 */
    protected abstract void     defineStyle();
    /** 小さいタイルの数を取得 */
    public abstract int         getSmallTileCount();
    /** ふつうタイルの数を取得 */
    public abstract int         getNomalTileCount();
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
    protected abstract void     costomArrangement(ArrayList<DialPanel> dialPanels);

    /** フォント */
    protected FontBase      mFont;
    /** ディスプレイスケール */
    protected Scale         mScale;
    /** 「分」のタイルサイズ。Tile.SIZE_SMALLかTile.NORMAL */
    protected int           mMiunteTileSize;
    /** 「秒」のタイルサイズ。Tile.SIZE_SMALLかTile.NORMAL */
    protected int           mSecondTileSize;

    public StyleBase(FontBase font, Scale scale) {
        mFont = font;
        mScale = scale;
        defineStyle();
    }

    public FontBase getFont() {
        return mFont;
    }

    /**
     *      DialPanelをスタイルの指定デザインに整列
     * @param dialPanels
     */
    public final void arrangeDialPanels(ArrayList<DialPanel> dialPanels) {
        baseArrangement(dialPanels);
        costomArrangement(dialPanels);
    }

    /**
     *      DialPanelをディスプレイ原点(左上)に配置
     * @param dialPanels
     */
    private void baseArrangement(ArrayList<DialPanel> dialPanels) {
        float   y = mScale.getDisplayHeightMeter();
        for(DialPanel panel : dialPanels) {
            panel.OffsetPosition(0, y);
        }
    }

    /**
     *      ディスプレイに対するDial幅との比を取得
     */
    public float getDialWidthRatio() {
        return 0.7f;
    }

    /**
     *      DialPanelに対するDialPanel内スペースとの比を取得
     */
    public float getDialPanelSpaceRatio() {
        return 0.1f;
    }

    /**
     *      Tileに対するTile内スペースとの比を取得
     */
    public float getTileSpaceRatio() {
        return 0.1f;
    }
}
