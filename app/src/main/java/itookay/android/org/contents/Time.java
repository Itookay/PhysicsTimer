package itookay.android.org.contents;

import java.io.Serializable;

/**
 *
 * 			表示する時間を保持
 *
 */
class Time implements Serializable {

    /** 時間のクリア */
    static int       CLEAR = -1;

    private int		mHour = CLEAR;
    private int		mMinute = CLEAR;
    private int		mSecond = CLEAR;

    Time() {
    }

    Time( int hour, int minute, int second ) {
        mHour = hour;
        mMinute = minute;
        mSecond = second;
    }

    int getHour() {
        return mHour;
    }

    int getMinute() {
        return mMinute;
    }

    int getSecond() {
        return mSecond;
    }

    void set( Time time ) {

        mHour = time.getHour();
        mMinute = time.getMinute();
        mSecond = time.getSecond();
    }
}
