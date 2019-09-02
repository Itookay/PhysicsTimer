package itookay.android.org.contents;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;

import itookay.android.org.MainActivity;
import itookay.android.org.R;
import itookay.android.org.setting.RingtoneList;
import itookay.android.org.setting.SetBackgroundNotificationDialog;
import itookay.android.org.setting.Settings;
import itookay.android.org.setting.VibrationList;

/**
 *      ForeGroundServiceで時間の経過を監視し単位秒ごとに通知する
 */
public class TimeWatchingService extends Service {

    private static Context  mContext = null;

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
    /** サービスの停止 */
    private static boolean         mStopService = false;
    /** ForegroundServiceで起動するか */
    private boolean         mBindService = true;

    /** 通知チャンネルID */
    private String          mChannelId = "";
    /**  */
    private int             mNotificationId = 1;

    /** 誤差修正用DateTime：秒 */
    private int             mSecondInStart = 0;
    /** 誤差修正用DateTime：ミリ秒 */
    private int             mMicroSecondInStart = 0;

    /** デバッグ用 */
    private static LocalDateTime mStartTime = null;

    public static void setContext(Context context) {
        mContext = context;
    }

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
        mChannelId = "PhysicsTimer_Channel";
        setNotificationChannel();

        startTimer(true);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        removeCallback();
    }

    public void setNotificationChannel() {
        NotificationManager     notificationMgr = getSystemService(NotificationManager.class);
        String      name = getString(R.string.notification_channel_name);
        String      description = "";
        if(notificationMgr.getNotificationChannel(mChannelId) == null) {
            NotificationChannel     channel = new NotificationChannel(mChannelId, name, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(description);
            //通知時に無音化(変更にはアプリの再インストールor新しいチャンネルID必要)
            channel.setSound(null, null);
            notificationMgr.createNotificationChannel(channel);
        }
    }

    /**
     *      Notificationを表示
     * @param title タイトル
     * @param text タイマーカウントを表示
     * @return
     */
    public Notification setNotification(String title, String text) {
        /* 通知をタップした時に起動するActivity */
        Intent      intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent   pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, mChannelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            //音・振動による通知は最初のみ
            .setOnlyAlertOnce(true);

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
    public void startTimer(boolean bindService) {
        mBindService = bindService;
        mCallbackAvailability = true;
        if(mObserver != null) {
            if(mBindService) {
                String      title = getText(R.string.notification_title).toString();
                startForeground(1, setNotification(title, ""));
            }

            /* 誤差調整用 */
            mSecondInStart = getNowSecond();
            mMicroSecondInStart = getNowMicroSecond();
            /* デバッグ用 */
            mStartTime = LocalDateTime.now();


            mRunnable.run();
            mIsAlive = true;
        }
    }

    /**
     *      現在時刻の秒を取得
     */
    private int getNowSecond() {
        return LocalDateTime.now().getSecond();
    }

    /**
     *      現在時刻のミリ秒を取得
     * @return
     */
    private int getNowMicroSecond() {
        int     nanoSecond = LocalDateTime.now().getNano();
        float   microSecond = nanoSecond / (1000 * 1000);
        return Math.round(microSecond);
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
                mObserver.onTimeChanged(0, 0, TIMER_FINISHED);
                startAlert();
            }
            else {
                if(mCallbackAvailability) {
                    mObserver.onTimeChanged(0, mMinute, mSecond);
                }

                /* Notificationの更新 */
                if(mBindService) {
                    String title = getString(R.string.notification_title).toString();
                    String text = getString(R.string.notification_text) + " " + Integer.toString(mMinute) + ":" + Integer.toString(mSecond);
                    NotificationManager notificationMgr = getSystemService(NotificationManager.class);
                    notificationMgr.notify(mNotificationId, setNotification(title, text));
                }

                /* 時間調整あり */
                int     delayTime = getNextDelayTime();
                mHandler.postDelayed(mRunnable, delayTime);
                /* 時間調整なし */
//                mHandler.postDelayed(mRunnable, DELAY_TIME);
            }
        }
    };

    /**
     *      誤差を調整したHandler呼び出しミリ秒を取得
     */
    private int getNextDelayTime() {
        int     secondInNow = getNowSecond();
        int     microSecondInNow = getNowMicroSecond();
        int     diff_second = secondInNow - mSecondInStart;

        int     diffMicroSecond = 0;
        switch(diff_second) {
            case 0:
                diffMicroSecond = 1 - microSecondInNow + mMicroSecondInStart;
                break;
            case 1:
                diffMicroSecond = - (microSecondInNow - mMicroSecondInStart);
                break;
            case 2:
                diffMicroSecond = - (1 - mMicroSecondInStart + microSecondInNow);
                break;
            default:
                diffMicroSecond = 0;
        }

        mSecondInStart = secondInNow;
        mMicroSecondInStart = microSecondInNow;

        Log.d("DELAY", Integer.toString(diffMicroSecond));

        return DELAY_TIME + diffMicroSecond;
    }

    /**
     *      デバッグ用
     */
    public static String showPassedTime() {
        int startHour = mStartTime.getHour();
        int startMinute = mStartTime.getMinute();
        int startSecond = mStartTime.getSecond();
        int startMicroSecond = Math.round(mStartTime.getNano() / (1000 * 1000));

        int nowHour = LocalDateTime.now().getHour();
        int nowMinute = LocalDateTime.now().getMinute();
        int nowSecond = LocalDateTime.now().getSecond();
        int nowMicroSecond = Math.round(LocalDateTime.now().getNano() / (1000 * 1000));

        String start = startHour + ":" + startMinute + ":" + startSecond + "." + startMicroSecond + System.getProperty("line.separator");
        String now = nowHour + ":" + nowMinute + ":" + nowSecond + "." + nowMicroSecond;

        return start + now;
    }

    /**
     *      コールバックとサービスの停止
     */
    private void removeCallback() {
        if(mRunnable != null) {
            if(mBindService) {
                stopForeground(true);
            }
            mHandler.removeCallbacks(mRunnable);
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

    /**
     *      サウンドとバイブレーションでアラート
     */
    private void startAlert() {
        int     soundIndex = Settings.getSavedRingtoneIndex(mContext);
        int     vibrationIndex = Settings.getSavedVibrationIndex(mContext);

        try {
            RingtoneList.start(mContext, soundIndex, true);
        }
        catch(Exception e) {
        }

        VibrationList.vibrate(mContext, vibrationIndex, VibrationList.REPEAT);
    }

    /**
     *      アラートを停止
     */
    public static void stopAlert() {
        RingtoneList.stop();
        VibrationList.stop();
    }
}