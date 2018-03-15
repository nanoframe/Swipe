package com.paperatus.swipe.objects

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.Observer
import com.paperatus.swipe.core.RenderComponent
import ktx.math.plus

class ParticleGenerator : GameObject(), Observer {
    override fun receive(what: Int, payload: Any?) {
        if (what != Notification.PARTICLE_SPAWN) return
        val position = payload as Vector2

        addChild(createParticle(position))
    }

    private fun createParticle(p: Vector2) = GameObject().apply {
        val startScale = MathUtils.random(0.1f, 0.2f)
        val endScale = MathUtils.random(0.5f, 1.0f)
        val duration = MathUtils.random(0.5f, 1.2f)
        val positionOffset = Vector2(
                MathUtils.random(-0.5f, 0.5f),
                0.0f
        )

        transform.position.set(p + positionOffset)
        transform.worldSize.set(1.7f, 1.7f)
        transform.scale.set(startScale)
        transform.anchor.set(0.5f, 0.5f)
        attachComponent<RenderComponent>(RenderComponent(sprite="particle.png"))

        runAction(Actions.sequence {
            spawn {
                scaleTo(endScale, duration, Interpolation.pow2Out)
                fade(1.5f)
            }
            execute { requestRemove() }
        })
    }
}
