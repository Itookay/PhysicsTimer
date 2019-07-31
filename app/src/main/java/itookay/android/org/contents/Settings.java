package itookay.android.org.contents;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import itookay.android.org.R;
import itookay.android.org.font.FontBase;
import itookay.android.org.font.FontBaseA;
import itookay.android.org.font.NormalA;
import itookay.android.org.font.NormalRoundA;
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

    private static Context     mAppContext = null;
    /** フォントリスト */
    private static List<FontBaseA> mFontList = Arrays.asList(
            new NormalA(),
            new NormalRoundA()
    );
    /** スタイルリスト */
    private static List<StyleBase> mStyleList = Arrays.asList(
            new SingleRow(),
            new TwoRowsBigSecond(),
            new TwoRows()
    );
    /** デフォルト値 */
    public static FontBase     DEFAULT_FONT = mFontList.get(0);
    public static StyleBase    DEFAULT_STYLE = mStyleList.get(1);

    public static void setContext(Context appContext) {
        mAppContext = appContext;
    }

    /**
     *      SharedPreferenceのフォントを取得
     */
    public static FontBase getSavedFont() {
        SharedPreferences   pref = mAppContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        String              fontName = pref.getString(PREFERENCE_KEY_FONT, "");

        for(FontBase font : mFontList) {
            if(fontName.equals(font.NAME)) {
                return font;
            }
        }
        return DEFAULT_FONT;
    }

    /**
     *      SharedPreferenceのスタイルを取得
     */
    public static StyleBase getSavedStyle() {
        SharedPreferences   pref =mAppContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        String              StyleName = pref.getString(PREFERENCE_KEY_STYLE, "");

        for(StyleBase style : mStyleList) {
            if(StyleName.equals(style.NAME)) {
                return style;
            }
        }
        return DEFAULT_STYLE;
    }

    /**
     *      フォントを保存
     */
    public static void saveFont(String fontName) {
        SharedPreferences   pref = mAppContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(PREFERENCE_KEY_FONT, fontName).apply();
    }

    /**
     *      リストインデックスでフォントを保存
     */
    public static void saveFontByIndex(int index) {
        String  fontName = mFontList.get(index).NAME;
        saveFont(fontName);
    }

    /**
     *      スタイルを保存
     */
    public static void saveStyle(String styleName) {
        SharedPreferences   pref = mAppContext.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(PREFERENCE_KEY_STYLE, styleName).apply();
    }

    /**
     *      リストインデックスでスタイルを保存
     */
    public static void saveStyleByIndex(int index) {
        String  style = mStyleList.get(index).NAME;
        saveStyle(style);
    }

    /**
     *      リストからindex位置のフォントを取得
     */
    public static FontBase getFont(int index) {
        if(0 <= index && index < mFontList.size()) {
            return mFontList.get(index);
        }
        else {
            return null;
        }
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
    public static int getSavedFontIndex() {
        int     index = 0;
        for(FontBase font : mFontList) {
            if(font.NAME.equals(getSavedFont().NAME)) {
                return index;
            }
            index++;
        }
        return -1;
    }

    /**
     *      保存されたスタイルのリスト上でのインデックスを返す
     */
    public static int getSavedStyleIndex() {
        int     index = 0;
        for(StyleBase style : mStyleList) {
            if(style.NAME.equals(getSavedStyle().NAME)) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
