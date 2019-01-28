package com.paperatus.swipe.core.actions

import Actions
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.scene.GameObject
import ktx.collections.GdxArray

abstract class ActionGroup : Action() {
    protected val actionList = GdxArray<Action>()

    override fun start() {
        actionList.forEach {
            it.setGameObject(gameObject)
        }
    }

    // Action groups

    fun sequence(actions: Sequence.() -> Unit) = add(Sequence().also { it.actions() })

    fun spawn(actions: Spawn.() -> Unit) = add(Spawn().also { it.actions() })

    // State actions

    fun moveTo(x: Float,
               y: Float,
               duration: Float,
               interpolation: Interpolation = Interpolation.linear) =
            add(Actions.moveTo(x, y, duration, interpolation))

    fun moveTo(position: Vector2,
               duration: Float,
               interpolation: Interpolation = Interpolation.linear) =
            moveTo(position.x, position.y, duration, interpolation)

    fun scaleTo(x: Float,
                y: Float,
                duration: Float,
                interpolation: Interpolation = Interpolation.linear) =
            add(Actions.scaleTo(x, y, duration, interpolation))

    fun scaleTo(scl: Float,
                duration: Float,
                interpolation: Interpolation = Interpolation.linear) =
            scaleTo(scl, scl, duration, interpolation)

    fun fade(duration: Float,
             interpolation: Interpolation = Interpolation.linear) =
            add(Actions.fade(duration, interpolation))

    // Others

    fun execute(func: GameObject.() -> Unit) = add(ExecuteAction(func))

    fun delay(duration: Float,
              interpolation: Interpolation = Interpolation.linear) =
            add(DelayAction(duration, interpolation))

    private fun add(action: Action) = actionList.add(action)
}

class Sequence internal constructor() : ActionGroup() {

    private var index = 0
    private var activeAction: Action? = null

    override fun update(delta: Float) {
        // Calculate the amount of time the action has went over
        // if the action has been completed
        val timeOffset = activeAction?.let {
            if (!it.isFinished()) return@let 0.0f

            it.end()
            activeAction = null
            index++

            return@let if (it is TimeAction) it.currentDuration - it.duration
            else 0.0f
        } ?: 0.0f

        if (isFinished()) return

        activeAction = activeAction ?: actionList[index].apply {
            start()
        }

        val action = activeAction!!
        action.update(delta + timeOffset)
    }

    override fun end() = Unit

    override fun isFinished() = index >= actionList.size
}

class Spawn internal constructor() : ActionGroup() {
    private var finished: Boolean = false

    override fun start() {
        super.start()
        actionList.forEach {
            it.start()
        }
    }

    override fun update(delta: Float) {
        // Calculate the amount of time the action has went over
        // if the action has been completed

        if (isFinished()) return

        finished = true
        actionList.forEach {
            it.update(delta)
            finished = (it.isFinished() and finished)
        }
    }

    override fun end() = Unit

    override fun isFinished() = finished
}
