package itookay.android.org.setting;

import android.content.Context;
import android.content.SharedPreferences;
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
    public static FontBaseA    DEFAULT_FONT = mFontList.get(0);
    public static StyleBase    DEFAULT_STYLE = mStyleList.get(1);

    public static List<FontBaseA> getFontList() {
        return mFontList;
    }

    /**
     *      SharedPreferenceのフォントを取得
     */
    public static FontBaseA getSavedFont(Context context) {
        SharedPreferences   pref = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        String              fontName = pref.getString(PREFERENCE_KEY_FONT, "");

        for(FontBaseA font : mFontList) {
            if(fontName.equals(font.NAME)) {
                return font;
            }
        }
        return DEFAULT_FONT;
    }

    /**
     *      SharedPreferenceのスタイルを取得
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
     *      フォントを保存
     */
    public static void saveFont(Context context, String fontName) {
        SharedPreferences   pref = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(PREFERENCE_KEY_FONT, fontName).apply();
    }

    /**
     *      リストインデックスでフォントを保存
     */
    public static void saveFontByIndex(Context context, int index) {
        String  fontName = mFontList.get(index).NAME;
        saveFont(context, fontName);
    }

    /**
     *      スタイルを保存
     */
    public static void saveStyle(Context context, String styleName) {
        SharedPreferences   pref = context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(PREFERENCE_KEY_STYLE, styleName).apply();
    }

    /**
     *      リストインデックスでスタイルを保存
     */
    public static void saveStyleByIndex(Context context, int index) {
        String  style = mStyleList.get(index).NAME;
        saveStyle(context, style);
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
    public static int getSavedFontIndex(Context context) {
        int     index = 0;
        for(FontBase font : mFontList) {
            if(font.NAME.equals(getSavedFont(context).NAME)) {
                return index;
            }
            index++;
        }
        return -1;
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
