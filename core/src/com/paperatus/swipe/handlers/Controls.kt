package com.paperatus.swipe.handlers

import com.paperatus.swipe.objects.Player

/**
 * The Input component of the Player class.
 */
abstract class InputComponent {
    /**
     * Update the properties of the [character].
     */
    abstract fun updateInput(character: Player)
}

/**
 * Provides a way for users to control the Player.
 */
class PlayerInput : InputComponent() {
    override fun updateInput(character: Player) {

    }

}