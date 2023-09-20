package ro.brite.android.nehe23;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


class GlPlane extends GlObject {
	
	private final static float[] planeVertexCoordsTemplate = new float[] {
		 1, 1, 0,
		-1, 1, 0,
		-1,-1, 0,
		 1,-1, 0
	};

	private final static float[] planeNormalCoords = new float[] {
		 0, 0, 1,
		 0, 0, 1,
		 0, 0, 1,
		 0, 0, 1
	};
	
	private final static float[] planeTextureCoords = new float[] {
		1, 1,
		0, 1,
		0, 0,
		1, 0
	};

	private final static FloatBuffer planeNormalBfr;
	private final static FloatBuffer planeTextureBfr;
	
	static {
		planeNormalBfr = FloatBuffer.wrap(planeNormalCoords);
		planeTextureBfr = FloatBuffer.wrap(planeTextureCoords);
	}
	
	private float width;
	private float height;
	
	private boolean useNormals;
	private boolean useTexCoords;
	
	private FloatBuffer planeVertexBfr;
	
	public GlPlane(float width, float height, boolean useNormals, boolean useTexCoords) {
		this.width = width;
		this.height = height;
		this.useNormals = useNormals;
		this.useTexCoords = useTexCoords;
		generateData();
	}
	
	private void generateData() {
		float[] vertices = new float[planeVertexCoordsTemplate.length];
		for (int i = 0; i < planeVertexCoordsTemplate.length / 3; i++)
		{
			vertices[3 * i] = planeVertexCoordsTemplate[3 * i] * width / 2;
			vertices[3 * i + 1] = planeVertexCoordsTemplate[3 * i + 1] * height / 2;
			vertices[3 * i + 2] = planeVertexCoordsTemplate[3 * i + 2];
		}
		planeVertexBfr = FloatBuffer.wrap(vertices);
	}

	@Override
	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		if (useNormals) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		}
		if (useTexCoords) {
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		
		// draw plane
		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, planeVertexBfr);
		if (useNormals) {
			gl.glNormalPointer(GL10.GL_FLOAT, 0, planeNormalBfr);
		}
		if (useTexCoords) {
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, planeTextureBfr);
		}
		gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if (useNormals) {
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
		if (useTexCoords) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
	}

	@Override
	public void calculateReflectionTexCoords(GlVertex vEye, GlMatrix mInvRot) {
		// don't do reflections
	}
}
