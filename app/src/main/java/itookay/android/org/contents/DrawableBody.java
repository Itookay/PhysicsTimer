package itookay.android.org.contents;

import android.graphics.Canvas;
import org.jbox2d.dynamics.Body;

/**
 * 			描画する必要があるボディを持つ　インターフェース
 */
interface DrawableBody {

    public void drawBody(Canvas canvas, Body body );
}

