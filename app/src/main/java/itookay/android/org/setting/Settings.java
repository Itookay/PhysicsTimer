package itookay.android.org.setting;

import android.content.Context;
import android.content.SharedPreferences;
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
    public static String    PREFERENCE_FILE_NAME = "pref";
    /** preferenceキー：フォント */
    public static String    PREFERENCE_KEY_FONT = "prefkey_font";
    /** preferenceキー：スタイル */
    public static String    PREFERENCE_KEY_STYLE = "prefkey_style";
    /** preferenceキー：サウンド */
    public static String    PREFERENCE_KEY_RINGTONE = "prefkey_ringtone";

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
    public static FontBase getSavedFont(Context context) {
        SharedPreferences   pref = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        String              fontName = pref.getString(PREFERENCE_KEY_FONT, "");

        for(FontBase font : Fonts.getList()) {
            if(fontName.equals(font.NAME)) {
                return font;
            }
        }
        return Fonts.getDefault();
    }

    /**
     *      スタイルを取得
     */
    public static StyleBase getSavedStyle(Context context) {
        SharedPreferences   pref = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        String              StyleName = pref.getString(PREFERENCE_KEY_STYLE, "");

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
    public static int getSavedRingtoneIndex(Context context) {
        SharedPreferences   pref = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        return pref.getInt(PREFERENCE_KEY_RINGTONE, RingtoneList.getDefault());
    }

    /**
     *      フォントを保存
     */
    public static void saveFont(Context context, String fontName) {
        SharedPreferences   pref = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
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
        SharedPreferences   pref = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(PREFERENCE_KEY_STYLE, styleName).apply();
    }

    /**
     *      サウンドのインデックスを保存
     */
    public static void saveRingtoneIndex(Context context, int index) {
        SharedPreferences   pref = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        pref.edit().putInt(PREFERENCE_KEY_RINGTONE, index).apply();
    }

    /**
     *      スタイルのインデックスを保存
     */
    public static void saveStyleIndex(Context context, int index) {
        String  style = mStyleList.get(index).NAME;
        saveStyle(context, style);
    }

    /**
     *      リストからindex位置のスタイルを取得
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
     *      保存されたフォントのリスト上でのインデックスを返す
     */
    public static int getSavedFontIndex(Context context) {
        int         index = 0;
        String      name = getSavedFont(context).NAME;
        for(FontBase font : Fonts.getList()) {
            if(font.NAME.equals(name)) {
                return index;
            }
            index++;
        }
        return Fonts.getDefaultIndex();
    }

    /**
     *      保存されたスタイルのリスト上でのインデックスを返す
     */
    public static int getSavedStyleIndex(Context context) {
        int     index = 0;
        for(StyleBase style : mStyleList) {
            if(style.NAME.equals(getSavedStyle(context).NAME)) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
