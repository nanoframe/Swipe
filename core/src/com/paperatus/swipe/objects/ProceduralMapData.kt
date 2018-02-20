package com.paperatus.swipe.objects

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.World
import ktx.collections.lastIndex
import ktx.log.Logger
import kotlin.math.max

private const val CHUNK_SIZE = 70.0f
private const val GENERATE_GAP = 30.0f

private const val MIN_Y = 8.0f
private const val MAX_Y = 40.0f

private const val LIMIT_FOLLOW_DISTANCE = 120.0f
private const val CHUNK_DISPOSAL_DISTANCE = 150.0f

class ProceduralMapData : MapData() {
    override var pathColor = Color(204.0f / 255.0f, 230.0f / 255.0f, 228.0f / 255.0f, 1.0f)

    companion object {
        private val log = Logger("ProceduralMapData")
    }

    private lateinit var recentPoint: Point
    private var currentChunk = 0

    private var mapLimit: Body? = null

    override fun create() {
        recentPoint = Point.obtain()
    }

    override fun update(world: World, camera: Camera) {
        updateChunk(world, camera)
        updateBottomBounds(world, camera)
    }

    private fun updateChunk(world: World, camera: Camera) {
        // Clean up memory by removing unused chunks
        if (leftChunks.size > 0) {
            val lastLeftChunk = leftChunks[0]
            val lastRightChunk = rightChunks[0]
            val lastY = max(
                    lastLeftChunk[lastLeftChunk.lastIndex].y,
                    lastRightChunk[lastRightChunk.lastIndex].y)

            if (camera.position.y - lastY > CHUNK_DISPOSAL_DISTANCE) {
                leftChunks.removeIndex(0)
                rightChunks.removeIndex(0)

                world.destroyBody(lastLeftChunk.body!!)
                world.destroyBody(lastRightChunk.body!!)

                Chunk.free(lastLeftChunk)
                Chunk.free(lastRightChunk)

                log.debug { "Disposed chunk #${currentChunk - leftChunks.size}" }
            }
        }

        val cameraTop = camera.position.y + camera.viewportHeight / 2.0f

        // Create more paths if the top of the screen is near the
        // end of the chunk subtracted by the gap
        if (currentChunk * CHUNK_SIZE - cameraTop < GENERATE_GAP) {
            currentChunk++

            log.debug { "Creating chunk #$currentChunk" }

            // Left/right bounds
            val cameraLeft = -camera.viewportWidth / 2.0f
            val cameraRight = camera.viewportWidth / 2.0f

            val chunkLeft = Chunk.obtain()
            val chunkRight = Chunk.obtain()
            val width = 10.0f

            // Start from the last point of the previous chunk
            chunkLeft.addPoint(
                    recentPoint.x - width / 2.0f,
                    recentPoint.y)
            chunkRight.addPoint(
                    recentPoint.x + width / 2.0f,
                    recentPoint.y
            )

            do { // Generate points until the y threshold is reached
                val point = createNextPoint(cameraLeft, cameraRight)

                // Generate the walls of the map
                chunkLeft.addPoint(point.x - width / 2.0f, point.y)
                chunkRight.addPoint(point.x + width / 2.0f, point.y)

                Point.free(recentPoint)
                recentPoint = point

            } while (recentPoint.y < currentChunk * CHUNK_SIZE)

            createBodyChunk(world, chunkLeft)
            createBodyChunk(world, chunkRight)

            leftChunks.add(chunkLeft)
            rightChunks.add(chunkRight)
        }
    }

    /**
     * Creates a world body based on the chunk specifications.
     *
     * @param world the physics world.
     * @param chunk points of the map.
     */
    private fun createBodyChunk(world: World,
                                chunk: Chunk) {
        val restitution = 0.7f

        val bodyDef = BodyDef()
        val body = world.createBody(bodyDef)
        val shape = EdgeShape()

        // Generate world edges
        for (i in 1 until chunk.size) {
            val point1 = chunk[i - 1]
            val point2 = chunk[i]

            shape.set(
                    point1.x, point1.y,
                    point2.x, point2.y)

            val fixture = body.createFixture(shape, 0.0f)
            fixture.restitution = restitution

        }
        shape.dispose()

        chunk.body = body
    }

    private fun createNextPoint(leftBound: Float, rightBound: Float): Point {
        // Distance between the current x-position and the left/right edge
        val leftDelta = leftBound - recentPoint.x
        val rightDelta = rightBound - recentPoint.x

        val deltaY = MathUtils.random(MIN_Y, MAX_Y)

        // Create a new path point
        val point = Point.obtain()
        point.set(
                recentPoint.x + MathUtils.random(leftDelta, rightDelta),
                recentPoint.y + deltaY
        )

        return point
    }

    private fun updateBottomBounds(world: World, camera: Camera) {
        if (mapLimit == null) {
            val edge = EdgeShape()
            edge.set(-camera.viewportWidth/2.0f, 0.0f,
                    camera.viewportHeight/2.0f, 0.0f)

            mapLimit = world.createBody(BodyDef())
            mapLimit!!.createFixture(edge, 0.0f)
            edge.dispose()
        }

        if (camera.position.y - mapLimit!!.position.y > LIMIT_FOLLOW_DISTANCE) {
            mapLimit!!.setTransform(
                    0.0f,
                    camera.position.y - LIMIT_FOLLOW_DISTANCE,
                    0.0f)
        }
    }

}
