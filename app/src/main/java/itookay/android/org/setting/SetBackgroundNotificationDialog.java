package itookay.android.org.setting;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Switch;

import itookay.android.org.R;

public class SetBackgroundNotificationDialog implements DialogInterface.OnClickListener  {

    private Context         mContext = null;

    Switch      mSwitch1 = null;
    Switch      mSwitch2 = null;
    Switch      mSwitch3 = null;

    public SetBackgroundNotificationDialog(Context context) {
        mContext = context;
    }

    public void show() {
        LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.background_notification_dialog, null);

        mSwitch1 = view.findViewById(R.id.swBackgroundAction1);
        mSwitch2 = view.findViewById(R.id.swBackgroundAction2);
        mSwitch3 = view.findViewById(R.id.swBackgroundAction3);

        boolean[]       values = Settings.getSavedBackgroundNotificationAction(mContext);
        mSwitch1.setChecked(values[0]);
        mSwitch3.setChecked(values[1]);
        mSwitch2.setChecked(values[2]);

        new AlertDialog.Builder(mContext)
                .setView(view)
                .setPositiveButton(R.string.ok, this)
                .setNegativeButton(R.string.cancel, this)
                .setTitle(R.string.preference_title_background_notification)
                .show();

    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        switch(which) {
            case DialogInterface.BUTTON_POSITIVE:
                boolean     value1 = mSwitch1.isChecked();
                boolean     value2 = mSwitch2.isChecked();
                boolean     value3 = mSwitch3.isChecked();
                boolean[]   values = {value1, value2, value3};
                Settings.saveBackgroundNotificationAction(mContext, values);

            case DialogInterface.BUTTON_NEGATIVE:
                dialog.cancel();

            default:
        }
    }
}
