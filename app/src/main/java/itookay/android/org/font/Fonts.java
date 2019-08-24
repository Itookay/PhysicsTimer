package itookay.android.org.font;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import itookay.android.org.R;
import itookay.android.org.font.FontBase;
import itookay.android.org.font.NormalA;
import itookay.android.org.font.NormalRoundA;

/**
 *          フォントをまとめちゃってるクラス
 */
public class Fonts {

    private static final int       DEFAULT_INDEX = 0;

    private static List<FontBase> fontList() {
        return Arrays.asList(
            new NormalA(),
            new NormalRoundA(),
            new NormalB(),
            new NormalRoundB()
        );
    }

    private static List<Bitmap> fontBitmapList(Resources res) {
        return Arrays.asList(
            BitmapFactory.decodeResource(res, R.drawable.normal_a),
            BitmapFactory.decodeResource(res, R.drawable.normal_round_a),
            BitmapFactory.decodeResource(res, R.drawable.normal_b),
            BitmapFactory.decodeResource(res, R.drawable.normal_round_b)
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
