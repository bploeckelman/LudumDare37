#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;
uniform float u_time;
uniform sampler2D u_texture;


void main() {
	float cutoff = ((sin(u_time/3.0) + 1.) /6.) + .66;
	vec4 sampledColor = texture2D(u_texture, v_texCoords);
	float alpha = 1.;
    if (sampledColor.a < cutoff){
    	alpha = sampledColor.a / cutoff;
    }

	gl_FragColor = vec4(sampledColor.r,sampledColor.g,sampledColor.b,alpha) * v_color;

}
