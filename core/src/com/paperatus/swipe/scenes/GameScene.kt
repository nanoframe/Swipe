package com.paperatus.swipe.scenes

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.Game
import com.paperatus.swipe.components.KeyInputComponent
import com.paperatus.swipe.components.PlayerPhysicsComponent
import com.paperatus.swipe.components.TouchInputComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.InputComponent
import com.paperatus.swipe.core.PhysicsComponent
import com.paperatus.swipe.core.PhysicsScene
import com.paperatus.swipe.core.TiledTexture
import com.paperatus.swipe.objects.GameCamera
import com.paperatus.swipe.map.GameMap
import com.paperatus.swipe.map.MapData
import com.paperatus.swipe.map.ProceduralMapGenerator
import ktx.log.debug

const val WORLD_SIZE = 50.0f // World height

class GameScene(game: Game) : PhysicsScene(game, Vector2.Zero) {

    private val camera = GameCamera(WORLD_SIZE, WORLD_SIZE)
    private val player: GameObject = GameObject("player.png")
    lateinit var gameMap: GameMap

    private lateinit var background: TiledTexture

    init {
        debug { "Created GameScene instance" }

        player.apply {
            anchor.set(0.5f, 0.5f)
            size.set(2.0f, 2.0f)
            attachComponent<InputComponent>(
                    when (Gdx.app.type) {
                        Application.ApplicationType.Desktop -> KeyInputComponent()
                        Application.ApplicationType.Android -> TouchInputComponent()
                        Application.ApplicationType.iOS -> TouchInputComponent()
                        else -> TouchInputComponent()
                    })
            attachComponent<PhysicsComponent>(PlayerPhysicsComponent())
        }

        addObject(player)
    }

    override fun create() {
        background = TiledTexture(game.assets["background.png", Texture::class.java])
        background.direction = TiledTexture.Direction.Y

        background.repeatCount = 640.0f / 15.0f

        val mapData = MapData(
                Color(
                        204.0f / 255.0f,
                        230.0f / 255.0f,
                        228.0f / 255.0f,
                        1.0f),
                game.assets["edge.png"]
        )
        val mapGenerator = ProceduralMapGenerator()
        gameMap = GameMap(mapData, mapGenerator)
        gameMap.create()
    }

    override fun update(delta: Float) {
        super.update(delta)

        // Background
        background.width = camera.viewportWidth * 1.5f
        background.height = camera.viewportHeight * 2.0f

        val backgroundTileSize: Float = background.height / background.repeatCount

        background.position.set(
                -background.width / 2.0f,
                -background.height / 2.0f +
                        (player.position.y / backgroundTileSize).toInt() *
                        backgroundTileSize
        )

        gameMap.update(physicsWorld, camera)

        camera.update(delta, player)
    }

    override fun preRender(batch: SpriteBatch) {
        batch.projectionMatrix = camera.combined
    }

    override fun render(batch: SpriteBatch) {
        background.draw(batch)
        batch.end()
        gameMap.render(camera)
        batch.begin()
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
        val worldWidth = (width.toFloat() / height.toFloat()) * WORLD_SIZE
        val worldHeight = WORLD_SIZE

        camera.viewportWidth = worldWidth
        camera.viewportHeight = worldHeight

        debug { "World dimensions: ($worldWidth, $worldHeight)" }
    }

    override fun reset() {
    }

    override fun dispose() {
    }
}
