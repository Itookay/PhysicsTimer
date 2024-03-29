package itookay.android.org.setting

import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import itookay.android.org.R

class RingtoneListActivity : AppCompatActivity(), AdapterView.OnItemClickListener {

    /** サウンド再生用のMediaPlayer */
    private var mMediaPlayer : MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.ringtone_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val listView = findViewById<ListView>(R.id.ringtoneListView)
        listView.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_single_choice, RingtoneList.getRingtoneList(applicationContext))
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.onItemClickListener = this
        val index = Settings.getRingtoneIndex(applicationContext)
        listView.setItemChecked(index, true)
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        //他のサウンドを再生中なら停止
        if(RingtoneList.isPlaying()) {
            RingtoneList.stop();
        }

        Settings.saveRingtoneIndex(applicationContext, position)

        if(position != 0) {
            RingtoneList.start(applicationContext, position, false)
        }
    }

    /**
     *      アクションバーのボタン処理
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                if(RingtoneList.isPlaying()) {
                    RingtoneList.stop()
                }

                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        mMediaPlayer?.stop()
    }
}
