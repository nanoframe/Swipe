package com.paperatus.swipe.core.physics

import com.badlogic.gdx.math.Vector2

class PhysicsBodyData(init: (PhysicsBodyData.() -> Unit)? = null) {

    enum class Type {
        DYNAMIC, STATIC, KINEMATIC
    }

    val position = Vector2()
    var shape: PhysicsShape? = null
    var mass: Float = 0.0f
    var bodyType: Type = Type.STATIC
    var linearDampening = 0.0f
    var angularDampening = 0.0f
    var restitution = 0.5f
    var fixedRotation = false
    var isSensor = false

    init {
        if (init != null) {
            init()
        }
    }
}
