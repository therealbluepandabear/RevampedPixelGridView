package com.therealbluepandabear.sizingtests

import android.graphics.Bitmap
import android.graphics.Color

fun Bitmap.drawTransparent() {
    val color = Color.parseColor("#d9d9d9")

    for (i_1 in 0 until width) {
        for (i_2 in 0 until height) {
            if (i_1 % 2 == 0) {
                if (i_2 % 2 == 0) {
                    setPixel(i_1, i_2, color)
                } else {
                    setPixel(i_1, i_2, Color.WHITE)
                }
            } else {
                if (i_2 % 2 != 0) {
                    setPixel(i_1, i_2, color)
                } else {
                    setPixel(i_1, i_2, Color.WHITE)
                }
            }
        }
    }
}