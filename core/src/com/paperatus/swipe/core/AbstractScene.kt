package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.g2d.SpriteBatch

abstract class AbstractScene : Scene {
    override fun preUpdate(delta: Float) = Unit

    override fun update(delta: Float) = Unit

    override fun postUpdate(delta: Float) = Unit

    override fun preRender(batch: SpriteBatch) = Unit

    override fun render(batch: SpriteBatch) = Unit

    override fun postRender(batch: SpriteBatch) = Unit

    override fun resize(width: Int, height: Int) = Unit

    override fun reset() = Unit

    override fun dispose() = Unit
}
