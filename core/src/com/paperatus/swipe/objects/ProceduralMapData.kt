package com.paperatus.swipe.objects

import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.World
import ktx.collections.GdxArray
import ktx.log.Logger

private const val CHUNK_SIZE = 70.0f
private const val GENERATE_GAP = 30.0f

private const val MIN_Y = 3.0f
private const val MAX_Y = 20.0f

class ProceduralMapData : MapData {

    companion object {
        private val log = Logger("ProceduralMapData")
    }

    private lateinit var recentPoint: Point
    private var currentChunk = 0

    private val points = GdxArray<Point>()

    override fun create() {
        points.add(Point.obtain().also { // Defaults to (0, 0)
            recentPoint = it
        })
    }

    override fun update(world: World, camera: Camera) {
        val cameraTop = camera.position.y + camera.viewportHeight / 2.0f

        // Create more paths if the top of the screen is near the
        // end of the chunk subtracted by the gap
        if (currentChunk * CHUNK_SIZE - cameraTop < GENERATE_GAP) {
            currentChunk++

            log.debug { "Creating chunk #$currentChunk"}

            val cameraLeft = -camera.viewportWidth / 2.0f
            val cameraRight = camera.viewportWidth / 2.0f

            do { // Generate a bunch of points for the chunk

                val point = createNextPoint(cameraLeft, cameraRight)
                points.add(point)

                recentPoint = point

                log.debug { "  Point created: (${point.x}, ${point.y})" }
            } while (recentPoint.y < currentChunk * CHUNK_SIZE)
        }

    }

    private fun createNextPoint(leftBound: Float, rightBound: Float) : Point {
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
}
