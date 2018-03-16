package com.paperatus.swipe.core

class NodeTraversal {

    private var nodeCallback: Callback? = null

    fun traverse(callback: Callback, root: GameObject, data: Any = Unit) {
        nodeCallback = callback
        traverse(root, data)
        nodeCallback = null
    }

    private fun traverse(root: GameObject, data: Any) {
        root.children.forEach {
            nodeCallback!!.onTraverse(it, data)
            if (nodeCallback!!.canTraverse(it)) traverse(it, data)
        }
    }

    interface Callback {
        fun canTraverse(gameObject: GameObject): Boolean
        fun onTraverse(gameObject: GameObject, data: Any)
    }
}
