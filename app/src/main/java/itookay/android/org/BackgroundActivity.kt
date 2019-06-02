package itookay.android.org

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TableRow
import itookay.android.org.R
import itookay.android.org.contents.Scale
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlin.math.roundToInt

/**
 *          MainActivityに使用するボタンなどのWidgetを宣言・定義
 */
open class BackgroundActivity : Activity() {
    /** NumPadボタンリスト */
    open lateinit var NumpadButtonList : List<ImageButton>
    /** コントロールボタンリスト */
    open lateinit var ControlButtonList : List<ImageButton>
    /** ボタンサイズ */
    private var ButtonWidth : Int = 0
    /** ボタンパディング */
    private var ButtonPadding : Int = 0

    /** 戻るボタン */
    open lateinit var btBack : ImageButton

    @SuppressLint("ResourceType")
    fun initControlButtons() {
        btBack = ImageButton(this)
        btBack.setImageResource(R.drawable.button_state_0)
        btBack.layoutParams = LinearLayout.LayoutParams(ButtonWidth, ButtonWidth)
        btBack.visibility = View.VISIBLE
        btBack.id = 100
        btBack.scaleType = ImageView.ScaleType.FIT_XY
        btBack.setBackgroundResource(0)
        btBack.setPadding(ButtonPadding, ButtonPadding, ButtonPadding, ButtonPadding)
        llControlButton.addView(btBack)
    }

    fun initButtonWidth(scale: Scale, widthScale:Double, buttonPaddingScale:Double) {
        val numpadWidth = scale.displayWidthPixel * widthScale
        ButtonWidth = (numpadWidth / 3.0).roundToInt()
        ButtonPadding = (ButtonWidth * buttonPaddingScale).roundToInt()
    }

    fun initNumPad() {
        val button1 = ImageButton(this)
        val button2 = ImageButton(this)
        val button3 = ImageButton(this)
        val button4 = ImageButton(this)
        val button5 = ImageButton(this)
        val button6 = ImageButton(this)
        val button7 = ImageButton(this)
        val button8 = ImageButton(this)
        val button9 = ImageButton(this)
        val button0 = ImageButton(this)
        val buttonSpace1 = ImageButton(this)
        val buttonSpace2 = ImageButton(this)

        val tableRow1 = TableRow(this)
        val tableRow2 = TableRow(this)
        val tableRow3 = TableRow(this)
        val tableRow4 = TableRow(this)

        button1.setImageResource(R.drawable.button_state_1)
        button2.setImageResource(R.drawable.button_state_2)
        button3.setImageResource(R.drawable.button_state_3)
        button4.setImageResource(R.drawable.button_state_4)
        button5.setImageResource(R.drawable.button_state_5)
        button6.setImageResource(R.drawable.button_state_6)
        button7.setImageResource(R.drawable.button_state_7)
        button8.setImageResource(R.drawable.button_state_8)
        button9.setImageResource(R.drawable.button_state_9)
        button0.setImageResource(R.drawable.button_state_0)
        buttonSpace1.setImageResource(R.drawable.button_state_0)
        buttonSpace2.setImageResource(R.drawable.button_state_0)

        tableRow1.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        tableRow2.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        tableRow3.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        tableRow4.layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)

        tableRow1.addView(button1)
        tableRow1.addView(button2)
        tableRow1.addView(button3)

        tableRow2.addView(button4)
        tableRow2.addView(button5)
        tableRow2.addView(button6)

        tableRow3.addView(button7)
        tableRow3.addView(button8)
        tableRow3.addView(button9)

        tableRow4.addView(buttonSpace1)
        tableRow4.addView(button0)
        tableRow4.addView(buttonSpace2)

        tlNumPad.addView(tableRow1)
        tlNumPad.addView(tableRow2)
        tlNumPad.addView(tableRow3)
        tlNumPad.addView(tableRow4)

        buttonSpace1.visibility = View.INVISIBLE
        buttonSpace2.visibility = View.INVISIBLE

        NumpadButtonList = listOf(
            button0,
            button1,
            button2,
            button3,
            button4,
            button5,
            button6,
            button7,
            button8,
            button9,
            buttonSpace1,
            buttonSpace2
        )

        var id = 0
        for(button in NumpadButtonList) {
            button.scaleType = ImageView.ScaleType.FIT_XY
            button.layoutParams = TableRow.LayoutParams(ButtonWidth, ButtonWidth)
            button.setBackgroundResource(0)
            button.setPadding(ButtonPadding, ButtonPadding, ButtonPadding, ButtonPadding)
            button.id = id++
        }

        /*
        for(tablerow in NumpadTablerowList) {
            tablerow.layoutParams = TableLayout.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT)
        }

        tlNumPad.layoutParams = ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT)
        */
    }
}