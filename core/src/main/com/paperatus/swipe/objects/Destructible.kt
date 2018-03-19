package com.paperatus.swipe.objects

import Message
import com.paperatus.swipe.core.components.AnimationRenderComponent
import com.paperatus.swipe.core.components.PhysicsComponent
import com.paperatus.swipe.core.components.RenderComponent
import com.paperatus.swipe.core.physics.PhysicsBodyData
import com.paperatus.swipe.core.physics.RectangleShape
import com.paperatus.swipe.core.scene.GameObject
import com.paperatus.swipe.data.ExplosionParticleBlending

private val explosionAnimation = Array(127) {
    "destructible/explosion$it"
}

private val staticPhysicsData = PhysicsBodyData().apply {
    shape = RectangleShape(2.0f, 1.859f)
    bodyType = PhysicsBodyData.Type.STATIC
    isSensor = true
}

class Destructible : GameObject(), PhysicsComponent.ContactListener {
    var isDestructing = false
        private set

    init {
        val physicsComponent = PhysicsComponent(staticPhysicsData)
        physicsComponent.positioning = PhysicsComponent.Positioning.BODY_TO_OBJECT
        physicsComponent.addContactListener(this)

        attachComponent<PhysicsComponent>(physicsComponent)
        transform.anchor.set(0.5f, 0.5f)
        transform.worldSize.set(2.0f, 1.859f)
    }

    override fun onContactBegin(other: GameObject) {
        if (other !is Player || isDestructing) return
        isDestructing = true

        detachComponent<RenderComponent>()

        transform.worldSize.set(10.0f, 10.0f)
        val animation = AnimationRenderComponent(
                1.0f / 60.0f,
                false,
                *explosionAnimation)
        animation.apply {
            onFinish = fun() {
                requestRemove()
            }
            addRenderParams(ExplosionParticleBlending())
            start()
        }
        attachComponent<RenderComponent>(animation)

        // Disabled input
        other.messageComponent(Message.BLOCKADE_COLLISION)
    }

    override fun onContactEnd(other: GameObject) {
    }
}
