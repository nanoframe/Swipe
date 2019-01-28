attribute vec2 a_position;
attribute vec2 a_texCoords;

uniform mat4 u_projTrans;

varying vec2 v_texCoords;

void main() {
    v_texCoords = a_texCoords;
	gl_Position = u_projTrans * vec4(a_position, 0.0, 1.0);
}
