package itookay.android.org

import android.os.Bundle
import android.view.View
import itookay.android.org.contents.Settings

class StyleSettingActivity : DrawableSettingActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
    /**
     *      ボタンクリック
     */
    override fun onClick(view: View?) {
        when(view) {
            btReturn -> {
                mPhysicsTimer.stop()
                Settings(applicationContext).saveStyleByIndex(StyleListIndex)
                finish()
            }
            btPrevious -> {
                val style = Settings(applicationContext).getStyle(--StyleListIndex)
                mPhysicsTimer.setStyle(style)
                mPhysicsTimer.initDial();
                mPhysicsTimer.invalidate()
            }
            btNext -> {
                val style = Settings(applicationContext).getStyle(++StyleListIndex)
                mPhysicsTimer.setStyle(style)
                mPhysicsTimer.initDial();
                mPhysicsTimer.invalidate()
            }
        }
        setButtonState()
    }

    /**
     *      Prev,Nextボタンの押下可否をセット
     */
    override fun setButtonState() {
        val setting = Settings(applicationContext)
        btNext.isEnabled = (setting.getStyle(StyleListIndex + 1) != null)
        btPrevious.isEnabled = (setting.getStyle(StyleListIndex - 1) != null)
    }
}
