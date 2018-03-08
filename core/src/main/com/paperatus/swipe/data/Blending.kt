package com.paperatus.swipe.data

import com.badlogic.gdx.graphics.GL20
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.paperatus.swipe.core.RenderParams

class ExplosionParticleBlending : RenderParams {
    private var src = 0
    private var srcAlpha = 0
    private var dst = 0
    private var dstAlpha = 0

    override fun applyParams(batch: SpriteBatch) {
        src = batch.blendSrcFunc
        srcAlpha = batch.blendSrcFuncAlpha
        dst = batch.blendDstFunc
        dstAlpha = batch.blendDstFuncAlpha

        batch.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE)
    }

    override fun resetParams(batch: SpriteBatch) =
        batch.setBlendFunctionSeparate(src, dst, srcAlpha, dstAlpha)
}
