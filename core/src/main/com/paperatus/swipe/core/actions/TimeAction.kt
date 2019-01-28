package com.paperatus.swipe.core.actions

import com.badlogic.gdx.math.Interpolation
import com.paperatus.swipe.core.InvalidActionException
import com.paperatus.swipe.core.scene.GameObject

abstract class TimeAction(internal val duration: Float) : Action() {
    internal var currentDuration = 0.0f
        private set

    abstract var interpolation: Interpolation

    init {
        if (duration < 0.0f) throw InvalidActionException("Duration $duration is < 0!")
    }

    override fun start() {
    }

    override fun update(delta: Float) {
        currentDuration += delta
        val alpha = (currentDuration / duration).coerceAtMost(1.0f)
        step(interpolation.apply(alpha))
    }

    override fun end() {
    }

    override fun isFinished() = (currentDuration / duration) >= 1.0f

    abstract fun step(alpha: Float)
}

class ExecuteAction internal constructor(private val func: GameObject.() -> Unit) : Action() {
    override fun start() {
        gameObject.func()
    }

    override fun update(delta: Float) = Unit

    override fun end() = Unit

    override fun isFinished() = true
}

class DelayAction internal constructor(duration: Float,
                                       override var interpolation: Interpolation) : TimeAction(duration) {
    override fun step(alpha: Float) = Unit
}
