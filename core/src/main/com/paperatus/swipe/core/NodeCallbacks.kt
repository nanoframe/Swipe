package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.paperatus.swipe.Game

class NodeUpdater : NodeTraversal.Callback {
    override fun onTraverse(gameObject: GameObject, data: Any) {
        val delta = data as Float

        // TODO: Add a custom component update - update components in specified order
        // Ex. Update all inputs first, then the GO itself, then the physics
        gameObject.getComponents().forEach {
            it.value.update(delta, gameObject)
        }
        gameObject.update(delta)
    }
}

class NodeRenderer(val game: Game) : NodeTraversal.Callback {

    var batch: SpriteBatch? = null

    override fun onTraverse(gameObject: GameObject, data: Any) {
        val batch = data as SpriteBatch

        gameObject.getComponent<RenderComponent>()?.let {
            if (it.renderMode == RenderComponent.Mode.CUSTOM) return
            val spriteName = it.sprite
                    ?: throw RenderException("Sprite name is null")

            if (spriteName == "") throw RenderException("Sprite name cannot be empty")
            if (!game.assets.isLoaded(spriteName)) throw AssetNotLoadedException(
                    "Sprite $spriteName isn't loaded!"
            )

            // Apply custom rendering params if requested
            it.customParams.forEach { it.applyParams(batch) }
            renderGameObject(batch, gameObject, spriteName)
            it.customParams.forEach { it.resetParams(batch) }
        }
    }

    private fun renderGameObject(batch: SpriteBatch,
                                 gameObject: GameObject,
                                 spriteName: String) {
        val image: Any = game.assets[spriteName]

        gameObject.apply {
            when (image) {
                is Texture -> batch.draw(image,
                        transform.position.x - transform.size.width * transform.anchor.x,
                        transform.position.y - transform.size.height * transform.anchor.y,
                        transform.anchor.x * transform.size.width,
                        transform.anchor.y * transform.size.height,
                        transform.size.width, transform.size.height,
                        1.0f, 1.0f,
                        transform.rotation,
                        0, 0, image.width, image.height,
                        false, false
                )
                is TextureRegion -> batch.draw(image,
                        transform.position.x - transform.size.width * transform.anchor.x,
                        transform.position.y - transform.size.height * transform.anchor.y,
                        transform.anchor.x * transform.size.width,
                        transform.anchor.y * transform.size.height,
                        transform.size.width, transform.size.height,
                        1.0f, 1.0f,
                        transform.rotation
                )
                else -> ktx.log.error(RuntimeException()) {
                    "Asset '$spriteName' is of type ${image::class}!"
                }
            }
        }
    }
}
