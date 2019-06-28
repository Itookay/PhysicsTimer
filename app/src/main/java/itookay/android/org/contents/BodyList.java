package itookay.android.org.contents;

import java.util.ArrayList;
import java.util.Iterator;

import org.jbox2d.dynamics.Body;

/**
 *
 * 			タイルのボディリスト
 *
 */
class BodyList {

	/** タイルのボディリスト */
	private ArrayList<Body> mBodyList = new ArrayList<Body>();

	ArrayList<Body> getList() {
		return mBodyList;
	}

	void add( Body body ) {
		mBodyList.add( body );
	}

	void remove( Body body ) {
		mBodyList.remove( body );
	}

	int size() {
		return mBodyList.size();
	}

	Iterator<Body> iterator() {
		return mBodyList.iterator();
	}

	Body get( int index ) {
		return mBodyList.get( index );
	}

	/**
	 * 			次に拘束するボディを取得
	 * @param sizeFormat 取得したいタイルサイズのサイズフォーマット
	 * @return 今までに拘束された回数が一番少ないボディを返す。アウェイクもする
	 */
	Body getNext(int sizeFormat) {
		int		count = Integer.MAX_VALUE;
		Body	minCountBody = null;

		Tile	tile = null;
		for(Body body : mBodyList) {
			tile = (Tile)body.getUserData();
			//出てきたタイルが引数のサイズと違えばスキップ
			if(tile.getSizeFormat() != sizeFormat) {
				continue;
			}
			//パネルに所属していないタイル
			if(tile.getIndex() == TileBase.INVALID_ID && tile.getPanelId() == TileBase.INVALID_ID) {
				if(tile.getRestrainCount() < count) {
					count = tile.getRestrainCount();
					minCountBody = body;
				}
			}
		}

		if(minCountBody != null) {
			minCountBody.setAwake(true);
			tile = (Tile)minCountBody.getUserData();
			tile.addRestrainCount();
		}
		return minCountBody;
	}

	/**
	 * 			パネルに拘束されていないタイルの数を取得
	 */
	int getFreeTileCount() {
		int		count = 0;
		Iterator<Body>		it = mBodyList.iterator();
		Tile	tile = null;
		while(it.hasNext()) {
			tile = (Tile)it.next().getUserData();
			if( tile.getPanelId() == TileBase.INVALID_ID && tile.getIndex() == TileBase.INVALID_ID ) {
				count++;
			}
		}

		return count;
	}

	/**
	 * 		タイルのユニークIDをクリア
	 */
	void clearTileId() {
		for(Body body : mBodyList) {
			Tile	tile = (Tile)body.getUserData();
			tile.setUniqueId(TileBase.INVALID_ID, TileBase.INVALID_ID);
		}
	}
}
