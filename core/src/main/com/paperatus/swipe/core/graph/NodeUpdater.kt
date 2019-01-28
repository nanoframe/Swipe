package com.paperatus.swipe.core.graph

import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.core.components.Component
import com.paperatus.swipe.core.components.InputComponent
import com.paperatus.swipe.core.components.PhysicsComponent
import com.paperatus.swipe.core.components.RenderComponent
import com.paperatus.swipe.core.components.TransformComponent
import com.paperatus.swipe.core.scene.GameObject
import kotlin.reflect.KClass

private val COMPONENT_ORDER: Array<KClass<out Component>> = arrayOf(
        InputComponent::class,
        Component::class, // Used to update the GameObject itself
        PhysicsComponent::class,
        TransformComponent::class,
        RenderComponent::class
)

/**
 * Updates the GameObject on every traversal.
 *
 * The data of the callback is the delta time of the game.
 */
open class NodeUpdater : NodeTraversal.Callback {

    override fun onTraverse(gameObject: GameObject, data: Any) {
        val delta = data as Float

        for (i in 0 until COMPONENT_ORDER.size) {
            val order = COMPONENT_ORDER[i]
            if (order == Component::class) {
                gameObject.update(delta)
                continue
            }

            gameObject.getComponent(order)?.update(delta, gameObject)
        }
    }

    override fun canTraverse(gameObject: GameObject) = gameObject.parent != null
}

class NodePhysicsUpdater(private val world: World) : NodeUpdater() {
    override fun onTraverse(gameObject: GameObject, data: Any) {
        gameObject.getComponent<PhysicsComponent>()?.let {
            it.takeIf { !it.initialized }?.let {
                it.init(world)
                it.physicsBody.userData = gameObject
            }
        }

        super.onTraverse(gameObject, data)
    }
}
