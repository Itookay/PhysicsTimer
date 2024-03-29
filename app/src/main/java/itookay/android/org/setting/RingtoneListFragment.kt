package itookay.android.org.setting

import android.media.MediaPlayer
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment

class RingtoneListFragment : ListFragment(), AdapterView.OnItemClickListener {

    /** サウンド再生用のMediaPlayer */
    private var mMediaPlayer : MediaPlayer? = null

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, RingtoneList.getRingtoneList(context))
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.onItemClickListener = this
        val index = Settings.getRingtoneIndex(context)
        listView.setItemChecked(index, true)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //他のサウンドを再生中なら停止
        if(RingtoneList.isPlaying()) {
            RingtoneList.stop()
        }

        Settings.saveRingtoneIndex(context, position)

        RingtoneList.start(context, position, false)
    }

    override fun onDestroy() {
        super.onDestroy()

        mMediaPlayer?.stop()
    }
}