package itookay.android.org.contents;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceView;
import android.view.WindowManager;
import itookay.android.org.Style.StyleBase;
import itookay.android.org.Style.TwoRowsBigSecond;
import itookay.android.org.font.FontBase;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.World;

import java.util.ArrayList;

public class PhysicsTimer implements TimeChangedListener {

    /** 端末向き：端末上が上 */
    public static final int     PORTRAIT = 50;
    /** 端末向き：端末左が上 */
    public static final int     LEFT_LANDSCAPE = 60;
    /** 端末向き：端末右が上 */
    public static final int     RIGHT_LANDSCAPE = 70;
    /** 端末向き：端末下が上 */
    public static final int     UPSIDEDOWN = 80;

    /** アプリケーションコンテキスト */
    private Context			mAppContext = null;

    /** Timerサービスとバインドされているか */
    private boolean         mIsTimerServiceBound = false;
    /** TimeChanegedサービスインスタンス */
    private TimeChanged     mTimeChagedService = null;

    /** 文字盤 */
    private Dial			mDial = null;
    /** 描画用サーフェースビュー */
    private MainSurfaceView				mMainSurface = null;
    /** R.layout.main_activityから取得したSurfaceView */
    private SurfaceView     mSurfaceViewFromLayout = null;
    /** ワールド管理 */
    private ControlWorld	mWorld = null;
    /** スクリーン座標管理 */
    private Scale			mScale = null;
    /** フォント */
    private FontBase		mFont = null;
    /** 表示スタイル */
    private StyleBase       mStyle = null;
    /** 端末向き */
    private int             mOrientation = 0;

    /**
     * 			コンストラクタ
     */
    public PhysicsTimer(Context appContext) {
        mAppContext = appContext;
    }

    public void setStyle(StyleBase style) {
        mStyle = style;
    }

    public void setFont(FontBase font) {
        mFont = font;
    }

    public void setScale(Scale scale) {
        mScale = scale;
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
        mDial.setTimerSize(mScale.getDisplayWidthMeter());
        mDial.createDials(mScale.getDisplayWidthMeter(), mScale.getDisplayHeightMeter());

        Vec2	gravity = new Vec2(0f, -10f);
        mWorld = new ControlWorld(mAppContext, gravity, true);
        mWorld.setStep(1f/60f, 10, 8);
        mWorld.setScale(mScale);
        mWorld.createWorld(mStyle.getSmallTileCount(mFont), mStyle.getNomalTileCount(mFont));
        mWorld.setDebugDial(mDial);

        mMainSurface = new MainSurfaceView(mAppContext, mSurfaceViewFromLayout, mWorld);
        mMainSurface.setScale(mScale);
        /* ----------------------------------------------------------------- */

        Intent      intentService = new Intent(mAppContext, TimeChanged.class);
        mAppContext.bindService(intentService, mTimerConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     *          bindService用インナーコネクションクラス
     */
    private ServiceConnection mTimerConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mTimeChagedService = ((TimeChanged.LocalBinder)service).getService();
            mIsTimerServiceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mIsTimerServiceBound = false;
        }
    };

    /**
     *      端末向きをセット
     * @param orientation PhysicsTimer.PORTRAIT, LEFT_LANDSCAPE, RIGHT_LANDSCAPE, UPSIDEDOWN
     */
    public void setOrientation(int orientation) {
        if(mOrientation == orientation) {
            return;
        }

        switch(orientation) {
            case PORTRAIT:
                break;

            case LEFT_LANDSCAPE:
                mDial.clearTime(true);
                mWorld.clearTime();
                mStyle.setOrientation(LEFT_LANDSCAPE);
                mStyle.rotateDial(mDial.getDialPanelList(), -90f);
                break;

            case RIGHT_LANDSCAPE:
                break;

            case UPSIDEDOWN:
                //なにもしない
                break;
        }

        mOrientation = orientation;
    }

    public void setSurfaceView(SurfaceView surfaceView) {
        mSurfaceViewFromLayout = surfaceView;
    }

    /*
     *      スタート時間をセット
     */
    public void setTime(int hour, int minute, int second) {
        try {
            mDial.setTime(new Time(hour, minute, second));
            mTimeChagedService.setTime(hour, minute, second);
            mWorld.invalidate(mDial);
        }
        catch (Exception ex) {
        }
    }

    /**
     *          タイマースタート<br>
     *              IntentServiceを使ってTimeChangedをサービスに投げる
     */
    public void startTimer() {
        mTimeChagedService.setOnTimeChangedListener(this);
        mTimeChagedService.startTimer();
    }

    /**
     *          タイマーを強制終了
     */
    public void stopTimer() {
        mTimeChagedService.removeCallback();
        mWorld.clearTime();
        mDial.clearTime(false);
        mWorld.clearTileId();
    }


    /**
     * 			時間の更新
     */
    @Override
    public void onTimeChanged(int hour, int minute, int second) {
        try {
            if(second == TimeChanged.TIMER_FINISHED) {
                mWorld.clearTime();
            }
            else {
                mDial.setTime(new Time(hour, minute, second));
                mWorld.invalidate(mDial);
            }
        }
        catch (Exception ex) {
        }
    }

    /**
     * 			終了処理
     */
    public void Destroy() {
        if(mTimeChagedService != null) {
            //mWatchTime.removeCallback();
        }
    }

    /**
     * 			再開処理
     */
    public void Resume() {
        if(mTimeChagedService != null) {
            //mWatchTime.setOnTimeChangedListener(this);
        }
    }

    public Scale getScale(){
        return mScale;
    }

    public void setGravity(float x, float y) {
        mWorld.setGravity(x, y);
    }

    public ControlWorld getWorld() {
        return mWorld;
    }
}
