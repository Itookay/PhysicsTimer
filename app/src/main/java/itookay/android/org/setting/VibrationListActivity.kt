package itookay.android.org.setting;

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity;
import itookay.android.org.R

class VibrationListActivity : AppCompatActivity(), AdapterView.OnItemClickListener  {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.vibration_list)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val listView = findViewById<ListView>(R.id.vibrationListView)
        listView.adapter = ArrayAdapter<String>(applicationContext, android.R.layout.simple_list_item_single_choice, VibrationList.getVibrationNameList(applicationContext))
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
        listView.onItemClickListener = this
        val index = Settings.getSavedVibrationIndex(applicationContext)
        listView.setItemChecked(index, true)
    }


    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Settings.saveVibration(applicationContext, position)

        //振動
        VibrationList.vibrate(applicationContext, position, VibrationList.NOT_REPEAT);
    }

    /**
     *      アクションバーのボタン処理
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                this.finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()

        VibrationList.stop()
    }
}
