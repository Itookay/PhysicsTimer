package itookay.android.org.Style;

import itookay.android.org.contents.Dial;
import itookay.android.org.contents.DialPanel;
import itookay.android.org.contents.Scale;
import itookay.android.org.contents.Tile;
import itookay.android.org.font.FontBase;
import org.jbox2d.common.Vec2;

import java.util.ArrayList;

/**
 *      DialPnaleスタイル「分¥n秒」 ex.12¥n34」<br>
 *          タイルサイズ　：分はTile.SMALL、秒はTile.NORMAL<br>
 *          列　　　　　　：2列
 */
public class TwoRowsBigSecond extends StyleBase {

    public TwoRowsBigSecond(FontBase font, Scale scale) {
        super(font, scale);
        mScale = scale;
    }

    @Override
    public void defineStyle() {
    }

    @Override
    public int getSmallTileCount() {
        return mFont.getDialPanelArrayCount(DialPanel.MINUTE);
    }

    @Override
    public int getNomalTileCount() {
        return mFont.getDialPanelArrayCount(DialPanel.SECOND);
    }

    @Override
    public int getMinuteTileSizeFormat() {
        return Tile.SMALL;
    }

    @Override
    public int getSecondTileSizeFormat() {
        return Tile.NORMAL;
    }

    @Override
    public int getCologneTileSizeFormat() {
        return 0;
    }

    @Override
    public int getSection() {
        return 4;
    }

    @Override
    public boolean existCologne() {
        return false;
    }

    /**
     *      このメソッドで各スタイルごとにDialPanelを配置する
     */
    @Override
    protected void costomArrangement(ArrayList<DialPanel> dialPanels) {
        float       x = 0;
        float       y = 0;
        DialPanel   minute = dialPanels.get(0);
        DialPanel   second = dialPanels.get(1);
        float       dialWidth = second.getWidthWithSpace();

        /* 「秒」を「分」の下に改行 */
        y = -minute.getHeightWithSpace();
        second.OffsetPosition(0, y);

        /* Dial全体をディスプレイ中央に配置 */
        x = (mScale.getDisplayWidthMeter() - dialWidth) / 2f;
        for(DialPanel panel : dialPanels) {
            panel.OffsetPosition(x, -x);
        }
    }
}
