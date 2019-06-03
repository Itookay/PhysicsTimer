package itookay.android.org

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.*
import android.graphics.*
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.support.constraint.ConstraintLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow

import itookay.android.org.BackgroundActivity
import itookay.android.org.contents.MainSurfaceView
import itookay.android.org.contents.PhysicsTimer
import itookay.android.org.contents.Scale
import itookay.android.org.font.FontBase
import itookay.android.org.font.FontList

import kotlin.math.*

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BackgroundActivity(), View.OnTouchListener, View.OnClickListener, SensorEventListener {

    //ドラッグしたと判定する距離
    private val DRAGABLE_DISTANCE = 20f

    private lateinit var mPhysicsTimer : PhysicsTimer
    //フォント
    private var mFont : FontBase? = null
    //スタイル
    private var mStyle : Int = -1

    //選択された1桁目
    private var SelectedNumber1 : Int = -1
    //選択された2桁目
    private var SelectedNumber2 : Int = -1
    //2つ目のボタンまでにドラッグされたか
    private var IsDragged = false
    //最初にタップされたポイント
    private var ActionDownPoint : PointF = PointF()
    //最初にタップされたボタン
    private var FirstTappedButton : ImageButton? = null
    //指が移動中に触れているボタン
    private var DraggingButton : ImageButton? = null

    //センサーマネージャ
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
        FirstTappedButton?.isPressed = false
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
            invalidButtonInput()
            return false
        }

        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                SelectedNumber1 = getSelectedButtonNumber(button)
                ActionDownPoint = PointF(event.rawX, event.rawY)
                FirstTappedButton = button

                button.performClick()
                button.isPressed = true
            }
            MotionEvent.ACTION_UP -> {
                SelectedNumber2 = getSelectedButtonNumber(button)

                val time = getInputTime()
                /* 数字をブロックで表示 */
                mPhysicsTimer.setInitialTime(0, time, 0)
                mPhysicsTimer.startTimer()

                //Numpadを非表示
                removeNumpad()
                //戻るボタンを表示
                showReturnButton()

                button.performClick()
                button.isPressed = true
            }
            MotionEvent.ACTION_MOVE -> {
                IsDragged = isDragging(event.rawX, event.rawY)
                if(IsDragged) {
                    button.isPressed = true
                    if(DraggingButton != button && FirstTappedButton != button) {
                        /* スライド中の数字を表示 */
                        val time = getInputTime()
                        mPhysicsTimer.setInitialTime(0, time, 0)

                        DraggingButton?.isPressed = false
                        DraggingButton = button
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
     *      ボタン入力が無効
     */
    private fun invalidButtonInput() {
        SelectedNumber1 = 0
        SelectedNumber2 = 0
        IsDragged = false
        ActionDownPoint = PointF()
        FirstTappedButton = null

        //全ての選択状態を解除
        for(button in NumpadButtonList) {
            button.isPressed = false
        }
    }

    /*
     *      入力された数字(分)を取得
     */
    private fun getInputTime() : Int {
        if(SelectedNumber1 == SelectedNumber2) {
            if(IsDragged == false) {
                SelectedNumber1 = 0
                IsDragged = false
            }
        }

        val timeString = SelectedNumber1.toString() + SelectedNumber2.toString()
        return timeString.toInt()
    }

    /*
     *      最初にタップした位置からドラッグされているか
     */
    private fun isDragging(x:Float, y:Float) : Boolean {
        val distanceX = (ActionDownPoint.x - x).toDouble()
        val distanceY = (ActionDownPoint.y - y).toDouble()

        val distance = Math.sqrt(Math.pow(distanceX.toDouble(), 2.0) + Math.pow(distanceY, 2.0))
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
