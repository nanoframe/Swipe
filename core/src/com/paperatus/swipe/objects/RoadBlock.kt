package com.paperatus.swipe.objects

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.paperatus.swipe.components.StaticPhysicsComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent

const val DAMAGE_PER_VELOCITY = 2.0f
class RoadBlock : GameObject(), PhysicsComponent.ContactListener {

    var health = 100.0f

    init {
        val shape = PolygonShape()
        shape.setAsBox(1.5f, 1.5f)
        attachComponent<PhysicsComponent>(StaticPhysicsComponent(shape).apply {
            addContactListener(this@RoadBlock)
            onInit = fun(body: Body) {
                body.fixtureList[0].restitution = 0.8f
            }
        })
    }

    override fun update(delta: Float) {
        if (health <= 0) requestRemove()
    }

    // TODO: Check if GameObject is a Player
    override fun onContactBegin(other: GameObject) {
        val body = other.getComponent<PhysicsComponent>()!!.getBody()
        val velocity = body.linearVelocity.len()
        health -= DAMAGE_PER_VELOCITY * velocity
    }

    override fun onContactEnd(other: GameObject) {
    }
}
