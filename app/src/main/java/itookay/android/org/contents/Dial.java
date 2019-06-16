package itookay.android.org.contents;

import java.util.ArrayList;
import java.util.Iterator;

import itookay.android.org.font.FontBase;

import org.jbox2d.common.Vec2;

import android.graphics.PointF;
import android.util.Log;

public class Dial {

    /** フォント */
    private FontBase	mFont = null;
    /** 表示スタイル */
    private int			mStyle = -1;
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
    /** 画面幅1に対するタイマーのスケール */
    private float       mTimerSizeScale = 0f;

    /** 数字2文字分のセクション数 **/
    private final int   SECTION_COUNT_NUMBER = 4;
    /** コロンのセクション数 **/
    private final int   SECTION_COUNT_COLOGNE = 1;

    /**
     *          DialPanelを生成
     */
    public void createDials(int dialStyle) {
        for(DialPanel dialPanel : mDialPanelList) {
            dialPanel.createDialPanel();
        }
    }
    /**
     *          タイマーのスタイルをセット
     * @param style スタイル値(現在は値に関わらず一定のスタイル)
     */
    public void setStyle(int style) {
        mStyle = style;

        switch (mStyle) {
            default:
                setDial();
                break;
        }
    }

    /**
     *          タイマーのサイズスケールをセットしてタイマーとタイルのサイズを計算
     * @param timerSizeScale 画面幅を1とした時のタイマーのスケール
     * @param dialSpaceScale DialPanelサイズを1とした時のDialPanel間すきまの割合
     * @param tileSpaceScale Tileサイズを1とした時のTile間すきまの割合
     */
    public void setTimerSizeScale(float timerSizeScale, float dialSpaceScale, float tileSpaceScale) {
        mTimerSizeScale = timerSizeScale;

        /* DialPanel一枚(数字2文字)が4セクションとしてセクションのサイズを計算 */
        float       sectionSize = getDialPanelSectionSize();
        float       dialPanelSize = sectionSize * SECTION_COUNT_NUMBER;

        /* 時・分のスペースサイズ */
        float       dialPanelSpaceSize = dialPanelSize * dialSpaceScale;
        DialPanel.setNumberSpaceSize(dialPanelSpaceSize/4f, dialPanelSpaceSize/2f, dialPanelSpaceSize/4f);

        //DialPanelスペースサイズからスペースサイズを除く
        float       dialPanelSizeWithoutSpace = dialPanelSize - dialPanelSpaceSize;
        //タイル一枚のサイズ
        float       tileSize = dialPanelSizeWithoutSpace / mFont.get2NumbersColumnsCount();
        Tile.setSize(tileSize, tileSpaceScale);

        /* コロンの前後スペースサイズ */
        dialPanelSpaceSize = (sectionSize - tileSize) / 2f;
        DialPanel.setCologneSpaceSize(dialPanelSpaceSize, dialPanelSpaceSize);
    }

    public void setFont( FontBase font ) {
        mFont = font;
    }

    public FontBase getFont() {
        return mFont;
    }

    public void setScale( Scale scale ) {
        mScale = scale;
    }

    /**
     *          DialPanelをリストから出てきた順に一列になるようオフセット
     */
    public void arrangeDials() {
        float       posX = 0;

        for(DialPanel dialPanel : mDialPanelList) {
            dialPanel.OffsetPosition(posX, 0f);
            //次の位置へ移動
            posX += dialPanel.getWidthWithSpace();
        }
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

        //分
        DialPanel	minute = new DialPanel(DialPanel.MINUTE, 1, id++);
        mDialPanelList.add( minute );

        //コロン
        DialPanel	cologne = new DialPanel(DialPanel.COLOGNE, 1, id++);
        mDialPanelList.add( cologne );

        //秒
        DialPanel	second = new DialPanel(DialPanel.SECOND, 1, id);
        mDialPanelList.add( second );
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
     *          画面サイズに合わせたダイアルサイズを取得<br>
     *          DialPanelは数字・コロンで幅が違うから、セクション(数字1字が2セクション、コロンが1セクション)として扱う
     * @return
     */
    private float getDialPanelSectionSize() {
        //セクション数
        int         section = 0;
        //セクション1つのサイズ
        float       sectionSize = 0f;

        //セクション数を取得
        for(DialPanel dialPanel : mDialPanelList) {
            if(dialPanel.getFormat() == DialPanel.MINUTE || dialPanel.getFormat() == DialPanel.SECOND) {
                section += SECTION_COUNT_NUMBER; //数字2桁*2セクション
            }
            else if(dialPanel.getFormat() == DialPanel.COLOGNE) {
                section += SECTION_COUNT_COLOGNE;
            }
        }

        //画面幅に対するタイマーの幅
        float   timerWidth = mScale.getDisplayWidthMeter() * mTimerSizeScale;
        sectionSize = timerWidth / section;

        return sectionSize;
    }

    /**
     * 			DialPanelの書き出し位置をオフセット
     */
    public void OffsetPosition( float x, float y ) {
        Iterator<DialPanel>		it = mDialPanelList.iterator();

        DialPanel	panel = null;
        while( it.hasNext() ) {
            panel = it.next();
            panel.OffsetPosition( x, y );
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
                case DialPanel.HOUR :
                    comparePanels( panel, mTime.getHour(), mNextTime.getHour() );
                    break;
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
            case DialPanel.DOT :
                return mFont.DOT;
            case DialPanel.COLOGNE :
                return mFont.COLOGNE;
            case DialPanel.BLANK :
                return mFont.BLANK;
            default :
                return mFont.BLANK;
        }
    }
}
