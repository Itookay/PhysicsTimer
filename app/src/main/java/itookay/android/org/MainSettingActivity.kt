package itookay.android.org

import android.content.Context
import android.media.RingtoneManager
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.*
import itookay.android.org.contents.Settings

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        setSoundList()
        loadSoundListPreference()
    }

    private fun loadSoundListPreference() {
        val listPref = findPreference<ListPreference>("sound_pattern")
        listPref?.setDefaultValue(Settings.getSavedSound())
    }
    /**
     *
     */
    private fun setSoundList() {
        val ringtoneMgr = RingtoneManager(context)
        val cursor = ringtoneMgr.cursor
        val ringtoneEntries = arrayOfNulls<CharSequence>(cursor.count)
        val ringtoneEntryValues = arrayOfNulls<CharSequence>(cursor.count)
        var index = 0
        while(cursor.moveToNext()) {
            ringtoneEntries[index] = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX)
            ringtoneEntryValues[index] = index.toString()
//            ringtoneEntryValues[index] = cursor.getInt(RingtoneManager.ID_COLUMN_INDEX).toString()
            index++
        }

        val listPref = findPreference<ListPreference>("sound_pattern")
        listPref?.entries = ringtoneEntries
        listPref?.entryValues = ringtoneEntryValues

        listPref?.setOnPreferenceChangeListener(this)
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when(preference?.key) {
            "sound_pattern" -> {
                if(newValue is String) {
                    Settings.saveSound(newValue)
                }
            }
        }
        return true
    }
}

class MainSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.main_settings_activity)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainSetting, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    /**
     *      アクションバーのボタン処理
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}