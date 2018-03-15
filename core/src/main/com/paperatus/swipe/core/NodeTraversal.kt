package com.paperatus.swipe.core

class NodeTraversal {

    var callback: Callback? = null

    fun traverse(root: GameObject) {
        root.children.forEach {
            callback?.onTraverse(it)
            traverse(it)
        }
    }

    interface Callback {
        fun onTraverse(gameObject: GameObject)
    }
}
