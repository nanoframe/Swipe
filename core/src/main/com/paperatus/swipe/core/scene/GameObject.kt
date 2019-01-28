package com.paperatus.swipe.core.scene

import com.badlogic.gdx.utils.ObjectMap
import com.paperatus.swipe.core.ComponentException
import com.paperatus.swipe.core.actions.Action
import com.paperatus.swipe.core.components.Component
import com.paperatus.swipe.core.components.ComponentMessage
import com.paperatus.swipe.core.components.TransformComponent
import com.paperatus.swipe.core.patterns.Subject
import ktx.collections.GdxArray
import kotlin.reflect.KClass

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
    val children = GdxArray<GameObject>()
    var parent: GameObject? = null
        private set

    init {
        attachComponent(TransformComponent())
    }

    /**
     * Updates the GameObject.
     *
     * @param [delta] the time since the last frame; capped at [SceneController.maxDeltaTime]
     */
    open fun update(delta: Float) {
        updateAction(delta)
    }

    /**
     * Requests the GameObject to be removed from its parent.
     *
     * Calling this method will set its parent to null as a flag to be later
     * removed from its parent.
     */
    fun requestRemove() {
        parent = null
    }

    /**
     * Executes the given action
     *
     * @param action the action to run.
     */
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

    /**
     * Stops any active action.
     *
     * Calling this method with no running action will do nothing.
     */
    fun stopAction() {
        activeAction?.end()
        activeAction?.setGameObject(null)
        activeAction = null
    }

    /**
     * Returns whether or not an action is running.
     *
     * @return a Boolean indicating if an action is running.
     */
    fun isActionActive() = activeAction != null

    private fun updateAction(delta: Float) = activeAction?.let {
        it.update(delta)
        if (it.isFinished()) stopAction()
    }

    /**
     * Adds a GameObject to this instance.
     *
     * @param child the GameObject to add.
     */
    fun addChild(child: GameObject) {
        if (child.parent != null) throw RuntimeException("GameObject already has a parent!")
        child.parent = this
        children.add(child)
    }

    /**
     * Adds a GameObject to this instance at a specified location
     *
     * @param child the GameObject to add.
     * @param at the position to insert the GameObject to.
     */
    fun addChild(child: GameObject, at: Int) {
        if (child.parent != null) throw RuntimeException("GameObject already has a parent!")
        child.parent = this
        children.insert(at, child)
    }

    /**
     * Removes the given GameObject from this instance.
     *
     * @param child the GameObject to remove.
     * @param identity true results in '==' comparison, false results in .equals().
     */
    fun removeChild(child: GameObject, identity: Boolean = true) = children.removeValue(child, identity)

    /**
     * Removes a GameObject at the given index
     *
     * @param index the index to remove.
     * @return the removed GameObject instance.
     */
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
