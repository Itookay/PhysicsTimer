package itookay.android.org.contents;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Handler;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * 			描画用サーフェースビュー
 */
public class MainSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	/** タッチイベント管理 */
	private ScreenTouch		mTouch = null;
	/** サーフェースホルダー */
	private SurfaceHolder	mHolder = null;
	/** ワールド管理 */
	private ControlWorld	mWorld = null;
	/** 描画スレッド */
	private Runnable		mDrawRunnable = null;
	/** 描画用ハンドラ */
	private Handler			mHandler = null;

	/** 背景の設定 */
	private BackgroundAttribution		mBgAttr = null;
	/** 画面スケール */
	private Scale		mScale = null;

	/** 壁紙がアクティブ */
	private boolean		mVisibility = true;

	/**
	 * 			コンストラクタ
	 */
	public MainSurfaceView( Context appContext, SurfaceView surfaceView, ControlWorld world ) {
		super( appContext );

		//XMLレイアウトされたSurfaceViewに描画
		mHolder = surfaceView.getHolder();
		mWorld = world;

		mHandler = new Handler();
		mTouch = new ScreenTouch( mWorld );

		mHolder.addCallback( this );
		setOnTouchListener( mTouch );

		setDisplaySize( appContext );
	}

	public void setBackground( BackgroundAttribution bgAttr ) {
		mBgAttr = bgAttr;
	}

	/**
	 * 			画面サイズを取得
	 */
	private void setDisplaySize( Context appContext ) {
	}

	/**
	 * 			サーフェース生成
	 */
	@Override
	public void surfaceCreated( SurfaceHolder holder ) {
		mDrawRunnable = new DrawCanvas();
		mDrawRunnable.run();
	}

	/**
	 * 			サーフェース変更
	 */
	@Override
	public void surfaceChanged( SurfaceHolder holder, int format, int width, int height ) {
	}

	/**
	 * 			サーフェース破棄
	 * @param holder
	 */
	@Override
	public void surfaceDestroyed( SurfaceHolder holder ) {
		removeCallbacks( mDrawRunnable );
	}

	/* ---------------------------------------------------------- */
	/**
	 * 			描画クラス
	 */
	public class DrawCanvas implements Runnable {
		/** 次の呼び出し時間 */
		public final int		DELAY_TIME = 10;

		/**
		 * 			コンストラクタ
		 */
		public DrawCanvas() {
		}

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

			Matrix	canvasMatrix = new Matrix();
			//画面上方向が正にする
			canvasMatrix.setScale(1, -1);
            canvasMatrix.preTranslate(0, -mScale.getDisplayHeightPixel());
			canvas.setMatrix(canvasMatrix);

			//背景の描画
			drawBackground(canvas);
			//ボディの描画
			mWorld.drawBodies(canvas);
			//ジョイントの描画
			//mWorld.drawJoints(canvas);

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
		private void drawBackground( Canvas canvas ) {
			if( mBgAttr.getColor() != BackgroundAttribution.INVALID_COLOR ) {
				canvas.drawColor( mBgAttr.getColor() );
			}
			else if( mBgAttr.getImage() != null ) {
				Bitmap	image = mBgAttr.getImage();
				Matrix	matrix = new Matrix();
				float	cx = image.getWidth() / 2f;
				float	cy = image.getHeight() / 2f;
				matrix.setTranslate( -cx, cy );
				matrix.preScale( 1f/mBgAttr.getScale(), -1f/mBgAttr.getScale(), cx, cy );
				canvas.drawBitmap( image, matrix, null );
			}
		}
	}

	/**
	 * 			画面スケールをセット
	 */
	public void setScale( Scale scale ) {
		mScale = scale;
		mTouch.setScale( mScale );
	}
}







