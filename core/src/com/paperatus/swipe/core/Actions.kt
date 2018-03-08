import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.paperatus.swipe.core.GameObject
import ktx.collections.GdxArray

object Actions {
    fun sequence(init: Sequence.() -> Unit): ActionGroup {
        val sequence = Sequence()
        sequence.init()
        return sequence
    }
}

interface Action {
    fun start(gameObject: GameObject)
    fun update(delta: Float)
    fun end()
    fun isFinished() : Boolean
}

// Action group

abstract class ActionGroup : Action {
    protected val actionList = GdxArray<Action>()

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

class Sequence internal constructor() : ActionGroup() {

    private lateinit var subject: GameObject

    private var index = 0
    private var activeAction: Action? = null

    override fun start(gameObject: GameObject) {
        subject = gameObject
    }

    override fun update(delta: Float) {
        if (isFinished()) return

        activeAction = activeAction ?: actionList[index].apply {
            start(subject)
        }

        val action = activeAction!!
        action.update(delta)

        if (action.isFinished()) {
            action.end()
            activeAction = null
            index++
        }
    }

    override fun end() = Unit

    override fun isFinished() = index >= actionList.size
}

// Actions that can modify the state of the GameObject

abstract class TimeAction(private val duration: Float) : Action {
    private var currentDuration = 0.0f

    override fun start(gameObject: GameObject) {
    }

    override fun update(delta: Float) {
        currentDuration += delta
        step((currentDuration / duration).coerceAtMost(1.0f))
    }

    override fun end() {
    }

    override fun isFinished() = (currentDuration / duration) >= 1.0f

    abstract fun step(alpha: Float)
}

class MoveTo internal constructor(val x: Float, val y: Float, duration: Float) : TimeAction(duration) {
    private var g: GameObject? = null
    private var startX: Float = 0.0f
    private var startY: Float = 0.0f

    override fun start(gameObject: GameObject) {
        g = gameObject
        startX = gameObject.position.x
        startY = gameObject.position.y
    }

    override fun step(alpha: Float) {
        val newX = MathUtils.lerp(startX, x, alpha)
        val newY = MathUtils.lerp(startY, y, alpha)
        g!!.position.set(newX, newY)
    }

}

// Others

class DelayAction internal constructor(duration: Float = 0.0f) : TimeAction(duration) {
    override fun step(alpha: Float) = Unit
}
