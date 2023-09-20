package ro.brite.android.nehe23;

class GlVertex {
	public float[] v;
	
	public GlVertex() {
		v = new float[4];
		v[3] = 1;
	}
	
	public GlVertex(float x, float y, float z) {
		v = new float[4];
		v[0] = x;
		v[1] = y;
		v[2] = z;
		v[3] = 1;
	}
	
	public GlVertex(GlVertex vertex) {
		v = new float[4];
		assign(vertex);
	}
	
	public void assign(GlVertex vertex) {
		for (int i = 0; i < 4; i++) {
			v[i] = vertex.v[i];
		}
	}

	public void add(GlVertex vertex) {
		for (int i = 0; i < 3; i++) {
			v[i] += vertex.v[i];
		}
	}

	public void subtract(GlVertex vertex) {
		for (int i = 0; i < 3; i++) {
			v[i] -= vertex.v[i];
		}
	}
	
	public void normalize() {
		v[3] = (float)Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
		if (v[3] != 0) {
			v[0] /= v[3];
			v[1] /= v[3];
			v[2] /= v[3];
			v[3] = 1;
		}
	}
	
	public void scale(float factor) {
		for (int i = 0; i < 3; i++) {
			v[i] *= factor;
		}
	}
	
	public static float dotProduct(GlVertex a, GlVertex b) {
		float s = 0;
		for (int i = 0; i < 3; i++) {
			s += a.v[i] * b.v[i];
		}
		return s;
	}
}
