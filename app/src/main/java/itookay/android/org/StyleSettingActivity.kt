package itookay.android.org

import android.view.View
import itookay.android.org.contents.Settings

class StyleSettingActivity : DrawableSettingActivity() {

    /**
     *      ボタンクリック
     */
    override fun onClick(view: View?) {
        when(view) {
            btReturn -> {
                mPhysicsTimer.stop()
                Settings(applicationContext).saveStyleByIndex(ListIndex)
                finish()
            }
            btPrevious -> {
                val style = Settings(applicationContext).getStyle(--ListIndex)
                mPhysicsTimer.setStyle(style)
                mPhysicsTimer.invalidate()
            }
            btNext -> {
                val style = Settings(applicationContext).getStyle(++ListIndex)
                mPhysicsTimer.setStyle(style)
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
        btNext.isEnabled = (setting.getStyle(ListIndex + 1) != null)
        btPrevious.isEnabled = (setting.getStyle(ListIndex - 1) != null)
    }
}
