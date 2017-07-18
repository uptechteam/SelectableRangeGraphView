package com.uptech.makhrov.selectablerangegraphview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import com.uptech.makhrov.selectablerangegraphview.DragCircle
import kotlinx.android.synthetic.main.range_picker_view.view.*

/**
 * Created on 21.03.17.
 */
internal class RangePickerView : FrameLayout {
  var onRangePickedListener: ((Pair<Float, Float>) -> Unit)? = null

  private val selectedRangeIndicator: FrameLayout
  private val rangeIndicator: FrameLayout
  private var plotWidth: Float = 0f
  private var plotStart: Float = 0f
  private var plotEnd: Float = 0f

  constructor(context: Context) : super(context)

  constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

  constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context,
    attributeSet, defStyleAttr)

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes)

  init {
    View.inflate(context, R.layout.range_picker_view, this)
    selectedRangeIndicator = findViewById(R.id.selectedRangeIndicator) as FrameLayout
    rangeIndicator = findViewById(R.id.rangeIndicator) as FrameLayout

    firstCircle.onDragListener = {
      updateSelectedRange()
      onRangePickedListener?.invoke(getLeftAndRightBorders())
    }

    secondCircle.onDragListener = {
      updateSelectedRange()
      onRangePickedListener?.invoke(getLeftAndRightBorders())
    }
  }

  fun setPlotPosition(xStart: Float, xEnd: Float) {
    plotWidth = xEnd - xStart
    plotStart = xStart
    plotEnd = xEnd

    rangeIndicator.layout(plotStart.toInt(), rangeIndicator.top, plotEnd.toInt(), rangeIndicator.bottom)
    invalidate()
  }

  fun getLeftAndRightBorders(): Pair<Float, Float> {
    val pair = if (firstCircle.circleSide == DragCircle.CircleSide.START) {
      val left = (firstCircle.x + (firstCircle.measuredWidth.toDouble() / 2).toFloat()) - plotStart
      val right = plotWidth - (plotEnd - (secondCircle.x + (secondCircle.measuredWidth.toDouble() / 2).toFloat()))
      left / plotWidth to right / plotWidth
    } else {
      val left = plotWidth - (plotEnd - (secondCircle.x + (secondCircle.measuredWidth.toDouble() / 2).toFloat()))
      val right = (firstCircle.x + (firstCircle.measuredWidth.toDouble() / 2).toFloat()) - plotStart
      left / plotWidth to right / plotWidth
    }

    println(pair)
    return pair
  }

  fun clearSelection() {
    firstCircle.x = rangeIndicator.x
    secondCircle.x = rangeIndicator.x + rangeIndicator.measuredWidth
    invalidate()
  }

  fun setCircleSize(sizeInPx: Int) {
    val firstCircleParams = firstCircle.layoutParams
    firstCircleParams.width = sizeInPx
    firstCircleParams.height = sizeInPx
    firstCircle.layoutParams = firstCircleParams

    val secondCircleParams = secondCircle.layoutParams
    secondCircleParams.height = sizeInPx
    secondCircleParams.width = sizeInPx
    secondCircle.layoutParams = secondCircleParams
  }

  //TODO: IMO it may be solved better
  fun setCircleBackgroundColor(backgroundDrawable: Drawable, circleSide: DragCircle.CircleSide) {
    if (firstCircle.circleSide == circleSide) {
      firstCircle.background = backgroundDrawable
    } else {
      secondCircle.background = backgroundDrawable
    }
  }

  fun setRangeIndicatorColors(selectedColor: Int, unselectedColor: Int) {
    selectedRangeIndicator.setBackgroundColor(selectedColor)
    rangeIndicator.setBackgroundColor(unselectedColor)
  }

  override fun invalidate() {
    super.invalidate()
    updateSelectedRange()
    onRangePickedListener?.invoke(getLeftAndRightBorders())
  }

  override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
    super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    onRangePickedListener?.invoke(getLeftAndRightBorders())
    updateSelectedRange()
  }

  private fun updateSelectedRange() {
    if (firstCircle.x > secondCircle.x) {
      firstCircle.circleSide = DragCircle.CircleSide.END
      secondCircle.circleSide = DragCircle.CircleSide.START
    } else {
      firstCircle.circleSide = DragCircle.CircleSide.START
      secondCircle.circleSide = DragCircle.CircleSide.END
    }

    if (firstCircle.circleSide == DragCircle.CircleSide.START) {
      selectedRangeIndicator.layout((firstCircle.x + firstCircle.measuredWidth).toInt(), selectedRangeIndicator.top, secondCircle.x.toInt(), selectedRangeIndicator.bottom)
    } else {
      selectedRangeIndicator.layout((secondCircle.x + secondCircle.measuredWidth).toInt(), selectedRangeIndicator.top, firstCircle.x.toInt(), selectedRangeIndicator.bottom)
    }
  }
}