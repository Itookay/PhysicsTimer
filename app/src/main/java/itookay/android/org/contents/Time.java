package itookay.android.org.contents;

/**
 *
 * 			表示する時間を保持
 *
 */
public class Time {

	private int		mHour = -1;
	private int		mMinute = -1;
	private int		mSecond = -1;

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
