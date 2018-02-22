import com.badlogic.gdx.math.MathUtils

object Path {

    // TODO: Add probability

    private val types: Array<Type>

    init {
        types = Type.values()
    }

    enum class Type {
        CurveLeft, CurveRight,
        //BackLeft, BackRight,
        //HorizontalLeft, HorizontalRight,
        Up;
    }

    fun random() = types[MathUtils.random(0, types.lastIndex)]
}
