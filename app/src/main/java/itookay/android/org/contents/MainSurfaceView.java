package itookay.android.org.contents;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 			描画用サーフェースビュー
 */
public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    /** サーフェースホルダー */
    private SurfaceHolder	mHolder = null;
    /** ワールド管理 */
    private ControlWorld	mWorld = null;
    /** 描画スレッド */
    private Runnable		mDrawRunnable = null;
    /** 描画用ハンドラ */
    private Handler			mHandler = null;

    /** 壁紙がアクティブ */
    private boolean 		mVisibility = true;

    /**
     * 			コンストラクタ
     */
    public MainSurfaceView(Context appContext, SurfaceView surfaceView, ControlWorld world) {
        super(appContext);

        //XMLレイアウトされたSurfaceViewに描画
        mHolder = surfaceView.getHolder();
        mWorld = world;

        mHandler = new Handler();
        mHolder.addCallback(this);
    }

    /**
     * 			サーフェース生成
     */
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mDrawRunnable = new DrawCanvas();
        mDrawRunnable.run();
    }

    /**
     * 			サーフェース変更
     */
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    /**
     * 			サーフェース破棄
     */
    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        removeCallbacks(mDrawRunnable);
    }

    /**
     *      サーフェース描画を停止
     */
    public void pauseDrawing() {
        mVisibility = false;
    }

    /**
     *      サーフェース描画を再開
     */
    public void resumeDrawing() {
        if(mDrawRunnable == null) {
            mDrawRunnable = new DrawCanvas();
        }
        mVisibility = true;
        mDrawRunnable.run();
    }

    /* ---------------------------------------------------------- */
    /**
     * 			描画クラス
     */
    public class DrawCanvas implements Runnable {
        /** 次の呼び出し時間 */
        public final int		DELAY_TIME = Math.round(mWorld.getStep() * 1000);

        @Override
        public void run() {
            draw();
        }

        /**
         * 			描画メソッド
         */
        public void draw() {
            mWorld.step();

            Canvas		canvas = mHolder.lockCanvas();
            if(canvas == null) {
                return;
            }

            /*
                Canvasのデフォルト原点は左上、x軸：右が正、y軸：下が正
                Worldのデフォルト原点は左下、x軸：右が正、y軸：上が正
                  -> Canvasの座標系を変換してWorld座標系と一致させる
             */
            Matrix	canvasMatrix = new Matrix();
            canvasMatrix.setScale(1, -1);
            canvasMatrix.preTranslate(0, -Scale.getDisplayHeightPixel());
            canvas.setMatrix(canvasMatrix);

            //背景の描画
            drawBackground(canvas);
            //ボディの描画
            mWorld.drawBodies(canvas);
            //デバッグ用描画
            mWorld.debugDraw(canvas);

            mHolder.unlockCanvasAndPost(canvas);

            //次の描画をセット
            mHandler.removeCallbacks(mDrawRunnable);
            if(mVisibility) {
                mHandler.postDelayed(mDrawRunnable, DELAY_TIME);
            }
        }

        /**
         * 			背景を描画
         */
        private void drawBackground(Canvas canvas) {
            canvas.drawColor(Color.WHITE);
        }
    }
}







