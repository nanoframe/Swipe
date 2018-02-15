package com.paperatus.swipe.components

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.Component
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.InputComponent
import ktx.math.times

class KeyInputComponent : InputComponent() {

    val direction = Vector2()

    override fun update(gameObject: GameObject) {
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

        gameObject.messageComponent(Component.Message.MOVEMENT, direction)
    }

    override fun receive(what: Component.Message, payload: Any?) {
    }
}
