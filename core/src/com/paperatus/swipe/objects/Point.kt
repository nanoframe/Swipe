package com.paperatus.swipe.objects

import com.badlogic.gdx.utils.Pool

class Point internal constructor(var x: Float = 0.0f,
                                 var y: Float = 0.0f) : Pool.Poolable {

    override fun reset() {
        x = 0.0f
        y = 0.0f
    }

    fun set(newX: Float, newY: Float) {
        x = newX
        y = newY
    }

    operator fun component1() = x
    operator fun component2() = y
}

val PointFactory = object: Pool<Point>() {
    override fun newObject() = Point()
}
