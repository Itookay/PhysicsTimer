package itookay.android.org.debug;

import android.util.Log;

import itookay.android.org.contents.PhysicsTimer;

public class Debug {

    private static String        TAG = "PhysicsTimer";

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
     *      アプリケーションの開始を知らせるログ
     */
    public static void startLog() {
        String  text = "App start. --------------------------";
        Log.d(TAG, text);
    }

    /**
     *      PhysicsTimer.getState()の値をログ
     */
    public static void timerStateLog() {
        int         state = PhysicsTimer.getState();
        String      stateString = "";
        switch(state) {
            case PhysicsTimer.STATE_PROCESSING:
                stateString = "STATE_PROCESSING";
                break;
            case PhysicsTimer.STATE_IDLING:
                stateString = "STATE_IDLING";
                break;
            case PhysicsTimer.STATE_FINISHED:
                stateString = "STATE_FINISHED";
                break;
            default:
                stateString = "invalid state";
        }

        StackTraceElement[]     ste = new Throwable().getStackTrace();
        String      text = ste[1].getClassName() + "." + ste[1].getMethodName() + " : PhysicsTimer.getState() = " + stateString;
        log(text);
    }
}
