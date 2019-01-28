package com.paperatus.swipe.core.actions

import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.MathUtils
import com.paperatus.swipe.core.InvalidActionException
import com.paperatus.swipe.core.components.RenderComponent

class FadeAction internal constructor(duration: Float,
                                      override var interpolation: Interpolation) : TimeAction(duration) {
    private var startAlpha: Float = 0.0f
    private var renderComponent: RenderComponent? = null

    override fun start() {
        renderComponent = gameObject.getComponent() ?: throw InvalidActionException("The given component does not have a RenderComponent!")
        startAlpha = renderComponent!!.alpha
    }

    override fun step(alpha: Float) {
        renderComponent!!.alpha = MathUtils.lerp(startAlpha, 0.0f, alpha)
    }

    override fun end() {
        renderComponent!!.alpha = 0.0f
        renderComponent = null
    }
}
