package itookay.android.org.contents;

import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.joints.JointEdge;

/**
 *
 * 			タイルのボディリスト
 *
 */
class TileBodyList {

	/** タイルのボディリスト */
	private ArrayList<Body>		mTileBodyList = new ArrayList<Body>();

	public ArrayList<Body> getList() {

		return mTileBodyList;
	}

	public void add( Body body ) {

		mTileBodyList.add( body );
	}

	public void remove( Body body ) {

		mTileBodyList.remove( body );
	}

	public int size() {

		return mTileBodyList.size();
	}

	public Iterator<Body> iterator() {

		return mTileBodyList.iterator();
	}

	public Body get( int index ) {

		return mTileBodyList.get( index );
	}

	/**
	 * 			次に拘束するボディを取得
	 * @return		今までに拘束された回数が一番少ないボディを返す。アウェイクもする
	 */
	public Body getNext() {

//		Log.d( "phc", "TileBodyList#getNext" );

		int		count = Integer.MAX_VALUE;
		Body	minCountBody = null;

		Iterator<Body>		it = mTileBodyList.iterator();
		Body	body = null;
		Tile	tile = null;
		while(it.hasNext()) {
			body = it.next();
			tile = (Tile)body.getUserData();
			//パネルに所属していないタイル
			if( tile.getIndex() == TileBase.INVALID_ID && tile.getPanelId() == TileBase.INVALID_ID ) {
//				Log.d( "phc", "Restrain count : " + Integer.toString( tile.getRestrainCount() ) );
				if( tile.getRestrainCount() < count ) {
					count = tile.getRestrainCount();
					minCountBody = body;
				}
			}
		}

		if( minCountBody != null ) {
			minCountBody.setAwake( true );
			tile = (Tile)minCountBody.getUserData();
	//		Log.d( "phc", "Return tile count : " + Integer.toString( tile.getRestrainCount() ) );
			tile.addRestrainCount();
		}
		return minCountBody;
	}

	/**
	 * 			パネルに拘束されていないタイルの数を取得
	 */
	public int getFreeTileCount() {

		int		count = 0;
		Iterator<Body>		it = mTileBodyList.iterator();
		Tile	tile = null;
		while(it.hasNext()) {
			tile = (Tile)it.next().getUserData();
			if( tile.getPanelId() == TileBase.INVALID_ID && tile.getIndex() == TileBase.INVALID_ID ) {
				count++;
			}
		}

		return count;
	}

}
