package com.tbruyelle.donkor.cn.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.tbruyelle.rxpermissions3.R

class WifiSignalView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    // 可配置属性
    private var signalColor = Color.BLUE
    private var inactiveColor = Color.LTGRAY
    private var barCount = 4  // 标准WiFi信号条数
    private var barWidth = 10f
    private var barSpacing = 4f
    private var barCornerRadius = 2f

    // 当前信号强度 (0-4)
    private var signalLevel = 0
        set(value) {
            field = value.coerceIn(0, barCount)
            invalidate()
        }

    init {
        // 从XML属性读取配置
        val ta = context.obtainStyledAttributes(attrs, R.styleable.WifiSignalView)
        signalColor = ta.getColor(R.styleable.WifiSignalView_signalColor, signalColor)
        inactiveColor = ta.getColor(R.styleable.WifiSignalView_inactiveColor, inactiveColor)
        barCount = ta.getInt(R.styleable.WifiSignalView_barCount, barCount)
        barWidth = ta.getDimension(R.styleable.WifiSignalView_barWidth, barWidth)
        barSpacing = ta.getDimension(R.styleable.WifiSignalView_barSpacing, barSpacing)
        barCornerRadius = ta.getDimension(R.styleable.WifiSignalView_barCornerRadius, barCornerRadius)
        ta.recycle()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // 计算View的理想大小
        val desiredWidth = (barWidth * barCount + barSpacing * (barCount - 1)).toInt()
        val desiredHeight = (barWidth * barCount * 1.5f).toInt() // 高度比宽度大一些

        val width = resolveSize(desiredWidth, widthMeasureSpec)
        val height = resolveSize(desiredHeight, heightMeasureSpec)
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val totalBarHeight = height.toFloat()
        val barHeightStep = totalBarHeight / barCount

        for (i in 0 until barCount) {
            val barHeight = barHeightStep * (i + 1)
            val barLeft = i * (barWidth + barSpacing)
            val barRight = barLeft + barWidth
            val barTop = height - barHeight
            val barBottom = height.toFloat()

            val paint = Paint().apply {
                color = if (i < signalLevel) signalColor else inactiveColor
                style = Paint.Style.FILL
                isAntiAlias = true
            }

            // 绘制圆角矩形信号条
            canvas.drawRoundRect(
                barLeft, barTop, barRight, barBottom,
                barCornerRadius, barCornerRadius, paint
            )
        }
    }

    // 更新信号强度 (0-4)
    fun updateSignalLevel(level: Int) {
        this.signalLevel = level
    }

    // 更新信号强度 (RSSI值)
    fun updateSignalStrength(rssi: Int) {
        // 将RSSI转换为0-4的信号等级
        signalLevel = when {
            rssi >= -50 -> 4  // 最强
            rssi >= -60 -> 3
            rssi >= -70 -> 2
            rssi >= -80 -> 1
            else -> 0          // 最弱
        }
    }
}