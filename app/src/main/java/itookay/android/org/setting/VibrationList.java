package itookay.android.org.setting;

import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import itookay.android.org.R;

public class VibrationList {

    /** デフォルトバイブレーション */
    private static final int       DEFAULT_VIBRATION_INDEX = 0;

    /** リピートする */
    public static final int         REPEAT = 1;
    /** リピートしない */
    public static final int         NOT_REPEAT = -1;

    /** 時間：なし */
    private static final long       TIME_NONE = 0;
    /** 時間：ロング */
    private static final long       TIME_LONG = 1000;
    /** 時間：ショート */
    private static final long       TIME_SHORT = 500;
    /** 時間：ベリーショート */
    private static final long       TIME_VERY_SHORT = 200;
    /** 時間：ウルトラショート */
    private static final long       TIME_ULTRA_SHORT = 50;

    private static final long[]     PATTERN_NONE = {
            TIME_NONE
    };
    private static final long[]     PATTERN_1 = {
            TIME_NONE,
            TIME_LONG, TIME_SHORT
    };
    private static final long[]     PATTERN_2 = {
            TIME_NONE,
            TIME_SHORT, TIME_SHORT
    };
    private static final long[]     PATTERN_3 = {
            TIME_NONE,
            TIME_VERY_SHORT, TIME_VERY_SHORT,
            TIME_VERY_SHORT, TIME_VERY_SHORT,
            TIME_VERY_SHORT, TIME_VERY_SHORT,
            TIME_NONE, TIME_LONG,
    };
    private static final long[]     PATTERN_4 = {
            TIME_NONE,
            TIME_ULTRA_SHORT, TIME_ULTRA_SHORT,
            TIME_ULTRA_SHORT, TIME_ULTRA_SHORT,
            TIME_ULTRA_SHORT, TIME_ULTRA_SHORT,
            TIME_NONE, TIME_SHORT,
    };

    /** タイミングリスト */
    private static List       mTimingList = Arrays.asList(
            PATTERN_NONE,
            PATTERN_1,
            PATTERN_2,
            PATTERN_3,
            PATTERN_4
    );

    /** バイブレーション */
    private static Vibrator        mVibrator = null;

    /**
     *      振動させる
     */
    public static void vibrate(Context context, int index, int repeat) {
        mVibrator = (Vibrator)context.getSystemService(Context.VIBRATOR_SERVICE);
        long[]              timing = (long[])mTimingList.get(index);
        VibrationEffect     effect = VibrationEffect.createWaveform(timing, repeat);
        mVibrator.vibrate(effect);
    }

    /**
     *      振動を止める
     */
    public static void stop() {
        if(mVibrator != null) {
            mVibrator.cancel();
        }
    }

    /**
     *      バイブレーション名リスト取得
     */
    public static String[] getVibrationNameList(Context context) {
        return new String[]{
                context.getString(R.string.vibration_item_1),
                context.getString(R.string.vibration_item_2),
                context.getString(R.string.vibration_item_3),
                context.getString(R.string.vibration_item_4),
                context.getString(R.string.vibration_item_5),
        };
    }
}
