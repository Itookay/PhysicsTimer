package itookay.android.org.contents;

/**
 * 			時間が変化したことを知らせるリスナー
 */
public interface TimeChangedListener {

    /**
     * 			1秒ごとに時間を通知
     */
    public void onTimeChanged( int hour, int minute, int second );
}

