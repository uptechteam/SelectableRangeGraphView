package com.uptech.makhrov.selectablerangegraphview

import android.annotation.TargetApi
import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.filter_view.view.*
import org.apache.commons.math3.analysis.UnivariateFunction
import org.apache.commons.math3.analysis.interpolation.AkimaSplineInterpolator

/**
 * Created on 21.03.17.
 */
class SelectableRangeGraphView : FrameLayout {
  var onRangePickedListener: ((Pair<Float, Float>) -> Unit)? = null
  lateinit var circleBackgroundDrawable: Drawable
  var selectedPlotColor: Int = 0
  var unselectedPlotColor: Int = 0
  var selectedRangeColor: Int = 0
  var unselectedRangeColor: Int = 0
  var circleSizePixels: Int = 0

  private val TAG = this.javaClass.simpleName

  private var interpolationFunc: UnivariateFunction? = null
  private var xRange: Pair<Double, Double>? = null
  private var yRange: Pair<Double, Double>? = null
  private var funcMaxValue: Double = -1.0

  constructor(context: Context) : super(context)

  constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
    parseArguments(attributeSet, R.style.FilterViewStyle)
  }

  constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context,
    attributeSet, defStyleAttr) {
    parseArguments(attributeSet, R.style.FilterViewStyle)
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attrs, defStyleAttr, defStyleRes) {
    parseArguments(attrs, R.style.FilterViewStyle)
  }

  fun getSelectedRange() = rangePicker.getLeftAndRightBorders()

  fun cleanSelection() {
    rangePicker.clearSelection()
  }

  fun setPlotFunction(func: (Double) -> Float) {
    plotView.plotFunction = {
      func(it)
    }
  }

  fun setData(data: List<Rectangle>) {
    if (data.isNotEmpty()) {
      funcMaxValue = 0.0
      xRange = data.first().scope.first to data.last().scope.second

      val xValuesArray = DoubleArray(data.size)
      val yValuesArray = DoubleArray(data.size)

      data.forEachIndexed { index, (scope, value) ->
        if (index != data.size - 1) {
          xValuesArray[index] = scope.second - Math.abs(scope.second - scope.first) / 2
        } else {
          xValuesArray[index] = scope.second
        }
        if (value > 0) {
          yValuesArray[index] = value
        }
      }

      yRange = yValuesArray.min()!! to yValuesArray.max()!!

      val interpolator = AkimaSplineInterpolator()
      try {
        interpolationFunc = interpolator.interpolate(xValuesArray, yValuesArray)
        findMaxValue()
      } catch (e: Exception) {
        Log.e(TAG, "Interpolation func creation exception")
      }
    } else {
      xRange = null
      yRange = null
    }

    plotView.invalidate()
    rangePicker.invalidate()
  }

  override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
    super.onLayout(changed, left, top, right, bottom)
    rangePicker.setPlotPosition(plotView.x, plotView.x + plotView.measuredWidth)

    val pair = rangePicker.getLeftAndRightBorders()
    plotView.showSelectedRange(pair.first.toDouble(), pair.second.toDouble())
  }

  private fun init() {
    View.inflate(context, R.layout.filter_view, this)

    setAttributes()

    plotView.plotFunction = { x ->
      interpolationFunc?.let { func ->
        xRange?.let { (first, second) ->
          yRange?.let { yRange ->
            ((func.value((first + (second - first) * x)
              .coerceIn(first, second)) / yRange.second)
              .toFloat() / (funcMaxValue / yRange.second)).toFloat()
          }
        }
      } ?: 0f
    }

    val plotViewLayoutParams = plotView.layoutParams as LinearLayout.LayoutParams
    plotViewLayoutParams.leftMargin = (circleSizePixels / 2)
    plotViewLayoutParams.rightMargin = (circleSizePixels / 2)

    val params = rangePicker.layoutParams as LinearLayout.LayoutParams
    params.setMargins(params.leftMargin, (-circleSizePixels / 2), params.rightMargin, params.bottomMargin)

    rangePicker.onRangePickedListener = { values ->
      plotView.showSelectedRange(values.first.toDouble(), values.second.toDouble())
      onRangePickedListener?.invoke(values)
    }
  }

  private fun findMaxValue() {
    var pos = 0.0
    for (i in 1..context.resources.displayMetrics.widthPixels) {
      pos += i * 1.toDouble() / context.resources.displayMetrics.widthPixels
      xRange?.let { (first, second) ->
        interpolationFunc?.let { func ->
          val xValue = (first + (second - first) * pos)
          if (xValue > first && xValue < second) {
            val funcValue = func.value(xValue)
            if (funcValue > funcMaxValue) {
              funcMaxValue = funcValue
            }
          }
        }
      }
    }
  }

  private fun setAttributes() {
    rangePicker.setCircleBackgroundColor(circleBackgroundDrawable, DragCircle.CircleSide.START)
    rangePicker.setCircleBackgroundColor(circleBackgroundDrawable, DragCircle.CircleSide.END)
    rangePicker.setRangeIndicatorColors(selectedRangeColor, unselectedRangeColor)
    rangePicker.setCircleSize(circleSizePixels)
    plotView.setPlotColors(selectedPlotColor, unselectedPlotColor)
  }

  private fun parseArguments(attributeSet: AttributeSet, defStyleRes: Int) {
    val a = context.obtainStyledAttributes(
      attributeSet,
      R.styleable.SelectableRangeGraphView,
      0, defStyleRes)

    try {
      circleBackgroundDrawable = a.getDrawable(R.styleable.SelectableRangeGraphView_circleDrawable)
      selectedPlotColor = a.getColor(R.styleable.SelectableRangeGraphView_selectedPlotColor, R.color.selected_plot_color)
      unselectedPlotColor = a.getColor(R.styleable.SelectableRangeGraphView_unselectedPlotColor, R.color.unselected_plot_color)
      selectedRangeColor = a.getColor(R.styleable.SelectableRangeGraphView_selectedRangeColor, R.color.selected_range_indicator_color)
      unselectedRangeColor = a.getColor(R.styleable.SelectableRangeGraphView_unselectedRangeColor, R.color.unselected_range_indicator_color)
      circleSizePixels = a.getDimensionPixelSize(R.styleable.SelectableRangeGraphView_circleSize, context.resources.getDimensionPixelSize(R.dimen.range_picker_circle_size))
    } finally {
      a.recycle()
    }

    init()
  }
}