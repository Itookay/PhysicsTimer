package itookay.android.org.contents;

import android.graphics.Bitmap;

/**
 * 			背景の設定値
 */
class BackgroundAttribution {

	public static final int		INVALID_COLOR = 0;

	private int			mColor = 0;
	private Bitmap		mImage = null;
	private float		mBgScale = 1f;

	public void setColor( int color ) {

		mColor = color;
		if( mImage != null ) {
			mImage.recycle();
			mImage = null;
		}
	}

	public int getColor() {

		return mColor;
	}

	public void setImage( Bitmap image ) {

		mImage = image;
		mColor = INVALID_COLOR;
	}

	public Bitmap getImage() {

		return mImage;
	}

	public void setScale( float scale ) {

		mBgScale = scale;
	}

	public float getScale() {

		return mBgScale;
	}
}
