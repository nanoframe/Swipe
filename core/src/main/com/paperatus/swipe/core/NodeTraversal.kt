package com.paperatus.swipe.core

class NodeTraversal {

    var callback: Callback? = null

    fun traverse(root: GameObject, data: Any = Unit) {
        root.children.forEach {
            callback?.onTraverse(it, data)
            traverse(it)
        }
    }

    interface Callback {
        fun onTraverse(gameObject: GameObject, data: Any)
    }
}
