package itookay.android.org.contents;

import android.content.Context;
import android.content.Intent;
import android.view.SurfaceView;

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

    /** 端末向き：端末上が上 */
    public static final int     PORTRAIT = 50;
    /** 端末向き：端末左が上 */
    public static final int     LEFT_LANDSCAPE = 60;
    /** 端末向き：端末右が上 */
    public static final int     RIGHT_LANDSCAPE = 70;
    /** 端末向き：端末下が上 */
    public static final int     UPSIDEDOWN = 80;

    /** タイマーが稼働中 */
    public static final int     STATE_PROCESSING = 1001;
    /** タイマーが終了してNumpad画面に戻るのを待っている */
    public static final int     STATE_FINISHED = 1002;
    /** 待機中 */
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
    /** タイマーの初期化が全て終了 */
    //private boolean         mIsReadyToStart = false;
    /** タイマーをサービスで起動 */
    private boolean         mBindService = true;
    /** タイマータイム */
    private Time            mTime = null;
    /** 現在の状態 */
    private int             mState = STATE_IDLING;

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

    public int getState() {
        return mState;
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

    /**
     *      タイマースタート<br>
     *      サービスを使うかどうかはbindServiceによる
     */
    public void start() {
        mState = STATE_PROCESSING;

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
     *          タイマーを強制終了
     */
    public void stop() {
        mState = STATE_IDLING;
        TimeWatchingService.stopTimer();
        mControlWorld.clearTime();
        mDial.clearTime(false);
    }

    /**
     *      タイマー描画再開
     */
    public void resume() {
        TimeWatchingService.setOnTimeChangedListener(this);
        TimeWatchingService.setCallbackAvailability(true);
        mMainSurface.resumeDrawing();
    }

    /**
     *      タイマー描画停止(タイマー自体は停止しない)
     */
    public void pause() {
        TimeWatchingService.setCallbackAvailability(false);
        mMainSurface.pauseDrawing();
    }

    /**
     * 			時間の更新
     */
    @Override
    public void onTimeChanged(int hour, int minute, int second) {
        try {
            if(second == TimeWatchingService.TIMER_FINISHED) {
                mState = STATE_FINISHED;
                //何もしない
            }
            else {
                mDial.setTime(new Time(hour, minute, second));
                mControlWorld.invalidate(mDial);
            }
        }
        catch (Exception ex) {
        }
    }

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
     *      端末向きをセット
     * @param orientation PhysicsTimer.PORTRAIT, LEFT_LANDSCAPE, RIGHT_LANDSCAPE, UPSIDEDOWN
     */
    public void setOrientation(int orientation) {
        if(mOrientation == orientation) {
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
                    case UPSIDEDOWN:
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
                    case UPSIDEDOWN:
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
                    case UPSIDEDOWN:
                        deg = 90f;
                        break;
                }
                break;
            case UPSIDEDOWN:
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
