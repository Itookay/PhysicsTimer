package itookay.android.org.contents;

import org.jbox2d.callbacks.QueryCallback;
import org.jbox2d.collision.AABB;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.Fixture;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.joints.MouseJoint;
import org.jbox2d.dynamics.joints.MouseJointDef;

import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;

public class ScreenTouch implements OnTouchListener {

	ControlWorld	mWorld = null;
	Scale			mScale = null;
	
	/** マウスジョイント */
	MouseJoint		mMouseJoint = null;

	/**
	 * 			コンストラクタ
	 */
	public ScreenTouch( ControlWorld world ) {

		mWorld = world;
	}
	
	public void setScale( Scale scale ) {
		
		mScale = scale;
	}

	@Override
	public boolean onTouch( View v, MotionEvent event ) {

		float	x = mScale.getWorldCoordinateX( event.getX() );
		float	y = mScale.getWorldCoordinateY( event.getY() );

		switch( event.getAction() ) {
			case MotionEvent.ACTION_DOWN :
//				touchStart( x, y );
				break;

			case MotionEvent.ACTION_MOVE :
//				touchMove( x, y );
				break;

			case MotionEvent.ACTION_UP :
//				touchEnd( x, y );
				break;

			default :
		}

		return true;
	}

	/**
	 * 			タッチスタート
	 */
	private void touchStart( final float x, final float y ) {

		final float		HEIGHT = 0.1f;

		if( mWorld.isAlive() == false ) return;

		AABB	aabb = new AABB();
		aabb.lowerBound.set( x - HEIGHT, y - HEIGHT );
		aabb.upperBound.set( x + HEIGHT, y + HEIGHT );
		Log.d( "wp", "touch start (" + Float.toString( x ) + ", " + Float.toString( y ) + ")" );

		final World	world = mWorld.getWorld();
		world.queryAABB( new QueryCallback() {

			@Override
			public boolean reportFixture( Fixture fixture ) {

				Body		body = null;
				if( fixture.getBody().m_userData instanceof DrawableBody ) {
					body = fixture.getBody();

					MouseJointDef	mouseDef = new MouseJointDef();
					mouseDef.bodyA = world.createBody( new BodyDef() );
					mouseDef.bodyB = body;
					mouseDef.target.set( x, y );
					mouseDef.maxForce = 1000f * body.getMass();
					mMouseJoint = (MouseJoint)world.createJoint( mouseDef );

					Log.d( "wp", "body chatch! " + body.m_userData.toString() );
				}

				return false;
			}
		}, aabb );

	}

	/**
	 * 			タッチムーブ
	 */
	private void touchMove( float x, float y ) {

		if( mWorld.isAlive() == false ) return;
		
		if( mMouseJoint != null ) {
			mMouseJoint.setTarget( new Vec2( x, y ) );
		}
	}

	/**
	 * 			タッチエンド
	 */
	private void touchEnd( float x, float y ) {

		if( mWorld.isAlive() == false ) return;
		
		if( mMouseJoint != null ) {
			mWorld.getWorld().destroyJoint( mMouseJoint );
			mMouseJoint = null;
		}
	}
}






