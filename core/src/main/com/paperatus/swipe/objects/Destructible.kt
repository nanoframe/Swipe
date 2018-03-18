package com.paperatus.swipe.objects

import com.paperatus.swipe.core.AnimationRenderComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsBodyData
import com.paperatus.swipe.core.PhysicsComponent
import com.paperatus.swipe.core.RenderComponent
import com.paperatus.swipe.core.Square
import com.paperatus.swipe.data.ExplosionParticleBlending

private val explosionAnimation = Array(127) {
    "destructible/explosion$it"
}

private val staticPhysicsData = PhysicsBodyData().apply {
    shape = Square(3.0f, 3.0f)
    bodyType = PhysicsBodyData.Type.STATIC
    isSensor = true
}

class Destructible : GameObject(), PhysicsComponent.ContactListener {

    init {
        val physicsComponent = PhysicsComponent(staticPhysicsData)
        physicsComponent.positioning = PhysicsComponent.Positioning.BODY_TO_OBJECT
        physicsComponent.addContactListener(this)

        attachComponent<PhysicsComponent>(physicsComponent)
        transform.anchor.set(0.5f, 0.5f)
        transform.worldSize.set(3.0f, 2.789f)
    }

    override fun onContactBegin(other: GameObject) {
        detachComponent<RenderComponent>()

        transform.worldSize.set(10.0f, 10.0f)
        val animation = AnimationRenderComponent(
                1.0f / 60.0f,
                false,
                *explosionAnimation)
        animation.apply {
            onFinish = fun () {
                requestRemove()
            }
            addRenderParams(ExplosionParticleBlending())
            start()
        }
        attachComponent<RenderComponent>(animation)
    }

    override fun onContactEnd(other: GameObject) {
    }
}
