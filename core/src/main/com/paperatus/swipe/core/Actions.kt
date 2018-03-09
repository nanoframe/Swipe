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

    fun moveTo(x: Float, y: Float, duration: Float) = MoveTo(x, y, duration)

    fun moveTo(position: Vector2, duration: Float) =
            moveTo(position.x, position.y, duration)

    fun scaleTo(x: Float, y: Float, duration: Float) = ScaleTo(x, y, duration)

    fun scaleTo(scl: Float, duration: Float) = scaleTo(scl, scl, duration)
}

interface Action {
    fun start(gameObject: GameObject)
    fun update(delta: Float)
    fun end()
    fun isFinished() : Boolean
}

abstract class ActionGroup : Action {
    protected val actionList = GdxArray<Action>()

    // Action groups

    fun sequence(actions: Sequence.() -> Unit) {
        val s = Sequence()
        s.actions()
        add(s)
    }

    // State actions

    fun moveTo(x: Float, y: Float, duration: Float) {
        add(MoveTo(x, y, duration))
    }

    fun moveTo(position: Vector2, duration: Float) =
            moveTo(position.x, position.y, duration)

    fun scaleTo(x: Float, y: Float, duration: Float) {
        add(ScaleTo(x, y, duration))
    }

    fun scaleTo(scl: Float, duration: Float) {
        scaleTo(scl, scl, duration)
    }

    // Others

    fun execute(func: (GameObject) -> Unit) {
        add(ExecuteAction(func))
    }

    fun delay(duration: Float) {
        add(DelayAction(duration))
    }

    private fun add(action: Action) = actionList.add(action)
}

abstract class TimeAction(internal val duration: Float) : Action {
    internal var currentDuration = 0.0f
        private set

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

// Action groups

class Sequence internal constructor() : ActionGroup() {

    private lateinit var subject: GameObject

    private var index = 0
    private var activeAction: Action? = null

    override fun start(gameObject: GameObject) {
        subject = gameObject
    }

    override fun update(delta: Float) {
        // Calculate the amount of time the action has went over
        // if the action has been completed
        val timeOffset = activeAction?.let {
            if (!it.isFinished()) return@let 0.0f

            it.end()
            activeAction = null
            index++

            return@let if (it is TimeAction) it.currentDuration - it.duration
            else 0.0f
        } ?: 0.0f

        if (isFinished()) return

        activeAction = activeAction ?: actionList[index].apply {
            start(subject)
        }

        val action = activeAction!!
        action.update(delta + timeOffset)
    }

    override fun end() = Unit

    override fun isFinished() = index >= actionList.size
}

// Actions that can modify the state of the GameObject

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

    override fun end() {
        g!!.position.set(x, y)
    }
}

class ScaleTo internal constructor(val x: Float,
                                   val y: Float,
                                   duration: Float) : TimeAction(duration) {
    private var g: GameObject? = null
    private var startX = 0.0f
    private var startY = 0.0f

    override fun start(gameObject: GameObject) {
        g = gameObject
        startX = gameObject.size.width
        startY = gameObject.size.height
    }

    override fun step(alpha: Float) {
        val newX = MathUtils.lerp(startX, startX * x, alpha)
        val newY = MathUtils.lerp(startY, startY * y, alpha)
        g!!.size.set(newX, newY)
    }

    override fun end() {
        g!!.size.set(startX * x, startY * y)
    }
}

// Others

class ExecuteAction internal constructor(val func: (GameObject) -> Unit) : Action {
    override fun start(gameObject: GameObject) {
        func(gameObject)
    }

    override fun update(delta: Float) = Unit

    override fun end() = Unit

    override fun isFinished() = true
}

class DelayAction internal constructor(duration: Float = 0.0f) : TimeAction(duration) {
    override fun step(alpha: Float) = Unit
}
