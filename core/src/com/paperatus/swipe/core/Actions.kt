import com.badlogic.gdx.math.Vector2
import ktx.collections.GdxArray

object Actions {
    fun sequence(init: Sequence.() -> Unit): ActionGroup {
        val sequence = Sequence()
        sequence.init()
        return sequence
    }
}

interface Action {
    fun step(delta: Float)
}

// Action group

abstract class ActionGroup : Action {
    private val actionList = GdxArray<Action>()

    override fun step(delta: Float) {

    }

    fun delay(duration: Float) {
        add(DelayAction(duration))
    }

    fun moveTo(position: Vector2, duration: Float) {
        moveTo(position.x, position.y, duration)
    }

    fun moveTo(x: Float, y: Float, duration: Float) {
        add(MoveTo(x, y, duration))
    }

    fun sequence(actions: Sequence.() -> Unit) {
        val s = Sequence()
        s.actions()
        add(s)
    }

    private fun add(action: Action) = actionList.add(action)
}

class Sequence : ActionGroup() {

}

// Actions that can modify the state of the GameObject

abstract class TimeAction(val duration: Float) : Action {
    override fun step(delta: Float) = Unit
}

class MoveTo(val x: Float, val y: Float, duration: Float) : TimeAction(duration) {

}

// Others

class DelayAction(duration: Float = 0.0f) : TimeAction(duration)

fun temp() {
    val a = Actions.sequence {
        delay(0.5f)
        moveTo(-1.0f, -2.0f, 1.0f)
        sequence {
            moveTo(0.0f, 1.0f, 0.0f)
            moveTo(2.0f, 1.0f, 9.0f)
        }
    }
}
