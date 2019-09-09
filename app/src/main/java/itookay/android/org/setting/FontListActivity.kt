package itookay.android.org.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.ListView
import itookay.android.org.R

class FontListActivity : AppCompatActivity() {

    lateinit var mAdapter : FontListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.font_list_activity)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val listView:ListView = findViewById(R.id.fontListView)
        mAdapter = FontListAdapter(this, resources)
        listView.adapter = mAdapter
        listView.choiceMode = ListView.CHOICE_MODE_SINGLE
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
}
