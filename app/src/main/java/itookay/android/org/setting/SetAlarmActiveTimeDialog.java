package itookay.android.org.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.NumberPicker;

import itookay.android.org.R;

public class SetAlarmActiveTimeDialog implements DialogInterface.OnClickListener {

    private final int       MAX_ALERT_MIN = 60;
    private final int       MIN_ALERT_MIN = 1;

    private Context         mContext = null;
    private NumberPicker    mNumberPicker = null;

    public SetAlarmActiveTimeDialog(Context context) {
        mContext = context;
    }

    public void show() {
        LayoutInflater      inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.alarm_active_time_dialog, null);
        int     value = Settings.getAlarmTime(mContext);

        mNumberPicker = view.findViewById(R.id.npSecond);
        mNumberPicker.setMaxValue(MAX_ALERT_MIN);
        mNumberPicker.setMinValue(MIN_ALERT_MIN);
        mNumberPicker.setValue(value);

        new AlertDialog.Builder(mContext)
            .setView(view)
            .setPositiveButton(R.string.ok, this)
            .setNegativeButton(R.string.cancel, this)
            .setTitle(R.string.alarm_active_time_dialog_title)
            .show();

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case DialogInterface.BUTTON_POSITIVE:
                int value = mNumberPicker.getValue();
                Settings.saveAlarmTime(mContext, value);

            case DialogInterface.BUTTON_NEGATIVE:
                dialog.cancel();

            default:
        }
    }
}
