package com.uptech.makhrov.selectablerangegraphview

/**
 * Created on 06.04.17.
 */

/**
 * A Scope is a pair of two values a left border and a right border of a rectangle.
 * A Value e is height of rectangle
 */
data class Rectangle(val scope: Pair<Double, Double>, val value: Double)