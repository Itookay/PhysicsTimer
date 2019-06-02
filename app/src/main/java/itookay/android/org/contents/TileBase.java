package itookay.android.org.contents;

import org.jbox2d.common.Vec2;

/**
 * 			グラウンド上のボディを設置するべき場所を保持する。
 * 			タイルがなくても，ジョイント位置，パネル，インデックスなどを保持する。
 * 			タイルとは一対一対応
 */
class TileBase {

	/** 無効なIDもしくはIndex */
	public static final int		INVALID_ID = -1;

	/** ジョイントのワールド座標１ */
	private Vec2		mWorldJointPos1 = new Vec2();
	/** ジョイントのワールド座標２ */
	private Vec2		mWorldJointPos2 = new Vec2();
	/** パネルID */
	private int			mPanelId = -1;
	/** パネル内の配列インデックス */
	private int			mIndex = -1;

	public TileBase( int panelId, int index ) {
		mPanelId = panelId;
		mIndex = index;
	}

	public void setWorldJointPos1( Vec2 jointPos ) {
		mWorldJointPos1.set( jointPos );
	}

	public void setWorldJointPos2( Vec2 jointPos ) {
		mWorldJointPos2.set( jointPos );
	}

	public Vec2 getWorldJointPos1() {
		return mWorldJointPos1;
	}

	public Vec2 getWorldJointPos2() {
		return mWorldJointPos2;
	}

	public int getIndex() {
		return mIndex;
	}

	public int getPanelId() {
		return mPanelId;
	}

	public void setPanelId( int id ) {
		mPanelId = id;
	}

	public void setIndex( int index ) {
		mIndex = index;
	}

	/**
	 * 			ジョイントアンカーを引数でオフセットする
	 */
	public void setOffset( float x, float y ) {
		mWorldJointPos1.x += x;
		mWorldJointPos1.y += y;

		mWorldJointPos2.x += x;
		mWorldJointPos2.y += y;
	}
}
