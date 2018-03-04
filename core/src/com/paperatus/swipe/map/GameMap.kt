package com.paperatus.swipe.map

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.EdgeShape
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.data.PathPoint
import com.paperatus.swipe.data.Solver
import com.paperatus.swipe.data.lastItem
import com.paperatus.swipe.core.Subject
import ktx.collections.GdxArray
import ktx.collections.lastIndex
import ktx.log.Logger
import kotlin.math.max

private const val CHUNK_SIZE = 70.0f
private const val GENERATE_GAP = 40.0f

private const val LIMIT_FOLLOW_DISTANCE = 120.0f
private const val CHUNK_DISPOSAL_DISTANCE = 150.0f

// TODO: Dispose object and renderer
class GameMap(
    var mapData: MapData,
    var mapGenerator: MapGenerator
) : Subject() {

    companion object {
        private val log = Logger("GameMap")
    }

    // Chunk data
    private var currentChunk = 0
    private val leftChunks = GdxArray<Chunk>()
    private val rightChunks = GdxArray<Chunk>()
    private val pathPoints = GdxArray<PathPoint>()

    private var mapLimit: Body? = null

    private val mapRenderer = MapRenderer(
            mapData.backgroundColor,
            mapData.edgeTexture)

    fun create() {
        val start = PathPoint.obtain()
        pathPoints.add(start)
        applyDependencies()
    }

    fun update(world: World, camera: Camera) {
        cleanupChunks(world, camera)
        updateChunks(world, camera)
        updateBottomBounds(world, camera)
        mapRenderer.projectionMatrix = camera.combined

        if (mapGenerator.shouldSpawnDestructible(camera.position.y)) {
            val position = mapGenerator.nextDestructible()
            post(Notification.DESTRUCTIBLE_SPAWN, position)

            log.debug { "Spawn Destructible at $position" }
        }

        if (mapGenerator.shouldSpawnBlockade(camera.position.y)) {
            val position = mapGenerator.nextBlockade()
            post(Notification.DESTRUCTIBLE_SPAWN, position)

            log.debug { "Spawn Blockade at $position" }
        }
    }

    fun applyDependencies() {
        mapRenderer.pathColor = mapData.backgroundColor
        mapRenderer.edgeTexture = mapData.edgeTexture
    }

    private fun cleanupChunks(world: World, camera: Camera) {
        // Clean up memory by removing unused chunks
        if (leftChunks.size > 0) {
            val lastLeftChunk = leftChunks[0]
            val lastRightChunk = rightChunks[0]
            val lastY = max(
                    lastLeftChunk.lastItem().y,
                    lastRightChunk.lastItem().y)

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

        // Generate more chunks (if needed) to create an unending path
        if (currentChunk * CHUNK_SIZE - cameraTop < GENERATE_GAP) {
            currentChunk++
            log.debug { "Creating chunk #$currentChunk" }

            val cameraLeft = -camera.viewportWidth / 2.0f
            val cameraRight = camera.viewportWidth / 2.0f
            val chunkLeft = Chunk.obtain()
            val chunkRight = Chunk.obtain()

            // Decreasing width over time to increase difficulty
            val width = max(
                    10.0f - camera.position.y / 500.0f,
                    4.0f)

            // Initialize the start point
            if (currentChunk == 1) {
                chunkLeft.addPoint(-width / 2.0f, 0.0f)
                chunkRight.addPoint(width / 2.0f, 0.0f)
            }

            val totalPoints = createPoints(cameraLeft, cameraRight, width)

            val startIndex = max(
                    // Offset by -1 to connect the previous chunk
                    pathPoints.size - totalPoints - 1,

                    // We'll need to access the last two points that have been
                    // generated
                    2)

            for (i in startIndex..pathPoints.lastIndex) {
                val (left, right) = generatePathSide(
                        pathPoints[i - 2],
                        pathPoints[i - 1],
                        pathPoints[i])

                chunkLeft.addPoint(left.x, left.y)
                chunkRight.addPoint(right.x, right.y)
            }

            createBodyChunk(world, chunkLeft)
            createBodyChunk(world, chunkRight)

            leftChunks.add(chunkLeft)
            rightChunks.add(chunkRight)
        }
    }

    fun renderPath() {
        assert(leftChunks.size == rightChunks.size)
        if (leftChunks.size == 0) return

        for (i in 0 until leftChunks.size) {
            val leftChunk = leftChunks[i]
            val rightChunk = rightChunks[i]

            mapRenderer.drawPath(leftChunk, rightChunk)
        }

        mapRenderer.flushPath()
    }

    fun renderEdge() {
        assert(leftChunks.size == rightChunks.size)
        if (leftChunks.size == 0) return

        for (i in 0..leftChunks.lastIndex) {
            mapRenderer.drawEdge(leftChunks[i])
            mapRenderer.drawEdge(rightChunks[i])
        }

        mapRenderer.flushEdge()
    }

    fun getLimit() = mapLimit!!.position.y

    private fun createPoints(leftBound: Float, rightBound: Float, width: Float): Int {
        var totalPoints = 0 // Total points created

        // Keep creating until the chunk exceeds the minimum size
        do {
            val points = mapGenerator.generatePoints(
                    leftBound, rightBound,
                    pathPoints.lastItem())

            points.forEach {
                it.width = width
                pathPoints.add(it)
            }

            totalPoints += points.size
        } while (pathPoints.lastItem().y < currentChunk * CHUNK_SIZE)

        return totalPoints
    }

    private val sides = SidePoints()
    private fun generatePathSide(
        point1: PathPoint,
        point2: PathPoint,
        point3: PathPoint
    ): SidePoints {
        // Point data
        val direction12 = PathPoint.obtain()
        val direction23 = PathPoint.obtain()
        val pathPoint1 = PathPoint.obtain()
        val pathPoint2 = PathPoint.obtain()
        val intersection = PathPoint.obtain()
        val edge1Slope = (point2.y - point1.y) / (point2.x - point1.x)
        val edge2Slope = (point3.y - point2.y) / (point3.x - point2.x)

        Solver.getPerpendicularDelta(point1, point2, point2.width, direction12)
        Solver.getPerpendicularDelta(point2, point3, point2.width, direction23)

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
        sides.setLeft(intersection.x, intersection.y)

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
        sides.setRight(intersection.x, intersection.y)

        PathPoint.free(direction12)
        PathPoint.free(direction23)
        PathPoint.free(pathPoint1)
        PathPoint.free(pathPoint2)
        PathPoint.free(intersection)

        return sides
    }

    private fun updateBottomBounds(world: World, camera: Camera) {
        if (mapLimit == null) {
            val edge = EdgeShape()

            // camera.viewportWidth / 2.0f doesn't give the exact dimensions
            // because the resolution might change during gameplay. We'll just
            // cheat and create a long line to deal with this problem.
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
    private fun createBodyChunk(
        world: World,
        chunk: Chunk
    ) {
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
}

private class SidePoints {
    private val left = Vector2()
    private val right = Vector2()

    fun set(l: Vector2, r: Vector2) {
        left.set(l)
        right.set(r)
    }

    fun set(
        lx: Float,
        ly: Float,
        rx: Float,
        ry: Float
    ) {
        left.set(lx, ly)
        right.set(rx, ry)
    }

    fun setLeft(l: Vector2) {
        left.set(l)
    }

    fun setLeft(x: Float, y: Float) {
        left.set(x, y)
    }

    fun setRight(r: Vector2) {
        right.set(r)
    }

    fun setRight(x: Float, y: Float) {
        right.set(x, y)
    }

    operator fun component1() = left
    operator fun component2() = right
}
