package itookay.android.org.setting;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import itookay.android.org.R;
import itookay.android.org.font.Fonts;

public class FontListAdapter extends BaseAdapter {

    private final int   mFontCount = Fonts.getList().size();

    private LayoutInflater  mInflater = null;
    private RadioButton[]   mRadioButtons = new RadioButton[mFontCount];
    private Resources       mResources = null;
    private Context         mContext = null;

    public FontListAdapter(Context context, Resources res) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResources = res;
        mContext = context;
    }

    @Override
    public int getCount() {
        return mFontCount;
    }

    @Override
    public Object getItem(int position) {
        return Fonts.getBitmap(mResources, position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LinearLayout    itemView = (LinearLayout)mInflater.inflate(R.layout.font_list_item, null);
        TextView        textView = itemView.findViewById(R.id.tvFontName);
        ImageView       imageView = itemView.findViewById(R.id.ivFont);
        RadioButton     radioButton = itemView.findViewById(R.id.rbFont);

        textView.setText(Fonts.getName(position));
        imageView.setImageBitmap(Fonts.getBitmap(mResources, position));

        /*
         *      リストアイテムのonClickedクラス
         */
        class onClicked implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                //全てのラジオボタンのチェックを外す
                for(RadioButton radio : mRadioButtons) {
                    radio.setChecked(false);
                }

                mRadioButtons[position].setChecked(true);
                Settings.saveFontByIndex(mContext, position);
            }
        }

        radioButton.setOnClickListener(new onClicked());
        imageView.setOnClickListener(new onClicked());

        mRadioButtons[position] = radioButton;
        //Preferenceから復元
        if(position == Settings.getSavedFontIndex(mContext)) {
            mRadioButtons[position].setChecked(true);
        }

        return itemView;
    }
}
