package itookay.android.org.setting;

import android.content.Context;
import android.database.Cursor;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

import java.io.IOException;

import itookay.android.org.R;

public class RingtoneList {

    /** デフォルトサウンド */
    private static final int       DEFAULT_RINGTONE_INDEX = 0;
    /**  */
    private static MediaPlayer     mMediaPlayer = null;

    public static String[] getRingtoneList(Context context) {
        RingtoneManager ringtoneMgr = new RingtoneManager(context);
        Cursor cursor = ringtoneMgr.getCursor();
        String[]    ringtoneList = new String[cursor.getCount() + 1];

        /* サウンドなし */
        ringtoneList[0] = context.getString(R.string.setting_list_ringtone_no_sound);

        int index = 1;
        //システムから着信音リストを取得
        while(cursor.moveToNext()) {
            ringtoneList[index] = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            index++;
        }

        return ringtoneList;
    }

    public static Ringtone getRingtone(Context context, int index) {
        RingtoneManager ringtoneMgr = new RingtoneManager(context);
        Cursor cursor = ringtoneMgr.getCursor();

        int     i = 0;
        CharSequence      ringtoneName = getRingtoneList(context)[index];
        while(cursor.moveToNext()) {
            String      cursorName = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            if(ringtoneName.equals(cursorName)) {
                return ringtoneMgr.getRingtone(i);
            }
            i++;
        }

        return null;
    }

    public static String getRingtoneName(Context context, int index) {
        return getRingtone(context,index).getTitle(context);
    }

    public static int getDefault() {
        return DEFAULT_RINGTONE_INDEX;
    }

    public static Uri getUri(Context context, int index) {
        RingtoneManager     ringtoneMgr = new RingtoneManager(context);
        Cursor              cursor = ringtoneMgr.getCursor();
        //サウンドリストのindex=0にはサウンド「なし」が含まれている。Cursorには-1して渡す。
        cursor.moveToPosition(--index);
        return ringtoneMgr.getRingtoneUri(cursor.getPosition());
    }

    /**
     *      indexのサウンドを再生
     * @param context コンテキスト
     * @param index リストのインデックス
     * @param repeat リピートするか
     * @throws IOException
     */
    public static void start(Context context, int index, boolean repeat) throws IOException {
        Uri     uri = RingtoneList.getUri(context, index);

        AudioAttributes audioAttr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setDataSource(context, uri);
        mMediaPlayer.setAudioAttributes(audioAttr);
        mMediaPlayer.setLooping(repeat);
        mMediaPlayer.prepare();
        mMediaPlayer.start();
    }

    /**
     *      サウンドを停止
     */
    public static void stop() {
        if(mMediaPlayer != null) {
            mMediaPlayer.stop();
        }
    }

    public static boolean isPlaying() {
        if(mMediaPlayer != null) {
            return mMediaPlayer.isPlaying();
        }
        else {
            return false;
        }
    }
}
