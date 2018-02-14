package com.paperatus.swipe.scenes

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.paperatus.swipe.Game
import com.paperatus.swipe.core.Scene

class SplashScene(private val game: Game) : Scene {

    override fun create() {
        game.assets.load("player.png", Texture::class.java)
        game.assets.load("background.png", Texture::class.java)
    }

    override fun update(delta: Float) {
        game.assets.finishLoading()
        game.sceneController.createScenes()
        game.sceneController.setScene<GameScene>()
    }

    override fun render(batch: SpriteBatch) {
    }

    override fun resize(width: Int, height: Int) {
    }

    override fun reset() {
    }

    override fun dispose() {
    }
}
