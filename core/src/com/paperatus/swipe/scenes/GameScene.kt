package com.paperatus.swipe.scenes

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.viewport.ExtendViewport

import com.paperatus.swipe.Game
import com.paperatus.swipe.VIEWPORT_HEIGHT
import com.paperatus.swipe.components.PlayerPhysicsComponent
import com.paperatus.swipe.components.TouchInputComponent
import com.paperatus.swipe.core.InputComponent
import com.paperatus.swipe.core.PhysicsComponent
import com.paperatus.swipe.core.PhysicsScene
import com.paperatus.swipe.objects.Player

class GameScene(game: Game) : PhysicsScene(game, Vector2.Zero) {

    private val camera = OrthographicCamera()
    val viewport = ExtendViewport(0.0f, VIEWPORT_HEIGHT, camera)

    private val player: Player = Player()

    init {
        player.attachComponent<InputComponent>(TouchInputComponent())
        player.attachComponent<PhysicsComponent>(PlayerPhysicsComponent())
        addObject(player)
    }

    init {
        viewport.maxWorldHeight = VIEWPORT_HEIGHT
        viewport.update(Gdx.graphics.width, Gdx.graphics.height)
    }

    override fun update(delta: Float) {
        super.update(delta)

        camera.position.set(0.0f, 0.0f, 0.0f)
        player.update(delta)
    }

    override fun preRender(batch: SpriteBatch) {
        camera.update()
        batch.projectionMatrix = camera.combined
    }

    override fun postRender(batch: SpriteBatch) {
        super.postRender(batch)
        debugRender(camera)
    }

    override fun reset() {
    }

    override fun dispose() {
    }
}
