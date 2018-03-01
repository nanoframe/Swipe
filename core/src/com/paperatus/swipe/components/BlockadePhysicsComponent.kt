package com.paperatus.swipe.components

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.core.Component
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent

class BlockadePhysicsComponent : PhysicsComponent() {
    private var physicsBody: Body? = null

    override fun init(world: World) {
        val body = world.createBody(BodyDef())
        val shape = PolygonShape()
        shape.setAsBox(2.0f, 2.0f)
        val fixture = body.createFixture(shape, 0.0f)
        fixture.restitution = 0.8f

        physicsBody = body
    }

    override fun update(gameObject: GameObject) {
    }

    override fun receive(what: Component.Message, payload: Any?) {
    }

    override fun getBody(): Body = physicsBody!!
}
