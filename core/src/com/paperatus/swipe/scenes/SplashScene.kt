package com.paperatus.swipe.scenes

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.paperatus.swipe.Game
import com.paperatus.swipe.core.AbstractScene
import com.paperatus.swipe.core.Scene

class SplashScene(private val game: Game) : AbstractScene() {

    override fun create() {
        game.assets.load("player.png", Texture::class.java)
    }

    override fun update(delta: Float) {
        game.assets.finishLoading()
        game.sceneController.setScene<GameScene>()
    }
}
