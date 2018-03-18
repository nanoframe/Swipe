package com.paperatus.swipe.core

class NodeTraversal {

    private var traversalCallback: Callback? = null

    fun traverse(callback: Callback, root: GameObject, data: Any = Unit) {
        //callback.onTraverse(root, data)

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

    interface Callback {
        fun canTraverse(gameObject: GameObject): Boolean
        fun onTraverse(gameObject: GameObject, data: Any)
    }
}
