package itookay.android.org.setting

import android.view.View

class StyleSettingActivity : DrawableSettingActivity() {

    /**
     *      ボタンクリック
     */
    override fun onClick(view: View?) {
        when(view) {
            btPrevious -> {
                val style = Settings.getStyle(--StyleListIndex)
                mPhysicsTimer.setStyle(style)
                mPhysicsTimer.invalidateDial();
                mPhysicsTimer.invalidateDrawing()
            }
            btNext -> {
                val style = Settings.getStyle(++StyleListIndex)
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
        btNext.isEnabled = (Settings.getStyle(StyleListIndex + 1) != null)
        btPrevious.isEnabled = (Settings.getStyle(StyleListIndex - 1) != null)
    }

    override fun saveSetting() {
        Settings.saveStyleByIndex(applicationContext, StyleListIndex)
    }
}
