package com.paperatus.swipe.core.graph

import com.badlogic.gdx.physics.box2d.World
import com.paperatus.swipe.core.components.PhysicsComponent
import com.paperatus.swipe.core.scene.GameObject
import ktx.collections.GdxArray

/**
 * Removes GameObjects from its parent.
 *
 * The status determining if the GameObject should be removed is based
 * on if the GameObject contains a parent. The parent is set to null
 * upon calling [GameObject.requestRemove].
 *
 * This callback does not have a payload data.
 */
open class NodeRemover : NodeTraversal.Callback {

    private val temp = GdxArray<GameObject>()
    override fun onTraverse(gameObject: GameObject, data: Any) {
        gameObject.children.forEach {
            if (it.parent == null) {
                temp.add(it)
            }
        }

        temp.forEach {
            onRemoval(it)
            gameObject.removeChild(it)
        }
        temp.clear()
    }

    open fun onRemoval(gameObject: GameObject) = Unit

    override fun canTraverse(gameObject: GameObject) = true
}

class NodePhysicsRemover(private val world: World) : NodeRemover() {
    override fun onRemoval(gameObject: GameObject) {
        super.onRemoval(gameObject)
        gameObject.getComponent<PhysicsComponent>()?.destroy(world)
    }
}
