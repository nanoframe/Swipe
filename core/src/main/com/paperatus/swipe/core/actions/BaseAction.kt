package com.paperatus.swipe.core.actions

import com.paperatus.swipe.core.scene.GameObject

abstract class Action {
    private var rawGameObject: GameObject? = null
    val gameObject get() = rawGameObject!!

    abstract fun start()
    abstract fun update(delta: Float)
    abstract fun end()
    abstract fun isFinished(): Boolean

    fun setGameObject(gameObject: GameObject?) {
        rawGameObject = gameObject
    }
}
