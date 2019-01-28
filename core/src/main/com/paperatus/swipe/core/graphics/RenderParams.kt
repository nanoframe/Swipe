package com.paperatus.swipe.core.graphics

import com.badlogic.gdx.graphics.g2d.SpriteBatch

interface RenderParams {
    fun applyParams(batch: SpriteBatch)
    fun resetParams(batch: SpriteBatch)
}
