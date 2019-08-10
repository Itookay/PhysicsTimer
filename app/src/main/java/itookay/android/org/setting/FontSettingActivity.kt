package itookay.android.org.setting

import android.app.Activity
import android.os.Bundle
import android.view.*
import itookay.android.org.R

class FontSettingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.font_setting_activity)
    }
}
