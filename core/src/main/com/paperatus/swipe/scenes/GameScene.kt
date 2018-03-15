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
import com.paperatus.swipe.objects.Player
import com.paperatus.swipe.objects.PlayerCollisionResponse
import ktx.log.debug
import ktx.math.plus

const val WORLD_SIZE = 50.0f // World height

class GameScene(game: Game) : PhysicsScene(game, Vector2.Zero) {

    private val camera = GameCamera(WORLD_SIZE, WORLD_SIZE)
    private val player: GameObject = Player()
    private lateinit var gameMap: GameMap

    private lateinit var background: TiledTexture

    init {
        debug { "Created GameScene instance" }

        player.apply {
            transform.anchor.set(0.5f, 0.5f)
            transform.size.set(2.0f, 2.0f)
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

            addObserver(ParticleSpawner())
        }

        addObject(player)
    }

    override fun create() {
        background = TiledTexture(game.assets["background.png"])
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
        gameMap.addObserver(PathObjectSpawner())
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

    override fun preRender(batch: SpriteBatch) {
        batch.projectionMatrix = camera.combined
    }

    override fun render(batch: SpriteBatch) {
        background.draw(batch)
        batch.end()
        gameMap.renderPath()
        batch.begin()
        super.render(batch)
        batch.end()
        gameMap.renderEdge()
        batch.begin()
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

    val particles = GameObject().apply {
        transform.position.set(-5.0f, 5.0f)
        addObject(this)
    }
    inner class ParticleSpawner : Observer {

        override fun receive(what: Int, payload: Any?) {
            if (what != Notification.PARTICLE_SPAWN) return
            particles.addChild(createParticle(player.transform.position))
        }

        private fun createParticle(p: Vector2) = GameObject().apply {
            val startSize = MathUtils.random(0.09f, 0.21f)
            val endSize = MathUtils.random(0.8f, 1.7f)
            val duration = MathUtils.random(0.5f, 1.2f)
            val positionOffset = Vector2(
                    MathUtils.random(-0.5f, 0.5f),
                    0.0f
            )

            transform.position.set(p + positionOffset)
            transform.size.set(startSize, startSize)
            transform.anchor.set(0.5f, 0.5f)
            attachComponent<RenderComponent>(RenderComponent(sprite="particle.png"))

            runAction(Actions.sequence {
                spawn {
                    sizeTo(endSize, duration, Interpolation.pow2Out)
                    fade(1.5f)
                }
                execute { requestRemove() }
            })
        }
    }

    inner class PathObjectSpawner : Observer {
        override fun receive(what: Int, payload: Any?) {

            val pathObject: GameObject = when (what) {
                Notification.BLOCKADE_SPAWN -> {
                    val d = Destructible()
                    d.attachComponent<RenderComponent>(
                            RenderComponent(sprite = "blockade.png"))
                    d
                }

                Notification.DESTRUCTIBLE_SPAWN -> {
                    val r = RoadBlock()
                    r.attachComponent<RenderComponent>(
                            RenderComponent(sprite = "blockade.png"))
                    r
                }
                else -> return
            }

            addObject(pathObject.apply {
                transform.size.set(3.0f, 2.789f)
                transform.position.set(payload as Vector2)
            })
        }
    }
}
