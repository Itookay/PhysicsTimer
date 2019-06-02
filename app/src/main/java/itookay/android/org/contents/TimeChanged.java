package itookay.android.org.contents;

import android.app.IntentService;
import android.app.Notification;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class TimeChanged extends Service {

    /** アプリケーションコンテキスト */
    private Context mAppContext = null;

    /** 次にハンドラをpostする時間 */
    protected final int			DELAY_TIME = 1000;
    /** 59秒 */
    protected final int			_59_SECONDS = 59;
    /** 59分 */
    protected final int			_59_MINUTE = 59;

    /** 通知する対象 */
    protected TimeChangedListener		mObserver = null;
    /** 時間監視ハンドラ */
    protected Handler		mHandler = new Handler();
    /** 分 */
    private int		mMinute = -1;
    /** 秒 */
    private int		mSecond = -1;
    /** 総秒数 */
    private int		mActualSeconds = -1;

    /** クライアントに渡すバインダー */
    private final IBinder       mBinder = new LocalBinder();

    /**
     *          クライアントへのバインダーのためのクラス
     */
    public class LocalBinder extends Binder {
        public TimeChanged getService() {
            return TimeChanged.this;
        }
    }

    /**
     *          コンストラクタ
     */
    public TimeChanged(Context appContext) {
        mAppContext = appContext;
    }

    public TimeChanged(){

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(mAppContext, "ChannelId");

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * 			タイマースタート
     */
    public void startTimer() {
        if(mObserver != null) {
            mRunnable.run();
        }
    }

    /**
     * 			スタート時間をセット
     * @param hour
     * @param minute
     * @param second
     */
    public void setInitialTime(int hour, int minute, int second) {
        mMinute = minute;
        mSecond = second;

        if(mMinute < 0) mMinute = 0;
        if(mSecond < 0) mSecond = 0;
        mActualSeconds = mMinute * 60 + mSecond;
    }

    /** 時間監視スレッド */
    protected Runnable		mRunnable = new Runnable() {
        @Override
        public void run() {
            if( mObserver != null ) {
                if(forward() == false) {
                    removeCallback();
                }
                else {
                    mObserver.onTimeChanged( 0, mMinute, mSecond );
                    mHandler.postDelayed( mRunnable, DELAY_TIME );
                }
            }
        }
    };

    public void removeCallback() {
        if( mRunnable != null ) {
            mHandler.removeCallbacks( mRunnable );
        }
    }

    /**
     * 			リスナーに登録
     */
    public void setOnTimeChangedListener( TimeChangedListener obj ) {
        mObserver = obj;
    }

    /**
     * 			タイマーを1秒進める
     */
    private boolean forward() {
        if(--mActualSeconds < 0) {
            return false;
        }

        if(--mSecond < 0) {
            mSecond = _59_SECONDS;
            if(--mMinute <= 0) {
                mMinute = 0;
            }
        }

        return true;
    }
}