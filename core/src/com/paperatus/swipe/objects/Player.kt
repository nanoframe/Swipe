package com.paperatus.swipe.objects

import com.paperatus.swipe.handlers.InputComponent

/**
 * The Player of the game.
 *
 * @param inputComponent component that handles the input and controls the Player.
 */
class Player(private val inputComponent: InputComponent) : GameObject() {

    init {
        bounds.set(0.0f, 0.0f, 1.5f, 1.5f)
    }

    override fun update(delta: Float) {
        inputComponent.updateInput(this)

    }
}