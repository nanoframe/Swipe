package com.paperatus.swipe.scenes

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.Game
import com.paperatus.swipe.components.KeyInputComponent
import com.paperatus.swipe.components.PlayerPhysicsComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.InputComponent
import com.paperatus.swipe.core.PhysicsComponent
import com.paperatus.swipe.core.PhysicsScene
import com.paperatus.swipe.core.TiledTexture
import com.paperatus.swipe.objects.GameCamera
import ktx.log.debug

const val WORLD_SIZE = 50.0f // World height

class GameScene(game: Game) : PhysicsScene(game, Vector2.Zero) {

    private val camera = GameCamera(WORLD_SIZE, WORLD_SIZE)
    private val player: GameObject = GameObject("player.png")

    private lateinit var background: TiledTexture

    init {
        debug { "Created GameScene instance" }

        player.apply {
            anchor.set(0.5f, 0.5f)
            size.set(2.0f, 2.0f)
            attachComponent<InputComponent>(KeyInputComponent())
            attachComponent<PhysicsComponent>(PlayerPhysicsComponent())
        }

        addObject(player)
    }

    override fun create() {
        background = TiledTexture(game.assets["background.png", Texture::class.java])
        background.direction = TiledTexture.Direction.Y

        background.repeatCount = 640.0f/50.0f
    }

    override fun update(delta: Float) {
        super.update(delta)

        // Background
        background.width = camera.viewportWidth
        background.height = camera.viewportHeight
        background.position.set(
                camera.position.x - camera.viewportWidth / 2.0f,
                camera.position.y - camera.viewportHeight / 2.0f
        )

        camera.update(delta, player)
    }

    override fun preRender(batch: SpriteBatch) {
        batch.projectionMatrix = camera.combined
    }

    override fun render(batch: SpriteBatch) {
        background.draw(batch)

        super.render(batch)
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
        val width = (width.toFloat() / height.toFloat()) * WORLD_SIZE
        val height = WORLD_SIZE

        camera.viewportWidth = width
        camera.viewportHeight = height

        debug { "World dimensions: ($width, $height)" }
    }

    override fun reset() {
    }

    override fun dispose() {
    }
}
