package itookay.android.org

import android.app.Activity
import android.content.Context
import android.graphics.Point
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import itookay.android.org.contents.PhysicsTimer
import itookay.android.org.contents.Scale
import itookay.android.org.contents.Settings

abstract class DrawableSettingActivity : Activity(), View.OnClickListener {

    abstract fun setButtonState()

    /**  */
    lateinit var mPhysicsTimer : PhysicsTimer

    /** フォントリスト配列インデックス */
    var FontListIndex : Int = 0
    /** スタイルリスト配列インデックス */
    var StyleListIndex : Int = 0

    /** リターンボタン */
    lateinit var btReturn : Button
    /** 次のフォントボタン */
    lateinit var btNext : Button
    /** 前のフォントボタン */
    lateinit var btPrevious : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.drawable_setting_activity)
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
        btReturn = findViewById(R.id.btReturnSetting)
        btReturn.setOnClickListener(this)

        btNext = findViewById(R.id.btNext)
        btNext.setOnClickListener(this)

        btPrevious = findViewById(R.id.btPrevious)
        btPrevious.setOnClickListener(this)
    }
}
