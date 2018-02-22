package com.paperatus.swipe.objects

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
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
import kotlin.math.min

private const val CHUNK_SIZE = 70.0f
private const val GENERATE_GAP = 40.0f

private const val MIN_Y = 8.0f
private const val MAX_Y = 20.0f
private const val MAX_X = 15.0f

private const val LIMIT_FOLLOW_DISTANCE = 120.0f
private const val CHUNK_DISPOSAL_DISTANCE = 150.0f

class ProceduralMapData : MapData() {
    override var pathColor = Color(204.0f / 255.0f, 230.0f / 255.0f, 228.0f / 255.0f, 1.0f)

    companion object {
        private val log = Logger("ProceduralMapData")
    }

    private val recentPoints = GdxArray<PathPoint>()
    private var currentChunk = 0

    private var mapLimit: Body? = null

    override fun create() {
        val start = PathPoint.obtain()
        recentPoints.add(start)
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
            val width = max(
                    // Smallest width at 3500
                    10.0f - camera.position.y / 500.0f, // Increase difficulty
                    4.0f)

            // Initialize the start point
            if (currentChunk == 1) {
                chunkLeft.addPoint(-width/2.0f, 0.0f)
                chunkRight.addPoint(width/2.0f, 0.0f)
            }

            val generatedCount = generatePoints(cameraLeft, cameraRight, width)

            // Extra -1 to connect the previous chunk to the current chunk
            val startIndex = recentPoints.size - generatedCount - 1

            // Point data
            val direction12 = PathPoint.obtain()
            val direction23 = PathPoint.obtain()
            val pathPoint1 = PathPoint.obtain()
            val pathPoint2 = PathPoint.obtain()
            val intersection = PathPoint.obtain()

            // The previous two points are needed for calculating the path position
            for (i in max(2, startIndex)..recentPoints.lastIndex) {

                val point1 = recentPoints[i - 2]
                val point2 = recentPoints[i - 1]
                val point3 = recentPoints[i]

                val edge1Slope = (point2.y - point1.y) / (point2.x - point1.x)
                val edge2Slope = (point3.y - point2.y) / (point3.x - point2.x)

                getPathDelta(point1, point2, point2.width, direction12)
                getPathDelta(point2, point3, point2.width, direction23)

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
            // As only three points are required to create a path, we'll
            // discard old points that doesn't contribute to path
            // generation
            while (recentPoints.size > 3) {
                val point = recentPoints.removeIndex(0)
                PathPoint.free(point)
            }

            createBodyChunk(world, chunkLeft)
            createBodyChunk(world, chunkRight)

            leftChunks.add(chunkLeft)
            rightChunks.add(chunkRight)
        }
    }

    private fun generatePoints(leftBound: Float, rightBound: Float, width: Float): Int {
        var count = 0
        do { // Generate points until the y threshold is reached
            count++
            recentPoints.add(createNextPoint(leftBound, rightBound, width))

        } while (recentPoints.lastItem().y < currentChunk * CHUNK_SIZE)

        return count
    }

    private fun getPathDelta(v1: Vector2, v2: Vector2, width: Float, out: Vector2) {
        out
                .set(v2.y - v1.y, v1.x - v2.x)
                .nor()
                .scl(width / 2.0f)
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

    private fun createNextPoint(leftBound: Float, rightBound: Float,
                                width: Float): PathPoint {
        // Distance between the current x-position and the left/right edge
        val recentPoint = recentPoints.lastItem()
        val leftDelta = leftBound - recentPoint.x
        val rightDelta = rightBound - recentPoint.x

        val deltaY = MathUtils.random(MIN_Y, MAX_Y)

        // Create a new path point
        val point = PathPoint.obtain()
        point.set(
                recentPoint.x + MathUtils.random(
                        min(leftDelta, MAX_X),
                        min(rightDelta, MAX_X)),
                recentPoint.y + deltaY
        )

        point.width = width + MathUtils.random(-width/10.0f, width/10.0f)

        return point
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

}
