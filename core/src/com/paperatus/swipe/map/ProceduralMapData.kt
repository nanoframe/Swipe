package com.paperatus.swipe.map

import Path
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.paperatus.swipe.data.PathPoint
import ktx.collections.GdxArray

private const val CURVE_MIN_Y_DISTANCE = 8.0f
private const val CURVE_MAX_Y_DISTANCE = 20.0f
private const val CURVE_MIN_POINTS = 3
private const val CURVE_MAX_POINTS = 8

private const val CURVE_UP_MIN_POINTS = 2
private const val CURVE_UP_MAX_POINTS = 4
private const val CURVE_UP_MAX_X_DELTA = 3.0f

class ProceduralMapData : MapData() {
    override var pathColor = Color(204.0f / 255.0f, 230.0f / 255.0f, 228.0f / 255.0f, 1.0f)

    private val tempArray = GdxArray<PathPoint>()

    override fun generatePoints(leftBound: Float, rightBound: Float,
                                start: PathPoint): GdxArray<PathPoint> {
        tempArray.clear()

        val pathType = Path.random()

        when (pathType) {
            Path.Type.CurveLeft -> generateLeftCurve(leftBound, start)
            Path.Type.CurveRight -> generateRightCurve(rightBound, start)
            Path.Type.CurveUp -> generateCurveUp(leftBound, rightBound, start)
        }

        return tempArray
    }

    private fun generateLeftCurve(leftBound: Float,
                                  start: PathPoint) {
        val count = randomCurvePointCount()

        var previous = start
        for (i in 1..count) {
            val nextPoint = PathPoint.obtain()
            nextPoint.set(previous)

            val leftDelta = leftBound - previous.x

            val offsetX = MathUtils.random(
                    leftDelta / 2.0f, leftDelta / 5.0f
            )
            val offsetY = MathUtils.random(
                    CURVE_MIN_Y_DISTANCE, CURVE_MAX_Y_DISTANCE
            )

            nextPoint.add(offsetX, offsetY)
            tempArray.add(nextPoint)

            previous = nextPoint
        }
    }

    private fun generateRightCurve(rightBound: Float,
                                   start: PathPoint) {
        val count = randomCurvePointCount()

        var previous = start
        for (i in 1..count) {
            val nextPoint = PathPoint.obtain()
            nextPoint.set(previous)

            val rightDelta = rightBound - previous.x

            val offsetX = MathUtils.random(
                    rightDelta / 5.0f, rightDelta / 2.0f
            )
            val offsetY = MathUtils.random(
                    CURVE_MIN_Y_DISTANCE, CURVE_MAX_Y_DISTANCE
            )

            nextPoint.add(offsetX, offsetY)
            tempArray.add(nextPoint)

            previous = nextPoint
        }
    }

    private fun generateCurveUp(leftBound: Float, rightBound: Float,
                                start: PathPoint) {
        val count = randomUpPointCount()

        var previousPoint = start
        for (i in 1..count) {
            val leftDelta = leftBound - previousPoint.x
            val rightDelta = rightBound - previousPoint.x

            val nextPoint = PathPoint.obtain()
            nextPoint.set(previousPoint)

            val offsetX = MathUtils.clamp(
                    MathUtils.random(leftDelta, rightDelta),
                    -CURVE_UP_MAX_X_DELTA,
                    CURVE_UP_MAX_X_DELTA
            )

            nextPoint.add(
                    offsetX,
                    MathUtils.random(CURVE_MIN_Y_DISTANCE, CURVE_MAX_Y_DISTANCE)
            )

            tempArray.add(nextPoint)
            previousPoint = nextPoint
        }
    }

    private fun randomCurvePointCount() = MathUtils.random(
            CURVE_MIN_POINTS, CURVE_MAX_POINTS)

    private fun randomUpPointCount() = MathUtils.random(
            CURVE_UP_MIN_POINTS, CURVE_UP_MAX_POINTS
    )
}
