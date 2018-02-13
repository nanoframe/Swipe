package com.paperatus.swipe.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.Component
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.InputComponent
import ktx.math.times

/**
 * Provides touch-event based input to control the Player
 */
class TouchInputComponent : InputComponent() {
    companion object {
        const val SPEED_MULTIPLIER = 15.0f
        const val MAX_SPEED = 12.0f
    }

    val direction = Vector2()
    var lastTouchTime = System.currentTimeMillis()

    override fun update(character: GameObject) {
        if (Gdx.input.justTouched()) {
            // Begin touch events
            lastTouchTime = System.currentTimeMillis()

        } else if (Gdx.input.isTouched) {
            val deltaTime = (System.currentTimeMillis() - lastTouchTime).toFloat()

            // Speed based on the change in the touch position to the change in time
            // Similar to y=1/t where y is the speed
            direction.set(Gdx.input.deltaX.toFloat(), Gdx.input.deltaY.toFloat()) *
                    (SPEED_MULTIPLIER / deltaTime)

            direction.y = -direction.y // y-down to y-up

            // Prevent the player from moving too fast
            if (direction.len2() > MAX_SPEED * MAX_SPEED) {
                direction.nor().scl(MAX_SPEED)
            }

            character.messageComponent(Component.Message.MOVEMENT, direction)

        }
    }

    override fun receive(what: Component.Message, payload: Any?) {
    }
}
