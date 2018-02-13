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
