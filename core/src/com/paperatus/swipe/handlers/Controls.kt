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
 * Provides touch-event based input to control the Player
 */
class PlayerTouchInput : InputComponent() {
    override fun updateInput(character: Player) {

    }

}