package com.paperatus.swipe.core

import Action
import com.badlogic.gdx.math.Rectangle
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.ObjectMap
import ktx.collections.GdxArray
import kotlin.reflect.KClass

private val temp = GdxArray<GameObject>()

/**
 * Interface for objects that can be rendered onto the scene.
 *
 * An empty string or a nonexistent file will throw an exception.
 * @property components Components that are attached to the GameObject
 */
open class GameObject : Subject() {

    val transform
        get() = getComponent<TransformComponent>()!!

    private val components = ObjectMap<KClass<out Component>, Component>()
    private var activeAction: Action? = null
    val children = GdxArray<GameObject>() // TODO: Implement custom data structure
    var parent: GameObject? = null
        private set

    init {
        attachComponent(TransformComponent())
    }

    var shouldRemove = false
        private set

    /**
     * Updates the GameObject.
     *
     * @param [delta] the time since the last frame; capped at [SceneController.maxDeltaTime]
     */
    open fun update(delta: Float) {
        children.forEach {
            if (it.shouldRemove) {
                temp.add(it)
            }
        }
        temp.forEach {
            children.removeValue(it, true)
        }

        updateAction(delta)
    }

    fun requestRemove() {
        shouldRemove = true
    }

    fun runAction(action: Action) {
        activeAction?.let {
            ktx.log.info("[WARN]") {
                "The current active action on a GameObject will be replaced!\n"
            }
        }

        action.setGameObject(this)
        action.start()
        activeAction = action
    }

    fun stopAction() {
        activeAction?.setGameObject(null)
        activeAction = null
    }

    fun isActionActive() = activeAction != null

    private fun updateAction(delta: Float) = activeAction?.let {
        it.update(delta)
        if (it.isFinished()) stopAction()
    }

    fun addChild(child: GameObject) {
        if (child.parent != null) throw RuntimeException("GameObject already has a parent!")
        child.parent = this
        children.add(child)
    }

    fun addChild(child: GameObject, at: Int) {
        if (child.parent != null) throw RuntimeException("GameObject already has a parent!")
        child.parent = this
        children.insert(at, child)
    }

    fun removeChild(child: GameObject, identity: Boolean = true) = children.removeValue(child, identity)

    fun removeAt(index: Int) = children.removeIndex(index)


    /**
     * Attaches a Component onto the GameObject instance.
     *
     * The type of the Component should be specified to prevent weird problems.
     *
     * @param component the component to attach to the GameObject.
     */
    inline fun <reified T : Component> attachComponent(component: T) =
            attachComponent(component, T::class)

    /**
     * Attaches a Component onto the GameObject instance.
     *
     * This method serves as a helper method to [attachComponent]
     *
     * The type of the Component should be specified to prevent weird problems.
     *
     * @param component the component to attach to the GameObject.
     */
    fun attachComponent(component: Component, type: KClass<out Component>) {
        if (components.containsKey(type)) {
            throw ComponentException(
                    "A component of type ${type.java.simpleName}" +
                    "is currently attached to this instance!")
        }

        components.put(type, component)
    }

    /**
     * Detaches a Component from the GameObject instance.
     */
    inline fun <reified T : Component> detachComponent(): Component = detachComponent(T::class)

    // TODO: Add callback for attaching/detaching components on the component
    /**
     * Detaches a Component from the GameObject instance.
     *
     * @param type class type of the Component. [detachComponent] should be
     * called instead for a cleaner code.
     */
    fun detachComponent(type: KClass<out Component>): Component = components.remove(type)

    inline fun <reified T : Component> getComponent() = getComponent(T::class)

    fun <T : Component> getComponent(type: KClass<T>): T? {
        return components[type, null] as T?
    }

    fun getComponents() = components

    /**
     * Sends a message to other components with an optional payload
     *
     * @param what message type
     * @param payload the data of the message; defaults to null
     */
    fun messageComponent(what: ComponentMessage, payload: Any? = null) {
        components.keys().forEach {
            components[it].receive(what, payload)
        }
    }
}
