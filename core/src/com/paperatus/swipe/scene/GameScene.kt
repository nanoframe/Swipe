package com.paperatus.swipe.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.paperatus.swipe.Game
import com.paperatus.swipe.VIEWPORT_HEIGHT
import com.paperatus.swipe.handlers.PlayerTouchInput
import com.paperatus.swipe.objects.Player

class GameScene(game: Game) : Scene(game) {

    private val camera = OrthographicCamera()
    val viewport = ExtendViewport(0.0f, VIEWPORT_HEIGHT, camera)

    private val player: Player = Player(PlayerTouchInput())

    init {
        viewport.maxWorldHeight = VIEWPORT_HEIGHT
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)

        gameObjects.operate {
            add(player)
        }
    }

    override fun update(delta: Float) {
        player.update(delta)
    }

    override fun preRender(batch: SpriteBatch) {
        camera.update()
        batch.projectionMatrix = camera.combined
    }

    override fun reset() {
    }

    override fun dispose() {
    }
}