package com.test.rahul.extension

import java.util.Locale

fun Double.percent(percent: Int): Double =
    if (this > 0) this.times(percent).div(100.0).dropDecimal() else 0.0.dropDecimal()

fun Double.dropDecimal() = this.roundTo(2)

fun Double.roundTo(decimalPoint: Int) =
    String.format(Locale.ENGLISH, "%.${decimalPoint}f", this).toDouble()

fun Double.format() = if ((this % 1) > 0) this.toString() else this.toInt().toString()
