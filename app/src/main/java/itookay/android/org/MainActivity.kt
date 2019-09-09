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
import itookay.android.org.setting.Settings
import itookay.android.org.debug.*
import itookay.android.org.setting.VibrationList


class MainActivity : Activity(), View.OnTouchListener, View.OnClickListener, SensorEventListener, View.OnLongClickListener {

    /** ドラッグしたと判定する距離 */
    private val DRAGABLE_DISTANCE = 50f
    /** 無効な時間 */
    private val INVALID_NUMBER = -1

    /**  */
    private lateinit var physicsTimer : PhysicsTimer
    /** numericPadボタンリスト */
    private lateinit var numericPadButtonList : List<Button>
    /** 設定ボタン */
    private lateinit var btSetting : Button
    /** タイマーストップ表示ボタン */
    private lateinit var btStopTimer : Button
    /** サーフェースビュー */
    private lateinit var svMain : SurfaceView
    /**  */
    private var numericPadLayout : LinearLayout? = null

    /** 直前にドラッグしていた番号 */
    private var previousDraggingNumber : Int = INVALID_NUMBER
    /** 最初に選択された番号 */
    private var firstSelectedNumber : Int = INVALID_NUMBER
    /** 現在ドラッグしているボタン */
    private var currentDraggingButton : Button? = null
    /** 2つ目のボタンまでにドラッグされたか */
    private var isDragged = false
    /** 最初にタップされたポイント */
    private var actionDownPoint : PointF = PointF()

    /** センサーマネージャ */
    private lateinit var sensorMgr : SensorManager

    /** デバッグモード */
    private var isDebugMode = false

    /*
     *      onCreate
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        Debug.startLog();
        Debug.calledLog();

        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        requestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.main_activity)

        /* numericPadの追加 ----------------- */
          // NumericPadの初期化は(センサー値によって)端末の方向が判明した時に行う
        /* ビューの初期化 ---------------- */
        initControlButton()
        initSurfaceView()
        /* ----------------------------- */
        getDisplayScale()
        TimeWatchingService.setContext(applicationContext);

        val font = Settings.getSavedFont(applicationContext)
        val style = Settings.getStyle(applicationContext)

        physicsTimer = PhysicsTimer(this)
        physicsTimer.setStyle(style)
        physicsTimer.setFont(font);
        val surfaceView = findViewById<SurfaceView>(R.id.svMain)
        physicsTimer.setSurfaceView(surfaceView)
        physicsTimer.init()

        /* ForegroundServiceが起動中 --------------- */
        if(PhysicsTimer.getState() == PhysicsTimer.STATE_PROCESSING) {
            numericPadVisibility(false)
            settingButtonVisibility(false)
            stopTimerButtonVisibility(true)
            physicsTimer.resume()
        }
        /* ---------------------------------------- */
    }

    fun initNumericPadButtonList() {
        Debug.calledLog();
        numericPadButtonList = listOf(
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

        for(button in numericPadButtonList) {
            button.setOnTouchListener(this)
        }
    }

    /**
     *      コントロールボタン初期化
     */
    fun initControlButton() {
        Debug.calledLog();
        btStopTimer = findViewById(R.id.btStopTimer)
        btStopTimer.setOnClickListener(this)
        btSetting = findViewById(R.id.btSetting)
        btSetting.setOnClickListener(this)
        btSetting.setOnLongClickListener(this)
    }

    fun initSurfaceView() {
        Debug.calledLog()
        svMain = findViewById(R.id.svMain)
        svMain.setOnTouchListener(this)
    }

    private fun getDisplayScale() {
        Debug.calledLog()
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
        Debug.calledLog()
        super.onPause()

        sensorMgr.unregisterListener(this)
        physicsTimer.pause()
    }

    /*
     *      バックグラウンドからの復帰
     */
    override fun onResume() {
        Debug.calledLog()
        super.onResume()

        sensorMgr = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val     sensors:List<Sensor> = sensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER)
        for(sensor in sensors) {
            sensorMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }

        val font = Settings.getSavedFont(applicationContext)
        val style = Settings.getStyle(applicationContext)
        physicsTimer.setFont(font)
        physicsTimer.setStyle(style)
        physicsTimer.invalidateDrawing()
        physicsTimer.invalidateDial()

        //タイマーの再開
        physicsTimer.resume()

        //タイマー終了後、サウンドとバイブレーションで通知中の場合
        if(PhysicsTimer.getState() == PhysicsTimer.STATE_FINISHED) {
            TimeWatchingService.stopAlarm()
            TimeWatchingService.cancelNotification()
            physicsTimer.stop()
        }
    }

    /**
     *      センサー値の変化
     */
    override fun onSensorChanged(event:SensorEvent) {
        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val     x:Float = -event.values[0]
            val     y:Float = -event.values[1]

            physicsTimer.setGravity(x, y)

            val orientation = getOrientation(x, y)
            //センサー値が検出範囲外
            if(orientation == PhysicsTimer.ORIENTATION_RANGE_OUT) {
                return
            }
            if(orientation != physicsTimer.orientation) {
                physicsTimer.orientation = orientation
                setNumericPadConstraint(orientation)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    /**
     *      端末向きを検出してDialの向きを変える
     */
    private fun getOrientation(X:Float, Y:Float) : Int {
        Debug.log("Gravity ${X}, ${Y}")

        val gravity = ControlWorld.GRAVITY / 8f;
        var orientation = PhysicsTimer.ORIENTATION_RANGE_OUT

        if(-gravity < X && X < gravity){
            //通常の向き
            if(Y < -gravity) {
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

    private fun setNumericPadConstraint(orientation:Int) {
        Debug.calledLog()

        val constraintLayout = findViewById<ConstraintLayout>(R.id.MainConstraintLayout)
        val constraintSet = ConstraintSet()
        val upperGuideline = findViewById<Guideline>(R.id.numericPadGuidelineUpper)
        val bottomGuideline = findViewById<Guideline>(R.id.numericPadGuidelineBottom)

        constraintSet.clone(constraintLayout)
        if(numericPadLayout != null) {
            constraintLayout.removeViewInLayout(numericPadLayout)
        }

        when(orientation) {
            PhysicsTimer.PORTRAIT -> {
                numericPadLayout = layoutInflater.inflate(R.layout.numeric_pad, null) as LinearLayout
                constraintLayout.addView(numericPadLayout,1)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.TOP, bottomGuideline.id, ConstraintSet.TOP)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            }
            PhysicsTimer.RIGHT_LANDSCAPE -> {
                numericPadLayout = layoutInflater.inflate(R.layout.numeric_pad_land_right, null) as LinearLayout
                constraintLayout.addView(numericPadLayout, 1)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.TOP, ConstraintSet.PARENT_ID, ConstraintSet.TOP)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.BOTTOM, upperGuideline.id, ConstraintSet.BOTTOM)
            }
            PhysicsTimer.LEFT_LANDSCAPE -> {
                numericPadLayout = layoutInflater.inflate(R.layout.numeric_pad_land_left, null) as LinearLayout
                constraintLayout.addView(numericPadLayout ,1)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.TOP, bottomGuideline.id, ConstraintSet.TOP)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.RIGHT, ConstraintSet.PARENT_ID, ConstraintSet.RIGHT)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.LEFT, ConstraintSet.PARENT_ID, ConstraintSet.LEFT)
                constraintSet.connect((numericPadLayout as LinearLayout).id, ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID, ConstraintSet.BOTTOM)
            }
        }

        constraintSet.applyTo(constraintLayout)
        initNumericPadButtonList()

        if(PhysicsTimer.getState() == PhysicsTimer.STATE_PROCESSING) {
            numericPadVisibility(false);
        }
    }

    /*
     *      選択されたボタンから数字を取得
     *      ボタンが存在すればその数字を、なければINVALID_NUMBER_BUTTONを返す
     */
    private fun getSelectedButtonNumber(view: View?) : Int {
        Debug.calledLog()

        var     index = 0;
        for(button in numericPadButtonList) {
            if(button.id == view?.id) {
                return index;
            }
            index++;
        }

        return INVALID_NUMBER
    }

    /*
     *      タッチイベント
     */
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        Debug.calledLog()

        if(PhysicsTimer.getState() == PhysicsTimer.STATE_FINISHED) {
            physicsTimer.stop()
            numericPadVisibility(true)
            settingButtonVisibility(true)
            return true;
        }
        if(PhysicsTimer.getState() == PhysicsTimer.STATE_PROCESSING) {
            return true
        }

        //座標からボタンを取得
        val button = getTouchPointButton(event?.rawX!!, event.rawY)

        var second = 0
        var minute = 0
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                Debug.log("ACTION_DOWN")

                if(button == null) {
                    return true
                }

                firstSelectedNumber = getSelectedButtonNumber(button)
                actionDownPoint = PointF(event.rawX, event.rawY)

                /* ----- デバッグモード ----- */
                if(isDebugMode) {
                    second = 2
                    minute = 0
                }
                else {
                    minute = firstSelectedNumber
                }
                /* ------------------------ */
                physicsTimer.setTime(0, minute, second)

                button.performClick()
                button.isPressed = true
            }
            MotionEvent.ACTION_UP -> {
                Debug.log("ACTION_UP")

                //ボタン領域外
                if(button == null) {
                    clearTime()
                    return true
                }
                /* 時間が00:00 */
                val time = physicsTimer.time
                if(time.minute == 0 && time.second == 0) {
                    clearTime()
                    return true
                }

                physicsTimer.start()

                //NumericPadを非表示
                numericPadVisibility(false)
                //タイマーストップボタンを表示
                stopTimerButtonVisibility(true)
                //設定ボタンを非表示
                settingButtonVisibility(false)

                button.performClick()
                clearButtonState()
            }
            MotionEvent.ACTION_MOVE -> {
                Debug.log("ACTION_MOVE")

                if(button == null) {
                    return true
                }

                isDragged = isDragging(event.rawX, event.rawY)
                if(isDragged == false) {
                    return true
                }

                var timeString = ""
                button.isPressed = true
                val currentDraggingNumber = getSelectedButtonNumber(button)

                //1桁目と同じボタンをドラッグ
                if(currentDraggingNumber == firstSelectedNumber) {
                    timeString = firstSelectedNumber.toString() + firstSelectedNumber.toString()
                    previousDraggingNumber = currentDraggingNumber

                    /* ----- デバッグモード ----- */
                    if(isDebugMode) {
                        second = 2
                        minute = 0
                    }
                    else {
                        minute = timeString.toInt()
                    }
                    /* ------------------------ */
                    physicsTimer.setTime(0, minute, second)
                }
                //2桁目以降をドラッグ
                else if(currentDraggingNumber != previousDraggingNumber) {
                    timeString = firstSelectedNumber.toString() + currentDraggingNumber.toString()
                    previousDraggingNumber = currentDraggingNumber

                    /* ----- デバッグモード ----- */
                    if(isDebugMode) {
                        second = 2
                        minute = 0
                    }
                    else {
                        minute = timeString.toInt()
                    }
                    /* ------------------------ */
                    physicsTimer.setTime(0, minute, second)

                    if(currentDraggingButton == null) {
                        currentDraggingButton = button
                    }
                    else {
                        currentDraggingButton!!.isPressed = false
                        currentDraggingButton = button
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
                Debug.log2("btSetting pressed.")
                val intent = Intent(this, MainSettingActivity::class.java)
                startActivity(intent)
            }
            btStopTimer -> {
                Debug.log2("btStopTimer pressed")

                numericPadVisibility(true)
                stopTimerButtonVisibility(false)
                settingButtonVisibility(true)

                physicsTimer.stop()
                TimeWatchingService.stopAlarm();
            }
        }
    }

    /*
     *          numericPadの表示・非表示
     */
    fun numericPadVisibility(visibility: Boolean) {
        Debug.calledLog()
        if(visibility) {
            numericPadLayout?.visibility = View.VISIBLE
        }
        else {
            numericPadLayout?.visibility = View.INVISIBLE
        }
    }

    /*
     *        設定ボタンの表示・非表示
     */
    fun settingButtonVisibility(visibility: Boolean) {
        Debug.calledLog()
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
        Debug.calledLog()
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
        Debug.calledLog()
        firstSelectedNumber = INVALID_NUMBER
        isDragged = false
        actionDownPoint = PointF()

        //全ての選択状態を解除
        for(button in numericPadButtonList) {
            button.isPressed = false
        }
    }

    /*
     *      最初にタップした位置からドラッグされているか
     */
    private fun isDragging(x:Float, y:Float) : Boolean {
        val distanceX = (actionDownPoint.x - x).toDouble()
        val distanceY = (actionDownPoint.y - y).toDouble()

        val distance = Math.sqrt(Math.pow(distanceX, 2.0) + Math.pow(distanceY, 2.0))
        return (distance >= DRAGABLE_DISTANCE)
    }

    /*
     *      タッチされたポイントにあるボタンを取得
     */
    private fun getTouchPointButton(_x:Float, _y:Float) : Button? {
        Debug.calledLog()
        for(button in numericPadButtonList) {
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

    /**
     *      ボタンロングクリック
     */
    override fun onLongClick(view: View?): Boolean {
        if(view === btSetting) {
            isDebugMode = !isDebugMode
            VibrationList.vibrate(applicationContext, 4, VibrationList.NOT_REPEAT)
            btSetting.performLongClick()
        }

        return true
    }

    /**
     *      時間をクリア
     */
    private fun clearTime() {
        physicsTimer.invalidateDrawing()
        clearButtonState()
    }
}
