package itookay.android.org.debug;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import itookay.android.org.contents.PhysicsTimer;
import itookay.android.org.contents.TimeWatchingService;
import itookay.android.org.style.TwoRows;

public class Debug {

    /** Logcatタグ名 */
    private static String   TAG = "PhysicsTimer";
    /** SharedPreferenceファイル名 */
    private static String    PREFERENCE_FILE_NAME = "physics_timer.debug.preference";
    /** Preferenceキー */
    private static String    PREFERENCE_KEY_DEBUG_MODE_FLAG = "preference_key_debug_mode";
    /** Preferenceキー */
    private static String    PREFERENCE_KEY_FINISH_TWO_SECOND_FLAG = "preference_key_finish_two_second";
    /** Preferenceキー */
    private static String    PREFERENCE_KEY_SHOW_PASSED_TIME_FLAG = "preference_key_show_passed_time";

    /**
     *      内容をLogcatに出力
     * @param text 内容
     */
    public static void log(String text) {
        Log.d(TAG, text);
    }

    /**
     *      内容を呼び出し元と共にLogcatに出力
     * @param text 内容
     */
    public static void log2(String text) {
        StackTraceElement[]     ste = new Throwable().getStackTrace();
        String method = ste[1].getClassName() + "." + ste[1].getMethodName();
        String t = method + " : " + text;
        log(t);
    }

    /**
     *      呼び出し元のメソッド名をログ
     */
    public static void calledLog() {
        StackTraceElement[]     ste = new Throwable().getStackTrace();
        String text = ste[1].getClassName() + "." + ste[1].getMethodName() + "() called.";
        log(text);
    }

    /**
     *      階層を指定して呼び出し元のメソッド名をログ
     * @param level 0:Debug.calledLog(int)
     *              1:呼び出したメソッド名
     *              2:呼び出したメソッドを呼んだメソッド
     *              3:(以降同様)
     */
    public static void calledLog(int level) {
        StackTraceElement[]     ste = new Throwable().getStackTrace();
        String text = ste[level].getClassName() + "." + ste[level].getMethodName() + "() called.";
        log(text);
    }

    /**
     *      アプリケーションの開始を知らせるログ
     */
    public static void startLog() {
        String  text = "App start. --------------------------";
        Log.d(TAG, text);
    }

    /**
     *      PhysicsTimer.getState()の値をログ
     * @param state PhysicsTimer.getState()の値
     * @param isGetter trueでgetState()とログに記述
     */
    public static void timerStateLog(int state, boolean isGetter) {
        String      stateString = "";
        switch(state) {
            case PhysicsTimer.STATE_PROCESSING:
                stateString = "STATE_PROCESSING";
                break;
            case PhysicsTimer.STATE_IDLING:
                stateString = "STATE_IDLING";
                break;
            case PhysicsTimer.STATE_ALARMING:
                stateString = "STATE_ALARMING";
                break;
            default:
                stateString = "invalid state";
        }

        String      text = "";
        if(isGetter) {
            text = "PhysicsTimer.getState() = " + stateString;
        }
        else {
            text = "PhysicsTimer.setState() = " + stateString;
        }

        log(text);
    }

    /**
     *      渡されたオリエンテーションの値をログ
     */
    public static String getOrientationString(int orientation) {
        String      orientationString = "";
        switch(orientation) {
            case PhysicsTimer.PORTRAIT:
                orientationString = "PORTRAIT";
                break;
            case PhysicsTimer.LEFT_LANDSCAPE:
                orientationString = "LEFT LANDSCAPE";
                break;
            case PhysicsTimer.RIGHT_LANDSCAPE:
                orientationString = "RIGHT LANDSCAPE";
                break;
            case PhysicsTimer.UPSIDE_DOWN:
                orientationString = "UPSIDE_DOWN";
                break;
            case PhysicsTimer.ORIENTATION_RANGE_OUT:
                orientationString = "ORIENTATION RANGE OUT";
                break;
        }
        return orientationString;
    }

    /**
     *      デバッグモード：メインダイアログの表示
     */
    public void showMainDialog(final Context context) {
        String[]        items = {
                "2秒でアラーム",
                "java.timeで経過時間を表示"
        };
        boolean[]       defaultCheckedItems = {
                getFinishTwoSecondFlag(context),
                getShowPassedTimeFlag(context)
        };

        new AlertDialog.Builder(context)
            .setCancelable(false)
            .setTitle("デバッグモード")
            .setMultiChoiceItems(items, defaultCheckedItems, new DialogInterface.OnMultiChoiceClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                    //ダイアログ表示で強制的にデバッグモード
                    saveDebugModeFlag(context, true);

                    switch(which) {
                        case 0:
                            saveFinishTwoSecondFlag(context, isChecked);
                            break;
                        case 1:
                            saveShowPassedTimeFlag(context, isChecked);
                    }
                }
            })
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .show();
    }

    /**
     *      経過時間ダイアログの表示
     */
    public void showPassedTimeDialog(Context context) {
        String mes = TimeWatchingService.showPassedTime();

        new AlertDialog.Builder(context)
            .setCancelable(false)
            .setTitle("java.time.LocalDateTimeでの経過時間")
            .setMessage(mes)
            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .show();
    }

    public static void saveDebugModeFlag(Context context, boolean state) {
        SharedPreferences   pref = getSharedPreference(context);
        pref.edit().putBoolean(PREFERENCE_KEY_DEBUG_MODE_FLAG, state).apply();
    }

    public static boolean getDebugModeFlag(Context context) {
        SharedPreferences   pref = getSharedPreference(context);
        return pref.getBoolean(PREFERENCE_KEY_DEBUG_MODE_FLAG, false);

    }

    private static void saveFinishTwoSecondFlag(Context context, boolean state) {
        SharedPreferences   pref = getSharedPreference(context);
        pref.edit().putBoolean(PREFERENCE_KEY_FINISH_TWO_SECOND_FLAG, state).apply();
    }

    public static boolean getFinishTwoSecondFlag(Context context) {
        SharedPreferences   pref = getSharedPreference(context);
        return pref.getBoolean(PREFERENCE_KEY_FINISH_TWO_SECOND_FLAG, false);
    }

    private static void saveShowPassedTimeFlag(Context context, boolean state) {
        SharedPreferences   pref = getSharedPreference(context);
        pref.edit().putBoolean(PREFERENCE_KEY_SHOW_PASSED_TIME_FLAG, state).apply();
    }

    public static boolean getShowPassedTimeFlag(Context context) {
        SharedPreferences   pref = getSharedPreference(context);
        return pref.getBoolean(PREFERENCE_KEY_SHOW_PASSED_TIME_FLAG, false);
    }

    private static SharedPreferences getSharedPreference(Context context) {
        return context.getSharedPreferences(PREFERENCE_FILE_NAME, Context.MODE_PRIVATE);
    }

}
