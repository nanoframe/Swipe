package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.World
import ktx.collections.GdxArray

typealias ComponentMessage = Int

/**
 * Components that can be attached to a GameObject instance to extend functionality.
 *
 * @property order update order in which the component should be updated.
 */
interface Component {

    enum class Order {
        PRE_UPDATE, UPDATE, POST_UPDATE,
        PRE_RENDER, RENDER, POST_RENDER,
        MANUAL
    }

    val order: Order

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

/**
 * Handles user input.
 */
abstract class InputComponent : Component {
    override val order = Component.Order.PRE_UPDATE
}

/**
 * Provides a Physics system to a GameObject.
 */
abstract class PhysicsComponent : Component {

    enum class Positioning {
        OBJECT_TO_BODY, BODY_TO_OBJECT
    }

    var positioning = Positioning.OBJECT_TO_BODY

    override val order = Component.Order.POST_UPDATE
    private val contactListeners = GdxArray<ContactListener>()
    var onInit: ((Body) -> Unit)? = null

    override fun update(delta: Float, gameObject: GameObject) {
        val physicsBody = getBody()
        when (positioning) {
            Positioning.OBJECT_TO_BODY -> {
                gameObject.position.set(
                        physicsBody.position.x + gameObject.size.width *
                                (gameObject.anchor.x - 0.5f),
                        physicsBody.position.y + gameObject.size.height *
                                (gameObject.anchor.y - 0.5f)
                )
            }
            Positioning.BODY_TO_OBJECT -> {
                physicsBody.setTransform(
                        gameObject.position.x + gameObject.size.width *
                                (0.5f - gameObject.anchor.x),
                        gameObject.position.y + gameObject.size.height *
                                (0.5f - gameObject.anchor.y),
                        0.0f)
            }
        }
    }

    /**
     * Called upon initialization to create Body instances.
     *
     * @param world Box2D game world.
     */
    abstract fun init(world: World)

    abstract fun destroy(world: World)

    abstract fun getBody(): Body

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

    interface ContactListener {
        fun onContactBegin(other: GameObject)
        fun onContactEnd(other: GameObject)
    }
}

open class RenderComponent(val renderMode: Mode = Mode.SPRITE, open var sprite: String? = null) : Component {
    enum class Mode {
        SPRITE, CUSTOM
    }

    var customParams = GdxArray<RenderParams>()

    override fun update(delta: Float, gameObject: GameObject) = Unit

    override fun receive(what: ComponentMessage, payload: Any?) = Unit


    override val order = Component.Order.RENDER
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
