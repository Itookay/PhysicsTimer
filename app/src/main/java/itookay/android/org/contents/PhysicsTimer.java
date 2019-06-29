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

    /**
     * 			コンストラクタ
     */
    public PhysicsTimer(Context appContext) {
        mAppContext = appContext;
    }

    public void setStyle(StyleBase style) {
        mStyle = style;
        mFont = style.getFont();
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

        mDial = new Dial();
        mDial.setScale(mScale);
        mDial.setStyle(mStyle);
        mDial.setTimerSizeScale();
        mDial.createDials();

        Vec2	gravity = new Vec2(0f, -10f);
        mWorld = new ControlWorld(mAppContext, gravity, true);
        mWorld.setStep(1f/60f, 10, 8);
        mWorld.setScale(mScale);
        mWorld.createWorld(mStyle.getSmallTileCount(), mStyle.getNomalTileCount());
        mWorld.setDebugDial(mDial);

        mMainSurface = new MainSurfaceView(mAppContext, mSurfaceViewFromLayout, mWorld);
        mMainSurface.setScale(mScale);

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
     *          タイマーの位置をセット
     */
    private void setDialPosition() {
        PointF      pos = new PointF();
        pos.x = (mScale.getDisplayWidthMeter() - mDial.getTimerWidth()) / 2;
        pos.y = mScale.getDisplayHeightMeter() * 0.8f;  //ひとまずここ

        //書き出し位置
        mDial.OffsetPosition(pos.x, pos.y);
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
        mWorld.destroyAllDistanceJoint();
        mDial.clearTime();
        mWorld.clearTileId();
    }


    /**
     * 			時間の更新
     */
    @Override
    public void onTimeChanged(int hour, int minute, int second) {
        try {
            if(second == TimeChanged.TIMER_FINISHED) {
                mWorld.destroyAllDistanceJoint();
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
