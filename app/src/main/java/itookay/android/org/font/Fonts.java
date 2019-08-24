package itookay.android.org.font;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Arrays;
import java.util.List;

import itookay.android.org.R;

/**
 *          フォントをまとめちゃってるクラス
 */
public class Fonts {

    private static final int       DEFAULT_INDEX = 0;

    private static List<FontBase> fontList() {
        return Arrays.asList(
            new Normal(),
            new NormalRound(),
            new Regular(),
            new RegularRound(),
            new Wide(),
            new WideCorner(),
            new SnowMan()
        );
    }

    private static List<Bitmap> fontBitmapList(Resources res) {
        return Arrays.asList(
            BitmapFactory.decodeResource(res, R.drawable.normal),
            BitmapFactory.decodeResource(res, R.drawable.normal_round),
            BitmapFactory.decodeResource(res, R.drawable.regular),
            BitmapFactory.decodeResource(res, R.drawable.regular_round),
            BitmapFactory.decodeResource(res, R.drawable.wide),
            BitmapFactory.decodeResource(res, R.drawable.wide_corner),
                BitmapFactory.decodeResource(res, R.drawable.snowman)
        );
    }

    public static FontBase get(int index) {
        return fontList().get(index);
    }

    public static List<FontBase> getList() {
        return fontList();
    }

    public static String getName(int index) {
        return fontList().get(index).NAME;
    }

    public static Bitmap getBitmap(Resources res, int index) {
        return fontBitmapList(res).get(index);
    }

    public static List<Bitmap> getBitmapList(Resources res) {
        return fontBitmapList(res);
    }

    public static FontBase getDefault() {
        return get(DEFAULT_INDEX);
    }

    public static int getDefaultIndex() {
        return DEFAULT_INDEX;
    }
}
