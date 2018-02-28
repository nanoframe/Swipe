package com.paperatus.swipe.map

import com.badlogic.gdx.graphics.Color

/* TODO: Simplify code by creating one method to generate all paths
 * That "one" method should have parameters that accept a base class
 * CurveData that provides the properties of the path
 */

class ProceduralMapData : GameMap() {
    override var pathColor = Color(
            204.0f / 255.0f,
            230.0f / 255.0f,
            228.0f / 255.0f,
            1.0f)

}
