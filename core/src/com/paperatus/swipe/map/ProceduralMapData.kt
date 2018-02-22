package com.paperatus.swipe.map

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils
import com.paperatus.swipe.data.PathPoint
import ktx.collections.GdxArray

private const val CURVE_MIN_Y_DISTANCE = 8.0f
private const val CURVE_MAX_Y_DISTANCE = 20.0f

private const val CURVE_MIN_POINTS = 3
private const val CURVE_MAX_POINTS = 8

class ProceduralMapData : MapData() {
    override var pathColor = Color(204.0f / 255.0f, 230.0f / 255.0f, 228.0f / 255.0f, 1.0f)

    val tempArray = GdxArray<PathPoint>()

    override fun generatePoints(leftBound: Float, rightBound: Float,
                                start: PathPoint): GdxArray<PathPoint> {
        tempArray.clear()

        val pathType = Path.random()
        println(pathType)

        when(pathType) {
            Path.Type.CurveLeft -> generateLeftCurve(leftBound, start)
            Path.Type.CurveRight -> generateRightCurve(rightBound, start)
            Path.Type.Up -> generateUp(leftBound, rightBound, start)
        }

        return tempArray
    }

    // TODO: Fix hardcoded numbers and return values

    private fun generateLeftCurve(leftBound: Float,
                                  start: PathPoint) {
        val count = MathUtils.random(CURVE_MIN_POINTS, CURVE_MAX_POINTS)

        var previous = start
        for (i in 1..count) {
            val nextPoint = PathPoint.obtain()
            nextPoint.set(previous)

            val leftDelta = leftBound - previous.x

            val offsetX = MathUtils.random(
                    leftDelta / 2.0f, 0.0f
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
        val count = MathUtils.random(CURVE_MIN_POINTS, CURVE_MAX_POINTS)

        var previous = start
        for (i in 1..count) {
            val nextPoint = PathPoint.obtain()
            nextPoint.set(previous)

            val rightDelta = rightBound - previous.x

            val offsetX = MathUtils.random(
                    0.0f, rightDelta / 2.0f
            )
            val offsetY = MathUtils.random(
                    CURVE_MIN_Y_DISTANCE, CURVE_MAX_Y_DISTANCE
            )

            nextPoint.add(offsetX, offsetY)
            tempArray.add(nextPoint)

            previous = nextPoint
        }
    }

    private fun generateUp(leftBound: Float, rightBound: Float,
                           start: PathPoint) {
        var previousPoint = start
        for (i in 1..5) {
            val leftDelta = leftBound - previousPoint.x
            val rightDelta = rightBound - previousPoint.x

            val nextPoint = PathPoint.obtain()
            nextPoint.set(previousPoint)

            val offsetX = MathUtils.clamp(
                    MathUtils.random(leftDelta, rightDelta),
                    -3.0f,
                    3.0f
            )

            nextPoint.add(
                    offsetX,
                    MathUtils.random(CURVE_MIN_Y_DISTANCE, CURVE_MAX_Y_DISTANCE)
            )

            tempArray.add(nextPoint)
            previousPoint = nextPoint
        }
    }
}
