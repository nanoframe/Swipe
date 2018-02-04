package com.paperatus.swipe.scene

import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.utils.Disposable

abstract class Scene : Disposable {
    lateinit var sceneController: SceneController

    abstract fun update(delta: Float)
    open fun render(batch: SpriteBatch) {

    }

    abstract fun reset()
}
