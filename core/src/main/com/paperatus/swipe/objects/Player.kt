package com.paperatus.swipe.objects

import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent

private const val PARTICLE_INITIAL_FREQUENCY: Float = 1.0f // per second
private const val PARTICLE_VELOCITY_MULTIPLIER: Float = 1.2f
private const val PARTICLE_MAX_FREQUENCY: Float = 35.0f
class Player : GameObject() {

    private var elapsed = 0.0f

    override fun update(delta: Float) {
        elapsed = (elapsed + delta).coerceAtMost(2.0f)

        val velocity = getComponent<PhysicsComponent>()!!.physicsBody.linearVelocity.len()
        val frequency =
                (PARTICLE_INITIAL_FREQUENCY *
                velocity *
                PARTICLE_VELOCITY_MULTIPLIER)
                        .coerceAtMost(PARTICLE_MAX_FREQUENCY)
        val spawnTimes = 1.0f / frequency
        if (elapsed >= spawnTimes) {
            elapsed -= spawnTimes
            post(Notification.PARTICLE_SPAWN, transform.position)
        }
    }
}
