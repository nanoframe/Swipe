package com.paperatus.swipe.objects

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.paperatus.swipe.components.StaticPhysicsComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent

class Destructible : GameObject() {

    init {
        val shape = PolygonShape()
        shape.setAsBox(1.5f, 1.5f)
        attachComponent<PhysicsComponent>(StaticPhysicsComponent(shape).apply {
            onInit = fun(body: Body) {
                val fixture = body.fixtureList[0]
                fixture.isSensor = true
            }
            addContactListener(object : PhysicsComponent.ContactListener {
                override fun onContactBegin(other: GameObject) {
                    requestRemove()
                }

                override fun onContactEnd(other: GameObject) {
                }
            })
        })
    }
}
