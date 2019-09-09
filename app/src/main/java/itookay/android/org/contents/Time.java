package itookay.android.org.contents;

import java.io.Serializable;

/**
 *
 * 			表示する時間を保持
 *
 */
public class Time implements Serializable {

    /** 時間のクリア */
    static int       CLEAR = -1;

    private int		mHour = CLEAR;
    private int		mMinute = CLEAR;
    private int		mSecond = CLEAR;

    public Time() {
    }

    public Time(int hour, int minute, int second) {
        mHour = hour;
        mMinute = minute;
        mSecond = second;
    }

    public int getHour() {
        return mHour;
    }

    public int getMinute() {
        return mMinute;
    }

    public int getSecond() {
        return mSecond;
    }

    public void set(Time time) {
        mHour = time.getHour();
        mMinute = time.getMinute();
        mSecond = time.getSecond();
    }
}
