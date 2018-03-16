package com.paperatus.swipe.scenes

import com.badlogic.gdx.Application
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.Game
import com.paperatus.swipe.components.KeyInputComponent
import com.paperatus.swipe.components.PlayerPhysicsComponent
import com.paperatus.swipe.components.TouchInputComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.InputComponent
import com.paperatus.swipe.core.Observer
import com.paperatus.swipe.core.PhysicsComponent
import com.paperatus.swipe.core.PhysicsScene
import com.paperatus.swipe.core.RenderComponent
import com.paperatus.swipe.core.TiledTexture
import com.paperatus.swipe.core.filterBy
import com.paperatus.swipe.map.GameMap
import com.paperatus.swipe.map.MapData
import com.paperatus.swipe.map.ProceduralMapGenerator
import com.paperatus.swipe.objects.Destructible
import com.paperatus.swipe.objects.RoadBlock
import com.paperatus.swipe.objects.GameCamera
import com.paperatus.swipe.objects.ParticleGenerator
import com.paperatus.swipe.objects.PathObjectGenerator
import com.paperatus.swipe.objects.Player
import com.paperatus.swipe.objects.PlayerCollisionResponse
import ktx.log.debug
import ktx.math.plus

const val WORLD_SIZE = 50.0f // World height

class GameScene(game: Game) : PhysicsScene(game, Vector2.Zero) {

    private val player: GameObject = Player()
    private val particles = ParticleGenerator()
    private val pathObjects = PathObjectGenerator()

    private val camera = GameCamera(WORLD_SIZE, WORLD_SIZE)
    private lateinit var gameMap: GameMap
    private lateinit var background: TiledTexture

    init {
        player.apply {
            transform.worldSize.set(2.0f, 2.0f)
            transform.anchor.set(0.5f, 0.5f)

            attachComponent<InputComponent>(
                    when (Gdx.app.type) {
                        Application.ApplicationType.Desktop -> KeyInputComponent()
                        Application.ApplicationType.Android -> TouchInputComponent()
                        Application.ApplicationType.iOS -> TouchInputComponent()
                        else -> TouchInputComponent()
                    })
            attachComponent<PhysicsComponent>(PlayerPhysicsComponent().apply {
                addContactListener(PlayerCollisionResponse(player))
            })
            attachComponent<RenderComponent>(RenderComponent(sprite = "player.png"))

            addObserver(particles)
        }

        addObject(player)
        addObject(particles)
        addObject(pathObjects)
    }

    override fun create() {
        debug { "Created GameScene instance" }

        background = TiledTexture(game.assets["background.png"])
        background.direction = TiledTexture.Direction.Y
        background.repeatCount = 640.0f / 15.0f

        val mapData = MapData(Color(
                204.0f / 255.0f,
                230.0f / 255.0f,
                228.0f / 255.0f,
                1.0f),
                game.assets["edge.png"]
        )
        val mapGenerator = ProceduralMapGenerator()
        gameMap = GameMap(mapData, mapGenerator)
        gameMap.addObserver(pathObjects)
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
                        (player.transform.position.y / backgroundTileSize).toInt() *
                        backgroundTileSize
        )

        gameMap.update(physicsWorld, camera)

        camera.update(delta, player)

//        gameObjects.filterBy { it is RoadBlock || it is Destructible }.forEach {
//            val shouldRemove = when {
//                it.transform.position.y < gameMap.getLimit() -> true
//                else -> false
//            }
//
//            // FIX: The program may crash if the object has been requested to be removed
//            if (shouldRemove) queueRemove(it)
//        }
    }

    override fun render(batch: SpriteBatch) {
        batch.projectionMatrix = camera.combined

        batch.begin()
        background.draw(batch)
        batch.end()
        gameMap.renderPath()
        batch.begin()
        super.render(batch)
        batch.end()
        gameMap.renderEdge()

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
