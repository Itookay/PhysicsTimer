package itookay.android.org.contents;

import java.util.ArrayList;
import java.util.Iterator;

import itookay.android.org.style.StyleBase;
import itookay.android.org.font.FontBase;

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

    /**
     *          DialPanelを初期化
     */
    public void initDialPanel() {
        mSeparator = DialPanel.BLANK;

        createDialPanel();
        mStyle.arrangeDialPanels(mDialPanelList);
    }
    /**
     *          タイマーのスタイルをセット
     */
    public void setStyle(StyleBase style) {
        mStyle = style;
    }

    public void setFont(FontBase font) {
        mFont = font;
    }

    /**
     *          タイマーのサイズスケールをセットしてタイマーとタイルのサイズを計算<br>
     *          TileとDialPanelのサイズも代入。
     */
    public void setTimerSize() {
        /* 端末方向に関わらず短い方の幅を基準にする */
        float   width = Scale.getDisplayWidthMeter();
        float   height = Scale.getDisplayHeightMeter();
        float   standardWidth = height > width ? width : height;

        /* DialPanel一枚(数字2文字)が4セクションとしてセクションのサイズを計算 */
        float   timerWidth = standardWidth * mStyle.getDialWidthRatio();
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

        //DialPanelサイズからスペースサイズを除く
        float       dialPanelSizeWithoutSpace = dialPanelSize - dialPanelSpace;
        //タイル一枚のサイズ
        float   normalTileSize = dialPanelSizeWithoutSpace / mFont.getTwoNumbersColumnsCount();
        Tile.setStaticSize(normalTileSize, mStyle.getTileSpaceRatio());
        TileBase.setStaticSize(normalTileSize, mStyle.getTileSpaceRatio());

        /* コロンの前後スペースサイズ */
        float   cologneSpace = (sectionSize - normalTileSize) / 2f;
        DialPanel.setStaticCologneSpace(cologneSpace, cologneSpace);
    }

    /**
     *          タイマーの幅を取得
     */
    public float getTimerWidthWithSpace() {
        float       timerWidth = 0;
        //パネルの横幅を取得
        for (DialPanel dial : mDialPanelList) {
            timerWidth += dial.getWidthWithSpace();
        }

        return timerWidth;
    }

    /**
     * 			DialPanelを作成
     */
    private void createDialPanel() {
        int		id = 0;
        int     column = 0;
        int     array = 0;

        mDialPanelList.clear();

        /* 分 */
        DialPanel	minute = new DialPanel(DialPanel.MINUTE, id++);
        column = mFont.getDialPanelColumnCount(DialPanel.MINUTE);
        array = mFont.getDialPanelArrayCount(DialPanel.MINUTE);
        minute.setTileBaseArray(column, array);
        minute.setSizeFormat(mStyle.getMinuteTileSizeFormat());
        minute.createTileBase();
        mDialPanelList.add(minute);

        /* コロン */
        if(mStyle.existCologne()) {
            DialPanel cologne = new DialPanel(DialPanel.COLOGNE, id++);
            column = mFont.getDialPanelColumnCount(DialPanel.COLOGNE);
            array = mFont.getDialPanelArrayCount(DialPanel.COLOGNE);
            cologne.setTileBaseArray(column, array);
            cologne.setSizeFormat(mStyle.getCologneTileSizeFormat());
            cologne.createTileBase();
            mDialPanelList.add(cologne);
        }

        /* 秒 */
        DialPanel	second = new DialPanel(DialPanel.SECOND, id);
        column = mFont.getDialPanelColumnCount(DialPanel.SECOND);
        array = mFont.getDialPanelArrayCount(DialPanel.SECOND);
        second.setTileBaseArray(column, array);
        second.setSizeFormat(mStyle.getSecondTileSizeFormat());
        second.createTileBase();
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
     *      時間をクリア【ControlWorld.clearTime()も呼ぶこと】<br>
     * @param rejoint trueで次の時間に再ジョイント
     */
    public void clearTime(boolean rejoint) {
        if(mTime == null) {
            return;
        }

        if(rejoint) {
            clearAndRejointTime();
        }
        else {
            clearTime();
        }
    }

    /**
     *      時間をクリアし終了処理<br>
     */
    private void clearTime() {
        mTime = null;
        mNextTime = null;
        mSeparator = DialPanel.BLANK;

        for(DialPanel panel : mDialPanelList) {
            panel.getDestroyTileList().clear();
            panel.getJointTileList().clear();
        }
    }

    /**
     *      時間をクリアしジョイントリストを更新して次の時間で再ジョイント
     */
    private void clearAndRejointTime() {
        for(DialPanel panel : mDialPanelList) {
            /* 現在のジョイントTileをクリア */
            panel.getJointTileList().clear();
            panel.getDestroyTileList().clear();

            mTime = new Time(Time.CLEAR, Time.CLEAR, Time.CLEAR);
        }
    }

    public ArrayList<DialPanel> getDialPanelList() {
        return mDialPanelList;
    }

    /**
     * 			パネルにタイル配列をセット。
     * 			時刻の更新によって必要となるタイルと、不必要となるタイルをセット。
     */
    private void setDialPanel() {
        Iterator<DialPanel>		it = mDialPanelList.iterator();
        DialPanel		panel = null;
        while(it.hasNext()) {
            panel = it.next();

            switch(panel.getFormat()) {
                case DialPanel.MINUTE :
                    compareNumber(panel, mTime.getMinute(), mNextTime.getMinute());
                    break;
                case DialPanel.SECOND :
                    compareNumber(panel, mTime.getSecond(), mNextTime.getSecond());
                    break;
                case DialPanel.COLOGNE :
                    compareSeparator(panel, mSeparator, mNextSeparator);
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
    private void compareNumber(DialPanel panel, int present, int next) {
        int[]	presentArray = getDialArray(present);
        int[]	nextArray = getDialArray(next);
        int		length = presentArray.length;

        for(int i = 0; i < length; i++) {
            if(presentArray[i] == 0 && nextArray[i] == 1) {
                panel.getJointTileList().add(i);
            }
            if(presentArray[i] == 1 && nextArray[i] == 0) {
                panel.getDestroyTileList().add(i);
            }
        }
    }

    /**
     * 			引数のintから対応するタイルの配列を取得
     * @return		引数に指定した2桁の数字に対応する文字盤配列を返す．引数が負で空白の配列2桁分を返す
     */
    private int[] getDialArray(int arg) {
        int[]	num = toArray(arg);

        int[]	a = mFont.getNumber(num[0]);
        int[]	b = mFont.getNumber(num[1]);

        int     column = mFont.getOneNumberColumnCount();
        return conbine(a, b, column, column);
    }

    /*
     *      ☆　ここでセパレータ(空白、コロン、ドット)タイルの描画を指示する　☆
     */
    private void compareSeparator(DialPanel panel, int present, int next) {
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
            default :
                return mFont.COLOGNE;
        }
    }

    /**
     * 			引数の配列を[array1 array2]の並びで1つの配列に結合<br>
     * 			配列の行数は同じであること
     */
    public int[] conbine( int[] array1, int[] array2, int columnCount1, int columnCount2 ) {

        int[]	ret = new int[ array1.length + array2.length ];
        int		len = ret.length;
        int		array1Count = 0;
        int		array2Count = 0;

        for( int i = 0; i < len; ) {
            for( int n = 0; n < columnCount1; n++ ) {
                ret[i++] = array1[array1Count++];
            }
            for( int n = 0; n < columnCount2; n++ ) {
                ret[i++] = array2[array2Count++];
            }
        }

        array2Count = 0;

        return ret;
    }

    /**
     * 			引数のint（二桁）を一桁ずつバラして配列に入れて返す<br>
     */
    public int[] toArray( int num ) {

        String	str = Integer.toString( num );
        int[]	ret = new int[ 2 ];

        //10より小さい正の数
        if( num < 10 && num >= 0 ) {
            ret[0] = 0;
            ret[1] = str.charAt( 0 ) - '0';
        }
        //負の数
        else if( num < 0 ) {
            ret[0] = -1;
            ret[1] = -1;
        }
        else {
            ret[0] = str.charAt( 0 ) - '0';
            ret[1] = str.charAt( 1 ) - '0';
        }

        return ret;
    }
}
