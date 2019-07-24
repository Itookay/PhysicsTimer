package itookay.android.org.contents;

import android.app.*;
import android.content.ContentProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import itookay.android.org.MainActivity;
import itookay.android.org.R;

/**
 *      ForeGroundServiceで時間の経過を監視し単位秒ごとに通知する
 */
public class TimeWatchingService extends Service {

    /** アプリケーションコンテキスト */
    private Context mAppContext = null;

    /** 次にハンドラをpostする時間 */
    protected final int		DELAY_TIME = 1000;
    /** 59秒 */
    protected final int		_59_SECONDS = 59;
    /** 59分 */
    protected final int		_59_MINUTE = 59;
    /** タイマー終了通知 */
    public static final int            TIMER_FINISHED = -1;

    /** 通知する対象 */
    protected static TimeChangedListener		mObserver = null;
    /** 時間監視ハンドラ */
    protected Handler		mHandler = new Handler();
    /** 分 */
    private int	            mMinute = -1;
    /** 秒 */
    private int	            mSecond = -1;
    /** 総秒数 */
    private int	            mActualSeconds = -1;
    /** タイマー動作中 */
    private static boolean         mIsAlive = false;
    /** コールバック有効 */
    private static boolean         mCallbackAvailability = true;
    /** 通知 */
    private Notification    mNotification = null;
    /** サービスの停止 */
    private static boolean         mStopService = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        ServiceInfo    info = (ServiceInfo)intent.getSerializableExtra(PhysicsTimer.INTENT_KEY_TIME_WATCHING_SERVICE);
        setTime(info.Time);

        /* 通知を表示 */
        String      channelId = "PhysicsTimer_Channel";
        setNotificationChannel(channelId);
        mNotification = getNotification(channelId);

        startTimer();

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        removeCallback();
    }

    public void setNotificationChannel(String channelId) {
        NotificationManager     notificationMgr = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        String      name = "Physics Timer Notification Channel";
        String      description = "It is Physics Timer Notification.";
        if(notificationMgr.getNotificationChannel(channelId) != null) {
            NotificationChannel     channel = new NotificationChannel(channelId, name, NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription(description);
            notificationMgr.createNotificationChannel(channel);
        }
    }

    public Notification getNotification(String channelId) {
        /* 通知をタップした時に起動するActivity */
        Intent      intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent   pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setContentTitle("Physics Timer Title")
                .setContentText("タイマー動作中です。")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentIntent(pendingIntent);
        return builder.build();
    }

    /**
     *      タイマー起動中か
     */
    public static boolean isAlive() {
        return mIsAlive;
    }

    /**
     *      (タイマー起動中なら)タイマーの停止
     */
    public static void stopTimer() {
        if(mIsAlive) {
            mStopService = true;
        }
    }

    /**
     * 			タイマースタート
     */
    private void startTimer() {
        if(mObserver != null) {
            mRunnable.run();
            mIsAlive = true;
            startForeground(1, mNotification);
        }
    }

    /**
     * 			スタート時間をセット
     */
    public void setTime(Time time) {
        mMinute = time.getMinute();
        mSecond = time.getSecond();

        if(mMinute < 0) mMinute = 0;
        if(mSecond < 0) mSecond = 0;
        mActualSeconds = mMinute * 60 + mSecond;
    }

    /** 時間監視スレッド */
    protected Runnable		mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mStopService) {
                removeCallback();
                mStopService = false;
                return;
            }

            if(!forward()) {
                removeCallback();
                //secondにTIMER_FINISHEDを渡して終了を通知
                if(mCallbackAvailability) {
                    mObserver.onTimeChanged(0, 0, TIMER_FINISHED);
                }
            }
            else {
                if(mCallbackAvailability) {
                    mObserver.onTimeChanged(0, mMinute, mSecond);
                }
                mHandler.postDelayed(mRunnable, DELAY_TIME);
            }
        }
    };

    /**
     *      コールバックとサービスの停止
     */
    private void removeCallback() {
        if(mRunnable != null) {
            mHandler.removeCallbacks(mRunnable);
            stopForeground(true);
            mIsAlive = false;
        }
    }

    /**
     *      コールバック有効・無効
     * @param availability trueで時間の通知有効
     */
    public static void setCallbackAvailability(boolean availability) {
        mCallbackAvailability = availability;
    }

    /**
     * 			リスナーに登録
     */
    public static void setOnTimeChangedListener( TimeChangedListener obj ) {
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