package itookay.android.org

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import itookay.android.org.contents.PhysicsTimer
import itookay.android.org.contents.Scale
import itookay.android.org.contents.Settings

abstract class DrawableSettingActivity : AppCompatActivity(), View.OnClickListener {

    abstract fun setButtonState()

    /**  */
    lateinit var mPhysicsTimer : PhysicsTimer

    /** フォントリスト配列インデックス */
    var FontListIndex : Int = 0
    /** スタイルリスト配列インデックス */
    var StyleListIndex : Int = 0

    /** 次ボタン */
    lateinit var btNext : Button
    /** 前ボタン */
    lateinit var btPrevious : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR_OVERLAY)

        setContentView(R.layout.drawable_setting_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initButton()

        /* 設定のロード */
        val setting = Settings(applicationContext)
        val font = setting.savedFont
        val style = setting.savedStyle;

        FontListIndex = setting.savedFontIndex
        StyleListIndex = setting.savedStyleIndex

        setButtonState()

        mPhysicsTimer = PhysicsTimer(applicationContext)
        mPhysicsTimer.setStyle(style)
        mPhysicsTimer.setFont(font)
        val surfaceView = findViewById<SurfaceView>(R.id.svDrawableSetting)
        mPhysicsTimer.setSurfaceView(surfaceView)
        mPhysicsTimer.bindService(false)
        mPhysicsTimer.init()

        mPhysicsTimer.setTime(0, 12,34)
        mPhysicsTimer.start()
    }

    /**
     *      ボタンの初期化
     */
    fun initButton() {
        btNext = findViewById(R.id.btNext)
        btNext.setOnClickListener(this)

        btPrevious = findViewById(R.id.btPrevious)
        btPrevious.setOnClickListener(this)
    }

    /**
     *      アクションバーのボタン処理
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            android.R.id.home -> {
                mPhysicsTimer.stop()
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
