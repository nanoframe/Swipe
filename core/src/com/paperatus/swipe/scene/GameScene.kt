package com.paperatus.swipe.scene

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ExtendViewport
import com.badlogic.gdx.utils.viewport.ScalingViewport
import com.paperatus.swipe.Game
import com.paperatus.swipe.VIEWPORT_HEIGHT
import com.paperatus.swipe.handlers.InputComponent
import com.paperatus.swipe.handlers.PhysicsComponent
import com.paperatus.swipe.handlers.PlayerPhysicsComponent
import com.paperatus.swipe.handlers.PlayerTouchInput
import com.paperatus.swipe.objects.Player

class GameScene(game: Game) : PhysicsScene(game, Vector2.Zero) {

    private val camera = OrthographicCamera()
    val viewport = ExtendViewport(0.0f, VIEWPORT_HEIGHT, camera)

    private val player: Player = Player()

    init {
        player.attachComponent<InputComponent>(PlayerTouchInput())
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