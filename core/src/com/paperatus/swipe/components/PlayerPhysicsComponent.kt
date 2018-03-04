package com.paperatus.swipe.components

import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.core.ComponentMessage
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent
import ktx.box2d.body
import ktx.math.times
import ktx.math.unaryMinus

const val MAX_VELOCITY = 27.0f

class PlayerPhysicsComponent : PhysicsComponent() {

    private lateinit var physicsBody: Body
    val radius = 0.7f

    override fun init(world: World) {
        physicsBody = world.body(BodyDef.BodyType.DynamicBody) {
            // TODO: Dispose created shape
            circle(radius) {
                density = 0.3f / (MathUtils.PI * radius * radius)
            }

            fixedRotation = true
            linearDamping = 0.8f
        }

        physicsBody.setTransform(0.0f, 2.0f, 0.0f)
    }

    override fun destroy(world: World) {
        world.destroyBody(physicsBody)
    }

    override fun getBody(): Body = physicsBody

    override fun update(gameObject: GameObject) {
        gameObject.position.set(
                physicsBody.position.x + gameObject.size.width *
                        (gameObject.anchor.x - 0.5f),
                physicsBody.position.y + gameObject.size.height *
                        (gameObject.anchor.y - 0.5f)
        )

        // Rotate in the direction of movement
        val velocity = physicsBody.linearVelocity
        val velocityLen2 = velocity.len2()

        if (!MathUtils.isEqual(velocityLen2, 0.0f)) {
            gameObject.rotation = (MathUtils.atan2(
                    velocity.y, velocity.x) -
                    MathUtils.PI / 2.0f) * MathUtils.radDeg
        }

        // Limit the max velocity
        if (velocityLen2 > MAX_VELOCITY * MAX_VELOCITY) {
            physicsBody.linearVelocity = velocity.nor() * MAX_VELOCITY
        }
    }

    override fun receive(what: ComponentMessage, payload: Any?) {
        // Receive input events from the InputComponent
        when (what) {
            Message.MOVEMENT -> {
                physicsBody.applyForceToCenter(
                        payload as Vector2,
                        true)
            }
            Message.BLOCKADE_COLLISION -> {
                physicsBody.applyForceToCenter(
                        -physicsBody.linearVelocity * 2.0f,
                        true)
            }
        }
    }
}
