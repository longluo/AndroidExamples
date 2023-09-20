package ro.brite.android.nehe23;

class GlMatrix {
	
	private float[][] m;
	
	public GlMatrix() {
		// create matrix
		m = new float[4][4];
		// make identity
		for (int i = 0; i < 4; i++) m[i][i] = 1;
	}
	
	public void identity() {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				m[i][j] = (i != j) ? 0 : 1;
	}
	
	public void assign(GlMatrix matrix) {
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++)
				m[i][j] = matrix.m[i][j];
	}
	
	public void translate(float dx, float dy, float dz) {
		GlMatrix mTr = new GlMatrix();
		
		mTr.m[0][3] += dx;
		mTr.m[1][3] += dy;
		mTr.m[2][3] += dz;
		
		assign(multiply(this, mTr));
	}
	
	public void rotate(float angle, float x, float y, float z) {
		angle = (float)Math.toRadians(angle);
		
		float c = (float)Math.cos(angle);
		float s = (float)Math.sin(angle);
		float _c = 1 - c;
		
		GlVertex v = new GlVertex(x, y, z);
		v.normalize();
		x = v.v[0];
		y = v.v[1];
		z = v.v[2];
		
		GlMatrix mRot = new GlMatrix();
		
		mRot.m[0][0] = x * x * _c + c;
		mRot.m[0][1] = x * y * _c - z * s;
		mRot.m[0][2] = x * z * _c + y * s;
		mRot.m[0][3] = 0;
		
		mRot.m[1][0] = y * x * _c + z * s;
		mRot.m[1][1] = y * y * _c + c;
		mRot.m[1][2] = y * z * _c - x * s;
		mRot.m[1][3] = 0;
		
		mRot.m[2][0] = z * x * _c - y * s;
		mRot.m[2][1] = z * y * _c + x * s;
		mRot.m[2][2] = z * z * _c + c;
		mRot.m[2][3] = 0;
		
		mRot.m[3][0] = 0;
		mRot.m[3][1] = 0;
		mRot.m[3][2] = 0;
		mRot.m[3][3] = 1;
		
		assign(multiply(this, mRot));
	}
	
	public static GlMatrix multiply(GlMatrix a, GlMatrix b) {
		GlMatrix r = new GlMatrix();
		for (int i = 0; i < 4; i++)
			for (int j = 0; j < 4; j++) {
				float val = 0;
				for (int k = 0; k < 4; k++) {
					val += a.m[i][k] * b.m[k][j];
				}
				r.m[i][j] = val;
			}
		return r;
	}
	
	public void transform(GlVertex vertex) {
		GlVertex vertexTr = new GlVertex();
		for (int i = 0; i < 4; i++) {
			vertexTr.v[i] = 0;
			for (int j = 0; j < 4; j++) {
				vertexTr.v[i] += m[i][j] * vertex.v[j];
			}
		}
		vertex.assign(vertexTr);
	}
}
