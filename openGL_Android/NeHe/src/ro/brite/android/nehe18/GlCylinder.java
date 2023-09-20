package ro.brite.android.nehe18;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;


class GlCylinder extends GlObject {
	
	float base;
	float top;
	float height;
	int slices;
	int stacks;
	
	private boolean normals;
	private boolean texCoords;
	
	private FloatBuffer[] slicesBuffers;
	private FloatBuffer[] normalsBuffers;
	private FloatBuffer[] texCoordsBuffers;
	
	public GlCylinder(float base, float top, float height, int slices, int stacks, boolean genNormals, boolean genTexCoords) {
		this.base = base;
		this.top = top;
		this.height = height;
		this.slices = slices;
		this.stacks = stacks;
		this.normals = genNormals;
		this.texCoords = genTexCoords;
		generateData();
	}
	
	private void generateData() {
		
		slicesBuffers = new FloatBuffer[slices];
		if (normals) {
			normalsBuffers = new FloatBuffer[slices];
		}
		if (texCoords) {
			texCoordsBuffers = new FloatBuffer[slices];
		}
		
		for (int i = 0; i < slices; i++) {
			
			float[] vertexCoords = new float[3 * 2 * (stacks + 1)];
			float[] normalCoords = new float[3 * 2 * (stacks + 1)];
			float[] textureCoords = new float[2 * 2 * (stacks + 1)];

			double alpha0 = (i + 0) * (2 * Math.PI) / slices;
			double alpha1 = (i + 1) * (2 * Math.PI) / slices;

			float cosAlpha0 = (float) Math.cos(alpha0);
			float sinAlpha0 = (float) Math.sin(alpha0);
			float cosAlpha1 = (float) Math.cos(alpha1);
			float sinAlpha1 = (float) Math.sin(alpha1);

			for (int j = 0; j <= stacks; j++) {

				float z = height * (0.5f - ((float)j) / stacks);
				float r = top + (base - top) * j / stacks;
				
				Utils.setXYZ(vertexCoords, 3 * 2 * j,
						r * cosAlpha1, r * sinAlpha1, z);

				Utils.setXYZ(vertexCoords, 3 * 2 * j + 3,
						r * cosAlpha0, r * sinAlpha0, z);
				
				if (normals) {
					Utils.setXYZn(normalCoords, 3 * 2 * j,
							height * cosAlpha1,
							height * sinAlpha1,
							base - top);
					Utils.setXYZn(normalCoords, 3 * 2 * j + 3,
							height * cosAlpha0,
							height * sinAlpha0,
							base - top);
				}

				if (texCoords) {
					textureCoords[2 * 2 * j + 0] = ((float) (i + 1)) / slices;
					textureCoords[2 * 2 * j + 1] = ((float) (j + 0)) / stacks;
					
					textureCoords[2 * 2 * j + 2] = ((float) (i + 0)) / slices;
					textureCoords[2 * 2 * j + 3] = ((float) (j + 0)) / stacks;
				}
			}
			
			slicesBuffers[i] = FloatBuffer.wrap(vertexCoords);
			
			if (normals) {
				normalsBuffers[i] = FloatBuffer.wrap(normalCoords);
			}
			
			if (texCoords) {
				texCoordsBuffers[i] = FloatBuffer.wrap(textureCoords);
			}
		}
	}

	@Override
	public void draw(GL10 gl) {
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
		if (normals) {
			gl.glEnableClientState(GL10.GL_NORMAL_ARRAY);
		}
		if (texCoords) {
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
		
		for (int i = 0; i < slices; i++) // draw each slice
		{
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, slicesBuffers[i]);
			if (normals) {
				gl.glNormalPointer(GL10.GL_FLOAT, 0, normalsBuffers[i]);
			}
			if (texCoords) {
				gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texCoordsBuffers[i]);
			}
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 2 * (stacks + 1));
		}
		
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		if (normals) {
			gl.glDisableClientState(GL10.GL_NORMAL_ARRAY);
		}
		if (texCoords) {
			gl.glDisableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
		}
	}
}
