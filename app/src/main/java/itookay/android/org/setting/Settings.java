package itookay.android.org.setting;

import android.content.Context;
import android.content.SharedPreferences;

import itookay.android.org.R;
import itookay.android.org.font.FontBase;
import itookay.android.org.font.Fonts;
import itookay.android.org.style.SingleRow;
import itookay.android.org.style.StyleBase;
import itookay.android.org.style.TwoRows;
import itookay.android.org.style.TwoRowsBigSecond;

import java.util.Arrays;
import java.util.List;

public class Settings {

    /** SharedPreferenceファイル名 */
    public static String    PREFERENCE_FILE_NAME = "physics_timer.preference";
    /** preferenceキー：フォント */
    public static String    PREFERENCE_KEY_FONT = "preference_key_font";
    /** preferenceキー：スタイル */
    public static String    PREFERENCE_KEY_STYLE = "preference_key_style";
    /** preferenceキー：サウンド */
    public static String    PREFERENCE_KEY_RINGTONE = "preference_key_ringtone";
    /** preferenceキー：アラーム動作時間 */
    public static String    PREFERENCE_KEY_ALARM_TIME = "preference_key_alarm_time";
    /** preferenceキー：バイブレーション */
    public static String    PREFERENCE_KEY_VIBRATION = "preference_key_alarm_vibration";
    /** preferenceキー：フォアグランドにして通知 */
    private static String   PREFERENCE_KEY_NOTIFICATION_FOREGROUND = "preference_key_notification_foreground";
    /** preferenceキー：サウンドで通知 */
    private static String   PREFERENCE_KEY_NOTIFICATION_SOUND = "preference_key_notification_sound";
    /** preferenceキー：バイブレーションで通知 */
    private static String   PREFERENCE_KEY_NOTIFICATION_VIBRATION = "preference_key_notification_vibration";

    /** スタイルリスト */
    private static List<StyleBase> mStyleList = Arrays.asList(
            new SingleRow(),
            new TwoRowsBigSecond(),
            new TwoRows()
    );
    /** デフォルト値 */
    public static StyleBase    DEFAULT_STYLE = mStyleList.get(1);

    /**
     *      フォントを取得
     */
    public static FontBase getFont(Context context) {
        SharedPreferences   pref = getSharedPreference(context);
        String              fontName = pref.getString(PREFERENCE_KEY_FONT, "");

        for(FontBase font : Fonts.getList()) {
            if(fontName.equals(font.NAME)) {
                return font;
            }
        }
        return Fonts.getDefault();
    }

    /**
     *      フォントのリスト上でのインデックスを取得
     */
    public static int getFontIndex(Context context) {
        int         index = 0;
        String      name = getFont(context).NAME;
        for(FontBase font : Fonts.getList()) {
            if(font.NAME.equals(name)) {
                return index;
            }
            index++;
        }
        return Fonts.getDefaultIndex();
    }

    /**
     *      スタイルを取得
     */
    public static StyleBase getStyle(Context context) {
        SharedPreferences   pref = getSharedPreference(context);
        String              StyleName = pref.getString(PREFERENCE_KEY_STYLE, new TwoRows().NAME);

        for(StyleBase style : mStyleList) {
            if(StyleName.equals(style.NAME)) {
                return style;
            }
        }
        return DEFAULT_STYLE;
    }

    /**
     *      サウンドを取得
     */
    public static int getRingtoneIndex(Context context) {
        SharedPreferences   pref = getSharedPreference(context);
        return pref.getInt(PREFERENCE_KEY_RINGTONE, RingtoneList.getDefault());
    }

    /**
     *      アラーム動作時間を取得
     */
    public static int getAlarmTime(Context context) {
        SharedPreferences   pref = getSharedPreference(context);
        return pref.getInt(PREFERENCE_KEY_ALARM_TIME, 1);
    }

    /**
     *      スタイルを取得
     */
    public static StyleBase getStyle(int index) {
        if(0 <= index && index < mStyleList.size()) {
            return mStyleList.get(index);
        }
        else {
            return null;
        }
    }

    /**
     *      スタイルのリスト上でのインデックスを取得
     */
    public static int getStyleIndex(Context context) {
        int     index = 0;
        for(StyleBase style : mStyleList) {
            if(style.NAME.equals(getStyle(context).NAME)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     *      バイブレーションを取得
     */
    public static int getVibrationIndex(Context context) {
        SharedPreferences   pref = getSharedPreference(context);
        return pref.getInt(PREFERENCE_KEY_VIBRATION, 1);
    }

    /**
     *      バックグラウンド時の通知方法を取得
     * @return ダイアログのチェックボックスの上から順番にint配列で返す。
     */
    public static boolean[] getBackgroundNotificationAction(Context context) {
        SharedPreferences   pref = getSharedPreference(context);
        boolean     action1 = pref.getBoolean(PREFERENCE_KEY_NOTIFICATION_FOREGROUND, false);
        boolean     action2 = pref.getBoolean(PREFERENCE_KEY_NOTIFICATION_SOUND, false);
        boolean     action3 = pref.getBoolean(PREFERENCE_KEY_NOTIFICATION_VIBRATION, true);
        boolean[]   action = {action1, action2, action3};

        return action;
    }

    public static boolean getFlagKeepScreenOn(Context context) {
        SharedPreferences   pref = getSharedPreference(context);
        String              key = context.getString(R.string.preference_key_keep_screen_on);
        return pref.getBoolean(key, true);
    }

    /**
     *      フォントを保存
     */
    public static void saveFont(Context context, String fontName) {
        SharedPreferences   pref = getSharedPreference(context);
        pref.edit().putString(PREFERENCE_KEY_FONT, fontName).apply();
    }

    /**
     *      フォントのインデックスを保存
     */
    public static void saveFontIndex(Context context, int index) {
        saveFont(context, Fonts.getName(index));
    }

    /**
     *      スタイルを保存
     */
    public static void saveStyle(Context context, String styleName) {
        SharedPreferences   pref = getSharedPreference(context);
        pref.edit().putString(PREFERENCE_KEY_STYLE, styleName).apply();
    }

    /**
     *      サウンドのインデックスを保存
     */
    public static void saveRingtoneIndex(Context context, int index) {
        SharedPreferences   pref = getSharedPreference(context);
        pref.edit().putInt(PREFERENCE_KEY_RINGTONE, index).apply();
    }

    /**
     *      スタイルのインデックスを保存
     */
    public static void saveStyleIndex(Context context, int index) {
        String  style = mStyleList.get(index).NAME;
        saveStyle(context, style);
    }

    public static void saveAlarmTime(Context context, int value) {
        SharedPreferences   pref = getSharedPreference(context);
        pref.edit().putInt(PREFERENCE_KEY_ALARM_TIME, value).apply();
    }

    /**
     *      バイブレーションを保存
     * @param context
     */
    public static void saveVibration(Context context, int index) {
        SharedPreferences   pref = getSharedPreference(context);
        pref.edit().putInt(PREFERENCE_KEY_VIBRATION, index).apply();
    }

    /**
     *      バックグラウンド時の通知方法を保存
     * @param context コンテキスト
     * @param values ダイアログのチェックボックスの上から順番にint配列で渡す
     */
    public static void saveBackgroundNotificationAction(Context context, boolean[] values) {
        SharedPreferences   pref = getSharedPreference(context);
        pref.edit().putBoolean(PREFERENCE_KEY_NOTIFICATION_FOREGROUND, values[0]).apply();
        pref.edit().putBoolean(PREFERENCE_KEY_NOTIFICATION_SOUND, values[1]).apply();
        pref.edit().putBoolean(PREFERENCE_KEY_NOTIFICATION_VIBRATION, values[2]).apply();
    }

    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    }
}
