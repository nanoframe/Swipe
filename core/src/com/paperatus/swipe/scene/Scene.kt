package com.paperatus.swipe.scene

import com.badlogic.gdx.graphics.Texture
import com.badlogic.gdx.graphics.g2d.SpriteBatch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Disposable
import com.paperatus.swipe.Game
import com.paperatus.swipe.handlers.Component
import com.paperatus.swipe.objects.GameObject
import ktx.collections.GdxArray

/**
 * Contains game components for updating and rendering.
 *
 * @property gameObjects contains GameObjects that will be rendered every frame
 */
abstract class Scene(protected val game: Game) : Disposable {
    private val gameObjects = GdxArray<GameObject>()

    open fun create() = Unit

    open fun preUpdate(delta: Float) = updateComponents(Component.Order.PRE_UPDATE)

    /**
     * Updates the Scene
     *
     * @param [delta] the time since the last frame; capped at [SceneController.maxDeltaTime]
     */
    open fun update(delta: Float) = updateComponents(Component.Order.UPDATE)

    open fun postUpdate(delta: Float) = updateComponents(Component.Order.POST_UPDATE)

    open fun preRender(batch: SpriteBatch) = updateComponents(Component.Order.PRE_RENDER)

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
    open fun render(batch: SpriteBatch) {
        updateComponents(Component.Order.RENDER)

        gameObjects.forEach {
            assert(it.spriteName != "")
            assert(game.assets.isLoaded(it.spriteName))

            val image: Any = game.assets[it.spriteName]

            when (image) {
                is Texture -> batch.draw(image,
                        it.position.x, it.position.y,
                        0.5f, 0.5f,
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

    open fun postRender(batch: SpriteBatch) = updateComponents(Component.Order.POST_RENDER)

    fun addObject(gameObject: GameObject) = gameObjects.add(gameObject)

    fun removeObject(gameObject: GameObject, identity: Boolean = true) =
            gameObjects.removeValue(gameObject, identity)

    // TODO: Implement a map for each order for faster updates
    private fun updateComponents(order: Component.Order) {
        gameObjects.forEach {gameObject ->
            gameObject.components.values().forEach {component ->
                if (component.order == order) component.update(gameObject)
            }
        }
    }


    /**
     * Resets the Scene before display.
     *
     * Called every time [SceneController.setScene] is called to reset the Scene
     * to start a new gameplay.
     */
    abstract fun reset()

    inline fun<T : Any> GdxArray<T>.operate(action: GdxArray<T>.() -> Unit) = action()
}
