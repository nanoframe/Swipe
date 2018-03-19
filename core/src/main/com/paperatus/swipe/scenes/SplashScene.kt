package com.paperatus.swipe.scenes

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.paperatus.swipe.core.Game
import com.paperatus.swipe.core.scene.Scene

class SplashScene(private val game: Game) : Scene {

    override fun create() {
        game.assets.loadTexture("player.png")
        game.assets.loadTexture("background.png")
        game.assets.loadTexture("edge.png")
        game.assets.loadTexture("blockade.png")
        game.assets.loadTextureAtlas("destructible/explosion.atlas")
        game.assets.loadTexture("particle.png")
    }

    override fun update(delta: Float) {
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
