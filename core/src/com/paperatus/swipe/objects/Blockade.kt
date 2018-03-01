package com.paperatus.swipe.objects

import com.paperatus.swipe.components.BlockadePhysicsComponent
import com.paperatus.swipe.core.GameObject
import com.paperatus.swipe.core.PhysicsComponent

class Blockade : GameObject() {
    var health = 100.0f
    var maxHealth = 100.0f

    init {
        attachComponent<PhysicsComponent>(BlockadePhysicsComponent())
    }
}
