package itookay.android.org.setting;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import itookay.android.org.R;

public class RingtoneList {

    public static String[] getRingtoneList(Context context) {
        RingtoneManager ringtoneMgr = new RingtoneManager(context);
        Cursor cursor = ringtoneMgr.getCursor();
        String[]    ringtoneList = new String[cursor.getCount() + 1];

        /* サウンドなし */
        ringtoneList[0] = context.getString(R.string.setting_list_ringtone_no_sound);

        int index = 1;
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
        return -1;
    }
}
