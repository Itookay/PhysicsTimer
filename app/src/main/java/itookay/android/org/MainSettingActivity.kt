package itookay.android.org

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
    }
}

class MainSettingActivity : AppCompatActivity() {

    lateinit var MainSettingLayout: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainSetting, SettingsFragment())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        MainSettingLayout = findViewById(R.id.mainSettingLayout)
    }

    override fun onPause() {
        super.onPause()
    }
}