package com.paperatus.swipe.core

import com.badlogic.gdx.utils.Queue

class NodeTraversal {

    private var updateQueue = Queue<GameObject>()

    fun traverse(callback: Callback, root: GameObject, data: Any = Unit) {
        callback.onTraverse(root, data)

        updateQueue.clear()
        root.children.forEach { updateQueue.addLast(it) }

        while (updateQueue.size > 0) {
            val node = updateQueue.removeFirst()
            callback.onTraverse(node, data)

            node.children.takeIf { callback.canTraverse(node) }?.forEach {
                updateQueue.addLast(it)
            }
        }
    }

    interface Callback {
        fun canTraverse(gameObject: GameObject): Boolean
        fun onTraverse(gameObject: GameObject, data: Any)
    }
}
