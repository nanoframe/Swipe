package com.paperatus.swipe.data

import com.badlogic.gdx.math.Vector2

object Solver {
    fun solveIntersection(m1: Float, x1: Float, y1: Float,
                          m2: Float, x2: Float, y2: Float,
                          out: Vector2) {
        val x = (y2 - y1 + m1 * x1 - m2 * x2) / (m1 - m2)
        val y = m1 * (x - x1) + y1

        out.set(x, y)
    }
}
