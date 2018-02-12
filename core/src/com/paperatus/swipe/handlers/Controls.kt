package com.paperatus.swipe.handlers

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.objects.GameObject
import ktx.box2d.body
import ktx.math.times

interface Component {
    enum class Message {
        MOVEMENT
    }

    enum class Order {
        PRE_UPDATE, UPDATE, POST_UPDATE,
        PRE_RENDER, RENDER, POST_RENDER,
        MANUAL
    }

    val order: Order

    fun update(character: GameObject)

    fun receive(what: Message, payload: Any? = null)
}

/**
 * The Input component of the Player class.
 */
abstract class InputComponent : Component {
    override val order = Component.Order.PRE_UPDATE
}

/**
 * Provides touch-event based input to control the Player
 */
class PlayerTouchInput : InputComponent() {
    companion object {
        const val MAX_SPEED = 12.0f
    }

    val direction = Vector2()
    var lastTouchTime = System.currentTimeMillis()

    override fun update(character: GameObject) {
        if (Gdx.input.justTouched()) {
            // Begin touch events
            lastTouchTime = System.currentTimeMillis()

        } else if (Gdx.input.isTouched) {
            val deltaTime = (System.currentTimeMillis() - lastTouchTime).toFloat()

            // Speed based on the change in the touch position to the change in time
            // Similar to y=1/t where y is the speed
            direction.set(Gdx.input.deltaX.toFloat(), Gdx.input.deltaY.toFloat()) *
                    (MAX_SPEED / deltaTime)

            direction.y = -direction.y // y-down to y-up

            // Prevent the player from moving too fast
            if (direction.len2() > MAX_SPEED * MAX_SPEED) {
                direction.nor().scl(MAX_SPEED)
            }

            character.messageComponent(Component.Message.MOVEMENT, direction)

        }
    }

    override fun receive(what: Component.Message, payload: Any?) {
    }
}

abstract class PhysicsComponent : Component {
    override val order = Component.Order.POST_UPDATE

    abstract fun initBody(world: World)
}

class PlayerPhysicsComponent : PhysicsComponent() {
    var body: Body? = null
    val radius = 0.7f

    override fun initBody(world: World) {
        body = world.body(BodyDef.BodyType.DynamicBody) {
            // TODO: Dispose created shape
            circle(radius)
        }
    }

    override fun update(character: GameObject) {
        character.position.set(
                body!!.position.x - character.bounds.width / 2.0f,
                body!!.position.y - character.bounds.height / 2.0f
        )
    }

    override fun receive(what: Component.Message, payload: Any?) {
        when (what) {
            Component.Message.MOVEMENT -> {
                body!!.applyForceToCenter(
                        payload as Vector2,
                        true)
            }
        }
    }

}