package itookay.android.org.contents;

import java.util.ArrayList;
import java.util.Iterator;

import itookay.android.org.Style.StyleBase;
import itookay.android.org.font.FontBase;

import org.jbox2d.common.Vec2;

import android.graphics.PointF;
import android.util.Log;

public class Dial {

    /** フォント */
    private FontBase	mFont = null;
    /** 表示スタイル */
    private StyleBase   mStyle = null;
    /** 現時刻 */
    private Time		mTime = null;
    /** 更新された時間 */
    private Time		mNextTime = null;
    /** 現在のセパレータ */
    private int         mSeparator = DialPanel.BLANK;
    /** 更新されたセパレータ */
    private int         mNextSeparator = DialPanel.COLOGNE;
    /** DialPanel格納場所 */
    private ArrayList<DialPanel>		mDialPanelList = new ArrayList<DialPanel>();
    /** ワールドスケール */
    private Scale		mScale = null;

    /**
     *          DialPanelを生成
     */
    public void createDials() {
        for(DialPanel dialPanel : mDialPanelList) {
            dialPanel.createDialPanel();
        }

        mStyle.arrangeDialPanels(mDialPanelList);
    }
    /**
     *          タイマーのスタイルをセット
     */
    public void setStyle(StyleBase style) {
        mStyle = style;
        mFont = mStyle.getFont();

        //仮置き------
        setDial();
        //-----------
    }

    public void setScale( Scale scale ) {
        mScale = scale;
    }

    /**
     *          タイマーのサイズスケールをセットしてタイマーとタイルのサイズを計算<br>
     *          TileとDialPanelのサイズも代入。
     */
    public void setTimerSizeScale() {
        /* DialPanel一枚(数字2文字)が4セクションとしてセクションのサイズを計算 */
        float   timerWidth = mScale.getDisplayWidthMeter() * mStyle.getDialWidthRatio();
        float   sectionSize = timerWidth / mStyle.getSection();
        float   dialPanelSize = sectionSize * 4;

        /* 時・分のスペースサイズ */
        float   dialPanelSpace = dialPanelSize * mStyle.getDialPanelSpaceRatio();
        float   left = dialPanelSpace / 4f;
        float   upper = left;
        float   center = dialPanelSpace / 2f;
        float   right = dialPanelSpace /4f;
        float   bottom = right;
        DialPanel.setStaticNormalSpace(upper, left, center, right, bottom);
        float   scale = 0.5f;
        left *= scale;
        upper *= scale;
        center *= scale;
        right *= scale;
        bottom *= scale;
        DialPanel.setStaticSmallSpace(upper, left, center, right, bottom);

        //DialPanelスペースサイズからスペースサイズを除く
        float       dialPanelSizeWithoutSpace = dialPanelSize - dialPanelSpace;
        //タイル一枚のサイズ
        float   normalTileSize = dialPanelSizeWithoutSpace / mFont.get2NumbersColumnsCount();
        float   smallTileSize = normalTileSize * 0.5f;
        Tile.setStaticSize(normalTileSize, smallTileSize, mStyle.getTileSpaceRatio());
        TileBase.setStaticSize(normalTileSize, smallTileSize, mStyle.getTileSpaceRatio());

        /* コロンの前後スペースサイズ */
        float   cologneSpace = (sectionSize - normalTileSize) / 2f;
        DialPanel.setStaticCologneSpace(cologneSpace, cologneSpace);
    }

    /**
     *          タイマーの幅を取得
     * @return
     */
    public float getTimerWidth() {
        float       timerWidth = 0;
        //パネルの横幅を取得
        for (DialPanel dial : mDialPanelList) {
            timerWidth += dial.getWidthWithSpace();
        }

        return timerWidth;
    }

    /**
     * 			タイマー（分:秒）を作成
     */
    private void setDial() {
        Vec2	pos = new Vec2();
        int		id = 0;
        int     column = 0;
        int     array = 0;
        Tile    tile = null;

        //分
        DialPanel	minute = new DialPanel(DialPanel.MINUTE, id++);
        column = mFont.getDialPanelColumnCount(DialPanel.MINUTE);
        array = mFont.getDialPanelArrayCount(DialPanel.MINUTE);
        minute.setTileBaseArray(column, array);
        minute.setSizeFormat(mStyle.getMinuteTileSizeFormat());
        mDialPanelList.add(minute);

        //コロン
        if(mStyle.existCologne()) {
            DialPanel cologne = new DialPanel(DialPanel.COLOGNE, id++);
            column = mFont.getDialPanelColumnCount(DialPanel.COLOGNE);
            array = mFont.getDialPanelArrayCount(DialPanel.COLOGNE);
            cologne.setTileBaseArray(column, array);
            cologne.setSizeFormat(mStyle.getCologneTileSizeFormat());
            mDialPanelList.add(cologne);
        }

        //秒
        DialPanel	second = new DialPanel(DialPanel.SECOND, id);
        column = mFont.getDialPanelColumnCount(DialPanel.SECOND);
        array = mFont.getDialPanelArrayCount(DialPanel.SECOND);
        second.setTileBaseArray(column, array);
        second.setSizeFormat(mStyle.getSecondTileSizeFormat());
        mDialPanelList.add(second);
    }

    /**
     * 			次の時間をセット
     * @param time
     */
    public void setTime(Time time) {
        mNextTime = time;
        if(mTime == null) {
            mTime = new Time(-1, -1, -1);
        }
        setDialPanel();

        mTime.set(mNextTime);
        mSeparator = mNextSeparator;
    }

    /**
     *      時間をクリア
     */
    public void clearTime() {
        mTime = null;
        mNextTime = null;
        mSeparator = DialPanel.BLANK;

        for(DialPanel panel : mDialPanelList) {
            panel.getDestroyTileList().clear();
            panel.getJointTileList().clear();
        }
    }

    public ArrayList<DialPanel> getDialPanelList() {
        return mDialPanelList;
    }

    /**
     * 			DialPanelの書き出し位置をオフセット
     */
    public void OffsetPosition( float x, float y ) {
        for(DialPanel panel : mDialPanelList) {
            panel.OffsetPosition(x, y);
        }
    }

    /**
     * 			パネルにタイル配列をセット。
     * 			時刻の更新によって必要となるタイルと、不必要となるタイルをセット。
     */
    private void setDialPanel() {
        Iterator<DialPanel>		it = mDialPanelList.iterator();
        DialPanel		panel = null;
        while( it.hasNext() ) {
            panel = it.next();

            switch( panel.getFormat() ) {
                case DialPanel.MINUTE :
                    comparePanels( panel, mTime.getMinute(), mNextTime.getMinute() );
                    break;
                case DialPanel.SECOND :
                    comparePanels( panel, mTime.getSecond(), mNextTime.getSecond() );
                    break;
                case DialPanel.COLOGNE :
                    setSeparator( panel, mSeparator, mNextSeparator);
                    break;
            }
        }
    }

    /**
     *          ☆　ここで数字タイルの描画を指示する　☆
     * 			現在時present->次の時間nextで不要となるタイル：
     * 			　present[index]=0,next[index]=1で{@link DialPanel#getJointTileList()}にindexを追加
     * 			現在時present->次の時間nextで必要となるタイル：
     * 			　present[inedx]=1,next[index]=0で{@link DialPanel#getDestroyTileList()}にindexを追加
     */
    private void comparePanels( DialPanel panel, int present, int next ) {
        int[]	presentArray = getDialArray( present );
        int[]	nextArray = getDialArray( next );
        int		length = presentArray.length;

        for( int i = 0; i < length; i++ ) {
            if( presentArray[i] == 0 && nextArray[i] == 1 ) {
                panel.getJointTileList().add( i );
            }
            if( presentArray[i] == 1 && nextArray[i] == 0 ) {
                panel.getDestroyTileList().add( i );
            }
        }
    }

    /**
     * 			引数のintから対応するタイルの配列を取得
     * @return		引数に指定した2桁の数字に対応する文字盤配列を返す．引数が負で空白の配列2桁分を返す
     */
    private int[] getDialArray( int arg ) {
        int[]	num = Array.toArray( arg );

        int[]	a = mFont.getNumber( num[0] );
        int[]	b = mFont.getNumber( num[1] );

        return Array.conbine( a, b, mFont.getColumnCount(), mFont.getColumnCount() );
    }

    /*
     *      ☆　ここでセパレータ(空白、コロン、ドット)タイルの描画を指示する　☆
     */
    private void setSeparator(DialPanel panel, int present, int next) {
        int[]   presentArray = getSeparatorArray(present);
        int[]   nextArray = getSeparatorArray(next);

        int     length = presentArray.length;
        for(int i=0; i<length; i++) {
            if( presentArray[i] == 0 && nextArray[i] == 1 ) {
                panel.getJointTileList().add( i );
            }
            if( presentArray[i] == 1 && nextArray[i] == 0 ) {
                panel.getDestroyTileList().add( i );
            }
        }
    }

    /*
     *      DialPanel定数からタイルの配列を取得
     */
    private int[] getSeparatorArray(int separator) {
        switch (separator) {
            case DialPanel.COLOGNE :
                return mFont.COLOGNE;
            case DialPanel.BLANK :
                return mFont.BLANK;
            default :
                return mFont.BLANK;
        }
    }
}
