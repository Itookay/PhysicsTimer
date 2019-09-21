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
}
