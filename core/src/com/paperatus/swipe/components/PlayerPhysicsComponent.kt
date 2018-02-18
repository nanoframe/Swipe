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
import ktx.math.times

const val MAX_VELOCITY = 25.0f
class PlayerPhysicsComponent : PhysicsComponent() {

    private var physicsBody: Body? = null
    val radius = 0.7f

    override fun init(world: World) {
        physicsBody = world.body(BodyDef.BodyType.DynamicBody) {
            // TODO: Dispose created shape
            circle(radius) {
                density = 0.3f / (MathUtils.PI * radius * radius)
            }

            fixedRotation = true
            linearDamping = 1.1f
        }
    }

    override fun getBody(): Body = physicsBody!!

    override fun update(gameObject: GameObject) {
        gameObject.position.set(
                physicsBody!!.position.x + gameObject.size.width *
                        (gameObject.anchor.x - 0.5f),
                physicsBody!!.position.y + gameObject.size.height *
                        (gameObject.anchor.y - 0.5f)
        )

        // Rotate in the direction of movement
        val velocity = physicsBody!!.linearVelocity
        val velocityLen2 = velocity.len2()

        if (!MathUtils.isEqual(velocityLen2, 0.0f)) {
            gameObject.rotation = (MathUtils.atan2(
                    velocity.y, velocity.x) -
                    MathUtils.PI / 2.0f) * MathUtils.radDeg
        }

        // Limit the max velocity
        if (velocityLen2 > MAX_VELOCITY * MAX_VELOCITY) {
            physicsBody!!.linearVelocity = velocity.nor() * MAX_VELOCITY
        }
    }

    override fun receive(what: Component.Message, payload: Any?) {
        // Receive input events from the InputComponent
        when (what) {
            Component.Message.MOVEMENT -> {
                physicsBody!!.applyForceToCenter(
                        payload as Vector2,
                        true)
            }
        }
    }
}
