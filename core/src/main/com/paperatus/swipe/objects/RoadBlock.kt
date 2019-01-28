package com.paperatus.swipe.objects

import com.paperatus.swipe.core.components.PhysicsComponent
import com.paperatus.swipe.core.physics.PhysicsBodyData
import com.paperatus.swipe.core.physics.RectangleShape
import com.paperatus.swipe.core.scene.GameObject

private val staticPhysicsData = PhysicsBodyData().apply {
    shape = RectangleShape(3.0f, 3.0f)
    bodyType = PhysicsBodyData.Type.STATIC
}

const val DAMAGE_PER_VELOCITY = 2.0f

class RoadBlock : GameObject(), PhysicsComponent.ContactListener {

    private var health = 100.0f

    init {
        val physicsComponent = PhysicsComponent(staticPhysicsData)
        physicsComponent.positioning = PhysicsComponent.Positioning.BODY_TO_OBJECT
        physicsComponent.addContactListener(this)

        attachComponent<PhysicsComponent>(physicsComponent)

        transform.worldSize.set(3.0f, 2.789f)
        transform.anchor.set(0.5f, 0.5f)
    }

    override fun update(delta: Float) {
        if (health <= 0) requestRemove()
    }

    // TODO: Check if GameObject is a Player
    override fun onContactBegin(other: GameObject) {
        val body = other.getComponent<PhysicsComponent>()!!.physicsBody
        val velocity = body.linearVelocity.len()
        health -= DAMAGE_PER_VELOCITY * velocity
    }

    override fun onContactEnd(other: GameObject) {
    }
}
