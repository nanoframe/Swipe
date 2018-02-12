package com.paperatus.swipe.objects

/**
 * The Player of the game.
 *
 * @param inputComponent component that handles the input and controls the Player.
 */
class Player : GameObject() {

    init {
        bounds.set(0.0f, 0.0f, 1.5f, 1.5f)
        spriteName = "player.png"
    }

    override fun update(delta: Float) {
    }
}