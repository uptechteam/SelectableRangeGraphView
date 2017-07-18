package com.uptech.makhrov.selectablerangegraphview

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.util.AttributeSet

/**
 * Created on 21.03.17.
 */
internal class DragCircle : HorizontalDraggableView {
  lateinit var circleSide: CircleSide

  constructor(context: Context) : super(context)

  constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet) {
    parseArguments(attributeSet)
  }

  constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(context,
    attributeSet, defStyleAttr) {
    parseArguments(attributeSet)
  }

  @TargetApi(Build.VERSION_CODES.LOLLIPOP)
  constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int)
    : super(context, attrs, defStyleAttr, defStyleRes) {
    parseArguments(attrs)
  }

  enum class CircleSide(val number: Int) {
    START(0), END(1);

    companion object {
      fun getCircleSideByNumber(number: Int): CircleSide {
        return CircleSide.values().filter {
          it.number == number
        }.firstOrNull() ?: START
      }
    }
  }

  private fun parseArguments(attributeSet: AttributeSet) {
    val a = context.theme.obtainStyledAttributes(
      attributeSet,
      R.styleable.HorizontalDraggableView,
      0, 0)

    try {
      circleSide = CircleSide.getCircleSideByNumber(a.getInt(R.styleable.DragCircle_circleState, 0))
    } finally {
      a.recycle()
    }
  }
}