package com.paperatus.swipe.objects

import Message
import com.paperatus.swipe.core.components.ComponentMessage
import com.paperatus.swipe.core.components.PhysicsComponent
import com.paperatus.swipe.core.scene.GameObject

// Similar to an extension function of the player GameObject
class PlayerCollisionResponse(private val player: GameObject) : PhysicsComponent.ContactListener {
    override fun onContactBegin(other: GameObject) {
        val message: ComponentMessage = when (other) {
            is Destructible -> Message.BLOCKADE_COLLISION
            else -> return
        }

        player.messageComponent(message)
    }

    override fun onContactEnd(other: GameObject) {
    }
}
