package com.paperatus.swipe.components

import COMPONENT_MESSAGE_MOVEMENT
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.ComponentMessage
import com.paperatus.swipe.core.GameObject
import ktx.math.times

/**
 * Provides touch-event based input to control the Player
 */
class TouchInputComponent : GameInputComponent() {
    companion object {
        const val SPEED_MULTIPLIER = 20.0f
        const val MAX_TOUCH_TIME = 1000.0f
    }

    val direction = Vector2()
    var lastTouchTime = System.currentTimeMillis()

    override fun updateInput(gameObject: GameObject) {

        if (Gdx.input.isTouched) {
            val currentTime = System.currentTimeMillis()

            if (Gdx.input.justTouched()) {
                // Begin touch events
                lastTouchTime = currentTime

                // Skip the first update as it uses the previous touch state
                return
            }

            // Limit on the touch duration
            if (currentTime - lastTouchTime >= MAX_TOUCH_TIME) return

            val deltaTime = (currentTime - lastTouchTime).toFloat()

            // Speed based on the change in the touch position to the change in time
            // Similar to y=1/t where y is the speed
            direction.set(Gdx.input.deltaX.toFloat(), Gdx.input.deltaY.toFloat()) *
                    (SPEED_MULTIPLIER / deltaTime)

            direction.y = -direction.y // y-down to y-up

            // Inform other components about the touch event
            gameObject.messageComponent(COMPONENT_MESSAGE_MOVEMENT, direction)

        }
    }
}
