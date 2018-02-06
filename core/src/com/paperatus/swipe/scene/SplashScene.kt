package com.paperatus.swipe.scene

import com.badlogic.gdx.graphics.Texture
import com.paperatus.swipe.Game

class SplashScene(game: Game) : Scene(game) {

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
