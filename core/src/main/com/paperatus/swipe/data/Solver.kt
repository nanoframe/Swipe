package com.paperatus.swipe.data

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2

object Solver {
    fun solveIntersection(
            m1: Float,
            x1: Float,
            y1: Float,
            m2: Float,
            x2: Float,
            y2: Float,
            out: Vector2
    ) {
        val x: Float
        val y: Float

        val isM1Vertical = m1.isNaN() || m1.isInfinite()
        val isM2Vertical = m2.isNaN() || m2.isInfinite()

        // TODO(not required): Check for no solutions

        // Check for vertical slopes
        if (isM1Vertical && isM2Vertical) {
            x = x1
            y = y1
        } else if ( // Infinite solutions
                MathUtils.isEqual(x1, x2) &&
                MathUtils.isEqual(y1, y2) && // Probably not needed
                MathUtils.isEqual(m1, m2)) {
            x = x1
            y = y1
        } else if (isM1Vertical) {
            x = x1
            y = m2 * (x - x2) + y2
        } else if (isM2Vertical) {
            x = x2
            y = m1 * (x - x1) + y1
        } else {
            x = (y2 - y1 + m1 * x1 - m2 * x2) / (m1 - m2)
            y = m1 * (x - x1) + y1
        }
        if (x.isNaN() || x.isInfinite() || y.isNaN() || y.isInfinite())
            throw RuntimeException()

        out.set(x, y)
    }

    fun getPerpendicularDelta(v1: Vector2, v2: Vector2, width: Float, out: Vector2) {
        out
                .set(v2.y - v1.y, v1.x - v2.x)
                .nor()
                .scl(width / 2.0f)
    }

    fun inverseLerp(x: Float, min: Float, max: Float) = (x - min) / (max - min)

    fun inverseLerpClamped(x: Float, min: Float, max: Float) = MathUtils.clamp(
            inverseLerp(x, min, max), 0.0f, 1.0f)
}
