attribute vec2 a_position;

uniform mat4 u_projTrans;

varying vec4 v_color;

void main() {
    v_color = vec4(1.0, 0.0, 1.0, 1.0);
    gl_Position = u_projTrans * vec4(a_position, 0.0, 1.0);
}
