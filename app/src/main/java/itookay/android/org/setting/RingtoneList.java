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
        cursor.moveToPosition(index);
        return ringtoneMgr.getRingtoneUri(cursor.getPosition());
    }

    /**
     *      indexのサウンドのMediaPlayerをすぐにstart()を呼べる状態で取得
     * @param context
     * @param index
     * @throws IOException
     */
    public static MediaPlayer getMediaPlayer(Context context, int index, boolean isLooping) throws IOException {
        Uri     uri = RingtoneList.getUri(context, index);

        AudioAttributes audioAttr = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        MediaPlayer     mediaPlayer = new MediaPlayer();
        mediaPlayer.setDataSource(context, uri);
        mediaPlayer.setAudioAttributes(audioAttr);
        mediaPlayer.setLooping(isLooping);
        mediaPlayer.prepare();
        return mediaPlayer;
    }
}
