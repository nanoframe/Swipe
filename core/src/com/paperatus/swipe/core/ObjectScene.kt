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
    private val gameObjects = GdxArray<GameObject>()

    override fun preUpdate(delta: Float) = updateComponents(Component.Order.PRE_UPDATE)

    override fun update(delta: Float) = updateComponents(Component.Order.UPDATE)

    override fun postUpdate(delta: Float) = updateComponents(Component.Order.POST_UPDATE)

    override fun preRender(batch: SpriteBatch) = updateComponents(Component.Order.PRE_RENDER)

    /**
     * Renders every GameObject in [gameObjects].
     *
     * Calling [render] will retrieve the [GameObject.spriteName] of the object
     * and render the image onto the screen. The asset should be loaded in
     * [com.paperatus.swipe.Game.assets].
     *
     * Overriding this method allows for custom rendering.
     *
     * @param batch the SpriteBatch to render onto.
     */
    override fun render(batch: SpriteBatch) {
        updateComponents(Component.Order.RENDER)

        gameObjects.forEach {
            assert(it.spriteName != "") {
                "The sprite name cannot be empty!"
            }
            assert(game.assets.isLoaded(it.spriteName)) {
                "Asset \"${it.spriteName}\" doesn't exist!"
            }

            val image: Any = game.assets[it.spriteName]

            when (image) {
                is Texture -> batch.draw(image,
                        it.position.x, it.position.y,
                        it.anchor.x, it.anchor.y,
                        it.bounds.width, it.bounds.height,
                        1.0f, 1.0f,
                        it.rotation,
                        0, 0, image.width, image.height,
                        false, false
                )
                is TextureRegion -> batch.draw(image,
                        it.position.x, it.position.y,
                        0.5f, 0.5f,
                        it.bounds.width, it.bounds.height,
                        1.0f, 1.0f,
                        it.rotation
                )
                else -> ktx.log.error(RuntimeException()) {
                    "Asset '$it.spriteName' is of type ${image::class}!"
                }
            }
        }
    }

    override fun postRender(batch: SpriteBatch) = updateComponents(Component.Order.POST_RENDER)

    open fun addObject(gameObject: GameObject) = gameObjects.add(gameObject)

    open fun removeObject(gameObject: GameObject, identity: Boolean = true) =
            gameObjects.removeValue(gameObject, identity)

    // TODO: Implement a map for each order for faster updates
    private fun updateComponents(order: Component.Order) {
        gameObjects.forEach {gameObject ->
            gameObject.components.values().forEach {component ->
                if (component.order == order) component.update(gameObject)
            }
        }
    }

    inline fun<T : Any> GdxArray<T>.operate(action: GdxArray<T>.() -> Unit) = action()
}
