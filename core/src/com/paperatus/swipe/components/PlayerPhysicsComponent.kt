package com.paperatus.swipe.components

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.core.Component
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent
import ktx.box2d.body

class PlayerPhysicsComponent : PhysicsComponent() {
    var body: Body? = null
    val radius = 0.7f

    override fun init(world: World) {
        body = world.body(BodyDef.BodyType.DynamicBody) {
            // TODO: Dispose created shape
            circle(radius) {
                density = 0.3f / (MathUtils.PI * radius * radius)
            }
            linearDamping = 2.0f
        }
    }

    override fun update(gameObject: GameObject) {
        gameObject.position.set(
                body!!.position.x - gameObject.bounds.width / 2.0f,
                body!!.position.y - gameObject.bounds.height / 2.0f
        )

        // Rotate in the direction of movement
        val velocity = body!!.linearVelocity
        if (!MathUtils.isEqual(velocity.len2(), 0.0f)) {
            gameObject.rotation = (MathUtils.atan2(
                    velocity.y, velocity.x) -
                    MathUtils.PI / 2.0f) * MathUtils.radDeg
        }
    }

    override fun receive(what: Component.Message, payload: Any?) {
        // Receive input events from the InputComponent
        when (what) {
            Component.Message.MOVEMENT -> {
                body!!.applyForceToCenter(
                        payload as Vector2,
                        true)
            }
        }
    }
}
