package itookay.android.org.setting

import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.fragment.app.ListFragment

class RingtoneListFragment : ListFragment(), AdapterView.OnItemClickListener {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        listAdapter = ArrayAdapter<String>(context, android.R.layout.simple_list_item_single_choice, RingtoneList.getRingtoneList(context))
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.onItemClickListener = this
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        var index = 0
        if(position == 0) {
            return
        }
        else {
            index = position - 1
        }

        val uri = RingtoneList.getUri(context, index)
        val mediaPlayer = MediaPlayer()

        val audioAttr = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_NOTIFICATION)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        mediaPlayer.setDataSource(context, uri)
        mediaPlayer.setAudioAttributes(audioAttr)
        mediaPlayer.isLooping = false
        mediaPlayer.prepare()
        mediaPlayer.start()
    }
}