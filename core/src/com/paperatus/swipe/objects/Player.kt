package com.paperatus.swipe.objects

import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.handlers.InputComponent
import com.paperatus.swipe.handlers.Subject

/**
 * The Player of the game.
 *
 * @param inputComponent component that handles the input and controls the Player.
 */
class Player(private val inputComponent: InputComponent) : GameObject, Subject() {
    override val spriteName = "player.png"

    override val position = Vector2()
    override val velocity = Vector2()
    override val acceleration = Vector2()
    override val rotation = 0.0f
    override val bounds = Rectangle(0.0f, 0.0f, 1.5f, 1.5f)

    override fun update(delta: Float) {
        inputComponent.updateInput(this)

    }
}