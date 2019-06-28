package itookay.android.org.contents;

import org.jbox2d.dynamics.joints.DistanceJointDef;

/**
 * 			PreferenceActivityからの設定値
 */
public class Value {

	/* 初期値 */
	private static final float			JOINT_FRAQ = 4f;
	private static final float			DAMPING_RATIO = 0.7f;

	/** 文字盤を拘束する距離ジョイントの振動数 */
	public static float		mJointFreq = JOINT_FRAQ;
	/** 文字盤を拘束する距離ジョイントの減衰係数 */
	public static float		mDampingRatio = DAMPING_RATIO;

	/** タイルのサイズ */
//	public static float		mTileSize = Tile.SMALL;

	/**
	 * 			タイルを拘束するジョイントの値を取得
	 */
	public static void getTileRestrainJointValue( DistanceJointDef jointDef ) {

		jointDef.frequencyHz = mJointFreq;
		jointDef.dampingRatio = mDampingRatio;
	}
}
