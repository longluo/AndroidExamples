package ro.brite.android.nehe18;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


class GlCube extends GlObject {
	
	private final static float[][] cubeVertexCoordsTemplate = new float[][] {
		new float[] { // top
			 1, 1,-1,
			-1, 1,-1,
			-1, 1, 1,
			 1, 1, 1
		},
		new float[] { // bottom
			 1,-1, 1,
			-1,-1, 1,
			-1,-1,-1,
			 1,-1,-1
		},
		new float[] { // front
			 1, 1, 1,
			-1, 1, 1,
			-1,-1, 1,
			 1,-1, 1
		},
		new float[] { // back
			 1,-1,-1,
			-1,-1,-1,
			-1, 1,-1,
			 1, 1,-1
		},
		new float[] { // left
			-1, 1, 1,
			-1, 1,-1,
			-1,-1,-1,
			-1,-1, 1
		},
		new float[] { // right
			 1, 1,-1,
			 1, 1, 1,
			 1,-1, 1,
			 1,-1,-1
		},
	};

	private final static float[][] cubeNormalCoords = new float[][] {
		new float[] { // top
			 0, 1, 0,
			 0, 1, 0,
			 0, 1, 0,
			 0, 1, 0
		},
		new float[] { // bottom
			 0,-1, 0,
			 0,-1, 0,
			 0,-1, 0,
			 0,-1, 0
		},
		new float[] { // front
			 0, 0, 1,
			 0, 0, 1,
			 0, 0, 1,
			 0, 0, 1
		},
		new float[] { // back
			 0, 0,-1,
			 0, 0,-1,
			 0, 0,-1,
			 0, 0,-1
		},
		new float[] { // left
			-1, 0, 0,
			-1, 0, 0,
			-1, 0, 0,
			-1, 0, 0
		},
		new float[] { // right
			 1, 0, 0,
			 1, 0, 0,
			 1, 0, 0,
			 1, 0, 0
		},
	};
	
	private final static float[][] cubeTextureCoords = new float[][] {
		new float[] { // top
			1, 0,
			1, 1,
			0, 1,
			0, 0
		},
		new float[] { // bottom
			0, 0,
			1, 0,
			1, 1,
			0, 1
		},
		new float[] { // front
			1, 1,
			0, 1,
			0, 0,
			1, 0
		},
		new float[] { // back
			0, 1,
			0, 0,
			1, 0,
			1, 1
		},
		new float[] { // left
			1, 1,
			0, 1,
			0, 0,
			1, 0
		},
		new float[] { // right
			0, 1,
			0, 0,
			1, 0,
			1, 1
		},
	};

	private final static FloatBuffer[] cubeNormalBfr;
	private final static FloatBuffer[] cubeTextureBfr;
	
	static {
		cubeNormalBfr = new FloatBuffer[6];
		cubeTextureBfr = new FloatBuffer[6];
		for (int i = 0; i < 6; i++)
		{
			cubeNormalBfr[i] = FloatBuffer.wrap(cubeNormalCoords[i]);
			cubeTextureBfr[i] = FloatBuffer.wrap(cubeTextureCoords[i]);
		}
	}
	
	private float size;
	
	private boolean useNormals;
	private boolean useTexCoords;
	
	private FloatBuffer[] cubeVertexBfr;
	
	public GlCube(float size, boolean useNormals, boolean useTexCoords) {
		this.size = size;
		this.useNormals = useNormals;
		this.useTexCoords = useTexCoords;
		generateData();
	}
	
	private void generateData() {
		cubeVertexBfr = new FloatBuffer[cubeVertexCoordsTemplate.length];
		
		float[] vertices;
		for (int i = 0; i < cubeVertexCoordsTemplate.length; i++)
		{
			vertices = new float[cubeVertexCoordsTemplate[i].length];
			for (int j = 0; j < cubeVertexCoordsTemplate[i].length; j++)
			{
				vertices[j] = cubeVertexCoordsTemplate[i][j] * size;
			}
			cubeVertexBfr[i] = FloatBuffer.wrap(vertices);
		}
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
		
		for (int i = 0; i < 6; i++) { // draw each face
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeVertexBfr[i]);
			if (useNormals) {
				gl.glNormalPointer(GL10.GL_FLOAT, 0, cubeNormalBfr[i]);
			}
			if (useTexCoords) {
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, cubeTextureBfr[i]);
			}
			gl.glDrawArrays(GL10.GL_TRIANGLE_FAN, 0, 4);
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if (useNormals) {
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
		if (useTexCoords) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
	}
}
