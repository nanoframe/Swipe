package com.paperatus.swipe.core

class LinkedList<T> {

    var size = 0
        private set

    var start: Node<T>? = null
        private set

    fun add(value: T) {
        if (start == null) {
            start = Node(value)
        } else {
            var node = start!!
            while (node.next != null) node = node.next!!
            node.next = Node(value)
        }

        size++
    }

    fun remove(value: T): Boolean {
        if (start == null) return false

        if (start!!.value == value) {
            start = start!!.next
            return true
        }

        var node = start!!

        while (node.next != null) {
            val next = node.next!!

            if (next.value == value) {
                node.next = next.next
                size--
                return true
            }

            node = next
        }

        return false
    }

    fun clear() {
        start = null
    }

    class Node<T>(val value: T) {
        var next: Node<T>? = null
    }

    override fun toString(): String {
        var output = "["
        var node = start
        while (node != null) {
            output += node.value

            node = node.next
            if (node != null) {
                output += ", "
            }
        }

        output += "]"
        return output
    }
}
