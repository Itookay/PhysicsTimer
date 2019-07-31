package itookay.android.org.contents;

import android.content.Context;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import itookay.android.org.R;

public class RingtoneList {

    private static Context     mAppContext = null;

    /** サウンドリストのEntity */
    private static CharSequence[]       mRingtoneEntityList;
    /** サウンドリストのEntityValue */
    private static CharSequence[]       mRingtoneEntityValueList;

    public static void setContext(Context appContext) {
        mAppContext = appContext;
        setRingtoneList();
    }

    private static void setRingtoneList() {
        RingtoneManager ringtoneMgr = new RingtoneManager(mAppContext);
        Cursor cursor = ringtoneMgr.getCursor();
        //リストに”サウンドなし”も含める
        int         count = cursor.getCount() + 1;
        mRingtoneEntityList = new CharSequence[count];
        mRingtoneEntityValueList = new CharSequence[count];

        /* サウンドなし */
        mRingtoneEntityList[0] = mAppContext.getString(R.string.list_item_none_entity);
        mRingtoneEntityValueList[0] = mAppContext.getString(R.string.list_item_none_entity_value);

        int index = 1;
        while(cursor.moveToNext()) {
            mRingtoneEntityList[index] = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            mRingtoneEntityValueList[index] = String.valueOf(index);
            index++;
        }
    }

    public static Ringtone getRingtone(int index) {
        RingtoneManager ringtoneMgr = new RingtoneManager(mAppContext);
        Cursor cursor = ringtoneMgr.getCursor();

        int     i = 0;
        CharSequence      ringtoneName = mRingtoneEntityList[index];
        while(cursor.moveToNext()) {
            String      cursorName = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            if(ringtoneName.equals(cursorName)) {
                return ringtoneMgr.getRingtone(i);
            }
            i++;
        }

        return null;
    }

    public static String getRingtoneName(int index) {
        return getRingtone(index).getTitle(mAppContext);
    }

    public static CharSequence[] getRingtoneEntityList() {
        return mRingtoneEntityList;
    }

    public static CharSequence[] getRingtoneEntityValueList() {
        return mRingtoneEntityValueList;
    }

    public static int getDefault() {
        return -1;
    }
}
