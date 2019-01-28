#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;

uniform sampler2D texture;

void main() {
	 gl_FragColor = texture2D(texture, v_texCoords);
}
