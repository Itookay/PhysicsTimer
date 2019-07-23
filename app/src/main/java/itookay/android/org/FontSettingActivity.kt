package itookay.android.org

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*
import itookay.android.org.contents.PhysicsTimer
import itookay.android.org.contents.Scale
import itookay.android.org.contents.Settings

class FontSettingActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.font_setting_activity)
    }
}
