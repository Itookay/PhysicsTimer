package itookay.android.org.contents;

import java.util.ArrayList;

import android.graphics.*;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.*;
import org.jbox2d.dynamics.joints.DistanceJointDef;
import org.jbox2d.dynamics.joints.Joint;
import org.jbox2d.dynamics.joints.JointEdge;

import android.content.Context;

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

    /** ボディとジョイントするグラウンド */
    private Body		mGround = null;
    /** タイルのリスト */
    private BodyList    mBodyList = new BodyList();

    /** 拘束タイルグループインデックス **/
    private final int   STRAIN_TILE_GROUPINDEX = 2;
    /** 自由タイルグループインデックス **/
    private final int   FREE_TILE_GROUPINDEX = 4;
    /** グラウンドグループインデックス **/
    private final int   GROUND_GROUPINDEX = 4;

    /** 重力加速度 */
    public static final float       GRAVITY = 9.8f;

    /**
     * 			コンストラクタ
     */
    ControlWorld(Context appContext, Vec2 gravity, boolean doSleep) {
        mWorld = new World(gravity, doSleep);
        mAppContext = appContext;
    }

    World getWorld() {
        return mWorld;
    }

    /**
     * 			stepの引数をセット
     */
    void setStep( float step, int velocityIterations, int positionIterations ) {
        mStep = step;
        mVelocityIterations = velocityIterations;
        mPositionIterations = positionIterations;
    }

    float getStep() {
        return mStep;
    }

    /**
     *          重力をセット
     * @param x 画面中心原点、右向き正
     * @param y 画面中心原点、上向き正
     */
    void setGravity(float x, float y) {
        //強さを調整
        x *= 2f;
        y *= 2f;

        ArrayList   list = mBodyList.getList();
        for(Object body : list) {
            ((Body)body).setAwake(true);
        }
        mWorld.setGravity(new Vec2(x, y));
    }

    /**
     * 			ワールド上にグラウンドとボディを生成<br>
     * 			ボディの配置はsetTime()の引数，グラウンドの大きさはsetScale()による<br>
     */
    void createWorld(int smallTileCount, int normalTileCount) {
        createGround();

        //ちいさいの
        for (int i = 0; i < smallTileCount; i++) {
            createTile(Tile.SMALL);
        }
        //ふつうの
        for (int i = 0; i < normalTileCount; i++) {
            createTile(Tile.NORMAL);
        }
    }

    /**
     * 			タイルを作成
     * @param sizeFormat タイルサイズ
     */
    private void createTile(int sizeFormat) {
        BodyDef		bodyDef = new BodyDef();
        Vec2		pos = new Vec2();

        //画面上のランダムな位置に配置
        pos.x = (float)(Scale.getDisplayWidthMeter() * Math.random());
        pos.y = (float)(Scale.getDisplayHeightMeter() * Math.random());

        Tile	tile = createUserData(pos);
        tile.setSizeFormat(sizeFormat);

        bodyDef.type = BodyType.DYNAMIC;
        bodyDef.position.set(tile.getPosition());
        bodyDef.angle = 0f;
        bodyDef.userData = tile;
        Body body = mWorld.createBody(bodyDef);

        PolygonShape	boxShape = new PolygonShape();
        //setAsBoxにはサイズの半分の値を渡す
        boxShape.setAsBox(tile.getSize() / 2f, tile.getSize() / 2f);

        FixtureDef		boxFixture = new FixtureDef();
        boxFixture.shape = boxShape;
        boxFixture.density = tile.getDensity();
        boxFixture.friction = tile.getFriction();
        boxFixture.restitution = tile.getRestitution();

        //接触条件をフィルタ
        boxFixture.filter.groupIndex = FREE_TILE_GROUPINDEX;

        body.createFixture(boxFixture);
        mBodyList.add(body);
    }

    /**
     *      ワールド内のタイルを全て削除
     */
    public void destroyTiles() {
        for(Body body : mBodyList.getList()) {
            body.setUserData(null);
            mWorld.destroyBody(body);
        }
        mBodyList.getList().clear();
    }

    /**
     * 			タイルのユーザーデータを生成
     */
    private Tile createUserData(Vec2 pos) {
        Tile	tile = new Tile();
        tile.setPosition(pos);
        tile.setUniqueId(TileBase.INVALID_ID, TileBase.INVALID_ID);
        tile.createTileBitmap(mAppContext.getResources(), Tile.COLOR_BLUE);

        return tile;
    }

    /**
     *          グラウンドを生成
     * 		    画面左上が原点(0,0)
     */
    private void createGround() {
        float	density = 1.0f;

        float	width = Scale.getDisplayWidthMeter();
        float	height = Scale.getDisplayHeightMeter();
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
     *      不必要なタイルをリリースし、必要なタイルをジョイントする
     */
    void invalidate(Dial dial) {
        for(DialPanel panel : dial.getDialPanelList()) {
            //タイルを開放
            for(int index : panel.getDestroyTileList()) {
                releaseTile(panel.getId(), index);
            }
            panel.getDestroyTileList().clear();

            //タイルを拘束
            for(int index : panel.getJointTileList()) {
                restrainTile(panel, index);
            }
            panel.getJointTileList().clear();
        }

        mWorld.clearForces();
    }

    /**
     * 			タイルを文字盤に拘束
     */
    private void restrainTile(DialPanel panel, int index) {
        TileBase	tileBase = panel.getTileBase(index);

        DistanceJointDef	jointDef = new DistanceJointDef();
        Body	body = null;
        Tile	tile = null;

        //空いてるタイルを取得
        body = mBodyList.getNext(panel.getSizeFormat());
        //接触条件フィルタを変更
        setRestrainTileFilter(body);

        tile = (Tile)body.getUserData();
        tile.setUniqueId(panel.getId(), index);

        //ジョイント１　左側
        jointDef.bodyA = mGround;
        jointDef.bodyB = body;
        jointDef.localAnchorA.set(tileBase.getWorldJointPos1());
        jointDef.localAnchorB.set(tile.getJointAnchorPosition1());
        jointDef.length = 0f;
        mWorld.createJoint(jointDef);

        //ジョイント２　右側
        jointDef.bodyA = mGround;
        jointDef.bodyB = body;
        jointDef.localAnchorA.set(tileBase.getWorldJointPos2());
        jointDef.localAnchorB.set(tile.getJointAnchorPosition2());
        jointDef.length = 0f;
        mWorld.createJoint(jointDef);
    }

    /**
     * 			ボディを文字盤から開放
     */
    private void releaseTile(int panelId, int index) {
        if(mWorld == null) return;

        Tile	tile = null;
        for(Body body : mBodyList.getList()) {
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
    void step() {
        if( mStep != 0 && mWorld != null ) {
            mWorld.step( mStep, mVelocityIterations, mPositionIterations );
            mWorld.clearForces();
        }
    }

    /**
     * 			ボディを描画
     */
    void drawBodies(Canvas canvas) {
        for(Body body : mBodyList.getList()) {
            Vec2	pos = body.getPosition();
            Tile    tile = (Tile)body.m_userData;
            float   size = tile.getSize();
            Bitmap  bitmap = tile.getBitmap();

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
     *      ControlWorldが保持している時間情報を初期化【Dial.clearTime()も呼ぶこと】<br>
     *      ・ディスタンス・ジョイントを全消去<br>
     *      ・TileのIDをクリア<br>
     */
    public void clearTime() {
        for(Joint joint = mWorld.getJointList(); joint != null; joint = joint.getNext()) {
            mWorld.destroyJoint(joint);
        }
        for(Body body : mBodyList.getList()) {
            Tile    tile = (Tile)body.getUserData();
            tile.setUniqueId(TileBase.INVALID_ID, TileBase.INVALID_ID);
            setFreeTileFilter(body);
        }
    }

    /**
     * 			ジョイントを描画(デバッグ用)
     */
    public void debugDraw(Canvas canvas) {
        Vec2	start = new Vec2();
        Vec2	end = new Vec2();
        Paint	paint = new Paint();
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

    private Dial        mDebugDial = null;
    void setDebugDial(Dial dial) {
        mDebugDial = dial;
    }
}






