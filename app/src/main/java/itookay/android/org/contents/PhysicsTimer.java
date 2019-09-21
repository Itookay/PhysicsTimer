package itookay.android.org.contents;

import android.content.Context;
import android.content.Intent;
import android.view.SurfaceView;

import itookay.android.org.debug.Debug;
import itookay.android.org.style.StyleBase;
import itookay.android.org.font.FontBase;
import org.jbox2d.common.Vec2;

import java.io.Serializable;

/**
 *      TimeWatchingServiceに渡す情報
 */
class ServiceInfo implements Serializable {
    public Time             Time = null;
    public boolean          BindService = true;
}

public class PhysicsTimer implements TimeChangedListener {

    /** 端末向き：センサー値が方向検出範囲外 */
    public static final int     ORIENTATION_RANGE_OUT = -1;
    /** 端末向き：端末上が上 */
    public static final int     PORTRAIT = 50;
    /** 端末向き：端末左が上 */
    public static final int     LEFT_LANDSCAPE = 60;
    /** 端末向き：端末右が上 */
    public static final int     RIGHT_LANDSCAPE = 70;
    /** 端末向き：端末下が上 */
    public static final int     UPSIDE_DOWN = 80;

    /** タイマーが稼働中 */
    public static final int     STATE_PROCESSING = 1001;
    /** タイマーが終了してアラームが鳴っている */
    public static final int     STATE_ALARMING = 1002;
    /** Numpadを表示して待機中 */
    public static final int     STATE_IDLING = 1003;

    /** TimeWatchingServiceインテントキー */
    public static final String  INTENT_KEY_TIME_WATCHING_SERVICE = "itookay.android.org.contents.PhysicsTimer.ServiceIntentKey";

    /** アプリケーションコンテキスト */
    private Context			mAppContext = null;

    /** 文字盤 */
    private Dial			mDial = null;
    /** 描画用サーフェースビュー */
    private MainSurfaceView				mMainSurface = null;
    /** R.layout.main_activityから取得したSurfaceView */
    private SurfaceView     mSurfaceViewFromLayout = null;
    /** ワールド管理 */
    private ControlWorld    mControlWorld = null;
    /** フォント */
    private FontBase		mFont = null;
    /** 表示スタイル */
    private StyleBase       mStyle = null;
    /** 端末向き */
    private int             mOrientation = 0;
    /** タイマーをサービスで起動 */
    private boolean         mBindService = true;
    /** タイマータイム */
    private Time            mTime = null;
    /** 現在の状態 */
    private static int      mState = STATE_IDLING;

    /**
     * 			コンストラクタ
     */
    public PhysicsTimer(Context appContext) {
        mAppContext = appContext;
    }

    public void setStyle(StyleBase style) {
        mStyle = style;
        if(mDial != null) {
            mDial.setStyle(mStyle);
            mDial.setTimerSize();
        }
    }

    public void setFont(FontBase font) {
        mFont = font;
        if(mDial != null) {
            mDial.setFont(mFont);
        }
    }

    /**
     * 			時計を生成して表示
     */
    public void init() {
        if( mFont == null && mAppContext == null ) {
            return;
        }

        /* -----------------------------------------------------------------
         * 【注意】ここでDial->ControlWorld->MainSurfaceの順番を変えると起動しない
         */
        mDial = new Dial();
        mDial.setStyle(mStyle);
        mDial.setFont(mFont);
        mDial.setTimerSize();
        mDial.initDialPanel();

        Vec2	gravity = new Vec2(0f, -10f);
        mControlWorld = new ControlWorld(mAppContext, gravity, true);
        mControlWorld.setStep(1f/60f, 10, 8);
        mControlWorld.createWorld(mStyle.getSmallTileCount(mFont), mStyle.getNormalTileCount(mFont));

        mMainSurface = new MainSurfaceView(mAppContext, mSurfaceViewFromLayout, mControlWorld);
        mMainSurface.setDebugDial(mDial);
        /* ----------------------------------------------------------------- */
    }

    public static int getState() {
        Debug.calledLog(2);
        Debug.timerStateLog(mState, true);
        return mState;
    }

    public static void setState(int state) {
        mState = state;
        Debug.calledLog(2);
        Debug.timerStateLog(mState, false);
    }

    public int getOrientation() {
        return mOrientation;
    }

    /**
     *      タイマーをサービスで起動する<br>デフォルトではtrue
     * @param bindService falseでサービスなし
     */
    public void bindService(boolean bindService) {
        mBindService = bindService;
    }

    /**
     *      描画を無効化<br>タイル解放->再拘束
     */
    public void invalidateDrawing() {
        mDial.clearTime(true);
        mControlWorld.clearTime();
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        mSurfaceViewFromLayout = surfaceView;
    }

    /*
     *      スタート時間をセット
     */
    public void setTime(int hour, int minute, int second) {
        Debug.calledLog();
        Debug.log(hour + ":" + minute + ":" + second);

        try {
            mTime = new Time(hour, minute, second);
            /* グラフィカル領域にタイマー表示 */
            TimeWatchingService.setOnTimeChangedListener(this);
            mDial.setTime(mTime);
            mControlWorld.invalidate(mDial);
        }
        catch (Exception ex) {
        }
    }

    public Time getTime() {
        return mTime;
    }

    /**
     *      タイマースタート<br>
     *      サービスを使うかどうかはbindServiceによる
     */
    public void start() {
        Debug.calledLog();

        setState(STATE_PROCESSING);

        if(mBindService) {
            startWithForegroundService();
        }
        else {
            startWithoutService();
        }
    }

    /**
     *      タイマーをForegroundServiceで起動
     */
    private void startWithForegroundService() {
        Debug.calledLog();

        Intent  service = new Intent(mAppContext, TimeWatchingService.class);
        service.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        ServiceInfo     info = new ServiceInfo();
        info.Time = mTime;
        info.BindService = mBindService;
        service.putExtra(INTENT_KEY_TIME_WATCHING_SERVICE, info);

        TimeWatchingService.setOnTimeChangedListener(this);
        mAppContext.startForegroundService(service);
    }

    /**
     *      タイマーをサービスなしで起動
     */
    private void startWithoutService() {
        TimeWatchingService     timeWatching = new TimeWatchingService();
        timeWatching.setTime(mTime);
        timeWatching.startTimer(false);
    }

    /**
     *      タイマーを停止
     */
    public void stop() {
        TimeWatchingService.stopTimer();

        mControlWorld.clearTime();
        mDial.clearTime(false);
        mTime = new Time();

        setState(STATE_IDLING);
    }

    /**
     *      アラームを停止
     */
    public void stopAlarm() {
        TimeWatchingService.stopAlarm();

        mControlWorld.clearTime();
        mDial.clearTime(false);
        mTime = new Time();

        setState(STATE_IDLING);
    }

    /**
     *      タイマー描画再開
     */
    public void resume() {
        TimeWatchingService.setOnTimeChangedListener(this);
        TimeWatchingService.setCallbackAvailability(true);
        TimeWatchingService.showHeadUpNotification(false);
        mMainSurface.resumeDrawing();
    }

    /**
     *      タイマー描画停止(タイマー自体は停止しない)
     */
    public void pause() {
        TimeWatchingService.setCallbackAvailability(false);
        TimeWatchingService.showHeadUpNotification(true);
        mMainSurface.pauseDrawing();
    }

    /**
     * 			時間の更新
     */
    @Override
    public void onTimeChanged(int hour, int minute, int second) {
        try {
            if(second == TimeWatchingService.TIMER_FINISHED) {
                setState(STATE_ALARMING);
            }
            else {
                mDial.setTime(new Time(hour, minute, second));
                mControlWorld.invalidate(mDial);
            }
        }
        catch (Exception ex) {
            ex.getMessage();
        }
    }

    /*
    private void showDialog() {
        String mes = TimeWatchingService.showPassedTime();

        new AlertDialog.Builder(mAppContext)
                .setTitle("title")
                .setMessage(mes)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                })
                .show();

    }
    */

    public void setGravity(float x, float y) {
        mControlWorld.setGravity(x, y);
    }

    /**
     *      Dialの中のDialPanelのTileBaseを初期化<br>
     *      スタイルの更新時に使用する
     */
    public void invalidateDial() {
        mControlWorld.destroyTiles();

        mDial.setTimerSize();
        mDial.initDialPanel();
        mControlWorld.createWorld(mStyle.getSmallTileCount(mFont), mStyle.getNormalTileCount(mFont));
    }

    /**
     *      現在のフォントでDial高さが画面幅よりも大きければtrue<br>
     *      (端末横向きにしてタイマーの数字が入りきるか)
     * @return trueなら入りきらない
     */
    public boolean isDialHeightBiggerThanDisplayWidth() {
        float   displayWidth = Scale.getDisplayWidthMeter();
        float   dialHeight = mDial.getHeightWithSpace();

        if(displayWidth < dialHeight) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     *      端末向きをセット
     * @param orientation PhysicsTimer.PORTRAIT, LEFT_LANDSCAPE, RIGHT_LANDSCAPE, UPSIDE_DOWN
     */
    public void setOrientation(int orientation) {
        if(mOrientation == orientation) {
            return;
        }
        if(isDialHeightBiggerThanDisplayWidth()) {
            mOrientation = orientation;
            return;
        }

        float       deg = 0;
        switch(orientation) {
            case PORTRAIT:
                switch(mOrientation) {
                    case LEFT_LANDSCAPE:
                        deg = 90f;
                        break;
                    case RIGHT_LANDSCAPE:
                        deg = -90f;
                        break;
                    case UPSIDE_DOWN:
                        break;
                }
                break;
            case LEFT_LANDSCAPE:
                switch(mOrientation) {
                    case PORTRAIT:
                        deg = -90f;
                        break;
                    case RIGHT_LANDSCAPE:
                        break;
                    case UPSIDE_DOWN:
                        deg = -90f;
                        break;
                }
                break;
            case RIGHT_LANDSCAPE:
                switch(mOrientation) {
                    case LEFT_LANDSCAPE:
                        break;
                    case PORTRAIT:
                        deg = 90f;
                        break;
                    case UPSIDE_DOWN:
                        deg = 90f;
                        break;
                }
                break;
            case UPSIDE_DOWN:
                switch(mOrientation) {
                    case LEFT_LANDSCAPE:
                        deg = 90f;
                        break;
                    case RIGHT_LANDSCAPE:
                        deg = -90f;
                        break;
                    case PORTRAIT:
                        break;
                }
                break;
        }

        /* 共通処理 ------------------------ */
        invalidateDrawing();
        mStyle.setOrientation(orientation);
        mStyle.rotateDial(mDial.getDialPanelList(), deg);
        mOrientation = orientation;
        /* -------------------------------- */
    }
}
