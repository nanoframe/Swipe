package com.paperatus.swipe.core.components

import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.paperatus.swipe.core.graphics.RenderParams
import com.paperatus.swipe.core.scene.GameObject
import ktx.collections.GdxArray

open class RenderComponent(val renderMode: Mode = Mode.SPRITE,
                           open var sprite: String? = null) : Component {
    enum class Mode {
        SPRITE, CUSTOM
    }

    var alpha = 1.0f
    val customParams = GdxArray<RenderParams>()

    private val internalParams = object : RenderParams {
        private val originalColor = Color()

        override fun applyParams(batch: SpriteBatch) {
            originalColor.set(batch.color)
            batch.setColor(
                    originalColor.r,
                    originalColor.g,
                    originalColor.b,
                    alpha)
        }

        override fun resetParams(batch: SpriteBatch) {
            batch.color = originalColor
        }
    }

    init {
        customParams.add(internalParams)
    }

    override fun update(delta: Float, gameObject: GameObject) = Unit

    override fun receive(what: ComponentMessage, payload: Any?) = Unit

    fun addRenderParams(params: RenderParams) = customParams.add(params)

    fun removeRenderParams(params: RenderParams, identity: Boolean = true) =
            customParams.removeValue(params, identity)
}

class AnimationRenderComponent(val delay: Float,
                               val repeat: Boolean = true,
                               private vararg var frames: String) : RenderComponent(Mode.SPRITE) {

    var onFinish: (() -> Unit)? = null

    var isRunning = false
        private set

    override var sprite: String? = frames[0]
    private var timeElapsed = 0.0f
    private var index = 0

    override fun update(delta: Float, gameObject: GameObject) {
        if (!isRunning) return

        timeElapsed += delta
        if (timeElapsed >= delay) {
            if (!repeat && index + 1 >= frames.size) {
                onFinish?.invoke()
                isRunning = false
            } else {
                index = (index + 1) % frames.size
            }
            sprite = frames[index]
        }
    }

    fun start() {
        index = 0
        isRunning = true
    }

    fun stop() {
        isRunning = false
    }
}
