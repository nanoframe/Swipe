package com.paperatus.swipe.map

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.data.PathPoint
import com.paperatus.swipe.data.Solver
import com.paperatus.swipe.data.lastItem
import ktx.collections.GdxArray
import ktx.collections.lastIndex
import ktx.log.Logger
import kotlin.math.max

private const val CHUNK_SIZE = 70.0f
private const val GENERATE_GAP = 40.0f

private const val LIMIT_FOLLOW_DISTANCE = 120.0f
private const val CHUNK_DISPOSAL_DISTANCE = 150.0f

abstract class MapData {
    val leftChunks = GdxArray<Chunk>()
    val rightChunks = GdxArray<Chunk>()

    companion object {
        private val log = Logger("MapData")
    }

    abstract var pathColor: Color

    private val pathPoints = GdxArray<PathPoint>()

    private var currentChunk = 0
    private val renderer = MapRenderer()

    private var mapLimit: Body? = null

    open fun create() {
        val start = PathPoint.obtain()
        pathPoints.add(start)
    }

    fun update(world: World, camera: Camera) {
        cleanupChunks(world, camera)
        updateChunks(world, camera)
        updateBottomBounds(world, camera)
    }

    private fun cleanupChunks(world: World, camera: Camera) {
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

        // As only three points are required to create a path, we'll
        // discard old points that doesn't contribute to path
        // generation
        while (pathPoints.size > 3) {
            val point = pathPoints.removeIndex(0)
            PathPoint.free(point)
        }
    }

    private fun updateChunks(world: World, camera: Camera) {

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
            val width = max(
                    // Smallest width at 3500
                    10.0f - camera.position.y / 500.0f, // Increase difficulty
                    4.0f)

            // Initialize the start point
            if (currentChunk == 1) {
                chunkLeft.addPoint(-width/2.0f, 0.0f)
                chunkRight.addPoint(width/2.0f, 0.0f)
            }

            var totalPoints = 0

            do {
                val points = generatePoints(cameraLeft, cameraRight, pathPoints.lastItem())
                pathPoints.addAll(points)

                totalPoints += points.size
            } while (pathPoints.lastItem().y < currentChunk * CHUNK_SIZE)

            // Extra -1 to connect the previous chunk to the current chunk
            val startIndex = pathPoints.size - totalPoints - 1

            // Point data
            val direction12 = PathPoint.obtain()
            val direction23 = PathPoint.obtain()
            val pathPoint1 = PathPoint.obtain()
            val pathPoint2 = PathPoint.obtain()
            val intersection = PathPoint.obtain()

            // The previous two points are needed for calculating the path position
            for (i in max(2, startIndex)..pathPoints.lastIndex) {

                val point1 = pathPoints[i - 2]
                val point2 = pathPoints[i - 1]
                val point3 = pathPoints[i]

                val edge1Slope = (point2.y - point1.y) / (point2.x - point1.x)
                val edge2Slope = (point3.y - point2.y) / (point3.x - point2.x)

                // TODO: Fix hardcoded widths
                Solver.getPerpendicularDelta(point1, point2, 10.0f, direction12)
                Solver.getPerpendicularDelta(point2, point3, 10.0f, direction23)

                // Left intersection
                pathPoint1
                        .set(point2)
                        .sub(direction12)
                pathPoint2
                        .set(point2)
                        .sub(direction23)
                Solver.solveIntersection(
                        edge1Slope, pathPoint1.x, pathPoint1.y,
                        edge2Slope, pathPoint2.x, pathPoint2.y,
                        intersection)
                chunkLeft.addPoint(intersection.x, intersection.y)

                // Right intersection
                pathPoint1
                        .set(point2)
                        .add(direction12)
                pathPoint2
                        .set(point2)
                        .add(direction23)
                Solver.solveIntersection(
                        edge1Slope, pathPoint1.x, pathPoint1.y,
                        edge2Slope, pathPoint2.x, pathPoint2.y,
                        intersection)
                chunkRight.addPoint(intersection.x, intersection.y)
            }

            PathPoint.free(direction12)
            PathPoint.free(direction23)
            PathPoint.free(pathPoint1)
            PathPoint.free(pathPoint2)
            PathPoint.free(intersection)

            createBodyChunk(world, chunkLeft)
            createBodyChunk(world, chunkRight)

            leftChunks.add(chunkLeft)
            rightChunks.add(chunkRight)
        }
    }

    open fun render(camera: Camera) {
        assert(leftChunks.size == rightChunks.size)
        if (leftChunks.size == 0) return

        renderer.projectionMatrix = camera.combined
        renderer.pathColor = pathColor

        for (i in 0 until leftChunks.size) {
            val leftChunk = leftChunks[i]
            val rightChunk = rightChunks[i]

            renderer.drawPath(leftChunk, rightChunk)
        }

        renderer.flush()
    }

    private fun updateBottomBounds(world: World, camera: Camera) {
        if (mapLimit == null) {
            val edge = EdgeShape()

            // camera.viewportWidth / 2.0f doesn't give the exact dimensions
            // because the resolution might change during gameplay. We'll just
            // lazily create a long line to deal with this problem.
            edge.set(-camera.viewportWidth, 0.0f,
                    camera.viewportWidth, 0.0f)

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

    abstract fun generatePoints(leftBound: Float, rightBound: Float,
                                start: PathPoint): GdxArray<PathPoint>
}
