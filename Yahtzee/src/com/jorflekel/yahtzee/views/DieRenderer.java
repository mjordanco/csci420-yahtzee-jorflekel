package com.jorflekel.yahtzee.views;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.Random;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.util.Log;

import com.jorflekel.yahtzee.R;


public class DieRenderer implements Renderer{
	// In case we ever need a context
	private Context hostContext;
	
	// Attribute GLpointers
	private int shaderPositionA;
	private int shaderNormalA;
	private int shaderTexCoordA;

	// Uniform GLpointers
	private int shaderColorU;
	private int shaderLightPosU;
	private int shaderAmbientU;
	private int shaderDieTexSamplerU;
	private int shaderModelViewProjMatrixU;
	private int shaderRotMatrixU;

	// Pointer to the entire GL program
	private int shaderProgramHandle;

	// Pointers to the GLbuffers that hold the vertex and normal coords
	private int vertexCoordGlBuffer;
	private int vertexNormGlBuffer;	
	private int vertexTexCoordGLBuffer;

	// Pointer to the GLTexture with our texture data
	private int shaderDieGLTexture;

	private final String vertexShaderCode =
					"precision mediump float;" +
					"uniform mat4 uModViewProjMatrix;" +
					"uniform mat4 uRotMatrix;" +
					"uniform vec3 lightPos;" +
					"attribute vec4 position;" +
					"attribute vec3 normal;" +
					"attribute vec2 texCoord;" +
					"varying float bright;" +
					"varying vec2 vTexCoord;" +
					"void main() {" +
					"  gl_Position = uModViewProjMatrix * position;" +
					"  bright = dot(normalize(lightPos), normalize(vec3(uRotMatrix * vec4(normal, 1.0))));" +
					"  bright = (bright < 0.0 ? 0.0 : bright);" +
					"  vTexCoord = texCoord;" +
					"}";

	private final String fragmentShaderCode =
						"precision mediump float;" +
						"uniform vec4 color;" +
						"uniform sampler2D tex;" +
						"uniform float ambient;" +
						"varying float bright;" +
						"varying vec2 vTexCoord;" +
						"void main() {" +
						"  float endBright = bright + ambient;" +
						"  endBright = (endBright > 1.0 ? 1.0 : endBright);" +
						"  gl_FragColor = texture2D(tex, vTexCoord) * endBright;" +
						"}";
	
	private static float vertCoords[] = { -0.5f,  0.5f,  0.5f,   
								-0.5f, -0.5f,  0.5f,  
								0.5f, -0.5f,  0.5f,   
								0.5f,  0.5f,  0.5f,
								-0.5f,  0.5f,  -0.5f,   
								-0.5f, -0.5f,  -0.5f,  
								0.5f, -0.5f,  -0.5f,   
								0.5f,  0.5f,  -0.5f}; 

	private static short drawOrder[] = { 0, 1, 2, 
								  0, 2, 3,
								  3, 2, 6,
								  3, 6, 7,
								  7, 6, 5, 
								  7, 5, 4,
								  4, 5, 1,
								  4, 1, 0,
								  4, 0, 3,
								  4, 3, 7,
								  1, 5, 6,
								  1, 6, 2}; // order to draw vertices
	
	private static float vertNorms[] = { 1.0f,  0.0f,  0.0f,   
								-1.0f,  0.0f,  0.0f,  
								 0.0f,  1.0f,  0.0f,  
								 0.0f, -1.0f,  0.0f,  
								 0.0f,  0.0f,  1.0f,  
								 0.0f,  0.0f, -1.0f}; 

	private static short normOrder[] = { 4, 4, 4, 
								  4, 4, 4,
								  0, 0, 0,
								  0, 0, 0,
								  5, 5, 5,
								  5, 5, 5,
								  1, 1, 1,
								  1, 1, 1,
								  2, 2, 2,
								  2, 2, 2,
								  3, 3, 3,
								  3, 3, 3}; // order to draw normals
	
	private static float texCoords[] = {  0.125f, 0.25f, // 0
								   0.125f, 0.5f,
								   0.375f, 0.0f,
								   0.375f, 0.25f,
								   0.375f, 0.5f,
								   0.375f, 0.75f, // 5
								   0.375f, 1.0f,
								   0.625f, 0.0f,
								   0.625f, 0.25f,
								   0.625f, 0.5f,
								   0.625f, 0.75f, // 10
								   0.625f, 1.0f,
								   0.875f, 0.25f,
								   0.875f, 0.5f };
	
	private static short texOrder[] = { 11, 6, 5,
								 11, 5, 10,
								 10, 5, 4,
								 10, 4, 9,
								 9, 4, 3,
								 9, 3, 8,
								 8, 3, 2,
								 8, 2, 7,
								 13, 9, 8,
								 13, 8, 12,
								 4, 1, 0,
								 4, 0, 3 };

	// This is a sort of a pale tan
	float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };

	// Arbitrarily off somewhere
	float lightPos[] = { 1.0f, 1.0f, 1.0f};
	
	// Power of the ambient lighting. [0, 1]
	private float ambientTerm = 0.4f;
	
	// Transformation matrices
	float projMatrix[] = new float[16];
	
	// Temporary matrices for accumulating transformations
	float tempMatrixA[] = new float[16];
	float tempMatrixB[] = new float[16];
	
	// For smooth translation demo
	int transTracker = 0;
	
	// Defines the "playable" boundaries for the dice
	float boundary = 3.0f;
	float ratio = 1.0f;
	
	// The current state of the cube/die
	// TODO: Extend to multiple dice
	ArrayList<DieState> dice = new ArrayList<DieState>();

	public DieRenderer(Context c){
		// Initialize the state of our dice stand-ins
		Random r = new Random();
		for(int i = 0; i < 5; i++){
			DieState die = new DieState();
			double angle = (r.nextFloat()*2*Math.PI);
			die.vel[0] = (float) (Math.cos(angle));
			die.vel[1] = (float) (Math.sin(angle));
			double speed = Math.sqrt(Math.pow(die.vel[0], 2)+Math.pow(die.vel[1], 2));
			double scaleF = 0.25f / speed;
			die.vel[0] *= scaleF;
			die.vel[1] *= scaleF;
			dice.add(die);
		}
		// Hold onto the host context
		hostContext = c;
	}

	private class DieState{
		public float translation[];
		public float rotation[];
		public float vel[];
		private float coord[];
		public DieState(){
			translation = new float[16];
			rotation = new float[16];
			vel = new float[2];
			coord = new float[2];
			Matrix.setIdentityM(rotation, 0);
			Matrix.setIdentityM(translation, 0);
			vel[0] = 0;
			vel[1] = 0;
			coord[0] = 0;
			coord[1] = 0;
		}
		public void move(){
			float diag = (float) (Math.sqrt(0.5*0.5+0.5*0.5+0.5*0.5)*.9);
			Random r = new Random();
			if(coord[0] > boundary*ratio-diag || coord[0] < -boundary*ratio+diag){
				vel[0] *= -1;
			}
			if(coord[1] > boundary-diag || coord[1] < -boundary+diag){
				vel[1] *= -1;
			}
			coord[0] += vel[0];
			coord[1] += vel[1];
			translate(vel[0], vel[1], 0);
			// TODO: Cross product, rotate
			float axis[] = new float[3];
			axis[0] =  (float) (-1.0 * vel[1]);
			axis[1] =  (float) (1.0 * vel[0]);
			axis[2] = 0;
			rotate(5.0f, axis[0], axis[1], axis[2]);
		}
		public void translate(float dx, float dy, float dz){
			Matrix.translateM(translation, 0, dx, dy, dz);
		}
		public void rotate(float angle, float ax, float ay, float az){
			if(ax == 0 && ay == 0 && az == 0){
				return;
			}
			Matrix.setIdentityM(tempMatrixA, 0);
			Matrix.rotateM(tempMatrixA, 0, angle, ax, ay, az);
			Matrix.multiplyMM(rotation, 0, tempMatrixA, 0, rotation, 0);
		}
		public void resetTranslate(){
			Matrix.setIdentityM(translation, 0);
		}
		public void resetRotate(){
			Matrix.setIdentityM(rotation, 0);
		}
	}

	@Override
	/*
	 * This is where our setup code happens.
	 * Since we now have a valid GL context, we
	 * can start requesting buffer addresses and
	 * attribute/uniform locations.
	 */
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		/*
		 *  Step One: Load, Compile, and Link the shaders.
		 */
		int vShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
		int fShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
	
		shaderProgramHandle = GLES20.glCreateProgram();
	
		GLES20.glAttachShader(shaderProgramHandle, vShader);
		GLES20.glAttachShader(shaderProgramHandle, fShader);
		GLES20.glLinkProgram(shaderProgramHandle);
	
		// Get the link status.
		final int[] linkStatus = new int[1];
		GLES20.glGetProgramiv(shaderProgramHandle, GLES20.GL_LINK_STATUS, linkStatus, 0);	 
		// If the link failed, log the log.
		if (linkStatus[0] == 0)
		{
			Log.v("Link", GLES20.glGetProgramInfoLog(shaderProgramHandle));
			Log.v("Link", GLES20.glGetShaderInfoLog(vShader));
			Log.v("Link", GLES20.glGetShaderInfoLog(fShader));
		}
	
		/*
		 * Step Two: Request addresses for uniforms and attributes
		 */
		shaderPositionA = GLES20.glGetAttribLocation(shaderProgramHandle, "position");
		shaderNormalA = GLES20.glGetAttribLocation(shaderProgramHandle, "normal");
		shaderTexCoordA = GLES20.glGetAttribLocation(shaderProgramHandle, "texCoord");
		shaderColorU = GLES20.glGetUniformLocation(shaderProgramHandle, "color");
		shaderLightPosU = GLES20.glGetUniformLocation(shaderProgramHandle, "lightPos");
		shaderAmbientU = GLES20.glGetUniformLocation(shaderProgramHandle, "ambient");
		shaderModelViewProjMatrixU = GLES20.glGetUniformLocation(shaderProgramHandle, "uModViewProjMatrix");
		shaderRotMatrixU = GLES20.glGetUniformLocation(shaderProgramHandle, "uRotMatrix");
		shaderDieTexSamplerU = GLES20.glGetUniformLocation(shaderProgramHandle, "tex");
		shaderDieGLTexture = loadTexture(hostContext, R.drawable.die_tex);
	
		/*
		 * Step Three: Generate the vertex coordinates and normal vectors
		 */
	
		float vertCoords[] = new float[drawOrder.length * 4];
		for(int i = 0; i < drawOrder.length; i++){
			vertCoords[i*4+0] = DieRenderer.vertCoords[drawOrder[i]*3+0];
			vertCoords[i*4+1] = DieRenderer.vertCoords[drawOrder[i]*3+1];
			vertCoords[i*4+2] = DieRenderer.vertCoords[drawOrder[i]*3+2];
			vertCoords[i*4+3] = 1;
		}
	
		float vertNorms[] = new float[normOrder.length * 3];
		for(int i = 0; i < normOrder.length; i++){
			vertNorms[i*3+0] = DieRenderer.vertNorms[normOrder[i]*3+0];
			vertNorms[i*3+1] = DieRenderer.vertNorms[normOrder[i]*3+1];
			vertNorms[i*3+2] = DieRenderer.vertNorms[normOrder[i]*3+2];
		}
	
		float texCoords[] = new float[texOrder.length * 2];
		for(int i = 0; i < texOrder.length; i++){
			texCoords[i*2+0] = DieRenderer.texCoords[texOrder[i]*2+0];
			texCoords[i*2+1] = DieRenderer.texCoords[texOrder[i]*2+1];
		}
		
		/*
		 * Step Four: Buffer the coords and norms, move into gl buffers
		 */
		
		int buffers[] = new int[3];
		GLES20.glGenBuffers(3, buffers, 0);
		
		vertexCoordGlBuffer = buffers[0];
		loadFloatsIntoBuffer(GLES20.GL_ARRAY_BUFFER, vertCoords, vertexCoordGlBuffer);
		
		vertexNormGlBuffer = buffers[1];
		loadFloatsIntoBuffer(GLES20.GL_ARRAY_BUFFER, vertNorms, vertexNormGlBuffer);
		
		vertexTexCoordGLBuffer = buffers[2];
		loadFloatsIntoBuffer(GLES20.GL_ARRAY_BUFFER, texCoords, vertexTexCoordGLBuffer);
	}

	/*
	 * @Override
	 * Our viewport changed, so we change our projection matrix to accomodate.
	 * We want our port to be 1 unit "tall," use the screen aspect ratio
	 * to produce a view that matches the screen, and the near/far values
	 * were obtained experimentally (should be a better way).
	 * TODO: Improve the near/far values.
	 */
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		GLES20.glViewport(0, 0, width, height);
		ratio = (float)width / height;
		float units = boundary;
		Matrix.frustumM(projMatrix, 0, -ratio*units, ratio*units, -units, units, 49, 51);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		/*
		 * Step Zero: Clean up the previous frame, declare the program to use, set culling
		 */
		
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
		GLES20.glUseProgram(shaderProgramHandle);
		GLES20.glClearColor(0.0f, 0.5f, 1.0f, 1.0f);
		GLES20.glEnable(GLES20.GL_CULL_FACE);
		GLES20.glCullFace(GLES20.GL_BACK);
		
		/*
		 * Step One: Activate and associate our two vertex buffers
		 */
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexCoordGlBuffer);
		GLES20.glVertexAttribPointer(shaderPositionA, 4, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(shaderPositionA);
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	    
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexNormGlBuffer);
		GLES20.glVertexAttribPointer(shaderNormalA, 3, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(shaderNormalA);
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	    
		GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, vertexTexCoordGLBuffer);
		GLES20.glVertexAttribPointer(shaderTexCoordA, 2, GLES20.GL_FLOAT, false, 0, 0);
		GLES20.glEnableVertexAttribArray(shaderTexCoordA);
	    GLES20.glBindBuffer(GLES20.GL_ARRAY_BUFFER, 0);
	    
	    GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
	    GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, shaderDieGLTexture);
	    GLES20.glUniform1i(shaderDieTexSamplerU, 0);
		
		/*
		 * Step Two: Decide what elements to draw
		 */
		short elements[] = new short[drawOrder.length];
		for(short i = 0; i < elements.length; i++){
			elements[i] = i;
		}
		ByteBuffer bb = ByteBuffer.allocateDirect(elements.length*Short.SIZE).order(ByteOrder.nativeOrder());
		ShortBuffer sb = bb.asShortBuffer();
		sb.put(elements).position(0);
		
		/*
		 * Step Three: Update and populate our uniforms that change from frame to frame and draw
		 */
		GLES20.glUniform4f(shaderColorU, color[0], color[1], color[2], color[3]);
		GLES20.glUniform3f(shaderLightPosU, lightPos[0], lightPos[1], lightPos[2]);
		GLES20.glUniform1f(shaderAmbientU, ambientTerm);
		
		// Basically, what degree of revolution are we on?
		transTracker = (transTracker > 360 ? 0 : transTracker+1);
		for(DieState die : dice){
			die.move();
	    
		    Matrix.setIdentityM(tempMatrixA, 0);
		    Matrix.setIdentityM(tempMatrixB, 0);
		    
			Matrix.setLookAtM(tempMatrixB, 0, 0, 0, 50, 0, 0, 0, 0, 1, 0);
			
			Matrix.multiplyMM(tempMatrixA, 0, die.rotation, 0, tempMatrixA, 0); // Rotate
			Matrix.multiplyMM(tempMatrixA, 0, die.translation, 0, tempMatrixA, 0); // Translate
			Matrix.multiplyMM(tempMatrixA, 0, tempMatrixB, 0, tempMatrixA, 0); // Look
			Matrix.multiplyMM(tempMatrixA, 0, projMatrix, 0, tempMatrixA, 0); // Project
			GLES20.glUniformMatrix4fv(shaderModelViewProjMatrixU, 1, false, tempMatrixA, 0); // Push Model/View/Projection matrix
			GLES20.glUniformMatrix4fv(shaderRotMatrixU, 1, false, die.rotation, 0); // Push Rotation matrix, for normals
			
			GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, sb);
		}
		
		/*
		 * Step Four: Shut down the vertex arrays
		 */
		GLES20.glDisableVertexAttribArray(shaderPositionA);
		GLES20.glDisableVertexAttribArray(shaderNormalA);
		GLES20.glDisableVertexAttribArray(shaderTexCoordA);
	}

	/*
	 * Loads and compiles shader code
	 * @type: GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
	 * @shaderCode: Source code for the coder to be compiled
	 * @return: GLint handle to the compiled shader, ready for linking
	 */
	public static int loadShader(int type, String shaderCode){
		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}
	
	/*
	 * Sends an array of floats into a GL buffer
	 * @programHandle: GLint handle to the program that holds the VAO to be bound into
	 * @values: Values to be sent
	 * @shaderVarAddr: GLint handle to the shader variable that accepts the VBO to be bound into
	 */
	private void loadFloatsIntoBuffer(int programHandle, float[] values, int shaderVarAddr){
		int sizeOfBuffer = Float.SIZE * values.length;
		ByteBuffer valBuffer = ByteBuffer.allocateDirect(sizeOfBuffer);
		valBuffer.order(ByteOrder.nativeOrder());
		FloatBuffer valBufferF = valBuffer.asFloatBuffer();
		valBufferF.put(values).position(0);
		putBufferInGLBuffer(programHandle, valBufferF, sizeOfBuffer, shaderVarAddr);
	}

	
	/*
	 * Sends an array of shorts into a GL buffer (usually for draw orders)
	 * @programHandle: GLint handle to the program that holds the VAO to be bound into
	 * @values: Values to be sent
	 * @shaderVarAddr: GLint handle to the shader variable that accepts the VBO to be bound into
	 */
	private void loadShortsIntoBuffer(int programHandle, short[] values, int shaderVarAddr){
		int sizeOfBuffer = Short.SIZE * values.length;
		ByteBuffer valBuffer = ByteBuffer.allocateDirect(sizeOfBuffer);
		valBuffer.order(ByteOrder.nativeOrder());
		ShortBuffer valBufferF = valBuffer.asShortBuffer();
		valBufferF.put(values).position(0);
		putBufferInGLBuffer(programHandle, valBufferF, sizeOfBuffer, shaderVarAddr);
	}
	
	/*
	 * Sends a buffer into a GL buffer
	 * @programHadle: GLint handle to the program that holds the VAO to be bound into
	 * @b: Buffer of values to be sent. Must be positioned at 0.
	 * @size: Size of a single entry in the buffer.
	 * @shaderVarAddr: GLint handle to the shader variable that accepts the VBO to be bound into
	 */
	private void putBufferInGLBuffer(int type, Buffer b, int size, int shaderVarAddr){
		if(type == GLES20.GL_LINE_STRIP){
			Log.v("GL_CALL", "Using LINE_STRIP");
		}
		GLES20.glBindBuffer(type, shaderVarAddr);
        GLES20.glBufferData(type, size, b, GLES20.GL_STATIC_DRAW);
        GLES20.glBindBuffer(type, 0);
	}
	
	/*
	 * Loads a texture file, specified by resid, into the GL server and returns the handle.
	 */
	public static int loadTexture(final Context context, final int resourceId)
	{
	    final int[] textureHandle = new int[1];
	 
	    // Request a new texture handle
	    GLES20.glGenTextures(1, textureHandle, 0);
	 
	    if (textureHandle[0] != 0)
	    {
	        final BitmapFactory.Options options = new BitmapFactory.Options();
	        options.inScaled = false;   // No pre-scaling
	 
	        // Read in the resource
	        final Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), resourceId, options);
	 
	        // Bind to the texture in OpenGL
	        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0]);
	 
	        // Set filtering
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
	        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
	 
	        // Load the bitmap into the bound texture.
	        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
	 
	        // Recycle the bitmap, since its data has been loaded into OpenGL.
	        bitmap.recycle();
	    }
	 
	    if (textureHandle[0] == 0)
	    {
	        throw new RuntimeException("Error loading texture.");
	    }
	 
	    return textureHandle[0];
	}
}
