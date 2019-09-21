package itookay.android.org.setting

import android.os.Bundle
import android.view.*
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat

import itookay.android.org.R
import itookay.android.org.contents.PhysicsTimer

abstract class DrawableSettingActivity : AppCompatActivity(), View.OnClickListener {

    /** ボタンの状態をセット */
    abstract fun setButtonState()
    /** 選択された設定を保存 */
    abstract fun saveSetting()

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
        val font = Settings.getFont(applicationContext)
        val style = Settings.getStyle(applicationContext)

        FontListIndex = Settings.getFontIndex(applicationContext)
        StyleListIndex = Settings.getStyleIndex(applicationContext)

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
                saveSetting()
                this.finish()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}
