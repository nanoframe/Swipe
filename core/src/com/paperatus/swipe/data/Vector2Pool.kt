package com.paperatus.swipe.data

import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool

object Vector2Pool : Pool<Vector2>() {
    override fun newObject() = Vector2()
    override fun reset(`object`: Vector2?) {
        `object`?.set(0.0f, 0.0f)
    }
}
