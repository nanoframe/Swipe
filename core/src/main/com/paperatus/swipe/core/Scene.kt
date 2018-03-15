package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.g2d.SpriteBatch

interface Scene {
    fun create()

    /**
     * Updates the Scene
     *
     * @param [delta] the time since the last frame; capped at [SceneController.maxDeltaTime]
     */
    fun update(delta: Float)

    /**
     * Renders the Scene.
     *
     * @param batch the SpriteBatch to render onto.
     */
    fun render(batch: SpriteBatch)

    /**
     * Called when the game resolution is changed during gameplay.
     *
     * @param width screen width
     * @param height screen height
     */
    fun resize(width: Int, height: Int)

    /**
     * Resets the ObjectScene before display.
     *
     * Called every time [SceneController.setScene] is called to reset the ObjectScene
     * to start a new gameplay.
     */
    fun reset()

    /**
     * Disposes the Scene.
     */
    fun dispose()
}
