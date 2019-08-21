package itookay.android.org.setting

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.*

import itookay.android.org.R

class MainSettingFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener, Preference.OnPreferenceClickListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_activity_preferences, rootKey)
        findPreference<Preference>(getString(R.string.preference_key_ringtone_list))?.onPreferenceClickListener =this
        findPreference<Preference>(getString(R.string.preference_key_font_list))?.onPreferenceClickListener = this
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        //ワイド画面用のXMLレイアウトがロードされていればフラグメントで表示
        val isDualPane = activity?.findViewById<FrameLayout>(R.id.mainSettingContainer_sub) != null

        when(preference?.key) {
            //サウンドリスト
            getString(R.string.preference_key_ringtone_list) -> {
                showRingtoneList(isDualPane)
            }
            //フォントリスト
            getString(R.string.preference_key_font_list) -> {
                showFontList(isDualPane)
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

    /**
     *      サウンドリスト表示
     */
    private fun showRingtoneList(isDualPane:Boolean) {
        if(isDualPane) {
            fragmentManager
                ?.beginTransaction()
                ?.add(R.id.mainSettingContainer_sub, RingtoneListFragment())
                ?.commit()
        }
        else {
            val intent = Intent()
            intent.setClass(activity, RingtoneListActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     *      フォントリスト表示
     */
    private fun showFontList(isDualPane: Boolean) {
        if(isDualPane) {
            fragmentManager
                ?.beginTransaction()
                ?.add(R.id.mainSettingContainer_sub, FontListFragment())
                ?.commit()
        }
        else {
            val intent = Intent()
            intent.setClass(activity, FontListActivity::class.java)
            startActivity(intent)
        }
    }
}

/**
 *      MainSettingActivity
 *      起動直後にフラグメントを表示し処理する
 */
class MainSettingActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.main_settings_activity)

        supportFragmentManager
            .beginTransaction()
            .replace(R.id.mainSettingContainer, MainSettingFragment())
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
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