package com.paperatus.swipe.core.graph

import com.paperatus.swipe.core.scene.GameObject

class NodeTraversal {

    private var traversalCallback: Callback? = null

    /**
     * Traverses the given GameObject and calls the callback for every traversal
     *
     * Not all GameObjects are traversed; the condition is determined by
     * [Callback.canTraverse].
     *
     * @param callback the Callback to call upon a GameObject traversal.
     * @param root the start GameObject.
     * @param data the payload to send to the callback.
     */
    fun traverse(callback: Callback, root: GameObject, data: Any = Unit) {

        traversalCallback = callback
        root.children.forEach { traverse(it, data) }
        traversalCallback = null
    }

    fun traverse(node: GameObject, data: Any) {
        traversalCallback!!.onTraverse(node, data)

        node.children.takeIf { traversalCallback!!.canTraverse(node) }?.forEach {
            traverse(it, data)
        }
    }

    /**
     * A callback interface for every GameObject traversal.
     */
    interface Callback {
        /**
         * Returns a Boolean determining if the GameObject can be traversed.
         *
         * @param gameObject the GameObject to request the permission from.
         * @return true if the GameObject can be traversed, false otherwise.
         */
        fun canTraverse(gameObject: GameObject): Boolean

        /**
         * Invoked upon every GameObject traversal.
         *
         * @param gameObject the GameObject being traversed.
         * @param data the data of the traversal.
         */
        fun onTraverse(gameObject: GameObject, data: Any)
    }
}
