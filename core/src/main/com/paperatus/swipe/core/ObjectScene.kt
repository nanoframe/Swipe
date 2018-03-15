package com.paperatus.swipe.core

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.paperatus.swipe.Game
import ktx.collections.GdxArray

/**
 * Contains game components for updating and rendering.
 *
 * @property gameObjects contains GameObjects that will be rendered every frame
 */
abstract class ObjectScene(protected val game: Game) : Scene {
    val gameObjects = GdxArray<GameObject>()
    private val removeQueue = GdxArray<GameObject>()

    private var componentDelta = 0.0f

    override fun preUpdate(delta: Float) {
        componentDelta = delta
        updateComponents(Component.Order.PRE_UPDATE)
    }

    override fun update(delta: Float) {
        gameObjects.forEach {
            it.update(delta)
        }
        updateComponents(Component.Order.UPDATE)
    }

    override fun postUpdate(delta: Float) {
        updateComponents(Component.Order.POST_UPDATE)
        removeQueue.forEach {
            removeObject(it)
        }
        gameObjects.forEach {
            if (it.shouldRemove) removeObject(it)
        }
        removeQueue.clear()
    }

    override fun preRender(batch: SpriteBatch) = updateComponents(Component.Order.PRE_RENDER)

    /**
     * Renders every GameObject in [gameObjects].
     *
     * Calling [render] will retrieve the [RenderComponent.spriteName] of the object
     * and render the image onto the screen. The asset should be loaded in
     * [com.paperatus.swipe.Game.assets].
     *
     * The GameObject will be skipped if it does not contain a RenderComponent component
     *
     * Overriding this method allows for custom rendering.
     *
     * @param batch the SpriteBatch to render onto.
     */
    override fun render(batch: SpriteBatch) {
        updateComponents(Component.Order.RENDER)

        gameObjects.forEach { gameObject ->
            gameObject.getComponent<RenderComponent>()?.let {
                if (it.renderMode == RenderComponent.Mode.CUSTOM) return@forEach
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

                Unit
            }
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

    override fun postRender(batch: SpriteBatch) = updateComponents(Component.Order.POST_RENDER)

    /**
     * Adds a GameObject to the ObjectScene.
     *
     * @param gameObject the GameObject to add.
     */
    open fun addObject(gameObject: GameObject) = gameObjects.add(gameObject)

    /**
     * Removes the GameObject from the ObjectScene.
     *
     * @param gameObject the GameObject to remove.
     * @param identity true will use == to compare, false will use .equals().
     */
    open fun removeObject(gameObject: GameObject, identity: Boolean = true) =
            gameObjects.removeValue(gameObject, identity)

    open fun queueRemove(gameObject: GameObject) {
        removeQueue.add(gameObject)
    }

    // TODO: Implement a map for each order for faster updates
    private fun updateComponents(order: Component.Order) {
        gameObjects.forEach { gameObject ->
            gameObject.getComponents().values().forEach { component ->
                if (component.order == order) component.update(componentDelta, gameObject)
            }
        }
    }

    inline fun <T : Any> GdxArray<T>.operate(action: GdxArray<T>.() -> Unit) = action()
}
