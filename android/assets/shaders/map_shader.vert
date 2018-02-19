attribute vec2 a_position;

uniform mat4 u_projTrans;
uniform vec3 u_pathColor;

varying vec4 v_color;

void main() {
    v_color = vec4(u_pathColor, 1.0);
    gl_Position = u_projTrans * vec4(a_position, 0.0, 1.0);
}
