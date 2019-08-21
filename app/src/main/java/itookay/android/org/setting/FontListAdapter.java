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
import itookay.android.org.font.NormalA;
import itookay.android.org.font.NormalRoundA;

public class FontListAdapter extends BaseAdapter {

    private final int   mFontCount = Settings.getFontList().size();

    private String[]    mFontNameList = new String[mFontCount];
    private Bitmap[]    mFontBitmapList = new Bitmap[mFontCount];
    private LayoutInflater  mInflater = null;
    private String          mSelectedFontName = "";
    private RadioButton[]   mRadioButtons = new RadioButton[mFontCount];

    public FontListAdapter(Context context, Resources res) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        mFontBitmapList[0] = BitmapFactory.decodeResource(res, R.drawable.normal_a);
        mFontBitmapList[1] = BitmapFactory.decodeResource(res, R.drawable.normal_round_a);
    }

    @Override
    public int getCount() {
        return mFontCount;
    }

    @Override
    public Object getItem(int position) {
        return mFontBitmapList[position];
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

        textView.setText(mFontNameList[position]);
        imageView.setImageBitmap(mFontBitmapList[position]);

        /*
         *      onClickedクラス
         */
        class onClicked implements View.OnClickListener {
            @Override
            public void onClick(View v) {
                mSelectedFontName = mFontNameList[position];
                for(RadioButton radio : mRadioButtons) {
                    radio.setChecked(false);
                }
                mRadioButtons[position].setChecked(true);
            }

        }

        radioButton.setOnClickListener(new onClicked());
        imageView.setOnClickListener(new onClicked());

        mRadioButtons[position] = radioButton;

        return itemView;
    }
}
