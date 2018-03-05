package com.paperatus.swipe.objects

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.paperatus.swipe.components.StaticPhysicsComponent
import com.paperatus.swipe.core.AnimateOnceRenderComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent
import com.paperatus.swipe.core.RenderComponent

private val explosionAnimation = Array(137) {
    "destructible/explosion$it"
}

class Destructible : GameObject(), PhysicsComponent.ContactListener {

    init {
        val shape = PolygonShape()
        shape.setAsBox(1.5f, 1.5f)
        attachComponent<PhysicsComponent>(StaticPhysicsComponent(shape).apply {
            onInit = fun(body: Body) {
                val fixture = body.fixtureList[0]
                fixture.isSensor = true
            }
            addContactListener(this@Destructible)
        })
    }

    override fun onContactBegin(other: GameObject) {
        size.set(10.0f, 10.0f)
        detachComponent<RenderComponent>()
        attachComponent<RenderComponent>(AnimateOnceRenderComponent(
                1.0f / 60.0f,
                *explosionAnimation
        ).also {
            it.onFinish = fun () {
                requestRemove()
            }
        })
    }

    override fun onContactEnd(other: GameObject) {
    }
}
