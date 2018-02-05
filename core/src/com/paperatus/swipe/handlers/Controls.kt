package com.paperatus.swipe.handlers

import com.paperatus.swipe.objects.Player

abstract class InputComponent {
    abstract fun updateInput(character: Player)
}

class PlayerInput : InputComponent() {
    override fun updateInput(character: Player) {

    }

}