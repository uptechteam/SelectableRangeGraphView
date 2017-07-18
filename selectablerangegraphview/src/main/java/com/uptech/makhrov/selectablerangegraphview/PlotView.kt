package com.uptech.makhrov.selectablerangegraphview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Build
import android.util.AttributeSet
import android.widget.FrameLayout

/**
 * Created on 21.03.17.
 */
internal class PlotView : FrameLayout {
  //Number of pixels to draw path element
  private val N = 1

  //Function that returns y of plot based on x
  lateinit var plotFunction: (Double) -> Float

  private val selectedRangePaint = Paint()
  private val selectedPath = Path()
  private val unselectedPath = Path()
  private val unselectedRangePaint = Paint()

  private var startBound = 0f
  private var endBound = 0f

  constructor(context: Context) : super(context)

  constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

  constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context,
    attributeSet, defStyleAttr)

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

  fun showSelectedRange(start: Double, end: Double) {
    val newStartValue = Math.floor(start * measuredWidth).toFloat()
    val newEndValue = Math.ceil(end * measuredWidth).toFloat()

    val redrawFrom = if (newStartValue > startBound) startBound
    else if (newStartValue < startBound) newStartValue
    else if (newEndValue < endBound) newEndValue
    else endBound

    val redrawTo = if (newEndValue < endBound) endBound
    else if (newEndValue > endBound) newEndValue
    else if (newStartValue > startBound) newStartValue
    else startBound

    invalidate(redrawFrom.toInt(), top, redrawTo.toInt(), bottom)
    startBound = newStartValue
    endBound = newEndValue
  }

  //TODO: add ability to customize colors
  init {
    selectedRangePaint.style = Paint.Style.FILL
    selectedRangePaint.strokeWidth = 1f
    selectedRangePaint.isAntiAlias = true

    unselectedRangePaint.style = Paint.Style.FILL
    unselectedRangePaint.strokeWidth = 1f
    unselectedRangePaint.isAntiAlias = true
  }

  fun setPlotColors(selectedPlotColor: Int, unselectedPlotColor: Int) {
    selectedRangePaint.color = selectedPlotColor
    unselectedRangePaint.color = unselectedPlotColor
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawPlot(canvas, startBound to endBound)
  }

  private fun drawPlot(canvas: Canvas, selectedRange: Pair<Float, Float>) {
    //Drawing left part of unselected path
    unselectedPath.moveTo(0f, canvas.height.toFloat())
    for (i in 0..startBound.toInt() / N) {
      unselectedPath.lineTo(i.toFloat() * N, measuredHeight - measuredHeight * plotFunction((i * N).toDouble() / measuredWidth))
    }
    unselectedPath.lineTo(startBound, canvas.height.toFloat())
    unselectedPath.close()
    canvas.drawPath(unselectedPath, unselectedRangePaint)
    unselectedPath.rewind()

    //Drawing selected path
    selectedPath.moveTo(startBound, canvas.height.toFloat())
    for (i in selectedRange.first.toInt() / N..selectedRange.second.toInt() / N) {
      selectedPath.lineTo(i.toFloat() * N, measuredHeight - measuredHeight * plotFunction((i * N).toDouble() / measuredWidth))
    }
    selectedPath.lineTo(endBound, canvas.height.toFloat())
    selectedPath.close()
    canvas.drawPath(selectedPath, selectedRangePaint)
    selectedPath.rewind()

    //Drawing right part of unselected path
    unselectedPath.moveTo(endBound, canvas.height.toFloat())
    for (i in endBound.toInt() / N..canvas.width / N) {
      unselectedPath.lineTo(i.toFloat() * N, measuredHeight - measuredHeight * plotFunction((i * N).toDouble() / measuredWidth))
    }
    unselectedPath.lineTo(canvas.width.toFloat(), canvas.height.toFloat())
    unselectedPath.close()
    canvas.drawPath(unselectedPath, unselectedRangePaint)
    unselectedPath.rewind()
  }
}