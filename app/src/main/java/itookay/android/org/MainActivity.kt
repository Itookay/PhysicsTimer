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
import android.widget.LinearLayout
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.constraintlayout.widget.Guideline

import itookay.android.org.contents.*
import itookay.android.org.setting.MainSettingActivity

class MainActivity : Activity(), View.OnTouchListener, View.OnClickListener, SensorEventListener {

    /** ドラッグしたと判定する距離 */
    private val DRAGABLE_DISTANCE = 20f
    /** 無効な時間 */
    private val INVALID_TIME = 0

    /**  */
    private lateinit var mPhysicsTimer : PhysicsTimer
    /** NumPadボタンリスト */
    private lateinit var NumpadButtonList : List<Button>
    /** 設定ボタン */
    private lateinit var btSetting : Button
    /** タイマーストップ表示ボタン */
    private lateinit var btStopTimer : Button
    /** サーフェースビュー */
    private lateinit var svMain : SurfaceView
    /**  */
    private var NumPad : LinearLayout? = null

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

        setContentView(R.layout.main_activity)

        /* Numpadの追加 ----------------- */
          // Numpadの初期化は(センサー値によって)端末の方向が判明した時に行う
        /* ビューの初期化 ---------------- */
        initControlButton()
        initSurfaceView()
        /* Staticクラス初期化 ------------ */
        Settings.setContext(applicationContext)
        /* ----------------------------- */
        getDisplayScale()

        val font = Settings.getSavedFont()
        val style = Settings.getSavedStyle()

        mPhysicsTimer = PhysicsTimer(applicationContext)
        mPhysicsTimer.setStyle(style)
        mPhysicsTimer.setFont(font);
        val surfaceView = findViewById<SurfaceView>(R.id.svMain)
        mPhysicsTimer.setSurfaceView(surfaceView)
        mPhysicsTimer.init()

        /* ForegroundServiceが起動中 --------------- */
        if(TimeWatchingService.isAlive()) {
            numpadVisibility(false)
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

        for(button in NumpadButtonList) {
            button.setOnTouchListener(this)
        }
    }

    /**
     *      コントロールボタン初期化
     */
    fun initControlButton() {
        btStopTimer = findViewById(R.id.btStopTimer)
        btStopTimer.setOnClickListener(this)
        btSetting = findViewById(R.id.btSetting)
        btSetting.setOnClickListener(this)
    }

    fun initSurfaceView() {
        svMain = findViewById(R.id.svMain)
        svMain.setOnTouchListener(this)
    }

    private fun getDisplayScale() {
        val windowManager = applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = windowManager.defaultDisplay
        val size = Point()
        display.getSize(size)

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

        val font = Settings.getSavedFont()
        val style = Settings.getSavedStyle()
        mPhysicsTimer.setFont(font)
        mPhysicsTimer.setStyle(style)
        mPhysicsTimer.invalidateDrawing()
        mPhysicsTimer.invalidateDial()

        mPhysicsTimer.resume()
    }

    /**
     *      センサー値の変化
     */
    override fun onSensorChanged(event:SensorEvent) {
        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val     x:Float = -event.values[0]
            val     y:Float = -event.values[1]

            mPhysicsTimer.setGravity(x, y)

            val orientation = getOrientation(x, y)
            if(orientation != mPhysicsTimer.orientation) {
                mPhysicsTimer.orientation = orientation
                setNumpadConstraint(orientation)
            }
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
    private fun getOrientation(X:Float, Y:Float) : Int {
        val gravity = ControlWorld.GRAVITY / 2f;
        var orientation = PhysicsTimer.PORTRAIT

        if(-gravity < X && X < gravity){
            //通常の向き
            if(Y < 0) {
                orientation = PhysicsTimer.PORTRAIT
            }
            //端末上側が下を向いている
            if(Y > 0) {
                //ひっくり返したら通常の向きにする
                orientation = PhysicsTimer.PORTRAIT
            }
        }
        else if(-gravity < Y && Y < gravity) {
            //端末左側が下を向いている
            if(X < 0) {
                orientation = PhysicsTimer.LEFT_LANDSCAPE
            }
            //端末右側が下を向いている
            if(X > 0) {
                orientation = PhysicsTimer.RIGHT_LANDSCAPE
            }
        }

        return orientation
    }

    private fun setNumpadConstraint(orientation:Int) {
        val constraintLayout = findViewById<ConstraintLayout>(R.id.MainConstraintLayout)
        val constraintSet = ConstraintSet()
        val upperGuideline = findViewById<Guideline>(R.id.numpadGuidelineUpper)
        val bottomGuideline = findViewById<Guideline>(R.id.numpadGuidelineBottom)

        constraintSet.clone(constraintLayout)
        if(NumPad != null) {
            constraintLayout.removeViewInLayout(NumPad)
        }

        when(orientation) {
            PhysicsTimer.PORTRAIT -> {
                NumPad = layoutInflater.inflate(R.layout.numpad, null) as LinearLayout
                constraintLayout.addView(NumPad,1)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.TOP, bottomGuideline.id, ConstraintSet.TOP)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            }
            PhysicsTimer.RIGHT_LANDSCAPE -> {
                NumPad = layoutInflater.inflate(R.layout.numpad_land_right, null) as LinearLayout
                constraintLayout.addView(NumPad, 1)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.BOTTOM, upperGuideline.id, ConstraintSet.BOTTOM)
            }
            PhysicsTimer.LEFT_LANDSCAPE -> {
                NumPad = layoutInflater.inflate(R.layout.numpad_land_left, null) as LinearLayout
                constraintLayout.addView(NumPad ,1)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.TOP, bottomGuideline.id, ConstraintSet.TOP)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                constraintSet.connect((NumPad as LinearLayout).id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            }
        }

        constraintSet.applyTo(constraintLayout)
        initNumpadButtonList()
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
        if(mPhysicsTimer.state == PhysicsTimer.STATE_FINISHED) {
            mPhysicsTimer.stop()
            numpadVisibility(true)
            settingButtonVisibility(true)
            return true;
        }
        if(mPhysicsTimer.state == PhysicsTimer.STATE_PROCESSING) {
            return false
        }

        /** ドラッグ中のボタンはviewに渡されないらしい */
        val button = getTouchPointButton(event?.rawX!!, event.rawY)
        if(button == null) {
            return false;
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
                numpadVisibility(false)
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
                numpadVisibility(true)
                stopTimerButtonVisibility(false)
                settingButtonVisibility(true)
                mPhysicsTimer.stop()
            }
        }
    }

    /*
     *          Numpadの表示・非表示
     */
    fun numpadVisibility(visibility: Boolean) {
        if(visibility) {
            NumPad?.visibility = View.VISIBLE
        }
        else {
            NumPad?.visibility = View.INVISIBLE
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
