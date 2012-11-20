package com.jorflekel.yahtzee.views;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.AttributeSet;

public class DieGLSurfaceView extends GLSurfaceView {

	public DieGLSurfaceView(Context context) {
		super(context);
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
		setRenderer(new DieRenderer());
		// Render the view only when there is a change in the drawing data
		//setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}
	
	public DieGLSurfaceView(Context context, AttributeSet attrs) {
		this(context);
	}

	private static class DieRenderer implements Renderer {
		Cube mQuad;
		private float[] mProjMatrix = new float[16];
		private float[] mVMatrix = new float[16];
		private float[] mMVPMatrix = new float[16];
		private float[] mRotationMatrix = new float[16];
		private float mAngle;

		public void onDrawFrame(GL10 gl) {
			// Redraw background color
			GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
			
			// Create a rotation transformation for the cube
			long time = SystemClock.uptimeMillis() % 4000L;
			mAngle = 0.090f * ((int) time);
			Matrix.setRotateM(mRotationMatrix, 0, mAngle, 1.0f, 1.0f, 1.0f);

		    // Set the camera position (View matrix)
		    Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		    // Calculate the projection and view transformation
		    Matrix.multiplyMM(mMVPMatrix, 0, mVMatrix, 0, mRotationMatrix, 0);
		    Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mMVPMatrix, 0);
		    
			mQuad.draw(mMVPMatrix);
		}

		public void onSurfaceChanged(GL10 gl, int width, int height) {
			GLES20.glViewport(0, 0, width, height);
			float ratio = (float)width / height;
			Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
			
		}

		public void onSurfaceCreated(GL10 gl, EGLConfig config) {
			// Set the background frame color
			GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
			mQuad = new Cube();
		}
	}
	
	private static class Cube {

		private FloatBuffer vertexBuffer;
		private ShortBuffer drawListBuffer;
		private FloatBuffer normBuffer;

		private final String vertexShaderCode =
						"uniform mat4 uMVPMatrix;" +
						"attribute vec3 normal;" +
						"attribute vec4 vPosition;" +
						"varying float bright;" +
						"void main() {" +
						"  gl_Position = uMVPMatrix * vPosition;" +
						"  bright = dot(normal, vec3(1, 0, 0));" +
						"}";

		private final String fragmentShaderCode =
						"precision mediump float;" +
						"uniform vec4 vColor;" +
						"varying float bright;" +
						"void main() {" +
						"  gl_FragColor = vColor * bright;" +
						"}";
		private int mProgram;

		// number of coordinates per vertex in this array
		static final int COORDS_PER_VERTEX = 3;
		static final float vertCoords[] = { -0.5f,  0.5f,  0.5f,   
									  -0.5f, -0.5f,  0.5f,  
									   0.5f, -0.5f,  0.5f,   
									   0.5f,  0.5f,  0.5f,
									  -0.5f,  0.5f, -0.5f,   
									  -0.5f, -0.5f, -0.5f,  
									   0.5f, -0.5f, -0.5f,   
									   0.5f,  0.5f, -0.5f}; 
		
		static final float vertNorms[] = { 1.0f, 0.0f, 0.0f,
									 0.0f, 1.0f, 0.0f,
									 0.0f, 0.0f, 1.0f,
									 -1.0f, 0.0f, 0.0f,
									 0.0f, -1.0f, 0.0f,
									 0.0f, 0.0f, -1.0f };

		private short drawOrder[] = { 0, 1, 2, 
									  0, 2, 3,
									  3, 2, 6,
									  3, 6, 7,
									  7, 6, 5,
									  7, 5, 4,
									  4, 5, 1,
									  4, 1, 0,
									  0, 3, 7,
									  0, 7, 4,
									  1, 5, 6,
									  1, 6, 2}; // order to draw vertices

		// Set color with red, green, blue and alpha (opacity) values
		// This is a sort of a pale tan
		float color[] = { 0.63671875f, 0.76953125f, 0.22265625f, 1.0f };
		private int mPositionHandle;
		private int vertexStride;
		private int mColorHandle;

		public Cube() {
			vertexStride = COORDS_PER_VERTEX * 4;                //4 are how many bytes in a float
			
			// initialize vertex byte buffer for shape coordinates
			ByteBuffer bb = ByteBuffer.allocateDirect(
					// (number of coordinate values * 4 bytes per float)
					vertCoords.length * 4);
			// use the device hardware's native byte order
			bb.order(ByteOrder.nativeOrder());
			vertexBuffer = bb.asFloatBuffer();
			vertexBuffer.put(vertCoords);
			vertexBuffer.position(0);               //4 are how many bytes in a float
			
			// initialize vertex byte buffer for normal vectors
			ByteBuffer nb = ByteBuffer.allocateDirect(
					// (number of coordinate values * 4 bytes per float)
					vertNorms.length * 4);
			// use the device hardware's native byte order
			nb.order(ByteOrder.nativeOrder());
			normBuffer = nb.asFloatBuffer();
			normBuffer.put(vertNorms);
			normBuffer.position(0);

			// initialize byte buffer for the draw list
			ByteBuffer dlb = ByteBuffer.allocateDirect(
					// (# of coordinate values * 2 bytes per short)
					drawOrder.length * 2);
			dlb.order(ByteOrder.nativeOrder());

			drawListBuffer = dlb.asShortBuffer();
			drawListBuffer.put(drawOrder);
			drawListBuffer.position(0);
			int vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
			int fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

			mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
			GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
			GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
			GLES20.glLinkProgram(mProgram);                  // creates OpenGL ES program executables

		}

		public static int loadShader(int type, String shaderCode){

		    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		    int shader = GLES20.glCreateShader(type);

		    // add the source code to the shader and compile it
		    GLES20.glShaderSource(shader, shaderCode);
		    GLES20.glCompileShader(shader);

		    return shader;
		}
		
		public void draw(float[] mMVPMatrix) {
		    // Add program to OpenGL ES environment
		    GLES20.glUseProgram(mProgram);

		    // get handle to vertex shader's vPosition member
		    mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

		    // Enable a handle to the triangle vertices
		    GLES20.glEnableVertexAttribArray(mPositionHandle);

		    // Prepare the triangle coordinate data
		    GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
		                                 GLES20.GL_FLOAT, false,
		                                 vertexStride, vertexBuffer);

		    // get handle to vertex shader's normal member
		    mPositionHandle = GLES20.glGetAttribLocation(mProgram, "normal");

		    // Enable a handle to the normals
		    GLES20.glEnableVertexAttribArray(mPositionHandle);

		    // Prepare the normal data
		    GLES20.glVertexAttribPointer(mPositionHandle, 3,
		                                 GLES20.GL_FLOAT, false,
		                                 3, normBuffer);

		    // get handle to fragment shader's vColor member
		    mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

		    // Set color for drawing the triangle
		    GLES20.glUniform4fv(mColorHandle, 1, color, 0);

		    // get handle to shape's transformation matrix
		    int mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");

			// Apply the projection and view transformation
		    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

		    // Draw the triangle
		    GLES20.glDrawElements(GLES20.GL_TRIANGLES, drawOrder.length, GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

		    // Disable vertex array
		    GLES20.glDisableVertexAttribArray(mPositionHandle);
		}
	}
}
