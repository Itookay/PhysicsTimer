package itookay.android.org

import android.content.Context
import android.os.Bundle
import android.view.*
import android.graphics.*
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.widget.ImageButton

import itookay.android.org.contents.PhysicsTimer
import itookay.android.org.font.FontBase
import itookay.android.org.font.FontList

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BackgroundActivity(), View.OnTouchListener, View.OnClickListener, SensorEventListener {

    /** ドラッグしたと判定する距離 */
    private val DRAGABLE_DISTANCE = 20f
    /** 無効な時間 */
    private val INVALID_TIME = 0

    private lateinit var mPhysicsTimer : PhysicsTimer
    //フォント
    private var mFont : FontBase? = null
    //スタイル
    private var mStyle : Int = -1

    /** 直前にドラッグしていた番号 */
    private var PreviousDraggingNumber : Int = INVALID_TIME
    /** 最初に選択された番号 */
    private var FirstSelectedNumber : Int = INVALID_TIME
    /** 現在ドラッグしているボタン */
    private var CurrentDraggingButton : ImageButton? = null
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
        loadPreference()

        mPhysicsTimer = PhysicsTimer(applicationContext)
        mPhysicsTimer.setFont(mFont)
        mPhysicsTimer.setStyle(mStyle)
        val surfaceView = findViewById<SurfaceView>(R.id.svMain)
        mPhysicsTimer.setSurfaceView(surfaceView)
        mPhysicsTimer.init()

        initButtonWidth(mPhysicsTimer.scale, 0.8, 0.2)
        initNumPad()
        initControlButtons()
        setListener()

        SensorMgr = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    fun loadPreference() {
        mFont = FontList.getFont(1)
        mStyle = -1
    }

    /*
     *      バックグラウンド移行の直前
     */
    override fun onPause() {
        super.onPause()

        SensorMgr.unregisterListener(this)
        mPhysicsTimer.Destroy()
    }

    /*
     *      バックグラウンドからの復帰
     */
    override fun onResume() {
        super.onResume()
        mPhysicsTimer.Resume()

        val     sensors:List<Sensor> = SensorMgr.getSensorList(Sensor.TYPE_ACCELEROMETER)
        for(sensor in sensors) {
            SensorMgr.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
        }
    }

    /**
     *      センサー値の変化
     */
    override fun onSensorChanged(event:SensorEvent) {
        if(event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val     x:Float = -event.values[0]
            val     y:Float = -event.values[1]

            mPhysicsTimer.setGravity(x, y)
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    override fun onDestroy() {
        super.onDestroy()
        mPhysicsTimer.Destroy()
    }

    /*
     *      イベントリスナーをセット
     */
    private fun setListener() {
        /* Numpad */
        for(button in NumpadButtonList) {
            button.setOnTouchListener(this)
        }

        /* 戻るボタン */
        btBack.setOnClickListener(this)
    }

    /*
     *
     */
    fun getSelectedButtonNumber(view: View?) : Int {
        var index = 0
        for(button in NumpadButtonList) {
            if(button.id == view?.id) {
                return index
            }
            index++
        }

        return -1
    }

    /*
     *      タッチイベント
     */
    override fun onTouch(view: View?, event: MotionEvent?): Boolean {
        val button = getTouchPointButton(event?.rawX!!, event.rawY)
        if(button == null) {
            clearButtonState()
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
                mPhysicsTimer.startTimer()

                //Numpadを非表示
                removeNumpad()
                //戻るボタンを表示
                showReturnButton()

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
            btBack -> {
                //removeReturnButton()
                showNumpad()
                mPhysicsTimer.stopTimer()
            }
        }
    }

    /*
     *          Numpadを表示
     */
    fun showNumpad() {
        tlNumPad.visibility = View.VISIBLE
    }

    /*
     *          Numpadを非表示にする
     */
    fun removeNumpad() {
        tlNumPad.visibility = View.INVISIBLE
    }

    /*
     *          戻るボタンを表示
     */
    fun showReturnButton() {
        btBack.visibility = View.VISIBLE
    }

    /*
     *          戻るボタンを非表示
     */
    fun removeReturnButton() {
        btBack.visibility = View.INVISIBLE
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
    private fun getTouchPointButton(_x:Float, _y:Float) : ImageButton? {
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
