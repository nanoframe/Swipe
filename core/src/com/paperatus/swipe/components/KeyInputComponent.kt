package com.paperatus.swipe.components

import COMPONENT_MESSAGE_BLOCKADE_COLLISION
import COMPONENT_MESSAGE_MOVEMENT
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.Component
import com.paperatus.swipe.core.ComponentMessage
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.InputComponent

class KeyInputComponent : GameInputComponent() {

    val direction = Vector2()

    override fun updateInput(gameObject: GameObject) {

        direction.x = when {
            Gdx.input.isKeyPressed(Input.Keys.LEFT) -> -1.0f
            Gdx.input.isKeyPressed(Input.Keys.RIGHT) -> 1.0f
            else -> 0.0f
        }

        direction.y = when {
            Gdx.input.isKeyPressed(Input.Keys.UP) -> 1.0f
            Gdx.input.isKeyPressed(Input.Keys.DOWN) -> -1.0f
            else -> 0.0f
        }

        direction.scl(5.0f)

        gameObject.messageComponent(COMPONENT_MESSAGE_MOVEMENT, direction)
    }
}
