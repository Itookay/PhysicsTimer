package itookay.android.org.contents;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;

import itookay.android.org.MainActivity;
import itookay.android.org.R;
import itookay.android.org.debug.Debug;
import itookay.android.org.setting.RingtoneList;
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
    public static final int TIMER_FINISHED = -1;

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
    /** コールバック有効 */
    private static boolean  mCallbackAvailability = true;
    /** サービスの停止 */
    private static boolean  mStopService = false;
    /** ForegroundServiceで起動するか */
    private boolean         mBindService = true;

    /** 通知チャンネルID */
    private String          mProcessingChannelId = "";
    private String          mEndChannelId = "";
    /** 通知ID */
    private static final int    PROCESSING_NOTIFICATION_ID = 1001;
    private static final int    END_NOTIFICATION_ID = 1002;

    /** 誤差修正用DateTime：秒 */
    private int             mSecondInStart = 0;
    /** 誤差修正用DateTime：ミリ秒 */
    private int             mMicroSecondInStart = 0;

    /** 終了通知にヘッドアップ表示が必要か */
    private static boolean  mShowHeadUpNotification = false;

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
        Debug.calledLog();

        ServiceInfo    info = (ServiceInfo)intent.getSerializableExtra(PhysicsTimer.INTENT_KEY_TIME_WATCHING_SERVICE);
        setTime(info.Time);

        /* 通知を表示 */
        mProcessingChannelId = mContext.getString(R.string.notification_channel_timer_processing_id);
        setNotificationChannel();

        startTimer(true);

        return START_REDELIVER_INTENT;
    }

    @Override
    public void onDestroy() {
        removeCallback();
    }

    /**
     *      Notificationチャンネルの設定
     */
    public void setNotificationChannel() {
        NotificationManager     notificationMgr = getSystemService(NotificationManager.class);

        /* タイマー動作中の通知(フォアグラウンドサービス用) */
        String      name = getString(R.string.notification_channel_timer_processing_name);
        String      description = "";
        if(notificationMgr.getNotificationChannel(mProcessingChannelId) == null) {
            //(変更にはアプリの再インストールor新しいチャンネルID必要)
            NotificationChannel     channel = new NotificationChannel(mProcessingChannelId, name, NotificationManager.IMPORTANCE_LOW);
            channel.setDescription(description);
            channel.setSound(null, null);
            notificationMgr.createNotificationChannel(channel);
        }

        /* タイマー終了時の通知 */
        name = getString(R.string.notification_channel_timer_end_name);
        description = "";
        if(notificationMgr.getNotificationChannel(mEndChannelId) == null) {
            //(変更にはアプリの再インストールor新しいチャンネルID必要)
            NotificationChannel     channel = new NotificationChannel(mEndChannelId, name, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(description);
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
    private Notification setNotification(String channelId, String title, String text) {
        /* 通知をタップした時に起動するActivity */
        Intent      intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent   pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentIntent(pendingIntent)
            //音・振動による通知は最初のみ
            .setOnlyAlertOnce(true);

        return builder.build();
    }

    /**
     *      タイマー終了時にヘッドアップ通知を表示する
     */
    public static void showHeadUpNotification(boolean showHeadUp) {
        mShowHeadUpNotification = showHeadUp;
    }

    /**
     *      (タイマー起動中なら)タイマーの停止
     */
    public static void stopTimer() {
        if(PhysicsTimer.getState() == PhysicsTimer.STATE_PROCESSING) {
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
                String      title = getText(R.string.notification_timer_processing_title).toString();
                startForeground(PROCESSING_NOTIFICATION_ID, setNotification(mProcessingChannelId, title, ""));
            }

            /* 誤差調整用 */
            mSecondInStart = getNowSecond();
            mMicroSecondInStart = getNowMicroSecond();
            /* デバッグ用 */
            mStartTime = LocalDateTime.now();


            mRunnable.run();
            PhysicsTimer.setState(PhysicsTimer.STATE_PROCESSING);
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
     */
    private int getNowMicroSecond() {
        int     nanoSecond = LocalDateTime.now().getNano();
        float   microSecond = nanoSecond / (1000 * 1000);
        return Math.round(microSecond);
    }

    /**
     * 		スタート時間をセット
     */
    public void setTime(Time time) {
        Debug.calledLog();

        mMinute = time.getMinute();
        mSecond = time.getSecond();

        if(mMinute < 0) mMinute = 0;
        if(mSecond < 0) mSecond = 0;
        mActualSeconds = mMinute * 60 + mSecond;

        Debug.log(time.getHour() + ":" + mMinute + ":" + mSecond);
    }

    /**
     *      時間監視スレッド
     */
    protected Runnable		mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mStopService) {
                removeCallback();
                cancelNotification();
                mStopService = false;
                return;
            }

            //タイマーの終了
            if(!forward()) {
                removeCallback();
                //secondにTIMER_FINISHEDを渡して終了を通知
                mObserver.onTimeChanged(0, 0, TIMER_FINISHED);

                //サウンドとバイブレーション
                startAlarm();
                //ヘッドアップ通知を表示
                if(mShowHeadUpNotification) {
                    showEndingNotification();
                }
                //タイマー動作中の通知を削除
                cancelNotification();
            }
            else {
                if(mCallbackAvailability) {
                    mObserver.onTimeChanged(0, mMinute, mSecond);
                }

                /* Notificationの更新 */
                if(mBindService) {
                    String title = getString(R.string.notification_timer_processing_title).toString();
                    String text = getString(R.string.notification_timer_processing_text) + " " + Integer.toString(mMinute) + ":" + Integer.toString(mSecond);
                    NotificationManager notificationMgr = getSystemService(NotificationManager.class);
                    notificationMgr.notify(PROCESSING_NOTIFICATION_ID, setNotification(mProcessingChannelId, title, text));
                }

                int     delayTime = getNextDelayTime();
                mHandler.postDelayed(mRunnable, delayTime);
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
        }

        mSecondInStart = secondInNow;
        mMicroSecondInStart = microSecondInNow;

//        Log.d("DELAY", Integer.toString(diffMicroSecond));

        return DELAY_TIME + diffMicroSecond;
    }

    /**
     *      デバッグ用
     */
    /*
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
    */

    /**
     *      タイマー終了時のNotificationを表示
     */
    private void showEndingNotification() {
        NotificationManager     notificationMgr = mContext.getSystemService(NotificationManager.class);
        /* タイマー終了の通知 */
        String  title = mContext.getString(R.string.notification_timer_end_title);
        Notification    notification = setNotification(mEndChannelId, title, "");
        notificationMgr.notify(END_NOTIFICATION_ID, notification);
    }

    /**
     *      コールバックとサービスの停止
     */
    private void removeCallback() {
        if(mRunnable == null) {
            return;
        }

        /* コールバックの除去 */
        if(mBindService) {
            stopForeground(true);
        }
        mHandler.removeCallbacks(mRunnable);

        /* Timerステータス変更 */
        if(PhysicsTimer.getState() == PhysicsTimer.STATE_FINISHED) {
            //カウントが終わりタイマーが正常終了した
            //ステータスは変更しない
        }
        else if(PhysicsTimer.getState() == PhysicsTimer.STATE_PROCESSING) {
            //タイマー動作中からストップボタンが押された
            PhysicsTimer.setState(PhysicsTimer.STATE_IDLING);
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
    private void startAlarm() {
        int     soundIndex = Settings.getSavedRingtoneIndex(mContext);
        int     vibrationIndex = Settings.getSavedVibrationIndex(mContext);

        try {
            if(soundIndex != 0) {
                RingtoneList.start(mContext, soundIndex, true);
            }
        }
        catch(Exception e) {
        }

        VibrationList.vibrate(mContext, vibrationIndex, VibrationList.REPEAT);
    }

    /**
     *      アラームを停止
     */
    public static void stopAlarm() {
        RingtoneList.stop();
        VibrationList.stop();
    }

    public static void cancelNotification() {
        NotificationManager     notificationMgr = mContext.getSystemService(NotificationManager.class);
        notificationMgr.cancel(PROCESSING_NOTIFICATION_ID);
    }
}