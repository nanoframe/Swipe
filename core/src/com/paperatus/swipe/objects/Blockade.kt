package com.paperatus.swipe.objects

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.paperatus.swipe.components.StaticPhysicsComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent

class Blockade : GameObject("blockade.png"), PhysicsComponent.ContactListener {
    var isHit = false

    init {
        val shape = PolygonShape()
        shape.setAsBox(1.5f, 1.5f)
        attachComponent<PhysicsComponent>(StaticPhysicsComponent(shape).apply {
            addContactListener(this@Blockade)
            onInit = fun(body: Body) {
                val fixture = body.fixtureList[0]
                fixture.isSensor = true
            }
        })
    }

    override fun onContactBegin(other: Body) {
        isHit = true
    }

    override fun onContactEnd(other: Body) {
    }
}
