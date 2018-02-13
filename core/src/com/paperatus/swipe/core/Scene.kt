package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.g2d.SpriteBatch

interface Scene {
    fun create() = Unit

    fun preUpdate(delta: Float)

    /**
     * Updates the Scene
     *
     * @param [delta] the time since the last frame; capped at [SceneController.maxDeltaTime]
     */
    fun update(delta: Float)

    fun postUpdate(delta: Float)

    fun preRender(batch: SpriteBatch)

    fun render(batch: SpriteBatch)

    fun postRender(batch: SpriteBatch)

    /**
     * Resets the ObjectScene before display.
     *
     * Called every time [SceneController.setScene] is called to reset the ObjectScene
     * to start a new gameplay.
     */
    fun reset()

    fun dispose()
}
