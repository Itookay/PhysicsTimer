package itookay.android.org

import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.ListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.*
import itookay.android.org.contents.RingtoneList

class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

    private var ListPref: ListPreference? = null

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        setRingtonePreference()
    }

    /**
     *      アラームのサウンドリストEntityをセット
     */
    private fun setRingtonePreference() {
        ListPref = findPreference<ListPreference>(getString(R.string.preference_key_sound_list))
        ListPref?.entries = RingtoneList.getRingtoneEntityList()
        ListPref?.entryValues = RingtoneList.getRingtoneEntityValueList()
        ListPref?.setDefaultValue(RingtoneList.getDefault())

        val value = ListPref?.value
        ListPref?.summary = RingtoneList.getRingtoneName(Integer.parseInt(value))

        ListPref?.setOnPreferenceChangeListener(this)
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when(preference?.key) {
            getString(R.string.preference_key_sound_list) -> {
                if(newValue is String) {
                    preference.summary = RingtoneList.getRingtoneName(Integer.parseInt(newValue))
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