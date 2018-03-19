package com.paperatus.swipe.core.actions

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils

class ScaleTo internal constructor(val x: Float,
                                   val y: Float,
                                   duration: Float,
                                   override var interpolation: Interpolation) : TimeAction(duration) {
    private var startX = 0.0f
    private var startY = 0.0f

    override fun start() {
        startX = gameObject.transform.scale.x
        startY = gameObject.transform.scale.y
    }

    override fun step(alpha: Float) {
        val newX = MathUtils.lerp(startX, x, alpha)
        val newY = MathUtils.lerp(startY, y, alpha)
        gameObject.transform.scale.set(newX, newY)
    }

    override fun end() {
        gameObject.transform.scale.set(x, y)
    }
}
