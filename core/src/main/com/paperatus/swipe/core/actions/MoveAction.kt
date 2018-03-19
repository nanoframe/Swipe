package com.paperatus.swipe.core.actions

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils

class MoveTo internal constructor(val x: Float,
                                  val y: Float,
                                  duration: Float,
                                  override var interpolation: Interpolation) : TimeAction(duration) {
    private var startX: Float = 0.0f
    private var startY: Float = 0.0f

    override fun start() {
        startX = gameObject.transform.position.x
        startY = gameObject.transform.position.y
    }

    override fun step(alpha: Float) {
        val newX = MathUtils.lerp(startX, x, alpha)
        val newY = MathUtils.lerp(startY, y, alpha)
        gameObject.transform.position.set(newX, newY)
    }

    override fun end() {
        gameObject.transform.position.set(x, y)
    }
}
