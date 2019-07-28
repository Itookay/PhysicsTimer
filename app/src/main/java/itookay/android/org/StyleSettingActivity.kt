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
            btPrevious -> {
                val style = Settings(applicationContext).getStyle(--StyleListIndex)
                mPhysicsTimer.setStyle(style)
                mPhysicsTimer.invalidateDial();
                mPhysicsTimer.invalidateDrawing()
            }
            btNext -> {
                val style = Settings(applicationContext).getStyle(++StyleListIndex)
                mPhysicsTimer.setStyle(style)
                mPhysicsTimer.invalidateDial();
                mPhysicsTimer.invalidateDrawing()
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

    override fun saveSetting() {
        Settings(applicationContext).saveStyleByIndex(StyleListIndex)
    }
}
