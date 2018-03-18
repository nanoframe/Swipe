package com.paperatus.swipe.components

import Message
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.Circle
import com.paperatus.swipe.core.ComponentMessage
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsBodyData
import com.paperatus.swipe.core.PhysicsComponent
import ktx.math.times
import ktx.math.unaryMinus

const val MAX_VELOCITY = 27.0f

private val playerPhysicsData = PhysicsBodyData {
    shape = Circle(0.7f)
    position.set(0.0f, 10.0f)
    bodyType = PhysicsBodyData.Type.DYNAMIC
    mass = 0.3f
    fixedRotation = true
    linearDampening = 0.8f
}

class PlayerPhysicsComponent : PhysicsComponent(playerPhysicsData) {

    override fun update(delta: Float, gameObject: GameObject) {
        super.update(delta, gameObject)

        // Rotate in the direction of movement
        val velocity = physicsBody.linearVelocity
        val velocityLen2 = velocity.len2()

        if (!MathUtils.isEqual(velocityLen2, 0.0f)) {
            gameObject.transform.rotation = (MathUtils.atan2(
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
