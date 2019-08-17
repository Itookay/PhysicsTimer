package itookay.android.org.contents;

import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

/**
 * 			描画用サーフェースビュー
 */
public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    /** サーフェースホルダー */
    private SurfaceHolder	mHolder = null;
    /** ワールド管理 */
    private ControlWorld    mControlWorld = null;
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
        mControlWorld = world;

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
        public final int		DELAY_TIME = Math.round(mControlWorld.getStep() * 1000);

        @Override
        public void run() {
            draw();
        }

        /**
         * 			描画メソッド
         */
        public void draw() {
            mControlWorld.step();

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
            drawBodies(canvas);
            //デバッグ用描画
            //mControlWorld.debugDraw(canvas);

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

        /**
         * 			ボディを描画
         */
        void drawBodies(Canvas canvas) {
            for(Body body : mControlWorld.getBodyList().getList()) {
                Vec2 pos = body.getPosition();
                Tile    tile = (Tile)body.m_userData;
                float   size = tile.getSize();
                Bitmap bitmap = tile.getBitmap();

                float	scale = Scale.toPixel(size) / bitmap.getWidth();
                Matrix matrix = new Matrix();
                matrix.setScale(scale, scale);

                float	x = Scale.toPixel(pos.x - size / 2f);
                float	y = Scale.toPixel(pos.y - size / 2f);
                matrix.postTranslate(x, y);

                float	deg = (float)Math.toDegrees(body.getAngle());
                matrix.preRotate(deg, bitmap.getWidth() / 2.0f, bitmap.getHeight() / 2.0f);

                canvas.drawBitmap(bitmap, matrix, null);
            }
        }

        /**
         * 			ジョイントを描画(デバッグ用)
         */
        public void debugDraw(Canvas canvas) {
            Vec2	start = new Vec2();
            Vec2	end = new Vec2();
            Paint paint = new Paint();
            paint.setColor( Color.BLACK );
            paint.setStrokeWidth( 1 );

            /* TileBaseのジョイントアンカーを表示 */
            Paint   paint2 = new Paint();
            for(DialPanel panel : mDebugDial.getDialPanelList()) {
                for(TileBase tileBase : panel.getTileBaseList()) {
                    paint2.setColor(Color.RED);
                    Vec2    pos1 = Scale.toPixel(tileBase.getWorldJointPos1());
                    Vec2    pos2 = Scale.toPixel(tileBase.getWorldJointPos2());
                    canvas.drawCircle(pos1.x, pos1.y, 10, paint2);
                    canvas.drawCircle(pos2.x, pos2.y, 10, paint2);
                }
            }
        }
    }

    private Dial        mDebugDial = null;
    void setDebugDial(Dial dial) {
        mDebugDial = dial;
    }

}







