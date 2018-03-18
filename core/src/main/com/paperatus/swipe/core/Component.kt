package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Matrix3
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.BodyDef
import com.badlogic.gdx.physics.box2d.CircleShape
import com.badlogic.gdx.physics.box2d.PolygonShape
import com.badlogic.gdx.physics.box2d.Shape
import com.badlogic.gdx.physics.box2d.World
import ktx.collections.GdxArray

typealias ComponentMessage = Int

/**
 * Components that can be attached to a GameObject instance to extend functionality.
 */
interface Component {

    /**
     * Updates the given GameObject.
     *
     * @param gameObject current GameObject.
     */
    fun update(delta: Float, gameObject: GameObject)

    /**
     * Receives a message sent by other components
     */
    fun receive(what: ComponentMessage, payload: Any? = null)
}

private val identityMatrix = Matrix3()
class TransformComponent : Component {

    val position = Vector2()
    val scale = Scale()
    val worldSize = Size()
    val anchor = Vector2()
    var rotation: Float = 0.0f
        set(value) { field = value; dirty = true }

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

/**
 * Handles user input.
 */
abstract class InputComponent : Component

/**
 * Provides a Physics system to a GameObject.
 */
open class PhysicsComponent(private val data: PhysicsBodyData) : Component {

    enum class Positioning {
        OBJECT_TO_BODY, BODY_TO_OBJECT
    }

    private var _physicsBody: Body? = null
    val physicsBody get() = _physicsBody!!
    var initialized = false
        private set

    var onInit: ((Body) -> Unit)? = null
    var positioning = Positioning.OBJECT_TO_BODY

    private val contactListeners = GdxArray<ContactListener>()

    override fun update(delta: Float, gameObject: GameObject) {
        val transform = gameObject.transform
        when (positioning) {
            Positioning.OBJECT_TO_BODY -> {
                gameObject.transform.position.set(
                        physicsBody.position.x + transform.worldSize.width *
                                (transform.anchor.x - 0.5f),
                        physicsBody.position.y + transform.worldSize.height *
                                (transform.anchor.y - 0.5f)
                )
            }
            Positioning.BODY_TO_OBJECT -> {
                physicsBody.setTransform(
                        transform.position.x + transform.worldSize.width *
                                (0.5f - transform.anchor.x),
                        transform.position.y + transform.worldSize.height *
                                (0.5f - transform.anchor.y),
                        0.0f)
            }
        }
    }

    /**
     * Called upon initialization to create Body instances.
     *
     * @param world Box2D game world.
     */
    fun init(world: World) {
        if (initialized) throw RuntimeException()

        val bodyDef = BodyDef()
        bodyDef.type = when(data.bodyType) {
            PhysicsBodyData.Type.STATIC -> BodyDef.BodyType.StaticBody
            PhysicsBodyData.Type.DYNAMIC -> BodyDef.BodyType.DynamicBody
            PhysicsBodyData.Type.KINEMATIC -> BodyDef.BodyType.KinematicBody
        }
        bodyDef.linearDamping = data.linearDampening
        bodyDef.angularDamping = data.angularDampening
        bodyDef.fixedRotation = data.fixedRotation

        val dataShape = data.shape

        val body = world.createBody(bodyDef)
        val shape: Shape
        val area: Float

        when (dataShape) {
            is Circle -> {
                val circle = CircleShape()
                circle.radius = dataShape.radius
                shape = circle
                area = MathUtils.PI * circle.radius * circle.radius
            }
            is Square -> {
                val square = PolygonShape()
                square.setAsBox(dataShape.width / 2.0f, dataShape.height / 2.0f)
                shape = square
                area = dataShape.width * dataShape.height
            }
            else -> throw RuntimeException()
        }

        val fixture = body.createFixture(shape, data.mass / area)
        fixture.isSensor = data.isSensor
        fixture.restitution = data.restitution
        shape.dispose()

        body.position.set(data.position)

        _physicsBody = body
        onInit?.invoke(physicsBody)
        initialized = true
    }

    fun destroy(world: World) {
        world.destroyBody(physicsBody)
    }

    fun addContactListener(contactListener: ContactListener) {
        contactListeners.add(contactListener)
    }

    fun removeContactListener(contactListener: ContactListener) {
        contactListeners.removeValue(contactListener, true)
    }

    fun postCollisionStart(other: GameObject) {
        contactListeners.forEach { it.onContactBegin(other) }
    }

    fun postCollisionEnd(other: GameObject) {
        contactListeners.forEach { it.onContactEnd(other) }
    }

    override fun receive(what: ComponentMessage, payload: Any?) = Unit

    interface ContactListener {
        fun onContactBegin(other: GameObject)
        fun onContactEnd(other: GameObject)
    }


}

interface PhysicsShape
class Square(val width: Float, val height: Float) : PhysicsShape
class Circle(val radius: Float) : PhysicsShape

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

open class RenderComponent(val renderMode: Mode = Mode.SPRITE, open var sprite: String? = null) : Component {
    enum class Mode {
        SPRITE, CUSTOM
    }

    var alpha = 1.0f
    val customParams = GdxArray<RenderParams>()

    private val internalParams = object : RenderParams {
        private val originalColor = Color()

        override fun applyParams(batch: SpriteBatch) {
            originalColor.set(batch.color)
            batch.setColor(
                    originalColor.r,
                    originalColor.g,
                    originalColor.b,
                    alpha)
        }

        override fun resetParams(batch: SpriteBatch) {
            batch.color = originalColor
        }
    }

    init {
        customParams.add(internalParams)
    }

    override fun update(delta: Float, gameObject: GameObject) = Unit

    override fun receive(what: ComponentMessage, payload: Any?) = Unit

    fun addRenderParams(params: RenderParams) = customParams.add(params)

    fun removeRenderParams(params: RenderParams, identity: Boolean = true) =
            customParams.removeValue(params, identity)
}

class AnimationRenderComponent(val delay: Float,
                               val repeat: Boolean = true,
                               private vararg var frames: String) : RenderComponent(Mode.SPRITE) {

    var onFinish: (() -> Unit)? = null

    var isRunning = false
        private set

    override var sprite: String? = frames[0]
    private var timeElapsed = 0.0f
    private var index = 0

    override fun update(delta: Float, gameObject: GameObject) {
        if (!isRunning) return

        timeElapsed += delta
        if (timeElapsed >= delay) {
            if (!repeat && index + 1 >= frames.size) {
                onFinish?.invoke()
                isRunning = false
            } else {
                index = (index + 1) % frames.size
            }
            sprite = frames[index]
        }
    }

    fun start() {
        index = 0
        isRunning = true
    }

    fun stop() {
        isRunning = false
    }
}
