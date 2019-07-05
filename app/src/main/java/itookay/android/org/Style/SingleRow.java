package itookay.android.org.Style;

import itookay.android.org.contents.DialPanel;
import itookay.android.org.contents.Scale;
import itookay.android.org.contents.Tile;
import itookay.android.org.font.FontBase;

import java.util.ArrayList;

/**
 *      DialPnaleスタイル「分：秒」 ex.12:34」<br>
 *          タイルサイズ　：すべてTile.NORMAL<br>
 *          列　　　　　　：1列
 */
public class SingleRow extends StyleBase {

    public SingleRow() {
        super();
    }

    @Override
    protected void defineStyle() {

    }

    @Override
    public int getNomalTileCount(FontBase font) {
        int count = 0;
        count += font.getDialPanelArrayCount(DialPanel.MINUTE);
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
    }

    @Override
    public void alignCenter(ArrayList<DialPanel> dialPanels) {

    }

    @Override
    public float getDialWidthRatio() {
        return 0.7f;
    }

}
