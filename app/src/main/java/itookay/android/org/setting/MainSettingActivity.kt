package itookay.android.org.setting

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.NumberPicker
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.ListFragment
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.*

import itookay.android.org.R

class MainSettingFragment : PreferenceFragmentCompat(), Preference.OnPreferenceClickListener {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.setting_activity_preferences, rootKey)

        findPreference<Preference>(getString(R.string.preference_key_ringtone_list))?.onPreferenceClickListener =this
        findPreference<Preference>(getString(R.string.preference_key_font_list))?.onPreferenceClickListener = this
        findPreference<Preference>(getString(R.string.preference_key_alarm_active_time))?.onPreferenceClickListener = this
        findPreference<Preference>(getString(R.string.preference_key_vibration))?.onPreferenceClickListener = this;
    }

    override fun onPreferenceClick(preference: Preference?): Boolean {
        //ワイド画面用のXMLレイアウトがロードされていればフラグメントで表示
        val isDualPane = activity?.findViewById<FrameLayout>(R.id.mainSettingContainer_sub) != null

        /* (表示しているのがあれば)フラグメントを削除 */
        val fragment = fragmentManager?.findFragmentById(R.id.mainSettingContainer_sub)
        if(fragment != null) {
            fragmentManager
                ?.beginTransaction()
                ?.remove(fragment)
                ?.commit()
        }

        when(preference?.key) {
            //サウンドリスト
            getString(R.string.preference_key_ringtone_list) -> {
                showSubContainerList(isDualPane, RingtoneListActivity::class.java, RingtoneListFragment())
            }
            //フォントリスト
            getString(R.string.preference_key_font_list) -> {
                showSubContainerList(isDualPane, FontListActivity::class.java, FontListFragment())
            }
            //タイマー動作時間
            getString(R.string.preference_key_alarm_active_time) -> {
                SetAlarmActiveTimeDialog(context).show()
            }
            //バイブレーションリスト
            getString(R.string.preference_key_vibration) -> {
                showSubContainerList(isDualPane, VibrationListActivity::class.java, VibrationListFragment())
            }
            //アプリ非表示時の通知方法
            getString(R.string.preference_key_background_notification) -> {

            }
        }
        return true
    }

    /**
     *      サブコンテナにフラグメント表示
     */
    private fun showSubContainerList(isDualPane: Boolean, cls: Class<*>, listFragment: ListFragment) {
        if(isDualPane) {
            fragmentManager
                ?.beginTransaction()
                ?.add(R.id.mainSettingContainer_sub, listFragment)
                ?.commit()
        }
        else {
            val intent = Intent()
            intent.setClass(activity, cls)
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