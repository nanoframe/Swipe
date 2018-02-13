package com.paperatus.swipe.scenes

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.paperatus.swipe.Game
import com.paperatus.swipe.core.Scene

class SplashScene(private val game: Game) : Scene {

    override fun preUpdate(delta: Float) = Unit

    override fun postUpdate(delta: Float) = Unit

    override fun preRender(batch: SpriteBatch) = Unit

    override fun render(batch: SpriteBatch) = Unit

    override fun postRender(batch: SpriteBatch) = Unit

    override fun resize(width: Int, height: Int) = Unit

    override fun create() {
        game.assets.load("player.png", Texture::class.java)
    }

    override fun update(delta: Float) {
        game.assets.finishLoading()
        game.sceneController.setScene<GameScene>()
    }

    override fun reset() {
    }

    override fun dispose() {
    }

}
