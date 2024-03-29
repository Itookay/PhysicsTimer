package itookay.android.org.style;

import itookay.android.org.contents.*;
import itookay.android.org.font.FontBase;
import org.jbox2d.common.Vec2;

import java.util.ArrayList;

/**
 *      DialPnaleスタイル「分¥n秒」 ex.12¥n34」<br>
 *          タイルサイズ　：分はTile.SMALL、秒はTile.NORMAL<br>
 *          列　　　　　　：2列
 */
public class TwoRowsBigSecond extends StyleBase {

    public TwoRowsBigSecond() {
        super();
        NAME = "Two Rows Big Second";
    }

    @Override
    public void defineStyle() {
    }

    @Override
    public int getSmallTileCount(FontBase font) {
        return font.getDialPanelArrayCount(DialPanel.MINUTE);
    }

    @Override
    public int getNormalTileCount(FontBase font) {
        return font.getDialPanelArrayCount(DialPanel.SECOND) + 20;
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
    protected void customArrangement(ArrayList<DialPanel> dialPanels) {
        float       x = 0;
        float       y = 0;
        DialPanel   minute = dialPanels.get(0);
        DialPanel   second = dialPanels.get(1);

        /* 「秒」を「分」の下に改行 */
        y = -minute.getHeightWithSpace();
        second.OffsetPosition(x, y);

        //Dial全体をディスプレイ中央に配置
        alignCenter(dialPanels);
    }

    @Override
    public void alignCenter(ArrayList<DialPanel> dialPanels) {
        DialPanel   minute = dialPanels.get(0);
        DialPanel   second = dialPanels.get(1);
        float   dialWidth = second.getWidthWithSpace();
        float   dialHeight = minute.getHeightWithSpace() + second.getHeightWithSpace();
        float   x = 0f;
        float   y = 0f;

        //現在のDial原点位置
        Vec2    c = minute.getPosition();

        //端末縦向き
        switch(mOrientation) {
            case PhysicsTimer.PORTRAIT:
            case PhysicsTimer.UPSIDE_DOWN:
                x = (Scale.getDisplayWidthMeter() - dialWidth) / 2f;
                y = Scale.getDisplayHeightMeter() - x;
                break;
            case PhysicsTimer.LEFT_LANDSCAPE:
                x = (Scale.getDisplayWidthMeter() - dialHeight) / 2f + dialHeight;
                y = (Scale.getDisplayHeightMeter() - dialWidth) / 2f + dialWidth;
                break;
            case PhysicsTimer.RIGHT_LANDSCAPE:
                x = (Scale.getDisplayWidthMeter() - dialHeight) / 2f;
                y = (Scale.getDisplayHeightMeter() - dialWidth) / 2f;
                break;
            default:
                return;
        }

        float   dx = x - c.x;
        float   dy = y - c.y;
        minute.OffsetPosition(dx, dy);
        second.OffsetPosition(dx, dy);
    }

    @Override
    public float getDialHeight(DialPanel minute, DialPanel cologne, DialPanel second) {
        return minute.getHeightWithSpace() + second.getHeightWithSpace();
    }
}
