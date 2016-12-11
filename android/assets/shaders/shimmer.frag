#ifdef GL_ES
precision mediump float;
#endif

varying vec2 v_texCoords;
varying vec4 v_color;
uniform float u_time;
uniform sampler2D u_texture;


void main() {
	float n = -u_time/20.0;
	float x = v_texCoords.x/4.0+n/12.0;
	float y = v_texCoords.y/4.0-sin(n/10.0);
	vec4 sampledColor = texture2D(u_texture, v_texCoords);

	float plas=(y*32.0+n*1.5+sin(x*16.0+n*(0.7)-cos(y*22.0-n*1.5+sin(x*12.0+y*22.0+n*1.1-sin(x*32.0))+cos(x*32.0)+n)+cos(x*16.0-n*1.27)*2.0)*8.0-n+sin(x*2.0-n*1.0)*16.0);

	float final = abs(cos((plas+n-sin(y+n*2.0)*cos(x+n*0.2)*2.0)/28.0));
//	final = clamp(final, 0., .5);
//	gl_FragColor = vec4(u_time/10.,v_texCoords.x,v_texCoords.y,1.0) * v_color * sampledColor;
	gl_FragColor = vec4(final*.8,final*.9,final*1.,1.0) * v_color * sampledColor;

//	gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
}
