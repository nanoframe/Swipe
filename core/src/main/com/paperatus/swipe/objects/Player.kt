package com.paperatus.swipe.objects

import com.paperatus.swipe.core.GameObject

private const val PARTICLE_SPAWN_FREQUENCY = 25 // per second
class Player : GameObject() {

    private var elapsed = 0.0f

    override fun update(delta: Float) {
        elapsed = (elapsed + delta).coerceAtMost(2.0f)

        val spawnTimes = 1.0f / PARTICLE_SPAWN_FREQUENCY.toFloat()
        if (elapsed >= spawnTimes) {
            elapsed -= spawnTimes
            post(Notification.PARTICLE_SPAWN)
        }
    }
}
