import com.badlogic.gdx.math.Interpolation
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.actions.ActionGroup
import com.paperatus.swipe.core.actions.FadeAction
import com.paperatus.swipe.core.actions.MoveTo
import com.paperatus.swipe.core.actions.ScaleTo
import com.paperatus.swipe.core.actions.Spawn

object Actions {
    fun sequence(init: com.paperatus.swipe.core.actions.Sequence.() -> Unit): ActionGroup {
        val sequence = com.paperatus.swipe.core.actions.Sequence()
        sequence.init()
        return sequence
    }

    fun spawn(init: Spawn.() -> Unit): ActionGroup {
        val spawn = Spawn()
        spawn.init()
        return spawn
    }

    fun moveTo(x: Float,
               y: Float,
               duration: Float,
               interpolation: Interpolation = Interpolation.linear) =
            MoveTo(x, y, duration, interpolation)

    fun moveTo(position: Vector2,
               duration: Float,
               interpolation: Interpolation = Interpolation.linear) =
            moveTo(position.x, position.y, duration, interpolation)

    fun scaleTo(x: Float,
                y: Float,
                duration: Float,
                interpolation: Interpolation = Interpolation.linear) =
            ScaleTo(x, y, duration, interpolation)

    fun scaleTo(scl: Float,
                duration: Float,
                interpolation: Interpolation = Interpolation.linear) =
            scaleTo(scl, scl, duration, interpolation)

    fun fade(duration: Float,
             interpolation: Interpolation = Interpolation.linear) =
            FadeAction(duration, interpolation)
}
