package itookay.android.org.setting

import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.*

import itookay.android.org.R

class MainSettingFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        findPreference<Preference>(getString(R.string.preference_key_ringtone_list))?.setOnPreferenceClickListener(this)
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        when(preference?.key) {
            getString(R.string.preference_key_ringtone_list) -> {
                fragmentManager
                    ?.beginTransaction()
                    ?.add(R.id.mainSettingContainer_sub, RingtoneSettingFragment())
                    ?.commit()
            }
        }
        return true
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when(preference?.key) {
            getString(R.string.preference_key_ringtone_list) -> {
                if(newValue is String) {
                    preference.summary = RingtoneList.getRingtoneName(context, Integer.parseInt(newValue))

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
            .replace(R.id.mainSettingContainer_main, MainSettingFragment())
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