package com.paperatus.swipe.components

import com.paperatus.swipe.core.ComponentMessage
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.InputComponent

abstract class GameInputComponent : InputComponent() {

    private var disabledEnd = 0L

    abstract fun updateInput(gameObject: GameObject)

    override fun update(delta: Float, gameObject: GameObject) {
        if (System.currentTimeMillis() - disabledEnd >= 0) updateInput(gameObject)
    }

    override fun receive(what: ComponentMessage, payload: Any?) {
        when (what) {
            Message.BLOCKADE_COLLISION -> disabledEnd =
                    System.currentTimeMillis() + 500L
        }
    }
}
