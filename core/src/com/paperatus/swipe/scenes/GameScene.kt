package com.paperatus.swipe.scenes

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Scaling
import com.badlogic.gdx.utils.viewport.ScalingViewport

import com.paperatus.swipe.Game
import com.paperatus.swipe.components.PlayerPhysicsComponent
import com.paperatus.swipe.components.TouchInputComponent
import com.paperatus.swipe.core.InputComponent
import com.paperatus.swipe.core.PhysicsComponent
import com.paperatus.swipe.core.PhysicsScene
import com.paperatus.swipe.objects.Player
import ktx.log.debug

const val WORLD_SIZE = 50.0f // World height

class GameScene(game: Game) : PhysicsScene(game, Vector2.Zero) {

    private val camera = OrthographicCamera(WORLD_SIZE, WORLD_SIZE)
    private val viewport = ScalingViewport(
            Scaling.fit,
            0.0f,
            0.0f,
            camera)

    private val player: Player = Player()

    init {
        debug { "Created GameScene instance" }

        player.attachComponent<InputComponent>(TouchInputComponent())
        player.attachComponent<PhysicsComponent>(PlayerPhysicsComponent())
        addObject(player)
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

    override fun resize(width: Int, height: Int) {
        /*
        WORLD_HEIGHT = WORLD_SIZE
        UNIT_PX_RATIO = height / WORLD_HEIGHT

        WORLD_WIDTH = width / UNIT_PX_RATIO
        */

        // WORLD_WIDTH rearranged from the equation above
        val WORLD_WIDTH = (width.toFloat() / height.toFloat()) * WORLD_SIZE
        val WORLD_HEIGHT = WORLD_SIZE

        viewport.setWorldSize(WORLD_WIDTH, WORLD_HEIGHT)
        viewport.update(width, height)

        debug { "World dimensions: ($WORLD_WIDTH, $WORLD_HEIGHT)" }
    }

    override fun reset() {
    }

    override fun dispose() {
    }
}
