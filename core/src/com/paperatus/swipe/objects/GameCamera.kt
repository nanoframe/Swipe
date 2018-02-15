package com.paperatus.swipe.objects

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector3
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent

private const val DISTANCE_X_MIN = 1.0f
private const val DISTANCE_X_MAX = 15.0f
private const val DISTANCE_Y_MIN = 4.0f
private const val DISTANCE_Y_MAX = 30.0f
private const val VELOCITY_MIN = 2.0f
private const val VELOCITY_MAX = 20.0f
private const val ZOOM_MIN = 0.4f
private const val ZOOM_MAX = 1.4f

private const val POSITION_MAX_CHANGE_PER_SECOND = 25.0f
private const val ZOOM_MAX_CHANGE_PER_SECOND = 0.4f

class GameCamera(width: Float, height: Float) :
        OrthographicCamera(width, height) {

    var positionInterpolation: Interpolation = Interpolation.pow3Out
    var velocityInterpolation: Interpolation = Interpolation.pow2Out

    private val tempPosition = Vector3()

    fun update(delta: Float, player: GameObject) {
        updatePosition(delta, player)
        updateZoom(delta, player)

        super.update()
    }

    private fun updatePosition(delta: Float, player: GameObject) {

        // x-position update
        //position.x = player.position.x

        // y-position updates
        tempPosition.apply {
            x = Math.abs(player.position.x - position.x)
            y = Math.abs(player.position.y - position.y)

            x = inverseLerpClamped(x, DISTANCE_X_MIN, DISTANCE_X_MAX)
            y = inverseLerpClamped(y, DISTANCE_Y_MIN, DISTANCE_Y_MAX)

            x = positionInterpolation.apply(x) *
                    Math.signum(player.position.x - position.x) *
                    POSITION_MAX_CHANGE_PER_SECOND * delta
            y = positionInterpolation.apply(y) *
                    Math.signum(player.position.y - position.y) *
                    POSITION_MAX_CHANGE_PER_SECOND * delta
        }
        position.add(tempPosition)
    }

    private fun updateZoom(delta: Float, player: GameObject) {
        val physicsComponent: PhysicsComponent =
                player.components[PhysicsComponent::class] as PhysicsComponent

        val velocity = MathUtils.clamp(physicsComponent.getBody().linearVelocity.len(),
                VELOCITY_MIN, VELOCITY_MAX)

        // Inverse lerp between velocity min and max
        val velocityAlpha = inverseLerpClamped(velocity, VELOCITY_MIN, VELOCITY_MAX)

        // Expected zoom based on the player's velocity
        val targetZoom = MathUtils.lerp(ZOOM_MIN, ZOOM_MAX, velocityAlpha)

        /*
         * Difference between the absolute value of the target zoom and the zoom.
         * The higher the value, the farther away the zoom is from the target zoom.
         */
        val zoomDelta = Math.abs(targetZoom - zoom)

        // Interpolate to smooth out the changes in the camera zoom
        val interpolatedZoom = velocityInterpolation.apply(zoomDelta) * Math.signum(targetZoom - zoom) *
                ZOOM_MAX_CHANGE_PER_SECOND

        zoom += interpolatedZoom * delta
    }

    fun inverseLerp(x: Float, min: Float, max: Float) = (x - min) / (max - min)
    fun inverseLerpClamped(x: Float, min: Float, max: Float) = MathUtils.clamp(
            inverseLerp(x, min, max), 0.0f, 1.0f)
}
