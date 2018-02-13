package com.paperatus.swipe.objects

import com.paperatus.swipe.core.GameObject

/**
 * The Player of the game.
 */
class Player : GameObject() {

    init {
        bounds.set(0.0f, 0.0f, 2.0f, 2.0f)
        spriteName = "player.png"
        anchor.set(0.5f, 0.5f)
    }

    override fun update(delta: Float) {
    }
}
