package itookay.android.org.style;

import itookay.android.org.contents.DialPanel;
import itookay.android.org.contents.PhysicsTimer;
import itookay.android.org.contents.Scale;
import itookay.android.org.contents.Tile;
import itookay.android.org.font.FontBase;
import org.jbox2d.common.Vec2;

import java.util.ArrayList;

/**
 *      DialPnaleスタイル「分：秒」 ex.12:34」<br>
 *          タイルサイズ　：すべてTile.NORMAL<br>
 *          列　　　　　　：1列
 */
public class SingleRow extends StyleBase {

    public SingleRow() {
        super();
        NAME = "Single Row";
        DIAL_WIDTH_RATIO = 0.9f;
    }

    @Override
    protected void defineStyle() {

    }

    @Override
    public int getNormalTileCount(FontBase font) {
        int count = 0;
        count += font.getDialPanelArrayCount(DialPanel.MINUTE);
        count += font.getDialPanelArrayCount(DialPanel.COLOGNE);
        count += font.getDialPanelArrayCount(DialPanel.SECOND);
        return count;
    }

    @Override
    public int getSmallTileCount(FontBase font) {
        return 0;
    }

    @Override
    public int getMinuteTileSizeFormat() {
        return Tile.NORMAL;
    }

    @Override
    public int getSecondTileSizeFormat() {
        return Tile.NORMAL;
    }

    @Override
    public int getCologneTileSizeFormat() {
        return Tile.NORMAL;
    }

    @Override
    public int getSection() {
        return 9;
    }

    @Override
    public boolean existCologne() {
        return true;
    }

    @Override
    protected void customArrangement(ArrayList<DialPanel> dialPanels) {
        float       posX = 0;
        for(DialPanel panel : dialPanels) {
            panel.OffsetPosition(posX, 0f);
            //次の位置へ移動
            posX += panel.getWidthWithSpace();
        }

        alignCenter(dialPanels);
    }

    @Override
    public void alignCenter(ArrayList<DialPanel> dialPanels) {
        DialPanel   minute = dialPanels.get(0);
        DialPanel   cologne = dialPanels.get(1);
        DialPanel   second = dialPanels.get(2);
        float   dialWidth = minute.getWidthWithSpace() + cologne.getWidthWithSpace() + second.getWidthWithSpace();
        float   dialHeight = second.getHeightWithSpace();
        float   x = 0f;
        float   y = 0f;

        //現在のDial原点位置
        Vec2 c = minute.getPosition();

        //端末縦向き
        switch(mOrientation) {
            case PhysicsTimer.PORTRAIT:
            case PhysicsTimer.UPSIDE_DOWN:
                x = (Scale.getDisplayWidthMeter() - dialWidth) / 2f;
                y = Scale.getDisplayHeightMeter() * 0.7f;
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
        for(DialPanel panel : dialPanels) {
            panel.OffsetPosition(dx, dy);
        }
    }
}
