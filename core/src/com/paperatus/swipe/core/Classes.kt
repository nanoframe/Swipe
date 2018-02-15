package com.paperatus.swipe.core

data class Size(var width: Float = 0.0f, var height: Float = 0.0f) {
    fun set(w: Float, h: Float) {
        width = w
        height = h
    }
}
