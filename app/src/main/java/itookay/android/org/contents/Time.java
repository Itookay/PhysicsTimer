package itookay.android.org.contents;

/**
 *
 * 			表示する時間を保持
 *
 */
public class Time {

    /** 時間のクリア */
    public static int       CLEAR = -1;

    private int		mHour = CLEAR;
    private int		mMinute = CLEAR;
    private int		mSecond = CLEAR;

    public Time( int hour, int minute, int second ) {
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

    public String getHourString() {
        return Integer.toString( mHour );
    }

    public String getMinuteString() {
        return Integer.toString( mMinute );
    }

    public String getSecondString() {
        return Integer.toString( mSecond );
    }

    public void set( Time time ) {

        mHour = time.getHour();
        mMinute = time.getMinute();
        mSecond = time.getSecond();
    }
}
