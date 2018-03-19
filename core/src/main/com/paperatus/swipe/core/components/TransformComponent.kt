package com.paperatus.swipe.core.components

import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.Scale
import com.paperatus.swipe.core.Size
import com.paperatus.swipe.core.scene.GameObject

private val identityMatrix = Matrix3()

class TransformComponent : Component {

    val position = Vector2()
    val scale = Scale()
    val worldSize = Size()
    val anchor = Vector2()
    var rotation: Float = 0.0f
        set(value) {
            field = value; dirty = true
        }

    private val _position = Vector2()
    private val _scale = Scale()

    val transformMatrix = Matrix3()
    private var dirty = true

    override fun update(delta: Float, gameObject: GameObject) {
        updateVector(_position, position)
        updateVector(_scale, scale)

        if (dirty) {
            dirty = false
            val parentTransformMatrix =
                    gameObject.parent?.transform?.transformMatrix ?: identityMatrix

            transformMatrix.apply {
                idt()
                scale(_scale)
                rotateRad(rotation)
                translate(_position)
                mulLeft(parentTransformMatrix)
            }
        }
    }

    override fun receive(what: ComponentMessage, payload: Any?) {
    }

    fun updateVector(main: Vector2, new: Vector2) {
        if (!main.epsilonEquals(new)) {
            dirty = true
            main.set(new)
        }
    }
}
