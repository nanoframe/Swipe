package com.paperatus.swipe.components

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.core.ComponentMessage
import com.paperatus.swipe.core.PhysicsComponent

class StaticPhysicsComponent(private val shape: Shape) : PhysicsComponent() {
    private var physicsBody: Body? = null

    override fun init(world: World) {
        val body = world.createBody(BodyDef())
        body.createFixture(shape, 0.0f)

        shape.dispose()

        physicsBody = body
    }

    override fun destroy(world: World) {
        world.destroyBody(physicsBody)
    }

    override fun receive(what: ComponentMessage, payload: Any?) {
    }

    override fun getBody(): Body = physicsBody!!
}
