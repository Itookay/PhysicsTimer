package itookay.android.org.contents;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.DistanceJoint;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointEdge;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

/**
 *
 */
public class ControlWorld {

    /** アプリケーション・コンテキスト */
    private Context		mAppContext = null;

    /** ワールド */
    private World		mWorld = null;
    /** ステップ時間 */
    private float		mStep = 0;
    /** 速度イテレーション */
    private int			mVelocityIterations = 0;
    /** 位置イテレーション */
    private int			mPositionIterations = 0;

    /** 時間管理 */
    private Dial		mDial = null;
    /** 画面スケール */
    private Scale		mScale = null;

    /** ボディとジョイントするグラウンド */
    private Body		mGround = null;
    /** タイルのリスト */
    private TileBodyList	mTileList = new TileBodyList();

    /** 拘束タイル衝突カテゴリbit */
    private final int   STRAIN_TILE_CAT = 0x0001;
    /** 拘束タイル衝突マスクbit */
    private final int   STRAIN_TILE_MASK = 0xFFFF;
    /** 自由タイル衝突カテゴリbit */
    private final int   FREE_TILE_CAT = 0x0001;
    /** 自由タイル衝突マスクbit */
    private final int   FREE_TILE_MASK = 0xFFFF;
    /** 拘束タイルグループインデックス **/
    private final int   STRAIN_TILE_GROUPINDEX = 2;
    /** 自由タイルグループインデックス **/
    private final int   FREE_TILE_GROUPINDEX = 4;
    /** グラウンドグループインデックス **/
    private final int   GROUND_GROUPINDEX = 4;

    /**
     * 			コンストラクタ
     */
    public ControlWorld(Context appContext, Vec2 gravity, boolean doSleep) {
        mWorld = new World(gravity, doSleep);
        mAppContext = appContext;
    }

    public World getWorld() {
        return mWorld;
    }

    /**
     * 			stepの引数をセット
     */
    public void setStep( float step, int velocityIterations, int positionIterations ) {
        mStep = step;
        mVelocityIterations = velocityIterations;
        mPositionIterations = positionIterations;
    }

    public float getStep() {
        return mStep;
    }

    /**
     * 			文字盤をセット
     */
    public void setDial(Dial dial) {
        mDial = dial;
    }

    /**
     *          重力をセット
     * @param x 画面中心原点、右向き正
     * @param y 画面中心原点、上向き正
     */
    public void setGravity(float x, float y) {
        //強さを調整
        x *= 2f;
        y *= 2f;

        ArrayList   list = mTileList.getList();
        for(Object body : list) {
            ((Body)body).setAwake(true);
        }
        mWorld.setGravity(new Vec2(x, y));
    }

    /**
     * 			ワールド上にグラウンドとボディを生成<br>
     * 			ボディの配置はsetTime()の引数，グラウンドの大きさはsetScale()による<br>
     */
    public void createWorld() {
        createGround();

        //作成するタイルの数
        int target = mDial.getFont().getArrayLength() * 4;
        for( int i = 0; i < target; i++ ) {
            createTile();
        }
    }

    /**
     * 			タイルを作成
     */
    private Body createTile() {
        BodyDef		bodyDef = new BodyDef();
        Vec2		pos = new Vec2();

        //画面上のランダムな位置に配置
        pos.x = (float)(mScale.getDisplayWidthMeter() * Math.random());
        pos.y = (float)(mScale.getDisplayHeightMeter() * Math.random());

        Tile	tile = getUserData(pos, TileBase.INVALID_ID, TileBase.INVALID_ID);

        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(tile.getPosition());
        bodyDef.angle = 0f;
        bodyDef.userData = tile;
        Body body = mWorld.createBody(bodyDef);

        PolygonShape	boxShape = new PolygonShape();
        //setAsBoxにはサイズの半分の値を渡す
        boxShape.setAsBox(Tile.getSize() / 2f, Tile.getSize() / 2f);

        FixtureDef		boxFixture = new FixtureDef();
        boxFixture.shape = boxShape;
        boxFixture.density = tile.getDensity();
        boxFixture.friction = tile.getFriction();
        boxFixture.restitution = tile.getRestitution();

        //接触条件をフィルタ
        boxFixture.filter.groupIndex = FREE_TILE_GROUPINDEX;

        body.createFixture( boxFixture );
        mTileList.add( body );

        return body;
    }

    /**
     * 			タイルのユーザーデータを取得
     */
    private Tile getUserData( Vec2 pos, int id, int index ) {
        Tile	tile = new Tile();
        tile.setPosition( pos );
        tile.setUniqueId( id, index );
        tile.createTileBitmap( mAppContext.getResources(), Tile.COLOR_BLUE );

        return tile;
    }

    /**
     *          グラウンドを生成
     * 		    画面左上が原点(0,0)
     */
    private void createGround() {
        float	density = 1.0f;

        float	width = mScale.getDisplayWidthMeter();
        float	height = mScale.getDisplayHeightMeter();
        Vec2	upperLeft = new Vec2(0, 0); //左上
        Vec2	upperRight = new Vec2(width, 0); //右上
        Vec2	lowerLeft = new Vec2(0, height); //左下
        Vec2	lowerRight = new Vec2(width, height); //右下

        Filter      filter = new Filter();
        filter.groupIndex = GROUND_GROUPINDEX;

        BodyDef		bodyDef = null;
        Fixture     fixture = null;

        //底面
        bodyDef = new BodyDef();
        PolygonShape	bottomShape = new PolygonShape();
        bottomShape.setAsEdge(lowerLeft, lowerRight);
        mGround = mWorld.createBody(bodyDef);
        fixture = mGround.createFixture(bottomShape, density);
        fixture.setFilterData(filter);

        //左壁
        bodyDef = new BodyDef();
        PolygonShape	leftShape = new PolygonShape();
        leftShape.setAsEdge(upperLeft, lowerLeft);
        fixture =  mWorld.createBody(bodyDef).createFixture(leftShape, density);
        fixture.setFilterData(filter);

        //右壁
        bodyDef = new BodyDef();
        PolygonShape	rightShape = new PolygonShape();
        rightShape.setAsEdge(upperRight, lowerRight);
        fixture =  mWorld.createBody(bodyDef).createFixture(rightShape, density);
        fixture.setFilterData(filter);

        //天井
        bodyDef = new BodyDef();
        PolygonShape	topShape = new PolygonShape();
        topShape.setAsEdge(upperLeft, upperRight);
        fixture =  mWorld.createBody(bodyDef).createFixture(topShape, density);
        fixture.setFilterData(filter);

    }

    /**
     * 			不必要なタイルをリリースし、必要なタイルをジョイントする
     */
    public void invalidate() {
        Iterator<DialPanel>		it = mDial.getDialPanelList().iterator();
        Iterator<Integer>		it2 = null;
        DialPanel	panel = null;
        int			index = 0;

        //タイルを開放
        while(it.hasNext()) {
            panel = it.next();

            it2 = panel.getDestroyTileList().iterator();
            index = 0;
            while(it2.hasNext()) {
                index = it2.next();
                releaseTile(panel.getId(), index);
            }
            panel.getDestroyTileList().clear();
        }

        //タイルを拘束
        it = mDial.getDialPanelList().iterator();
        while(it.hasNext()) {
            panel = it.next();

            it2 = panel.getJointTileList().iterator();
            while(it2.hasNext()) {
                index = it2.next();
                restrainTile(panel, index);
            }
            panel.getJointTileList().clear();
        }
    }

    /**
     * 			タイルを文字盤に拘束
     */
    private void restrainTile(DialPanel panel, int index) {
        TileBase	tileBase = panel.getTileBase(index);

        DistanceJointDef	jointDef = new DistanceJointDef();
        Body	body = null;
        Tile	tile = null;

        //空いてるタイル
        body = mTileList.getNext();
        //タイルが足りない
        if(body == null) {
            body = createTile();
        }

        //接触条件フィルタを変更
        setRestrainTileFilter(body);

        tile = (Tile)body.getUserData();
        tile.setUniqueId(panel.getId(), index);
        createJoint(jointDef, body, tileBase);
    }

    /**
     * 			ボディを文字盤から開放
     */
    private void releaseTile(int panelId, int index) {

        if(mWorld == null) return;

        Iterator<Body>		it = mTileList.iterator();
        Body	body = null;
        Tile	tile = null;
        while(it.hasNext()) {
            body = it.next();
            tile = (Tile)body.getUserData();
            if( tile.getPanelId() == panelId && tile.getIndex() == index ) {
                //ジョイントを削除
                for( JointEdge jointEdge = body.getJointList(); jointEdge != null; jointEdge = body.getJointList()) {
                    mWorld.destroyJoint( jointEdge.joint );
                    jointEdge.joint = null;
                }
                //タイルのユニークIDを無効化
                tile.setUniqueId( TileBase.INVALID_ID, TileBase.INVALID_ID );
                //接触条件フィルタを変更
                setFreeTileFilter(body);
            }
        }
    }

    /**
     * 			ジョイント(タイルをワールドに拘束する)を生成
     * @param
     */
    private void createJoint(DistanceJointDef jointDef, Body body, TileBase tileBase) {
        //ジョイント１　左側
        jointDef.bodyA = mGround;
        jointDef.bodyB = body;
        jointDef.localAnchorA.set(tileBase.getWorldJointPos1());
        jointDef.localAnchorB.set(Tile.getJointAnchorPosition1());
        jointDef.length = 0f;
        mWorld.createJoint(jointDef);

        //ジョイント２　右側
        jointDef.bodyA = mGround;
        jointDef.bodyB = body;
        jointDef.localAnchorA.set(tileBase.getWorldJointPos2());
        jointDef.localAnchorB.set(Tile.getJointAnchorPosition2());
        jointDef.length = 0f;
        mWorld.createJoint(jointDef);
    }

    /**
     *          拘束タイルの接触フィルタをセット
     * @param body
     */
    private void setRestrainTileFilter(Body body) {
        Filter      filter = new Filter();
        filter.groupIndex = STRAIN_TILE_GROUPINDEX;
        body.getFixtureList().setFilterData(filter);
    }

    /**
     *          自由タイルの接触フィルタをセット
     * @param body
     */
    private void setFreeTileFilter(Body body) {
        Filter      filter = new Filter();
        filter.groupIndex = FREE_TILE_GROUPINDEX;
        body.getFixtureList().setFilterData(filter);
    }

    /**
     * 			ワールドを進める
     */
    public void step() {

        if( mStep != 0 && mWorld != null ) {
            mWorld.step( mStep, mVelocityIterations, mPositionIterations );
            mWorld.clearForces();
        }
    }

    /**
     * 			ボディを描画
     */
    public void drawBodies(Canvas canvas) {

        Tile	tile = null;
        Iterator<Body>	it = mTileList.iterator();
        Body	body = null;
        while(it.hasNext()){
            body = it.next();
            tile = (Tile)body.m_userData;
            tile.drawBody(canvas, body);
        }
    }

    /**
     * 		画面スケールをセット
     */
    public void setScale(Scale scale) {
        mScale = scale;
    }

    /**
     * 			ワールドが使用可能である
     * @return
     */
    public boolean isAlive() {

        if( mWorld != null ) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * 			ディスタンス・ジョイントを全消去
     */
    public void destroyAllDistanceJoint() {
        for(Joint joint = mWorld.getJointList(); joint != null; joint = joint.getNext()) {
            mWorld.destroyJoint(joint);
        }

        ArrayList<Body>       bodyList = mTileList.getList();
        Tile	tile = null;
        for(Body body : bodyList) {
            tile = (Tile)body.getUserData();
            tile.setUniqueId(TileBase.INVALID_ID, TileBase.INVALID_ID);
            setFreeTileFilter(body);
        }
    }

    /**
     *      タイルのIDをクリア
     */
    public void clearTileId() {
        for(Body body : mTileList.getList()) {
            Tile    tile = (Tile)body.getUserData();
            tile.setUniqueId(Tile.INVALID_ID, Tile.INVALID_ID);
        }
    }

    /**
     * 			ジョイントを描画(デバッグ用)
     */
    public void drawJoints(Canvas canvas) {
        Vec2	start = new Vec2();
        Vec2	end = new Vec2();
        Paint	paint = new Paint();
        paint.setColor( Color.BLACK );
        paint.setStrokeWidth( 1 );
        for( Joint joint = mWorld.getJointList(); joint != null; joint = joint.getNext() ) {
            if( joint instanceof DistanceJoint ) {
                joint.getAnchorA( start );
                joint.getAnchorB( end );
                start = Scale.toPixel( start );
                end = Scale.toPixel( end );
                canvas.drawLine( start.x, start.y, end.x, end.y, paint );
                //Tailbase側
                canvas.drawCircle(start.x, start.y, 10, paint);
                //Tile側
                Paint   paint2 = new Paint();
                paint2.setColor(Color.RED);
                canvas.drawCircle(end.x, end.y, 10, paint2);
            }
        }

        /*
        //上横ライン
        canvas.drawLine(0, Scale.toPixel(mScale.getDisplayHeightMeter()-1f), mScale.getDisplayWidthPixel(), Scale.toPixel(mScale.getDisplayHeightMeter()-1f), paint);
        //左縦ライン
        float   startX = (mScale.getDisplayWidthPixel() - Scale.toPixel(mDial.getTimerWidth())) / 2f;
        canvas.drawLine(startX, 0, startX, mScale.getDisplayHeightPixel(), paint);
        //右縦ライン
        float   endX = startX + Scale.toPixel(mDial.getTimerWidth());
        canvas.drawLine(endX, 0, endX, mScale.getDisplayHeightPixel(), paint);
        */

        //Bitmapタイルサイズを表示
        //float pos = Scale.toPixel(Tile.getSize()) * 1f/4f;
        float pos = 0;
        //canvas.drawRect(pos, pos, Scale.toPixel(Tile.getSize()) + pos, Scale.toPixel(Tile.getSize()) + pos, paint);

        canvas.drawCircle(0, 0, 20, paint);
        canvas.drawCircle(mScale.getDisplayWidthPixel()/2f, mScale.getDisplayHeightPixel(), 20, paint);
    }
}






