package com.paperatus.swipe.scenes

import com.badlogic.gdx.graphics.Texture
import com.paperatus.swipe.Game
import com.paperatus.swipe.core.ObjectScene

class SplashScene(game: Game) : ObjectScene(game) {

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
