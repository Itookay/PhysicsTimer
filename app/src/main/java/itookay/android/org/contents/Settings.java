package itookay.android.org.contents;

import android.content.Context;
import android.content.SharedPreferences;
import itookay.android.org.font.FontBase;
import itookay.android.org.font.FontBaseA;
import itookay.android.org.font.NormalA;
import itookay.android.org.font.NormalRoundA;
import itookay.android.org.style.SingleRow;
import itookay.android.org.style.StyleBase;
import itookay.android.org.style.TwoRowsBigSecond;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Settings {

    private Context     mAppContext = null;
    /** フォントリスト */
    private static List<FontBaseA> mFontList = Arrays.asList(
            new NormalA(),
            new NormalRoundA()
    );
    /** スタイルリスト */
    private static List<StyleBase> mStyleList = Arrays.asList(
            new SingleRow(),
            new TwoRowsBigSecond()
    );
    public static FontBase     DEFAULT_FONT = mFontList.get(0);
    public static StyleBase    DEFAULT_STYLE = mStyleList.get(1);

    /**
     *      コンストラクタ
     */
    public Settings(Context appContext) {
        mAppContext = appContext;
    }

    /**
     *      SharedPreferenceのフォントを取得
     */
    public FontBase getSavedFont() {
        SharedPreferences   pref =mAppContext.getSharedPreferences(PhysicsTimer.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        String              fontName = pref.getString(PhysicsTimer.PREFERENCE_KEY_FONT, "");

        for(FontBase font : mFontList) {
            if(fontName == font.NAME) {
                return font;
            }
        }
        return DEFAULT_FONT;
    }

    /**
     *      SharedPreferenceのスタイルを取得
     */
    public StyleBase getSavedStyle() {
        SharedPreferences   pref =mAppContext.getSharedPreferences(PhysicsTimer.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        String              StyleName = pref.getString(PhysicsTimer.PREFERENCE_KEY_STYLE, "");

        for(StyleBase style : mStyleList) {
            if(StyleName == style.NAME) {
                return style;
            }
        }
        return DEFAULT_STYLE;
    }

    /**
     *      フォントを保存
     */
    public void saveFont(String fontName) {
        SharedPreferences   pref = mAppContext.getSharedPreferences(PhysicsTimer.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(PhysicsTimer.PREFERENCE_KEY_FONT, fontName).apply();
    }

    /**
     *      リストインデックスでフォントを保存
     */
    public void saveFontByIndex(int index) {
        String  fontName = mFontList.get(index).NAME;
        saveFont(fontName);
    }

    /**
     *      スタイルを保存
     */
    public void saveStyle(String styleName) {
        SharedPreferences   pref = mAppContext.getSharedPreferences(PhysicsTimer.PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
        pref.edit().putString(PhysicsTimer.PREFERENCE_KEY_STYLE, styleName).apply();
    }

    /**
     *      リストインデックスでスタイルを保存
     */
    public void saveStyleByIndex(int index) {
        String  fontName = mStyleList.get(index).NAME;
        saveFont(fontName);
    }

    /**
     *      リストからindex位置のフォントを取得
     */
    public FontBase getFont(int index) {
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
    public StyleBase getStyle(int index) {
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
    public int getSavedFontIndex() {
        int     index = 0;
        for(FontBase font : mFontList) {
            if(font.NAME == getSavedFont().NAME) {
                return index;
            }
            index++;
        }
        return -1;
    }
}
