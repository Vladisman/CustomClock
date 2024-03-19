package ru.vladisman.clock

import android.graphics.Color
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ru.vladisman.clock.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
  private lateinit var binding: ActivityMainBinding
  private val colors = listOf(
    Color.BLACK,
    Color.RED,
    Color.WHITE,
    Color.GRAY,
    Color.GREEN,
    Color.BLUE,
    Color.CYAN,
    Color.MAGENTA,
    Color.YELLOW
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    binding = ActivityMainBinding.inflate(layoutInflater)
    val view = binding.root
    setContentView(view)

    with(binding) {
      btnBackground.setOnClickListener {
        clockView.backColor = nextColor()
      }
      btnFrame.setOnClickListener {
        clockView.frameColor = nextColor()
      }
      btnDigits.setOnClickListener {
        clockView.digitsColor = nextColor()
      }
      btnDots.setOnClickListener {
        clockView.dotsColor = nextColor()
      }
      btnHourHand.setOnClickListener {
        clockView.hourHandColor = nextColor()
      }
      btnMinuteHand.setOnClickListener {
        clockView.minuteHandColor = nextColor()
      }
      btnSecondHand.setOnClickListener {
        clockView.secondHandColor = nextColor()
      }
      btnReset.setOnClickListener {
        clockView.run {
          backColor = Color.WHITE
          frameColor = Color.BLACK
          digitsColor = Color.BLACK
          dotsColor = Color.BLACK
          hourHandColor = Color.BLACK
          minuteHandColor = Color.BLACK
          secondHandColor = Color.RED
        }
      }
    }
  }

  private fun nextColor() = colors.random()
}