package itookay.android.org

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.Button
import itookay.android.org.contents.ControlWorld

import itookay.android.org.contents.PhysicsTimer
import itookay.android.org.contents.Scale
import itookay.android.org.contents.Settings

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : Activity(), View.OnTouchListener, View.OnClickListener, SensorEventListener {

    /** ドラッグしたと判定する距離 */
    private val DRAGABLE_DISTANCE = 20f
    /** 無効な時間 */
    private val INVALID_TIME = 0

    /**  */
    private lateinit var mPhysicsTimer : PhysicsTimer
    /** NumPadボタンリスト */
    lateinit var NumpadButtonList : List<Button>
    /** 設定ボタン */
    lateinit var btSetting : Button
    /** タイマーストップ表示ボタン */
    lateinit var btStopTimer : Button

    /** 直前にドラッグしていた番号 */
    private var PreviousDraggingNumber : Int = INVALID_TIME
    /** 最初に選択された番号 */
    private var FirstSelectedNumber : Int = INVALID_TIME
    /** 現在ドラッグしているボタン */
    private var CurrentDraggingButton : Button? = null
    /** 2つ目のボタンまでにドラッグされたか */
    private var IsDragged = false
    /** 最初にタップされたポイント */
    private var ActionDownPoint : PointF = PointF()

    /** センサーマネージャ */
    private lateinit var SensorMgr : SensorManager

    /*
     *      onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_main)
        /* ボタンの初期化 ------ */
        initNumpadButtonList()
        initControlButton()
        setButtonListener()
        /* ------------------- */
        getDisplayScale()

        val setting = Settings(applicationContext)
        val font = setting.savedFont
        val style = setting.savedStyle;

        mPhysicsTimer = PhysicsTimer(applicationContext)
        mPhysicsTimer.setStyle(style)
        mPhysicsTimer.setFont(font);
        val surfaceView = findViewById<SurfaceView>(R.id.svMain)
        mPhysicsTimer.setSurfaceView(surfaceView)
        mPhysicsTimer.init()

        /* ForegroundServiceが起動中 --------------- */
        if(mPhysicsTimer.isAlive) {
            numpadVisivility(false)
            settingButtonVisibility(false)
            stopTimerButtonVisibility(true)
            mPhysicsTimer.resume()
        }
        /* ---------------------------------------- */
    }

    fun initNumpadButtonList() {
        NumpadButtonList = listOf(
            findViewById(R.id.btNum0),
            findViewById(R.id.btNum1),
            findViewById(R.id.btNum2),
            findViewById(R.id.btNum3),
            findViewById(R.id.btNum4),
            findViewById(R.id.btNum5),
            findViewById(R.id.btNum6),
            findViewById(R.id.btNum7),
            findViewById(R.id.btNum8),
            findViewById(R.id.btNum9))
    }

    /**
     *      コントロールボタンを表示<br>
     *      Numpad表示時：設定ボタン<br>
     *      タイマー表示時：Numpadに戻るボタン
     */
    fun initControlButton() {
        btStopTimer = findViewById(R.id.btStopTimer)
        btSetting = findViewById(R.id.btSetting)
    }

    /*
     *      イベントリスナーをセット
     */
    private fun setButtonListener() {
        /* Numpad */
        for(button in NumpadButtonList) {
            button.setOnTouchListener(this)
        }

        btSetting.setOnClickListener(this)
        btStopTimer.setOnClickListener(this)
    }

    private fun getDisplayScale() {
        val windowManager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val disp = windowManager.defaultDisplay
        val size = Point()
        disp.getSize(size)

        Scale.setDisplay(size.x, size.y, Scale.DISPLAY_HEIGHT_IN_METER)
    }

    /*
     *      バックグラウンド移行の直前
     */
    override fun onPause() {
        super.onPause()

        SensorMgr.unregisterListener(this)
        mPhysicsTimer.pause()
    }

    /*
     *      バックグラウンドからの復帰
     */
    override fun onResume() {
        super.onResume()

        SensorMgr = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val     sensors:List<Sensor> = SensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER)
        for(sensor in sensors) {
            SensorMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }

        mPhysicsTimer.resume()
    }

    /**
     *      センサー値の変化
     */
    override fun onSensorChanged(event:SensorEvent) {
        if(mPhysicsTimer.isReadyToStart == false) {
            return;
        }

        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val     x:Float = -event.values[0]
            val     y:Float = -event.values[1]

            mPhysicsTimer.setGravity(x, y)
            checkOrientation(x, y)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    /**
     *      端末向きを検出してDialの向きを変える
     */
    private fun checkOrientation(X:Float, Y:Float) {
        val gravity = ControlWorld.GRAVITY / 2f;

        if(-gravity < X && X < gravity){
            //通常の向き
            if(Y < 0) {
                mPhysicsTimer.setOrientation(PhysicsTimer.PORTRAIT)
            }
            //端末上側が下を向いている
            if(Y > 0) {
                //ひっくり返したら通常の向きにする
                mPhysicsTimer.setOrientation(PhysicsTimer.PORTRAIT)
            }
        }
        else if(-gravity < Y && Y < gravity) {
            //端末左側が下を向いている
            if(X < 0) {
                mPhysicsTimer.setOrientation(PhysicsTimer.LEFT_LANDSCAPE)
            }
            //端末右側が下を向いている
            if(X > 0) {
                mPhysicsTimer.setOrientation(PhysicsTimer.RIGHT_LANDSCAPE)
            }
        }
    }

    /*
     *      選択されたボタンから数字を取得
     */
    fun getSelectedButtonNumber(view: View?) : Int {
        var     index = 0;
        for(button in NumpadButtonList) {
            if(button.id == view?.id) {
                return index;
            }
            index++;
        }

        return -1
    }

    /*
     *      タッチイベント
     */
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        /** ドラッグ中のボタンはviewに渡されないらしい */
        val button = getTouchPointButton(event?.rawX!!, event.rawY)
        if(button == null) {
            return false
        }

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                FirstSelectedNumber = getSelectedButtonNumber(button)
                ActionDownPoint = PointF(event.rawX, event.rawY)

                mPhysicsTimer.setTime(0, FirstSelectedNumber, 0)

                button.performClick()
                button.isPressed = true
            }
            MotionEvent.ACTION_UP -> {
                mPhysicsTimer.start()

                //Numpadを非表示
                numpadVisivility(false)
                //タイマーストップボタンを表示
                stopTimerButtonVisibility(true)
                //設定ボタンを非表示
                settingButtonVisibility(false)

                button.performClick()
                clearButtonState()
            }
            MotionEvent.ACTION_MOVE -> {
                IsDragged = isDragging(event.rawX, event.rawY)
                if(IsDragged == false) {
                    return true
                }

                var time = ""
                button.isPressed = true
                val currentDraggingNumber = getSelectedButtonNumber(button)
                //1桁目と同じボタンをドラッグ
                if(currentDraggingNumber == FirstSelectedNumber) {
                    time = FirstSelectedNumber.toString() + FirstSelectedNumber.toString()
                    PreviousDraggingNumber = currentDraggingNumber
                    mPhysicsTimer.setTime(0, time.toInt(), 0)
                }
                //2桁目以降をドラッグ
                else {
                    if(currentDraggingNumber != PreviousDraggingNumber) {
                        time = FirstSelectedNumber.toString() + currentDraggingNumber.toString()
                        PreviousDraggingNumber = currentDraggingNumber
                        mPhysicsTimer.setTime(0, time.toInt(), 0)

                        if(CurrentDraggingButton == null) {
                            CurrentDraggingButton = button
                        }
                        else {
                            CurrentDraggingButton!!.isPressed = false
                            CurrentDraggingButton = button
                        }
                    }
                }
            }
        }

        return true
    }

    /**
     *          クリックイベント
     */
    override fun onClick(view: View?) {
        when(view) {
            btSetting -> {
                val intent = Intent(this, MainSettingActivity::class.java)
                startActivity(intent)
            }
            btStopTimer -> {
                numpadVisivility(true)
                stopTimerButtonVisibility(false)
                settingButtonVisibility(true)
                mPhysicsTimer.stop()
            }
        }
    }

    /*
     *          Numpadの表示・非表示
     */
    fun numpadVisivility(visivility: Boolean) {
        if(visivility) {
            NumPad.visibility = View.VISIBLE
        }
        else {
            NumPad.visibility = View.INVISIBLE
        }
    }

    /*
     *        設定ボタンの表示・非表示
     */
    fun settingButtonVisibility(visibility: Boolean) {
        if(visibility) {
            btSetting.visibility = View.VISIBLE
        }
        else {
            btSetting.visibility = View.INVISIBLE
        }
    }

    /*
     *        タイマーストップボタンの表示・非表示
     */
    fun stopTimerButtonVisibility(visibility: Boolean) {
        if(visibility) {
            btStopTimer.visibility = View.VISIBLE
        }
        else {
            btStopTimer.visibility = View.INVISIBLE
        }
    }

    /*
     *      ボタンの選択状態を初期化
     */
    private fun clearButtonState() {
        FirstSelectedNumber = INVALID_TIME
        IsDragged = false
        ActionDownPoint = PointF()

        //全ての選択状態を解除
        for(button in NumpadButtonList) {
            button.isPressed = false
        }
    }

    /*
     *      最初にタップした位置からドラッグされているか
     */
    private fun isDragging(x:Float, y:Float) : Boolean {
        val distanceX = (ActionDownPoint.x - x).toDouble()
        val distanceY = (ActionDownPoint.y - y).toDouble()

        val distance = Math.sqrt(Math.pow(distanceX, 2.0) + Math.pow(distanceY, 2.0))
        return (distance >= DRAGABLE_DISTANCE)
    }

    /*
     *      タッチされたポイントにあるボタンを取得
     */
    private fun getTouchPointButton(_x:Float, _y:Float) : Button? {
        for(button in NumpadButtonList) {
            val location = IntArray(2)
            button.getLocationInWindow(location)

            val right = location[0] + button.width
            val bottom = location[1] + button.height
            if(location[0] <= _x && right >= _x) {
                if(location[1] <= _y && bottom >= _y) {
                    return button
                }
            }
        }

        return null
    }
}
