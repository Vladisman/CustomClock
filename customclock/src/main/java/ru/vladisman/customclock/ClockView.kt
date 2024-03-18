package ru.vladisman.customclock

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PointF
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.View
import androidx.core.content.withStyledAttributes
import java.util.Calendar
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class ClockView @JvmOverloads constructor(
  context: Context,
  attrs: AttributeSet? = null,
  defStyleAttr: Int = 0,
  defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

  private companion object {
    const val DEFAULT_SIZE = 240
    const val REFRESH_DELAY = 180L
    const val BASE_ANGLE = -Math.PI / 2

    const val DEFAULT_BACK_COLOR = Color.WHITE
    const val DEFAULT_DIGITS_COLOR = Color.BLACK
    const val DEFAULT_FRAME_COLOR = Color.BLACK
    const val DEFAULT_DOTS_COLOR = Color.BLACK
    const val DEFAULT_HOUR_HAND_COLOR = Color.BLACK
    const val DEFAULT_MINUTE_HAND_COLOR = Color.BLACK
    const val DEFAULT_SECOND_HAND_COLOR = Color.RED
  }

  private val calendar = Calendar.getInstance()

  // Координаты
  private var centerX = 0f
  private var centerY = 0f
  private val position = PointF(0f, 0f)

  // Размеры
  private var clockRadius = 0f

  // Цвета
  private var backColor = 0
  private var digitsColor = 0
  private var frameColor = 0
  private var dotsColor = 0
  private var hourHandColor = 0
  private var minuteHandColor = 0
  private var secondHandColor = 0

  init {
    // Извлечение атрибутов
    context.withStyledAttributes(attrs, R.styleable.ClockView, defStyleAttr, defStyleRes) {
      backColor = getColor(R.styleable.ClockView_backColor, DEFAULT_BACK_COLOR)
      frameColor = getColor(R.styleable.ClockView_frameColor, DEFAULT_FRAME_COLOR)
      digitsColor = getColor(R.styleable.ClockView_digitsColor, DEFAULT_DIGITS_COLOR)
      dotsColor = getColor(R.styleable.ClockView_dotsColor, DEFAULT_DOTS_COLOR)
      hourHandColor = getColor(R.styleable.ClockView_hourHandColor, DEFAULT_HOUR_HAND_COLOR)
      minuteHandColor = getColor(R.styleable.ClockView_minuteHandColor, DEFAULT_MINUTE_HAND_COLOR)
      secondHandColor = getColor(R.styleable.ClockView_secondHandColor, DEFAULT_SECOND_HAND_COLOR)
    }
  }

  // Кисти
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
    textAlign = Paint.Align.CENTER
    textScaleX = 0.9f
    letterSpacing = -0.15f
    typeface = Typeface.DEFAULT
  }

  private fun Paint.forDigits() = apply {
    textSize = clockRadius * 0.3f
    strokeWidth = 0f
    color = digitsColor
  }

  private fun Paint.forDots() = apply {
    color = dotsColor
    style = Paint.Style.FILL
  }

  private fun Paint.forBack() = apply {
    color = backColor
    style = Paint.Style.FILL
  }

  private fun Paint.forFrame() = apply {
    color = frameColor
    style = Paint.Style.STROKE
    strokeWidth = clockRadius * 0.1f
  }

  private fun Paint.forHourHand() = apply {
    style = Paint.Style.STROKE
    color = hourHandColor
    strokeWidth = clockRadius * 0.055f
  }

  private fun Paint.forMinuteHand() = apply {
    style = Paint.Style.STROKE
    color = minuteHandColor
    strokeWidth = clockRadius * 0.03f
  }

  private fun Paint.forSecondHand() = apply {
    style = Paint.Style.STROKE
    color = secondHandColor
    strokeWidth = clockRadius * 0.015f
  }

  // Основа часов
  private fun drawClockBack(canvas: Canvas) {
    canvas.drawCircle(centerX, centerY, clockRadius, paint.forBack())
  }

  // Рамка часов
  private fun drawClockFrame(canvas: Canvas) {
    paint.forFrame()
    val boundaryRadius = clockRadius - paint.strokeWidth / 2
    canvas.drawCircle(centerX, centerY, boundaryRadius, paint)
  }

  // Минутные точки
  private fun drawClockDots(canvas: Canvas) {
    val circleOfPoints = clockRadius * 0.85f
    for (i in 0 until 60) {
      position.computeXYForDots(i, circleOfPoints)
      val dotRadius = if (i % 5 == 0) clockRadius / 96 else clockRadius / 128
      canvas.drawCircle(position.x, position.y, dotRadius, paint.forDots())
    }
  }

  // Цифры
  private fun drawClockDigits(canvas: Canvas) {
    val circleOfPoints = clockRadius * 0.7f
    for (i in 1..12) {
      position.computeXYForDigits(i, circleOfPoints)
      canvas.drawText(i.toString(), position.x, position.y, paint.forDigits())
    }
  }

  // Стрелки
  private fun drawClockHands(canvas: Canvas) {
    val hour = calendar.get(Calendar.HOUR)
    val minute = calendar.get(Calendar.MINUTE)
    val second = calendar.get(Calendar.SECOND)
    drawHourHand(canvas, hour + minute / 60f)
    drawMinuteHand(canvas, minute)
    drawSecondHand(canvas, second)
  }

  // Стрелка часов
  private fun drawHourHand(canvas: Canvas, hourWithMinutes: Float) {
    val angle = (Math.PI * hourWithMinutes / 6 + BASE_ANGLE).toFloat()
    canvas.drawLine(
      centerX - cos(angle) * clockRadius * 0.2f,
      centerY - sin(angle) * clockRadius * 0.2f,
      centerX + cos(angle) * clockRadius * 0.5f,
      centerY + sin(angle) * clockRadius * 0.5f,
      paint.forHourHand()
    )
  }

  // Стрелка минут
  private fun drawMinuteHand(canvas: Canvas, minute: Int) {
    val angle = (Math.PI * minute / 30 + BASE_ANGLE).toFloat()
    canvas.drawLine(
      centerX - cos(angle) * clockRadius * 0.3f,
      centerY - sin(angle) * clockRadius * 0.3f,
      centerX + cos(angle) * clockRadius * 0.7f,
      centerY + sin(angle) * clockRadius * 0.7f,
      paint.forMinuteHand()
    )
  }

  // Стрелка секунд
  private fun drawSecondHand(canvas: Canvas, second: Int) {
    val angle = (Math.PI * second / 30 + BASE_ANGLE).toFloat()
    canvas.drawLine(
      centerX - cos(angle) * clockRadius * 0.3f,
      centerY - sin(angle) * clockRadius * 0.3f,
      centerX + cos(angle) * clockRadius * 0.7f,
      centerY + sin(angle) * clockRadius * 0.7f,
      paint.forSecondHand()
    )
  }

  // Рассчитать координаты для минутных точек
  private fun PointF.computeXYForDots(pos: Int, radius: Float) {
    val angle = (pos * (Math.PI / 30)).toFloat()
    x = radius * cos(angle) + centerX
    y = radius * sin(angle) + centerY
  }

  // Рассчитать координаты для цифр
  private fun PointF.computeXYForDigits(hour: Int, radius: Float) {
    val angle = (BASE_ANGLE + hour * (Math.PI / 6)).toFloat()
    x = radius * cos(angle) + centerX
    val textBaselineToCenter = (paint.descent() + paint.ascent()) / 2
    y = radius * sin(angle) + centerY - textBaselineToCenter
  }

  // Рассчет размеров view
  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    // Минимально доступные размеры
    val minWidth = suggestedMinimumWidth + paddingLeft + paddingRight
    val minHeight = suggestedMinimumHeight + paddingTop + paddingBottom

    // Желаемые размеры
    val desiredSize = DEFAULT_SIZE * resources.displayMetrics.densityDpi
    val desiredWidth = max(minWidth, desiredSize + paddingLeft + paddingRight)
    val desiredHeight = max(minHeight, desiredSize + paddingTop + paddingBottom)

    // Предложить желаемые размеры view
    setMeasuredDimension(
      resolveSize(desiredWidth, widthMeasureSpec),
      resolveSize(desiredHeight, heightMeasureSpec)
    )
  }

  // Обновление свойств view после изменения размера
  override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
    centerX = width / 2f
    centerY = height / 2f
    clockRadius = min(centerX, centerY)
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawClockBack(canvas)
    drawClockFrame(canvas)
    drawClockDots(canvas)
    drawClockDigits(canvas)
    drawClockHands(canvas)
    postInvalidateDelayed(REFRESH_DELAY) // TODO
  }

  override fun onSaveInstanceState(): Parcelable {
    val bundle = Bundle()
    bundle.putParcelable("superState", super.onSaveInstanceState())
    bundle.putInt("backColor", backColor)
    bundle.putInt("digitsColor", digitsColor)
    bundle.putInt("frameColor", frameColor)
    bundle.putInt("dotsColor", dotsColor)
    bundle.putInt("hourHandColor", hourHandColor)
    bundle.putInt("minuteHandColor", minuteHandColor)
    bundle.putInt("secondHandColor", secondHandColor)
    return bundle
  }

  override fun onRestoreInstanceState(state: Parcelable) {
    val bundle = state as Bundle
    backColor = state.getInt("backColor")
    digitsColor = state.getInt("digitsColor")
    frameColor = state.getInt("frameColor")
    dotsColor = state.getInt("dotsColor")
    hourHandColor = state.getInt("hourHandColor")
    minuteHandColor = state.getInt("minuteHandColor")
    secondHandColor = state.getInt("secondHandColor")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      super.onRestoreInstanceState(bundle.getParcelable("superState", Parcelable::class.java))
    } else {
      @Suppress("DEPRECATION")
      super.onRestoreInstanceState(bundle.getParcelable("superState"))
    }
  }

}