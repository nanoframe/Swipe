package com.paperatus.swipe.core

class NodeTraversal {

    private var nodeCallback: Callback? = null

    fun traverse(callback: Callback, root: GameObject, data: Any = Unit) {
        nodeCallback = callback
        traverse(root, data)
        nodeCallback = null
    }

    private fun traverse(root: GameObject, data: Any = Unit) {
        root.children.forEach {
            nodeCallback!!.onTraverse(it, data)
            traverse(it)
        }
    }

    interface Callback {
        fun onTraverse(gameObject: GameObject, data: Any)
    }
}
