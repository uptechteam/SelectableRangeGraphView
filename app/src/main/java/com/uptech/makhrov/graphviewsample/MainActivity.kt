package com.uptech.makhrov.graphviewsample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.uptech.makhrov.selectablerangegraphview.SelectableRangeGraphView

class MainActivity : AppCompatActivity() {

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    val view = findViewById(R.id.sampleView) as SelectableRangeGraphView
    view.setPlotFunction {
      (Math.sin(it) * Math.cos(it)).toFloat()
    }
  }
}
