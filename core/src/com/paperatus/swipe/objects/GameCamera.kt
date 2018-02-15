package com.paperatus.swipe.objects

import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent

private const val VELOCITY_MIN = 2.0f
private const val VELOCITY_MAX = 30.0f
private const val ZOOM_MIN = 0.6f
private const val ZOOM_MAX = 1.0f

private const val ZOOM_MAX_CHANGE_PER_SECOND = 1.0f

class GameCamera(width: Float, height: Float) :
        OrthographicCamera(width, height) {

    var interpolation: Interpolation = Interpolation.pow2Out

    fun update(delta: Float, player: GameObject) {
        updateZoom(delta, player)

        super.update()
    }

    private fun updateZoom(delta: Float, player: GameObject) {
        val physicsComponent: PhysicsComponent =
                player.components[PhysicsComponent::class] as PhysicsComponent

        val velocity = MathUtils.clamp(physicsComponent.getBody().linearVelocity.len(),
                VELOCITY_MIN, VELOCITY_MAX)

        // Inverse lerp between velocity min and max
        val velocityAlpha = inverseLerp(velocity, VELOCITY_MIN, VELOCITY_MAX)

        // Expected zoom based on the player's velocity
        val targetZoom = MathUtils.lerp(ZOOM_MIN, ZOOM_MAX, velocityAlpha)

        /*
         * Difference between the absolute value of the target zoom and the zoom.
         * The higher the value, the farther away the zoom is from the target zoom.
         */
        val zoomDelta = Math.abs(targetZoom - zoom)

        // Interpolate to smooth out the changes in the camera zoom
        val interpolatedZoom = interpolation.apply(zoomDelta) * Math.signum(targetZoom - zoom) *
                ZOOM_MAX_CHANGE_PER_SECOND

        zoom += interpolatedZoom * delta
    }

    fun inverseLerp(x: Float, min: Float, max: Float) = (x - min) / (max - min)
}
